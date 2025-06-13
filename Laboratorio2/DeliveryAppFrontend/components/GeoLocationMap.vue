<template>
  <div class="location-map-picker">
    <button
      @click="fetchZonaCobertura"
      class="mb-2 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
    >
      Mostrar zona y ubicación del cliente
    </button>
    <div class="search-box mb-2">
      <input 
        v-model="searchQuery" 
        @keyup.enter="searchAddress"
        type="text" 
        placeholder="Buscar dirección en Chile" 
        class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400"
      />
      <button 
        @click="searchAddress" 
        type="button" 
        class="mt-2 bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400"
      >
        Buscar
      </button>
    </div>
    
    <div v-if="searchResults.length > 0" class="search-results mb-2 max-h-40 overflow-y-auto bg-white border rounded p-2">
      <ul>
        <li 
          v-for="(result, index) in searchResults" 
          :key="index" 
          @click="selectLocation(result)"
          class="cursor-pointer p-1 hover:bg-gray-100 border-b last:border-b-0"
        >
          {{ result.display_name }}
        </li>
      </ul>
    </div>
    
    <div 
      id="map" 
      ref="mapContainer" 
      class="h-60 w-full border-4 border-gray-300 rounded-lg shadow-md hover:border-gray-500 hover:shadow-lg transition-all duration-200 overflow-hidden"
    ></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import wellknown from 'wellknown'
import apiClient from '../service/http-common'

// ENTRADA: Coordenadas iniciales para el mapa (opcionales)
// PROCEDIMIENTO: Define las coordenadas iniciales del mapa y el marcador (Santiago de Chile por defecto)
// SALIDA: Objeto de propiedades disponibles para el componente
const props = defineProps({
  initialLatitude: {
    type: [Number, String],
    default: -33.4489 // Santiago de Chile
  },
  initialLongitude: {
    type: [Number, String],
    default: -70.6693 // Santiago de Chile
  },
  areaCoberturaWkt: {
    type: String,
    default: ''
  },
  ubicacionClienteWkt: {
    type: String,
    default: ''
  }
})

// ENTRADA: Ninguna
// PROCEDIMIENTO: Define eventos emitidos por el componente
// SALIDA: Método para comunicar cambios en la ubicación al componente padre
const emit = defineEmits(['update:location'])

// ----- VARIABLES DE ESTADO -----
const searchQuery = ref('')
const searchResults = ref([])
const mapContainer = ref(null)
const map = ref(null)
const marker = ref(null)
const currentAddress = ref('')
const currentAddressData = ref(null)
const areaCoberturaWkt = ref(props.areaCoberturaWkt)
const ubicacionClienteWkt = ref(props.ubicacionClienteWkt)

// Variables para importación dinámica de Leaflet
let L = null

// ----- INICIALIZACIÓN ----

// ENTRADA: Null
// PROCEDIMIENTO: Inicializa el mapa y configura eventos cuando el componente se monta
// SALIDA: Mapa interactivo con marcador inicial y eventos configurados
onMounted(async () => {
  try {
    // Importación dinámica de Leaflet
    const leafletModule = await import('leaflet')
    L = leafletModule.default

    // Importar CSS de Leaflet dinámicamente
    if (process.client) {
      await import('leaflet/dist/leaflet.css')
    }

    const initialLat = props.initialLatitude || -33.4489
    const initialLng = props.initialLongitude || -70.6693
    
    map.value = L.map(mapContainer.value).setView([initialLat, initialLng], 12)
    
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
      maxZoom: 19
    }).addTo(map.value)
    
    fixLeafletIcon()
    
    if (initialLat && initialLng) {
      marker.value = L.marker([initialLat, initialLng], { draggable: true }).addTo(map.value)
      getReverseGeocode(initialLat, initialLng)
      
      marker.value.on('dragend', function(event) {
        const position = marker.value.getLatLng()
        getReverseGeocode(position.lat, position.lng)
      })
    }
    
    map.value.on('click', function(e) {
      setMarkerPosition(e.latlng.lat, e.latlng.lng)
    })
  } catch (error) {
    console.error('Error al cargar Leaflet:', error)
  }
})

// ENTRADA: Null
// PROCEDIMIENTO: Obtiene la URL de los iconos de Leaflet y los configuramos
// SALIDA: Configuración adecuada para mostrar iconos de marcadores
function fixLeafletIcon() {
  if (!L) return
  
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
  })
}

// ENTRADA: Latitud, longitud y dirección opcional
// PROCEDIMIENTO: Establece o actualiza la posición del marcador en el mapa
// SALIDA: Marcador actualizado y emisión de evento con la nueva ubicación
const setMarkerPosition = (lat, lng, address = null) => {
  if (!L) return
  
  if (marker.value) {
    marker.value.setLatLng([lat, lng])
  } else {
    marker.value = L.marker([lat, lng], { draggable: true }).addTo(map.value)
    
    marker.value.on('dragend', function(event) {
      const position = marker.value.getLatLng()
      getReverseGeocode(position.lat, position.lng)
    })
  }
  
  emitLocationUpdate(lat, lng, address)
  
  if (!address) {
    getReverseGeocode(lat, lng)
  }
}

// ----- FUNCIONES DE BÚSQUEDA Y GEOCODIFICACIÓN ----

