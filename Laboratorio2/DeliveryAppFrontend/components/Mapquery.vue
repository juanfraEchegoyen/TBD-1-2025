<template>
  <div class="location-map-picker max-h-[80vh] overflow-y-auto">
    <!-- Botones principales -->
    <button
      v-if="!mostrarSelectorEmpresa"
      @click="mostrarEmpresasYPreparar"
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
      v-if="!mostrarSelectorRepartidor"
      @click="mostrarRepartidoresYPreparar"
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
    <button
      @click="mostrarClientesLejanos"
      class="mb-2 ml-2 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
    >
      6. Mostrar clientes lejanos (&gt;5km)
    </button>

    <!-- Selector de empresa solo para opción 1 (arriba del mapa) -->
    <div v-if="mostrarSelectorEmpresa" class="mb-2">
      <label for="empresaSelect" class="mr-2 font-semibold">Empresa:</label>
      <select
        id="empresaSelect"
        v-model="rutEmpresaSeleccionada"
        class="px-2 py-1 rounded border border-gray-300"
      >
        <option v-for="empresa in empresas" :key="empresa.rut" :value="empresa.rut">
          {{ empresa.nombre }}
        </option>
      </select>
      <button
        @click="buscarEntregasCercanas"
        class="ml-2 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Buscar entregas cercanas
      </button>
    </div>

    <!-- Selector de repartidor solo para opción 3 (DEBAJO de los botones) -->
    <div v-if="mostrarSelectorRepartidor" class="mb-2 mt-4">
      <label for="repartidorSelect" class="mr-2 font-semibold">Repartidor:</label>
      <select
        id="repartidorSelect"
        v-model="rutRepartidorSeleccionado"
        class="px-2 py-1 rounded border border-gray-300"
      >
        <option v-for="repartidor in repartidores" :key="repartidor.rut" :value="repartidor.rut">
          {{ repartidor.nombre }}
        </option>
      </select>
      <button
        @click="buscarDistanciaRecorrida"
        class="ml-2 bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
      >
        Buscar distancia recorrida
      </button>
    </div>

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
      <p><strong>Cantidad de pedidos entregados:</strong> {{ resultadoDistancia.cantidadPedidos ?? resultadoDistancia.idpedido }}</p>
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

    <!-- Mensaje cuando no hay clientes lejanos -->
    <div v-if="clientesLejanos.length === 0 && clientesLejanosConsultado" class="text-red-600 mb-2">
      No hay más clientes lejanos
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
const clientesLejanosMarkers = ref([])
const clientesLejanos = ref([])
const clientesLejanosConsultado = ref(false)
const rutEmpresaSeleccionada = ref('')
const empresas = ref([])
const mostrarSelectorEmpresa = ref(false)

let L = null

const repartidores = ref([])
const rutRepartidorSeleccionado = ref('')
const mostrarSelectorRepartidor = ref(false)

const fetchRepartidores = async () => {
  try {
    const res = await apiClient.get('http://localhost:8080/api/v1/repartidores/RutYnombres')
    if (res.data && Array.isArray(res.data)) {
      repartidores.value = res.data
      if (repartidores.value.length > 0) {
        rutRepartidorSeleccionado.value = repartidores.value[0].rut
      }
    }
  } catch (error) {
    console.error('Error al obtener la lista de repartidores:', error)
  }
}

const mostrarRepartidoresYPreparar = async () => {
  mostrarSelectorRepartidor.value = true
  await fetchRepartidores()
}

const buscarDistanciaRecorrida = async () => {
  mostrarSelectorRepartidor.value = false
  await calcularDistanciaRecorrida()
}
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

    // Obtener lista de empresas
    await fetchEmpresas()
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
    const rut = rutEmpresaSeleccionada.value || '80000000-1'
    const res = await apiClient.get(`http://localhost:8080/api/v1/sentenciassql/entregasCercanas/${rut}`)
    if (res.data && Array.isArray(res.data) && res.data.length > 0) {
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
    } else {
      alert('No existen puntos de entrega cercanos para esta empresa')
    }
  } catch (error) {
    alert('No existen puntos de entrega cercanos para esta empresa')
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
    const rutRepartidor = rutRepartidorSeleccionado.value || '12121212-1'
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

// Función para mostrar todos los clientes lejanos
const mostrarClientesLejanos = async () => {
  try {
    const res = await apiClient.get('/api/v1/sentenciassql/clientesLejanos')
    clientesLejanos.value = res.data
    clientesLejanosConsultado.value = true

    // Si la lista está vacía, muestra un prompt y termina
    if (clientesLejanos.value.length === 0) {
      window.alert('No hay clientes lejanos')
      return
    }

    // Limpia marcadores anteriores
    clientesLejanosMarkers.value.forEach(marker => map.value.removeLayer(marker))
    clientesLejanosMarkers.value = []

    // Dibuja un marcador para cada cliente lejano
    clientesLejanos.value.forEach(cliente => {
      const puntoGeoJson = wellknown(cliente.ubicacionClienteWkt)
      const marker = L.geoJSON(puntoGeoJson, {
        pointToLayer: (feature, latlng) =>
          L.marker(latlng, { title: cliente.nombreCliente })
            .bindPopup(`<b>${cliente.nombreCliente}</b><br>RUT: ${cliente.rutCliente}`)
      }).addTo(map.value)
      clientesLejanosMarkers.value.push(marker)
    })

    // Ajusta el mapa si hay marcadores
    if (clientesLejanosMarkers.value.length > 0) {
      const group = L.featureGroup(clientesLejanosMarkers.value)
      map.value.fitBounds(group.getBounds())
    }
  } catch (e) {
    clientesLejanos.value = []
    clientesLejanosConsultado.value = true
    window.alert('Error al obtener clientes lejanos')
  }
}

// Cambia el endpoint aquí:
const fetchEmpresas = async () => {
  try {
    const res = await apiClient.get('http://localhost:8080/api/v1/empresas/obtenerTodasNombres')
    if (res.data && Array.isArray(res.data)) {
      empresas.value = res.data
      if (empresas.value.length > 0) {
        rutEmpresaSeleccionada.value = empresas.value[0].rut
      }
    }
  } catch (error) {
    console.error('Error al obtener la lista de empresas:', error)
  }
}

// Mostrar selector y cargar empresas
const mostrarEmpresasYPreparar = async () => {
  mostrarSelectorEmpresa.value = true
  await fetchEmpresas()
}

// Al hacer clic en buscar, llama a la función real y oculta el selector
const buscarEntregasCercanas = async () => {
  mostrarSelectorEmpresa.value = false
  await fetchEntregasCercanas()
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