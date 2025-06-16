<template>
  <div class="location-map-picker max-h-[80vh] overflow-y-auto">
    
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

    <!-- Selector de repartidor solo para opción 3 -->
    <div v-if="mostrarSelectorRepartidor" class="mb-2">
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

    <!-- Botón para listar pedidos multi-zona -->
    <div v-if="mostrarBotonPedidosZonas" class="mb-2">
      <button
        @click="listarPedidosZonas"
        class="bg-pink-600 text-white px-4 py-2 rounded hover:bg-pink-700"
      >
        Buscar pedidos que cruzan zonas
      </button>
    </div>

    <div 
      v-if="mostrarMapa"
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
      <p><strong>Distancia recorrida:</strong> {{ (resultadoDistancia.distanciaRecorrida / 1000).toFixed(3) }} km</p>
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

    <!-- Contenedor de resultado entregas más lejanas -->
    <div
      v-if="entregasMasLejanas.length && mostrarEntregasMasLejanas"
      class="relative mt-4 bg-white p-4 rounded shadow"
    >
      <button
        @click="cerrarEntregasMasLejanas"
        class="close-btn"
        style="position:absolute;top:0.5rem;right:0.5rem;"
      >
        ✖
      </button>
      <h2 class="text-xl font-semibold mb-2">Punto de entrega más lejano a cada empresa</h2>
      <table class="table-auto w-full border-collapse border border-gray-300 mb-4">
        <thead>
          <tr class="bg-gray-200">
            <th class="border px-2 py-1">Color</th>
            <th class="border px-2 py-1">RUT Empresa</th>
            <th class="border px-2 py-1">Nombre Empresa</th>
            <th class="border px-2 py-1">RUT Cliente</th>
            <th class="border px-2 py-1">Nombre Cliente</th>
            <th class="border px-2 py-1">ID Pedido</th>
            <th class="border px-2 py-1">Distancia (km)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entrega, idx) in entregasMasLejanas" :key="entrega.idPedido">
            <td class="border px-2 py-1">
              <span :style="`display:inline-block;width:18px;height:18px;background:${colores[idx % colores.length]};border-radius:3px;border:2px solid #222;`"></span>
            </td>
            <td class="border px-2 py-1">{{ entrega.rutEmpresa }}</td>
            <td class="border px-2 py-1">{{ entrega.nombreEmpresa }}</td>
            <td class="border px-2 py-1">{{ entrega.rutCliente }}</td>
            <td class="border px-2 py-1">{{ entrega.nombreCliente }}</td>
            <td class="border px-2 py-1">{{ entrega.idPedido }}</td>
            <td class="border px-2 py-1">{{ (entrega.distancia /1000).toFixed(3) }}</td>
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
import { ref, onMounted, watch, defineExpose } from 'vue'
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
  },
  showMap: {
    type: Boolean,
    default: true
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
const mostrarMapa = ref(props.showMap)
const mostrarBotonPedidosZonas = ref(false)

let L = null

const repartidores = ref([])
const rutRepartidorSeleccionado = ref('')
const mostrarSelectorRepartidor = ref(false)
const entregasMasLejanas = ref([])
const entregasMasLejanasMarkers = ref([])

const colores = ['red', 'blue', 'green', 'orange', 'purple', 'brown', 'black']

/**
 * Obtiene la lista de repartidores desde la API
 */
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

/**
 * Muestra el selector de repartidores y prepara la consulta
 */
const mostrarRepartidoresYPreparar = async () => {
  mostrarSelectorRepartidor.value = true
  await fetchRepartidores()
}

/**
 * Busca la distancia recorrida por el repartidor seleccionado
 */
const buscarDistanciaRecorrida = async () => {
  await calcularDistanciaRecorrida()
}

/**
 * Muestra solo el selector de repartidor sin mapa para consulta de distancia
 */
const mostrarSelectorRepartidorSinMapa = async () => {
  mostrarMapa.value = false
  mostrarSelectorRepartidor.value = true
  await fetchRepartidores()
}

/**
 * Muestra solo el botón para consultar pedidos que cruzan zonas sin mapa
 */
const mostrarBotonPedidosZonasSinMapa = () => {
  mostrarMapa.value = false
  mostrarBotonPedidosZonas.value = true
}

onMounted(async () => {
  if (!props.showMap) {
    mostrarMapa.value = false
    return
  }
  
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

    await fetchEmpresas()
  } catch (error) {
    console.error('Error al cargar Leaflet:', error)
  }
})

/**
 * Corrige los iconos de Leaflet cargándolos desde CDN
 */
function fixLeafletIcon() {
  if (!L) return
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png'
  })
}

/**
 * Limpia todas las capas del mapa excepto la base
 */
function clearAllLayers() {
  if (!map.value) return
  map.value.eachLayer(layer => {
    if (layer instanceof L.TileLayer) return
    map.value.removeLayer(layer)
  })
  entregaMarkers.value = []
}

/**
 * Obtiene los 5 puntos de entrega más cercanos a una empresa
 */
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

/**
 * Muestra la zona de cobertura y ubicación del cliente autenticado
 */
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
      const areaList = res.data[0].areaCoberturaWkt
      ubicacionClienteWkt.value = res.data[0].ubicacionClienteWkt

      // Dibuja todas las áreas de cobertura
      if (map.value && Array.isArray(areaList)) {
        areaList.forEach(areaWkt => {
          const areaGeoJson = wellknown(areaWkt)
          const areaLayer = L.geoJSON(areaGeoJson, { color: 'blue', fillOpacity: 0.2 }).addTo(map.value)
          map.value.fitBounds(areaLayer.getBounds())
        })
      }

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

