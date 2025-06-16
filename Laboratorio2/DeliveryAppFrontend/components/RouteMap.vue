<template>
  <div class="route-map-container">
    <div class="map-header mb-2">
      <h3 class="text-lg font-semibold">Ruta de Entrega</h3>
      <p v-if="routeInfo" class="text-sm text-gray-600">
        Desde: {{ routeInfo.origin }} → Hasta: {{ routeInfo.destination }}
      </p>
    </div>
    
    <div 
      id="routeMap" 
      ref="mapContainer" 
      class="h-80 w-full border-4 border-gray-300 rounded-lg shadow-md overflow-hidden"
    ></div>
    
    <div v-if="loading" class="text-center py-4">
      <p class="text-gray-600">Calculando ruta...</p>
    </div>
    
    <div v-if="error" class="text-center py-2">
      <p class="text-red-600 text-sm">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import apiClient from '../service/http-common'

// Props del componente
const props = defineProps({
  rutCliente: {
    type: String,
    default: ''
  },
  nombreProducto: {
    type: String,
    default: ''
  },
  shouldCalculateRoute: {
    type: Boolean,
    default: false
  }
})

// Variables reactivas
const mapContainer = ref(null)
const map = ref(null)
const loading = ref(false)
const error = ref('')
const routeInfo = ref(null)

// Variables para Leaflet
let L = null
let routeLayer = null
let markersLayer = null // Nueva variable para manejar los marcadores

// Inicialización del mapa
onMounted(async () => {
  try {
    // Importación dinámica de Leaflet
    const leafletModule = await import('leaflet')
    L = leafletModule.default

    // Importar CSS de Leaflet
    if (process.client) {
      await import('leaflet/dist/leaflet.css')
    }

    // Crear mapa centrado en Santiago
    map.value = L.map(mapContainer.value).setView([-33.4489, -70.6693], 12)
    
    // Agregar capa de tiles
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
      maxZoom: 19
    }).addTo(map.value)
    
    // Crear grupo de capas para los marcadores
    markersLayer = L.layerGroup().addTo(map.value)
    
    // Configurar iconos
    fixLeafletIcon()
    
  } catch (error) {
    console.error('Error al cargar Leaflet:', error)
    error.value = 'Error al cargar el mapa'
  }
})

// Observar cambios en las props para recalcular la ruta
watch(() => [props.rutCliente, props.nombreProducto, props.shouldCalculateRoute], 
  ([rutCliente, nombreProducto, shouldCalculate]) => {
    if (shouldCalculate && rutCliente && nombreProducto && map.value) {
      calculateRoute(rutCliente, nombreProducto)
    }
  }
)

// Función para configurar iconos de Leaflet
function fixLeafletIcon() {
  if (!L) return
  
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
  })
}

// Función para calcular y mostrar la ruta
async function calculateRoute(rutCliente, nombreProducto) {
  if (!map.value || !L) return
  
  loading.value = true
  error.value = ''
  
  try {
    // Hacer petición al backend para obtener la ruta
    const response = await apiClient.get(`/api/v1/pedidos/calcularRuta/${rutCliente}/${nombreProducto}`)
    const lineString = response.data
    
    // Parsear el LINESTRING
    const coordinates = parseLineString(lineString)
    
    if (coordinates.length === 0) {
      throw new Error('No se pudieron obtener las coordenadas de la ruta')
    }
    
    // Limpiar ruta y marcadores anteriores
    clearRoute()
    
    // Crear la polilínea de la ruta
    routeLayer = L.polyline(coordinates, {
      color: 'blue',
      weight: 4,
      opacity: 0.7
    }).addTo(map.value)
    
    // Agregar marcadores de inicio y fin
    const startPoint = coordinates[0]
    const endPoint = coordinates[coordinates.length - 1]
    
    // Marcador de inicio (producto/empresa) - añadido directamente al mapa
    L.marker(startPoint, {
      icon: L.icon({
        iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
      })
    }).addTo(map.value).bindPopup('Producto/Empresa')
    
    // Marcador de fin (cliente) - añadido directamente al mapa
    L.marker(endPoint, {
      icon: L.icon({
        iconUrl: 'https://cdn.jsdelivr.net/gh/pointhi/leaflet-color-markers@master/img/marker-icon-red.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
      })
    }).addTo(map.value).bindPopup('Cliente')
    
    // Ajustar la vista del mapa para mostrar toda la ruta
    map.value.fitBounds(routeLayer.getBounds(), { padding: [20, 20] })
    
    // Actualizar información de la ruta
    routeInfo.value = {
      origin: `Producto: ${nombreProducto}`,
      destination: `Cliente: ${rutCliente}`,
      distance: calculateDistance(coordinates)
    }
    
  } catch (err) {
    console.error('Error al calcular la ruta:', err)
    error.value = 'Error al calcular la ruta: ' + (err.response?.data || err.message)
  } finally {
    loading.value = false
  }
}

// Función para parsear el LINESTRING y convertirlo a coordenadas
function parseLineString(lineString) {
  try {
    // Remover "LINESTRING (" y ")" del string
    const coordsString = lineString.replace('LINESTRING (', '').replace(')', '')
    
    // Dividir por comas para obtener pares de coordenadas
    const coordPairs = coordsString.split(',')
    
    // Convertir cada par a [lat, lng]
    const coordinates = coordPairs.map(pair => {
      const [lng, lat] = pair.trim().split(' ').map(parseFloat)
      return [lat, lng] // Leaflet usa [lat, lng]
    }).filter(coord => !isNaN(coord[0]) && !isNaN(coord[1]))
    
    return coordinates
  } catch (error) {
    console.error('Error al parsear LINESTRING:', error)
    return []
  }
}

// Función para calcular distancia aproximada
function calculateDistance(coordinates) {
  if (!coordinates || coordinates.length < 2) return 0
  
  let distance = 0
  for (let i = 1; i < coordinates.length; i++) {
    const prev = coordinates[i - 1]
    const curr = coordinates[i]
    
    // Fórmula de Haversine simplificada
    const dlat = (curr[0] - prev[0]) * Math.PI / 180
    const dlng = (curr[1] - prev[1]) * Math.PI / 180
    const a = Math.sin(dlat/2) * Math.sin(dlat/2) +
              Math.cos(prev[0] * Math.PI / 180) * Math.cos(curr[0] * Math.PI / 180) *
              Math.sin(dlng/2) * Math.sin(dlng/2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    distance += 6371 * c // Radio de la Tierra en km
  }
  
  return Math.round(distance * 100) / 100 // Redondear a 2 decimales
}

// Función para limpiar el mapa
function clearRoute() {
  // Limpiar la ruta anterior
  if (routeLayer && map.value) {
    map.value.removeLayer(routeLayer)
    routeLayer = null
  }
  
  // Limpiar todos los marcadores del mapa
  if (map.value) {
    map.value.eachLayer(function (layer) {
      if (layer instanceof L.Marker) {
        map.value.removeLayer(layer)
      }
    })
  }
  
  // También limpiar el grupo de marcadores si existe
  if (markersLayer) {
    markersLayer.clearLayers()
  }
  
  routeInfo.value = null
  error.value = ''
}

// Exponer funciones para uso externo
defineExpose({
  calculateRoute,
  clearRoute
})
</script>

<style scoped>
.route-map-container {
  width: 100%;
}

.map-header {
  padding: 0.5rem 0;
}

/* Asegurar que el mapa se renderice correctamente */
#routeMap {
  z-index: 1;
}

/* Estilos para el estado de carga */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
</style>