<template>
  <div class="route-map-container">
    <h3 class="text-lg font-semibold mb-4">Seleccionar Ruta de Entrega</h3>
    
    <!-- Controles de búsqueda -->
    <div class="search-controls mb-4">
      <!-- Búsqueda de origen -->
      <div class="search-section">
        <h4 class="font-medium text-green-700 mb-2"> Punto de Origen</h4>
        <div class="search-box">
          <input 
            v-model="origenQuery" 
            @keyup.enter="searchAddress('origen')"
            type="text" 
            placeholder="Buscar dirección de origen en Chile" 
            class="search-input"
          />
          <button 
            @click="searchAddress('origen')" 
            type="button" 
            class="search-button"
          >
            Buscar Origen
          </button>
        </div>
        
        <!-- Resultados de búsqueda origen -->
        <div v-if="origenResults.length > 0" class="search-results">
          <ul>
            <li 
              v-for="(result, index) in origenResults" 
              :key="'origen-' + index" 
              @click="selectLocation(result, 'origen')"
              class="search-result-item"
            >
              📍 {{ result.display_name }}
            </li>
          </ul>
        </div>
      </div>

      <!-- Búsqueda de destino -->
      <div class="search-section">
        <h4 class="font-medium text-red-700 mb-2"> Punto de Destino</h4>
        <div class="search-box">
          <input 
            v-model="destinoQuery" 
            @keyup.enter="searchAddress('destino')"
            type="text" 
            placeholder="Buscar dirección de destino en Chile" 
            class="search-input"
          />
          <button 
            @click="searchAddress('destino')" 
            type="button" 
            class="search-button"
          >
            Buscar Destino
          </button>
        </div>
        
        <!-- Resultados de búsqueda destino -->
        <div v-if="destinoResults.length > 0" class="search-results">
          <ul>
            <li 
              v-for="(result, index) in destinoResults" 
              :key="'destino-' + index" 
              @click="selectLocation(result, 'destino')"
              class="search-result-item"
            >
                 {{ result.display_name }}
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Información de la ruta -->
    <div v-if="origenSeleccionado || destinoSeleccionado" class="route-info mb-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-if="origenSeleccionado" class="location-card origen">
          <h5 class="font-medium text-green-700"> Origen</h5>
          <p class="text-sm">{{ origenSeleccionado.address }}</p>
          <button @click="clearLocation('origen')" class="clear-button"> Limpiar</button>
        </div>
        <div v-if="destinoSeleccionado" class="location-card destino">
          <h5 class="font-medium text-red-700"> Destino</h5>
          <p class="text-sm">{{ destinoSeleccionado.address }}</p>
          <button @click="clearLocation('destino')" class="clear-button"> Limpiar</button>
        </div>
      </div>
      
      <!-- Botón para calcular ruta -->
      <div v-if="origenSeleccionado && destinoSeleccionado" class="mt-4">
        <button 
          @click="calculateRoute" 
          :disabled="calculatingRoute"
          class="route-button"
        >
          {{ calculatingRoute ? 'Calculando Ruta...' : ' Calcular Ruta' }}
        </button>
        <div v-if="routeInfo" class="route-stats mt-2">
          <span class="stat"> Distancia: {{ routeInfo.distance }}</span>
          <span class="stat">⏱ Tiempo estimado: {{ routeInfo.duration }}</span>
        </div>
      </div>
    </div>

    <!-- Mapa -->
    <div 
      id="routeMap" 
      ref="mapContainer" 
      class="route-map"
    ></div>

    <!-- Instrucciones -->
    <div class="instructions mt-4">
      <p class="text-sm text-gray-600">
         <strong>Instrucciones:</strong> 
        Busca y selecciona un punto de origen y destino, o haz clic directamente en el mapa para establecer los puntos.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

// Props
const props = defineProps({
  initialOrigin: {
    type: Object,
    default: null
  },
  initialDestination: {
    type: Object,
    default: null
  }
})

// Eventos emitidos
const emit = defineEmits(['route-updated'])

// Variables de estado
const origenQuery = ref('')
const destinoQuery = ref('')
const origenResults = ref([])
const destinoResults = ref([])
const mapContainer = ref(null)
const map = ref(null)
const origenMarker = ref(null)
const destinoMarker = ref(null)
const routeControl = ref(null)
const origenSeleccionado = ref(null)
const destinoSeleccionado = ref(null)
const calculatingRoute = ref(false)
const routeInfo = ref(null)
const nextClickType = ref(null) // 'origen' o 'destino'

// Variables para importación dinámica
let L = null

