<template>
  <div class="rutas-frecuentes-map">
    <div class="map-header mb-4">
      <h3 class="text-lg font-semibold mb-2">Mapa de Zonas Frecuentes</h3>
    </div>
    
    <div 
      id="rutasMap" 
      ref="mapContainer" 
      class="h-96 w-full border-4 border-gray-300 rounded-lg shadow-md overflow-hidden"
    ></div>
    
    <!-- Panel de información de zona seleccionada -->
    <div v-if="zonaSeleccionada" class="mt-4 bg-white border rounded-lg p-4 shadow-sm">
      <div class="flex justify-between items-start mb-3">
        <h4 class="text-lg font-semibold text-gray-900">{{ zonaSeleccionada.nombre }}</h4>
        <button 
          @click="cerrarInfoZona"
          class="text-gray-400 hover:text-gray-600"
        >
          ✕
        </button>
      </div>
      
      <div class="grid grid-cols-2 gap-4 text-sm">
        <div>
          <span class="text-gray-600">Coordenadas:</span>
          <p class="font-mono">{{ zonaSeleccionada.latitud.toFixed(3) }}, {{ zonaSeleccionada.longitud.toFixed(3) }}</p>
        </div>
        <div>
          <span class="text-gray-600">Repartidores únicos:</span>
          <p class="font-semibold text-blue-600">{{ zonaSeleccionada.cantidadRepartidores }}</p>
        </div>
        <div>
          <span class="text-gray-600">Total visitas:</span>
          <p class="font-semibold text-green-600">{{ zonaSeleccionada.visitasFrecuentes }}</p>
        </div>
        <div>
          <span class="text-gray-600">Promedio visitas/repartidor:</span>
          <p class="font-semibold text-purple-600">{{ (zonaSeleccionada.visitasFrecuentes / zonaSeleccionada.cantidadRepartidores).toFixed(1) }}</p>
        </div>
      </div>
      
      <!-- Barra de popularidad -->
      <div class="mt-3">
        <div class="flex justify-between text-xs text-gray-500 mb-1">
          <span>Popularidad</span>
          <span>{{ getPopularidadScore(zonaSeleccionada.cantidadRepartidores, zonaSeleccionada.visitasFrecuentes) }}%</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2">
          <div 
            :class="getPopularidadColor(zonaSeleccionada.cantidadRepartidores, zonaSeleccionada.visitasFrecuentes)"
            class="h-2 rounded-full transition-all duration-300"
            :style="{ width: getPopularidadScore(zonaSeleccionada.cantidadRepartidores, zonaSeleccionada.visitasFrecuentes) + '%' }"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

// Props
const props = defineProps({
  rutasFrecuentes: {
    type: Array,
    default: () => []
  }
})

// Variables reactivas
const mapContainer = ref(null)
const map = ref(null)
const zonaSeleccionada = ref(null)
const markersLayer = ref(null)

// Variables para Leaflet
let L = null

// Inicialización del mapa
onMounted(async () => {
  try {
    // Importar Leaflet dinámicamente
    const leafletModule = await import('leaflet')
    L = leafletModule.default

    if (process.client) {
      await import('leaflet/dist/leaflet.css')
    }

    // Crear mapa centrado en Santiago
    map.value = L.map(mapContainer.value).setView([-33.4489, -70.6693], 11)
    
    // Agregar capa de tiles
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
      maxZoom: 19
    }).addTo(map.value)
    
    // Crear grupo de capas para los marcadores
    markersLayer.value = L.layerGroup().addTo(map.value)
    
    // Configurar iconos
    fixLeafletIcon()
    
    // Cargar zonas si ya hay datos
    if (props.rutasFrecuentes.length > 0) {
      loadZonasEnMapa()
    }
    
  } catch (error) {
    console.error('Error al cargar Leaflet:', error)
  }
})

// Observar cambios en rutasFrecuentes
watch(() => props.rutasFrecuentes, (newRutas) => {
  if (newRutas.length > 0 && map.value) {
    loadZonasEnMapa()
  }
}, { deep: true })

// Configurar iconos de Leaflet
function fixLeafletIcon() {
  if (!L) return
  
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
  })
}

