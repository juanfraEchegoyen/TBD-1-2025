<template>
  <div class="location-map-picker max-h-[80vh] overflow-y-auto">
    <button
      @click="fetchEntregasCercanas"
      class="mb-2 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
    >
      1. Obtener los 5 puntos de entrega más cercanos
    </button>
    <button
      @click="fetchZonaCobertura"
      class="mb-2 ml-2 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
    >
      2. Mostrar zona y ubicación del cliente
    </button>
    <button
      @click="calcularDistanciaRecorrida"
      class="mb-2 ml-2 bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
    >
      3. Calcular la distancia total recorrida por un repartidor
    </button>
    <button
      @click="listarPedidosZonas"
      class="mb-2 ml-2 bg-pink-600 text-white px-4 py-2 rounded hover:bg-pink-700"
    >
      5. Listar pedidos que cruzan más de 2 zonas de reparto
    </button>
    <div 
      id="map" 
      ref="mapContainer" 
      class="h-60 w-full border-4 border-gray-300 rounded-lg shadow-md hover:border-gray-500 hover:shadow-lg transition-all duration-200 overflow-hidden"
    ></div>

    <!-- Contenedor de resultado distancia recorrida -->
    <div
      v-if="resultadoDistancia && mostrarDistancia"
      class="relative mt-4 bg-white p-4 rounded shadow"
    >
      <button
        @click="cerrarDistancia"
        class="close-btn"
        style="position:absolute;top:0.5rem;right:0.5rem;"
      >
        ✖
      </button>
      <h2 class="text-xl font-semibold mb-2">Distancia total recorrida por repartidor</h2>
      <p><strong>Rut repartidor:</strong> {{ resultadoDistancia.rutCliente }}</p>
      <p><strong>ID Pedido:</strong> {{ resultadoDistancia.idpedido }}</p>
      <p><strong>Distancia recorrida:</strong> {{ resultadoDistancia.distanciaRecorrida.toFixed(3) }} km</p>
    </div>

    <!-- Contenedor de resultado pedidos que cruzan zonas -->
    <div
      v-if="pedidosZonas.length && mostrarPedidosZonas"
      class="relative mt-4 bg-white p-4 rounded shadow"
    >
      <button
        @click="cerrarPedidosZonas"
        class="close-btn"
        style="position:absolute;top:0.5rem;right:0.5rem;"
      >
        ✖
      </button>
      <h2 class="text-xl font-semibold mb-2">Pedidos que cruzan más de 2 zonas de reparto</h2>
      <table class="table-auto w-full border-collapse border border-gray-300 mb-4">
        <thead>
          <tr class="bg-gray-200">
            <th class="border px-2 py-1">ID Pedido</th>
            <th class="border px-2 py-1">Rut Repartidor</th>
            <th class="border px-2 py-1">Zonas Cruzadas</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pedido in pedidosZonas" :key="pedido.id_pedido">
            <td class="border px-2 py-1">{{ pedido.id_pedido }}</td>
            <td class="border px-2 py-1">{{ pedido.rut_repartidor }}</td>
            <td class="border px-2 py-1">{{ pedido.zonas_cruzadas }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import wellknown from 'wellknown'
import apiClient from '../service/http-common'

const props = defineProps({
  initialLatitude: {
    type: [Number, String],
    default: -33.4489
  },
  initialLongitude: {
    type: [Number, String],
    default: -70.6693
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

const emit = defineEmits(['update:location'])

const mapContainer = ref(null)
const map = ref(null)
const marker = ref(null)
const areaCoberturaWkt = ref(props.areaCoberturaWkt)
const ubicacionClienteWkt = ref(props.ubicacionClienteWkt)
const entregaMarkers = ref([])
const resultadoDistancia = ref(null)
const mostrarDistancia = ref(false)
const pedidosZonas = ref([])
const mostrarPedidosZonas = ref(false)

let L = null

onMounted(async () => {
  try {
    const leafletModule = await import('leaflet')
    L = leafletModule.default

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
    }
  } catch (error) {
    console.error('Error al cargar Leaflet:', error)
  }
})

function fixLeafletIcon() {
  if (!L) return
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
  })
}

// Limpia todas las capas del mapa excepto la base
function clearAllLayers() {
  if (!map.value) return
  map.value.eachLayer(layer => {
    if (layer instanceof L.TileLayer) return
    map.value.removeLayer(layer)
  })
  entregaMarkers.value = []
}

// 1. Obtener los 5 puntos de entrega más cercanos
const fetchEntregasCercanas = async () => {
  clearAllLayers()
  mostrarDistancia.value = false
  mostrarPedidosZonas.value = false
  try {
    const res = await apiClient.get('http://localhost:8080/api/v1/sentenciassql/entregasCercanas/80000000-1')
    if (res.data && Array.isArray(res.data)) {
      res.data.forEach(entrega => {
        const punto = wellknown.parse(entrega.distanciaLinea)
        if (punto && punto.coordinates) {
          const [lng, lat] = punto.coordinates
          const markerEntrega = L.circleMarker([lat, lng], {
            radius: 10,
            color: 'red',
            fillColor: '#f03',
            fillOpacity: 0.8
          }).addTo(map.value)
          markerEntrega.bindPopup(
            `<b>Cliente:</b> ${entrega.rutCliente}<br><b>ID Pedido:</b> ${entrega.idPedido}`
          )
          entregaMarkers.value.push(markerEntrega)
        }
      })
    }
  } catch (error) {
    alert('Error al obtener los puntos de entrega más cercanos')
  }
}

// 2. Mostrar zona y ubicación del cliente
const fetchZonaCobertura = async () => {
  clearAllLayers()
  mostrarDistancia.value = false
  mostrarPedidosZonas.value = false
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

      // Dibuja la zona de cobertura
      if (map.value && areaCoberturaWkt.value) {
        const areaGeoJson = wellknown(areaCoberturaWkt.value)
        const areaLayer = L.geoJSON(areaGeoJson, { color: 'blue', fillOpacity: 0.2 }).addTo(map.value)
        map.value.fitBounds(areaLayer.getBounds())
      }
      // Dibuja el punto del cliente
      if (map.value && ubicacionClienteWkt.value) {
        const puntoGeoJson = wellknown(ubicacionClienteWkt.value)
        L.geoJSON(puntoGeoJson, {
          pointToLayer: (feature, latlng) => L.marker(latlng, { icon: L.icon({
            iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
          }) })
        }).addTo(map.value)
      }
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      alert('No autorizado. Inicia sesión nuevamente.')
    } else {
      alert('Error al obtener la zona de cobertura')
    }
  }
}