// ENTRADA: Consulta de búsqueda del usuario
// PROCEDIMIENTO: Busca direcciones usando la API de OpenStreetMap Nominatim sin enviar el formulario
// SALIDA: Resultados de búsqueda actualizados en el estado
const searchAddress = async (event) => {
  // Prevenir envío de formulario si el evento existe
  if (event && event.preventDefault) {
    event.preventDefault()
  }
  
  if (!searchQuery.value.trim()) return
  
  try {
    // Agregar "Chile" a la búsqueda para mejores resultados
    const searchTerm = `${searchQuery.value}, Chile`
    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(searchTerm)}&limit=5&addressdetails=1&countrycodes=cl`
    )
    const data = await response.json()
    searchResults.value = data
  } catch (error) {
    console.error('Error al buscar la dirección:', error)
    searchResults.value = []
  }
}

// ENTRADA: Latitud y longitud
// PROCEDIMIENTO: Obtiene la dirección textual de unas coordenadas geográficas
// SALIDA: Dirección formateada y evento emitido con datos actualizados
const getReverseGeocode = async (lat, lng) => {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`
    )
    const data = await response.json()
    if (data && data.address) {
      currentAddressData.value = data.address
      currentAddress.value = formatSimplifiedAddress(data.address)
      emitLocationUpdate(lat, lng, currentAddress.value, data.address)
    } else {
      currentAddressData.value = null
      currentAddress.value = 'Dirección no disponible'
      emitLocationUpdate(lat, lng, currentAddress.value, null)
    }
  } catch (error) {
    console.error('Error en geocodificación inversa:', error)
    currentAddressData.value = null
    currentAddress.value = 'Dirección no disponible'
    emitLocationUpdate(lat, lng, currentAddress.value, null)
  }
}

// ENTRADA: Objeto de ubicación seleccionada por el usuario
// PROCEDIMIENTO: Procesa la selección del usuario y actualiza el mapa
// SALIDA: Mapa centrado en ubicación seleccionada con marcador actualizado
const selectLocation = (location) => {
  const lat = parseFloat(location.lat)
  const lng = parseFloat(location.lon)
  
  if (location.address) {
    currentAddressData.value = location.address
    currentAddress.value = formatSimplifiedAddress(location.address)
  } else {
    getReverseGeocode(lat, lng)
  }
  
  if (map.value) {
    map.value.setView([lat, lng], 16)
  }
  setMarkerPosition(lat, lng, currentAddress.value)
  searchResults.value = []
}

// ENTRADA: Objeto con datos de dirección
// PROCEDIMIENTO: Formatea los datos de dirección para mostrar solo campos relevantes
// SALIDA: Cadena de texto con la dirección formateada
const formatSimplifiedAddress = (addressData) => {
  if (!addressData || typeof addressData !== 'object') {
    return 'Dirección no disponible'
  }
  
  const street = addressData.road || addressData.street || ''
  const houseNumber = addressData.house_number || ''
  const comuna = addressData.county || addressData.city_district || addressData.suburb || addressData.municipality || ''
  const region = addressData.state || addressData.region || ''
  
  let formattedAddress = ''
  if (street) {
    formattedAddress += street
    if (houseNumber) formattedAddress += ' ' + houseNumber
  }
  
  if (comuna) {
    if (formattedAddress) formattedAddress += ', '
    formattedAddress += comuna
  }
  
  if (region) {
    if (formattedAddress) formattedAddress += ', '
    formattedAddress += region
  }
  
  return formattedAddress || 'Dirección no disponible'
}

// ----- FUNCIONES DE COMUNICACIÓN ----

// ENTRADA: Latitud, longitud y dirección
// PROCEDIMIENTO: Emite evento con los datos de ubicación actualizados
// SALIDA: Evento emitido al componente padre
const emitLocationUpdate = (lat, lng, address, addressData = null) => {
  emit('update:location', { 
    latitude: lat, 
    longitude: lng,
    address: address || currentAddress.value,
    addressData: addressData || currentAddressData.value
  })
}

// Observa cambios y redibuja el mapa si cambian los WKTs
watch([areaCoberturaWkt, ubicacionClienteWkt], ([newArea, newPunto]) => {
  if (map.value && newArea) {
    // Limpia capas anteriores excepto la capa base
    map.value.eachLayer(layer => {
      if (layer instanceof L.GeoJSON) map.value.removeLayer(layer)
    })
    // Dibuja nueva zona
    const areaGeoJson = wellknown(newArea)
    const areaLayer = L.geoJSON(areaGeoJson, { color: 'blue', fillOpacity: 0.2 }).addTo(map.value)
    map.value.fitBounds(areaLayer.getBounds())
    // Dibuja nuevo punto
    if (newPunto) {
      const puntoGeoJson = wellknown(newPunto)
      L.geoJSON(puntoGeoJson, {
        pointToLayer: (feature, latlng) => L.marker(latlng, { icon: L.icon({
          iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
          shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
        }) })
      }).addTo(map.value)
    }
  }
})

// Llama al endpoint y actualiza los datos usando fetch y token
const fetchZonaCobertura = async () => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null
  const rutCliente = user?.rut
  if (!rutCliente) {
    alert('No hay usuario autenticado. Inicia sesión.')
    return
  }
  try {
    const res = await apiClient.get(
      `/api/v1/sentenciassql/zonasCoberturaYUbicacionPorCliente/${rutCliente}`
    )
    if (res.data && res.data.length > 0) {
      areaCoberturaWkt.value = res.data[0].areaCoberturaWkt
      ubicacionClienteWkt.value = res.data[0].ubicacionClienteWkt
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      alert('No autorizado. Inicia sesión nuevamente.')
    } else {
      alert('Error al obtener la zona de cobertura')
    }
  }
}



</script>

<style scoped>
.location-map-picker {
  width: 100%;
}
</style>