// Método actualizado para obtener nombres de zona usando geocodificación
async function getZonaName(lat, lng) {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=14&addressdetails=1`
    )
    const data = await response.json()
    
    const address = data.address || {}
    
    // Priorizar diferentes niveles de ubicación
    if (address.suburb) return address.suburb
    if (address.neighbourhood) return address.neighbourhood
    if (address.city_district) return address.city_district
    if (address.municipality) return address.municipality
    if (address.town) return address.town
    if (address.city) return address.city
    
    return `Zona ${lat.toFixed(3)}, ${lng.toFixed(3)}`
  } catch (error) {
    console.error('Error obteniendo nombre de zona:', error)
    return `Zona ${lat.toFixed(3)}, ${lng.toFixed(3)}`
  }
}

// Cargar zonas en el mapa (método actualizado)
async function loadZonasEnMapa() {
  if (!map.value || !markersLayer.value || !L) return
  
  // Limpiar marcadores existentes
  markersLayer.value.clearLayers()
  
  for (let zona of props.rutasFrecuentes) {
    const lat = zona.latitudZona
    const lng = zona.longitudZona
    
    // Obtener nombre de zona si no está disponible
    const nombreZona = zona.nombreZona || await getZonaName(lat, lng)
    
    // Determinar color basado en cantidad de repartidores
    const color = getZonaMarkerColor(zona.cantidadRepartidores)
    const radius = getZonaRadius(zona.visitasFrecuentes)
    
    // Crear marcador circular
    const marker = L.circleMarker([lat, lng], {
      radius: radius,
      fillColor: color,
      color: '#ffffff',
      weight: 2,
      opacity: 1,
      fillOpacity: 0.7
    })
    
    // Crear popup con información de la zona
    const popupContent = `
      <div class="zona-popup">
        <h4 class="font-semibold text-gray-900 mb-2">${nombreZona}</h4>
        <div class="text-sm space-y-1">
          <div><strong>Repartidores únicos:</strong> ${zona.cantidadRepartidores}</div>
          <div><strong>Total visitas:</strong> ${zona.visitasFrecuentes}</div>
          <div><strong>Promedio:</strong> ${(zona.visitasFrecuentes / zona.cantidadRepartidores).toFixed(1)} visitas/repartidor</div>
          <div><strong>Coordenadas:</strong> ${lat.toFixed(3)}, ${lng.toFixed(3)}</div>
        </div>
      </div>
    `
    
    marker.bindPopup(popupContent)
    
    // Evento click para mostrar información detallada
    marker.on('click', () => {
      zonaSeleccionada.value = {
        nombre: nombreZona,
        latitud: lat,
        longitud: lng,
        cantidadRepartidores: zona.cantidadRepartidores,
        visitasFrecuentes: zona.visitasFrecuentes
      }
    })
    
    // Agregar al grupo de capas
    markersLayer.value.addLayer(marker)
  }
  
  // Ajustar vista para mostrar todas las zonas
  if (props.rutasFrecuentes.length > 0) {
    fitToAllZones()
  }
}

// Ajustar vista para mostrar todas las zonas
function fitToAllZones() {
  if (!map.value || !markersLayer.value || props.rutasFrecuentes.length === 0) return
  
  const group = new L.featureGroup(markersLayer.value.getLayers())
  map.value.fitBounds(group.getBounds(), { padding: [20, 20] })
}

// Limpiar mapa
function clearMap() {
  if (markersLayer.value) {
    markersLayer.value.clearLayers()
  }
  zonaSeleccionada.value = null
}

// Cerrar información de zona
function cerrarInfoZona() {
  zonaSeleccionada.value = null
}

// Funciones de utilidad para colores y tamaños
function getZonaMarkerColor(cantidadRepartidores) {
  if (cantidadRepartidores >= 4) return '#ef4444'
  if (cantidadRepartidores >= 3) return '#f97316'
  if (cantidadRepartidores >= 2) return '#3b82f6' 
  return '#6b7280' // gray-500
}

function getZonaRadius(visitasFrecuentes) {
  // Radio base 8, máximo 25
  return Math.min(25, 8 + (visitasFrecuentes * 2))
}

function getPopularidadScore(repartidores, visitas) {
  const score = Math.min(100, (repartidores * 30) + (visitas * 10))
  return Math.round(score)
}

function getPopularidadColor(repartidores, visitas) {
  const score = getPopularidadScore(repartidores, visitas)
  if (score >= 80) return 'bg-red-500'
  if (score >= 60) return 'bg-orange-500'
  if (score >= 40) return 'bg-yellow-500'
  return 'bg-green-500'
}

// Exponer funciones
defineExpose({
  fitToAllZones,
  clearMap,
  loadZonasEnMapa
})
</script>

<style scoped>
.rutas-frecuentes-map {
  width: 100%;
}

:deep(.zona-popup) {
  min-width: 200px;
}

:deep(.zona-popup h4) {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 0.5rem;
  margin-bottom: 0.5rem;
}

/* Asegurar que el mapa se renderice correctamente */
#rutasMap {
  z-index: 1;
}
</style>