/**
 * Calcula la distancia total recorrida por un repartidor
 */
const calcularDistanciaRecorrida = async () => {
  if (map.value) {
    clearAllLayers()
  }
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

/**
 * Cierra la visualización del resultado de distancia
 */
const cerrarDistancia = () => {
  resultadoDistancia.value = null
  mostrarDistancia.value = false
}

/**
 * Lista pedidos que cruzan más de 2 zonas de reparto - solo muestra tabla
 */
const listarPedidosZonas = async () => {
  if (map.value) {
    clearAllLayers()
  }
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

/**
 * Cierra la visualización de pedidos que cruzan zonas
 */
const cerrarPedidosZonas = () => {
  pedidosZonas.value = []
  mostrarPedidosZonas.value = false
}

/**
 * Muestra todos los clientes lejanos en el mapa
 */
const mostrarClientesLejanos = async () => {
  try {
    const res = await apiClient.get('/api/v1/sentenciassql/clientesLejanos')
    clientesLejanos.value = res.data
    clientesLejanosConsultado.value = true

    if (clientesLejanos.value.length === 0) {
      window.alert('No hay clientes lejanos')
      return
    }

    clientesLejanosMarkers.value.forEach(marker => map.value.removeLayer(marker))
    clientesLejanosMarkers.value = []

    clientesLejanos.value.forEach(cliente => {
      const puntoGeoJson = wellknown(cliente.ubicacionClienteWkt)
      const marker = L.geoJSON(puntoGeoJson, {
        pointToLayer: (feature, latlng) =>
          L.marker(latlng, { title: cliente.nombreCliente })
            .bindPopup(`<b>${cliente.nombreCliente}</b><br>RUT: ${cliente.rutCliente}`)
      }).addTo(map.value)
      clientesLejanosMarkers.value.push(marker)
    })

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

/**
 * Obtiene la lista de empresas desde la API
 */
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

/**
 * Muestra el selector de empresas y prepara la consulta
 */
const mostrarEmpresasYPreparar = async () => {
  mostrarSelectorEmpresa.value = true
  await fetchEmpresas()
}

/**
 * Busca entregas cercanas y oculta el selector
 */
const buscarEntregasCercanas = async () => {
  mostrarSelectorEmpresa.value = false
  await fetchEntregasCercanas()
}

/**
 * Muestra las entregas más lejanas por empresa en el mapa
 */
const mostrarEntregasMasLejanas = async () => {
  clearAllLayers()
  entregasMasLejanas.value = []
  entregasMasLejanasMarkers.value = []
  mostrarEntregasMasLejanas.value = false
  try {
    const res = await apiClient.get('http://localhost:8080/api/v1/sentenciassql/entregasMasLejanasPorEmpresa')
    if (res.data && Array.isArray(res.data)) {
      entregasMasLejanas.value = res.data
      mostrarEntregasMasLejanas.value = true

      res.data.forEach((entrega, idx) => {
        const color = colores[idx % colores.length]

        const empresaGeoJson = wellknown(entrega.ubicacionEmpresaWkt)
        const empresaMarker = L.geoJSON(empresaGeoJson, {
          pointToLayer: (feature, latlng) =>
            L.marker(latlng, {
              icon: L.divIcon({
                className: '',
                html: `<div style="background:${color};width:18px;height:18px;border-radius:3px;border:2px solid #222"></div>`,
                iconSize: [18, 18],
                iconAnchor: [9, 9]
              }),
              title: entrega.nombreEmpresa
            }).bindPopup(`<b>Empresa:</b> ${entrega.nombreEmpresa}<br>RUT: ${entrega.rutEmpresa}`)
        }).addTo(map.value)
        entregasMasLejanasMarkers.value.push(empresaMarker)

        const clienteGeoJson = wellknown(entrega.ubicacionClienteWkt)
        const clienteMarker = L.geoJSON(clienteGeoJson, {
          pointToLayer: (feature, latlng) =>
            L.circleMarker(latlng, {
              radius: 10,
              color: color,
              fillColor: color,
              fillOpacity: 0.7
            }).bindPopup(`<b>Cliente:</b> ${entrega.nombreCliente}<br>RUT: ${entrega.rutCliente}<br><b>Distancia:</b> ${entrega.distancia.toFixed(3)} km`)
        }).addTo(map.value)
        entregasMasLejanasMarkers.value.push(clienteMarker)
      })

      if (entregasMasLejanasMarkers.value.length > 0) {
        const group = L.featureGroup(entregasMasLejanasMarkers.value)
        map.value.fitBounds(group.getBounds())
      }
    } else {
      alert('No se encontraron entregas más lejanas')
    }
  } catch (error) {
    alert('Error al obtener las entregas más lejanas')
  }
}

/**
 * Cierra la visualización de entregas más lejanas
 */
const cerrarEntregasMasLejanas = () => {
  entregasMasLejanas.value = []
  entregasMasLejanasMarkers.value.forEach(marker => map.value.removeLayer(marker))
  entregasMasLejanasMarkers.value = []
  mostrarEntregasMasLejanas.value = false
}

defineExpose({
  mostrarEmpresasYPreparar,
  fetchZonaCobertura,
  mostrarRepartidoresYPreparar,
  mostrarEntregasMasLejanas,
  listarPedidosZonas,
  mostrarClientesLejanos,
  buscarEntregasCercanas,
  buscarDistanciaRecorrida,
  calcularDistanciaRecorrida,
  mostrarSelectorRepartidorSinMapa,
  mostrarBotonPedidosZonasSinMapa
})
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