// Inicialización
onMounted(async () => {
  try {
    // Importación dinámica de Leaflet y plugins
    const leafletModule = await import('leaflet')
    L = leafletModule.default

    // Importar CSS de Leaflet
    if (process.client) {
      await import('leaflet/dist/leaflet.css')
    }

    // Configuración inicial del mapa (Santiago de Chile)
    const initialLat = -33.4489
    const initialLng = -70.6693
    
    map.value = L.map(mapContainer.value).setView([initialLat, initialLng], 12)
    
    // Usar tiles de CartoDB
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
      maxZoom: 19
    }).addTo(map.value)
    
    fixLeafletIcon()
    
    // Event listener para clics en el mapa
    map.value.on('click', function(e) {
      handleMapClick(e.latlng.lat, e.latlng.lng)
    })

    // Inicializar con valores por defecto si existen
    if (props.initialOrigin) {
      setLocation(props.initialOrigin, 'origen')
    }
    if (props.initialDestination) {
      setLocation(props.initialDestination, 'destino')
    }
    
  } catch (error) {
    console.error('Error al cargar Leaflet:', error)
  }
})

// Función para arreglar iconos de Leaflet
function fixLeafletIcon() {
  if (!L) return
  
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
  })
}

// Función para manejar clics en el mapa
const handleMapClick = async (lat, lng) => {
  if (!origenSeleccionado.value) {
    // Si no hay origen, establecer como origen
    const address = await getReverseGeocode(lat, lng)
    setLocation({ latitude: lat, longitude: lng, address }, 'origen')
  } else if (!destinoSeleccionado.value) {
    // Si hay origen pero no destino, establecer como destino
    const address = await getReverseGeocode(lat, lng)
    setLocation({ latitude: lat, longitude: lng, address }, 'destino')
  }
  // Si ya hay ambos, no hacer nada (el usuario puede usar los botones de limpiar)
}

// Función para buscar direcciones
const searchAddress = async (type) => {
  const query = type === 'origen' ? origenQuery.value : destinoQuery.value
  
  if (!query.trim()) return
  
  try {
    const searchTerm = `${query}, Chile`
    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(searchTerm)}&limit=5&addressdetails=1&countrycodes=cl`
    )
    const data = await response.json()
    
    if (type === 'origen') {
      origenResults.value = data
    } else {
      destinoResults.value = data
    }
  } catch (error) {
    console.error('Error al buscar la dirección:', error)
    if (type === 'origen') {
      origenResults.value = []
    } else {
      destinoResults.value = []
    }
  }
}

// Función para seleccionar una ubicación de los resultados de búsqueda
const selectLocation = (location, type) => {
  const lat = parseFloat(location.lat)
  const lng = parseFloat(location.lon)
  
  let address = location.display_name
  if (location.address) {
    address = formatSimplifiedAddress(location.address)
  }
  
  setLocation({ latitude: lat, longitude: lng, address }, type)
  
  // Limpiar resultados de búsqueda
  if (type === 'origen') {
    origenResults.value = []
    origenQuery.value = ''
  } else {
    destinoResults.value = []
    destinoQuery.value = ''
  }
  
  // Centrar mapa en la nueva ubicación
  if (map.value) {
    map.value.setView([lat, lng], 14)
  }
}

// Función para establecer una ubicación (origen o destino)
const setLocation = (locationData, type) => {
  if (!L || !map.value) return
  
  const { latitude, longitude, address } = locationData
  
  if (type === 'origen') {
    // Remover marcador anterior si existe
    if (origenMarker.value) {
      map.value.removeLayer(origenMarker.value)
    }
    
    // Crear nuevo marcador verde para origen
    const origenIcon = L.divIcon({
      html: '<div style="background-color: #10b981; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>',
      iconSize: [20, 20],
      iconAnchor: [10, 10]
    })
    
    origenMarker.value = L.marker([latitude, longitude], { icon: origenIcon }).addTo(map.value)
    origenMarker.value.bindPopup(`📍 Origen: ${address}`)
    origenSeleccionado.value = { latitude, longitude, address }
    
  } else if (type === 'destino') {
    // Remover marcador anterior si existe
    if (destinoMarker.value) {
      map.value.removeLayer(destinoMarker.value)
    }
    
    // Crear nuevo marcador rojo para destino
    const destinoIcon = L.divIcon({
      html: '<div style="background-color: #ef4444; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>',
      iconSize: [20, 20],
      iconAnchor: [10, 10]
    })
    
    destinoMarker.value = L.marker([latitude, longitude], { icon: destinoIcon }).addTo(map.value)
    destinoMarker.value.bindPopup(`🎯 Destino: ${address}`)
    destinoSeleccionado.value = { latitude, longitude, address }
  }
  
  emitRouteUpdate()
}

// Función para limpiar una ubicación
const clearLocation = (type) => {
  if (type === 'origen') {
    if (origenMarker.value) {
      map.value.removeLayer(origenMarker.value)
      origenMarker.value = null
    }
    origenSeleccionado.value = null
    origenQuery.value = ''
    origenResults.value = []
  } else if (type === 'destino') {
    if (destinoMarker.value) {
      map.value.removeLayer(destinoMarker.value)
      destinoMarker.value = null
    }
    destinoSeleccionado.value = null
    destinoQuery.value = ''
    destinoResults.value = []
  }
  
  // Limpiar ruta si existe
  clearRoute()
  emitRouteUpdate()
}

// Función para calcular ruta
const calculateRoute = async () => {
  if (!origenSeleccionado.value || !destinoSeleccionado.value || !L) return
  
  calculatingRoute.value = true
  
  try {
    // Limpiar ruta anterior
    clearRoute()
    
    // Usar OpenRouteService o OSRM para calcular la ruta
    const origen = origenSeleccionado.value
    const destino = destinoSeleccionado.value
    
    const response = await fetch(
      `https://router.project-osrm.org/route/v1/driving/${origen.longitude},${origen.latitude};${destino.longitude},${destino.latitude}?overview=full&geometries=geojson`
    )
    
    if (!response.ok) {
      throw new Error('Error al calcular la ruta')
    }
    
    const data = await response.json()
    
    if (data.routes && data.routes.length > 0) {
      const route = data.routes[0]
      const coordinates = route.geometry.coordinates.map(coord => [coord[1], coord[0]]) // Invertir lat/lng
      
      // Dibujar la ruta en el mapa
      const routeLine = L.polyline(coordinates, {
        color: '#3b82f6',
        weight: 4,
        opacity: 0.8
      }).addTo(map.value)
      
      routeControl.value = routeLine
      
      // Ajustar vista del mapa para mostrar toda la ruta
      const bounds = L.latLngBounds(coordinates)
      map.value.fitBounds(bounds, { padding: [20, 20] })
      
      // Calcular información de la ruta
      const distance = (route.distance / 1000).toFixed(2) + ' km'
      const duration = Math.round(route.duration / 60) + ' min'
      
      routeInfo.value = { distance, duration }
      
      emitRouteUpdate()
    }
    
  } catch (error) {
    console.error('Error al calcular la ruta:', error)
    alert('Error al calcular la ruta. Por favor, inténtalo de nuevo.')
  } finally {
    calculatingRoute.value = false
  }
}