// 3. Calcular la distancia total recorrida por un repartidor
const calcularDistanciaRecorrida = async () => {
  clearAllLayers()
  resultadoDistancia.value = null
  mostrarDistancia.value = false
  mostrarPedidosZonas.value = false
  try {
    const rutRepartidor = '12121212-1'
    const res = await apiClient.get(`http://localhost:8080/api/v1/sentenciassql/distanciaRecorrida/${rutRepartidor}`)
    if (res.data && res.data.distanciaRecorrida !== undefined) {
      resultadoDistancia.value = res.data
      mostrarDistancia.value = true
    } else {
      alert('No se pudo obtener la distancia recorrida')
    }
  } catch (error) {
    alert('Error al obtener la distancia recorrida')
  }
}

const cerrarDistancia = () => {
  resultadoDistancia.value = null
  mostrarDistancia.value = false
}

// 5. Listar pedidos que cruzan más de 2 zonas de reparto
const listarPedidosZonas = async () => {
  clearAllLayers()
  pedidosZonas.value = []
  mostrarPedidosZonas.value = false
  mostrarDistancia.value = false
  try {
    const res = await apiClient.get('http://localhost:8080/api/v1/sentenciassql/pedidosQueCruzaronZonas')
    if (res.data && Array.isArray(res.data)) {
      pedidosZonas.value = res.data
      mostrarPedidosZonas.value = true
    } else {
      alert('No se pudo obtener la lista de pedidos')
    }
  } catch (error) {
    alert('Error al obtener la lista de pedidos')
  }
}

const cerrarPedidosZonas = () => {
  pedidosZonas.value = []
  mostrarPedidosZonas.value = false
}
</script>

<style scoped>
.location-map-picker {
  width: 100%;
}
.close-btn {
  color: #6b7280;
  cursor: pointer;
  transition: color 0.2s;
  background: none;
  border: none;
  font-size: 1.2rem;
}
.close-btn:hover {
  color: #374151;
}
</style>