// Función para limpiar la ruta
const clearRoute = () => {
  if (routeControl.value && map.value) {
    map.value.removeLayer(routeControl.value)
    routeControl.value = null
    routeInfo.value = null
  }
}

// Función para geocodificación inversa
const getReverseGeocode = async (lat, lng) => {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`
    )
    const data = await response.json()
    if (data && data.address) {
      return formatSimplifiedAddress(data.address)
    }
    return 'Dirección no disponible'
  } catch (error) {
    console.error('Error en geocodificación inversa:', error)
    return 'Dirección no disponible'
  }
}

// Función para formatear dirección
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

// Función para emitir actualización de ruta
const emitRouteUpdate = () => {
  const routeData = {
    origen: origenSeleccionado.value,
    destino: destinoSeleccionado.value,
    routeInfo: routeInfo.value,
    // Generar coordenadas para LineString si ambos puntos están definidos
    coordinates: null
  }
  
  if (origenSeleccionado.value && destinoSeleccionado.value && routeControl.value) {
    // Obtener coordenadas de la ruta dibujada
    const latLngs = routeControl.value.getLatLngs()
    routeData.coordinates = latLngs.map(latlng => ({
      lng: latlng.lng,
      lat: latlng.lat
    }))
  }
  
  emit('route-updated', routeData)
}
</script>

<style scoped>
.route-map-container {
  width: 100%;
  background: white;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.search-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

@media (max-width: 768px) {
  .search-controls {
    grid-template-columns: 1fr;
  }
}

.search-section {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 1rem;
  background: #f9fafb;
}

.search-box {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.search-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-button {
  background-color: #3b82f6;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.search-button:hover {
  background-color: #2563eb;
}

.search-results {
  max-height: 150px;
  overflow-y: auto;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  margin-top: 0.5rem;
}

.search-result-item {
  padding: 0.5rem;
  cursor: pointer;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.85rem;
  transition: background-color 0.2s;
}

.search-result-item:hover {
  background-color: #f3f4f6;
}

.search-result-item:last-child {
  border-bottom: none;
}

.route-info {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 6px;
  padding: 1rem;
}

.location-card {
  background: white;
  border-radius: 6px;
  padding: 0.75rem;
  border-left: 4px solid;
}

.location-card.origen {
  border-left-color: #10b981;
}

.location-card.destino {
  border-left-color: #ef4444;
}

.clear-button {
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  cursor: pointer;
  margin-top: 0.5rem;
}

.clear-button:hover {
  background: #dc2626;
}

.route-button {
  background-color: #059669;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.route-button:hover:not(:disabled) {
  background-color: #047857;
}

.route-button:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

.route-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.9rem;
  color: #374151;
}

.stat {
  background: white;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #d1d5db;
}

.route-map {
  height: 400px;
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.instructions {
  background: #fffbeb;
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 0.75rem;
}
</style>