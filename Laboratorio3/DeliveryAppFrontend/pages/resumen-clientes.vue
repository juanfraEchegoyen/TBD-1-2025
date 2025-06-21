<template>
  <div class="min-h-screen max-h-screen overflow-hidden bg-gradient-to-br from-slate-50 to-slate-100 flex flex-col">
    <!-- Header mejorado -->
    <div class="flex-shrink-0 p-6 text-center">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Resumen de Clientes</h1>
      <p class="text-gray-600">Análisis detallado de pedidos y riesgos por cliente</p>
      <div class="w-24 h-1 bg-gradient-to-r from-blue-500 to-blue-600 mx-auto mt-3 rounded-full"></div>
    </div>

    <!-- Contenedor principal con scroll -->
    <div class="flex-1 overflow-y-auto px-6 pb-6">
      <div class="max-w-7xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl border border-gray-200">
          <div class="p-6">
            
            <!-- Estado de carga -->
            <div v-if="loading" class="text-center py-12">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
              <p class="text-gray-600 text-lg">Cargando resumen de clientes...</p>
            </div>

            <!-- Contenido de la tabla -->
            <div v-else class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4 border border-blue-200">
              <div class="flex items-center mb-6">
                <div class="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center mr-3">
                  <span class="text-white text-lg">👥</span>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-800">Dashboard de Clientes</h2>
                  <p class="text-gray-600 text-sm">Resumen completo de actividad y análisis de riesgo</p>
                </div>
              </div>
              
              <div class="bg-white rounded-lg border border-blue-200 overflow-hidden">
                <!-- Contenedor de tabla con scroll horizontal -->
                <div class="overflow-x-auto max-h-[60vh] overflow-y-auto">
                  <table class="min-w-full">
                    <thead class="sticky top-0 bg-blue-50 border-b border-blue-200 z-10">
                      <tr>
                        <th class="py-4 px-6 text-left font-bold text-blue-800 text-sm">RUT Cliente</th>
                        <th class="py-4 px-6 text-left font-bold text-blue-800 text-sm">Nombre</th>
                        <th class="py-4 px-6 text-left font-bold text-blue-800 text-sm">Cantidad de Pedidos</th>
                        <th class="py-4 px-6 text-left font-bold text-blue-800 text-sm">Monto Total</th>
                        <th class="py-4 px-6 text-left font-bold text-blue-800 text-sm">Riesgo (%)</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="cliente in resumen" :key="cliente.rutCliente" class="border-b border-blue-100 hover:bg-blue-25 transition-colors duration-200">
                        <td class="py-4 px-6 font-mono text-xs bg-gray-50 text-gray-700">{{ cliente.rutCliente }}</td>
                        <td class="py-4 px-6 font-medium text-gray-800">{{ cliente.nombreCliente }}</td>
                        <td class="py-4 px-6">
                          <span class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                            {{ cliente.cantidadPedidos }}
                          </span>
                        </td>
                        <td class="py-4 px-6 font-bold text-green-600">${{ cliente.montoTotal }}</td>
                        <td class="py-4 px-6">
                          <span v-if="riesgos[cliente.rutCliente] !== undefined" 
                                :class="getRiesgoClass(riesgos[cliente.rutCliente])"
                                class="px-3 py-1 rounded-full text-sm font-bold">
                            {{ riesgos[cliente.rutCliente] }}%
                          </span>
                          <span v-else class="inline-flex items-center text-gray-400">
                            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-gray-400 mr-2"></div>
                            Cargando...
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                
                <!-- Mensaje cuando no hay datos -->
                <div v-if="resumen.length === 0" class="text-center py-12">
                  <div class="text-4xl mb-3">📊</div>
                  <h3 class="text-xl font-bold text-gray-600 mb-2">No hay datos disponibles</h3>
                  <p class="text-gray-500">No se encontraron clientes para mostrar en el resumen</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { tokenAutorizacion } from '~/service/http-common'
import clienteAPI from '~/service/http-common'

const resumen = ref<any[]>([])
const riesgos = ref<Record<string, number | string>>({})
const loading = ref(true)

/**
 * Obtiene el porcentaje de riesgo de un cliente específico desde la API
 * @param {string} rut - RUT del cliente para consultar su riesgo
 */
async function fetchRiesgo(rut: string) {
  try {
    const res = await clienteAPI.get(`/api/v1/clientes/${rut}/riesgo`)
    riesgos.value[rut] = res.data
  } catch (e) {
    if (e.response && e.response.status === 401) {
      riesgos.value[rut] = 'No autorizado'
    } else {
      riesgos.value[rut] = 'Error'
    }
  }
}

/**
 * Determina la clase CSS apropiada según el nivel de riesgo del cliente
 * @param {number|string} riesgo - Valor del riesgo (porcentaje o mensaje de error)
 * @returns {string} - Clase CSS correspondiente al nivel de riesgo
 */
function getRiesgoClass(riesgo: number | string) {
  if (typeof riesgo === 'string') {
    return 'bg-gray-100 text-gray-600'
  }
  
  if (riesgo >= 70) {
    return 'bg-red-100 text-red-800'
  } else if (riesgo >= 40) {
    return 'bg-yellow-100 text-yellow-800'
  } else {
    return 'bg-green-100 text-green-800'
  }
}

/**
 * Calcula el monto total de todos los pedidos de todos los clientes
 * @returns {string} - Monto total formateado con separadores de miles
 */
function calcularMontoTotal() {
  return resumen.value.reduce((total, cliente) => total + parseFloat(cliente.montoTotal || 0), 0).toLocaleString()
}

/**
 * Calcula la cantidad total de pedidos de todos los clientes
 * @returns {number} - Suma total de todos los pedidos
 */
function calcularPedidosTotal() {
  return resumen.value.reduce((total, cliente) => total + parseInt(cliente.cantidadPedidos || 0), 0)
}

/**
 * Inicializa el componente cargando el resumen de clientes y sus respectivos riesgos
 * Se ejecuta al montar el componente
 */
onMounted(async () => {
  try {
    const res = await tokenAutorizacion('http://localhost:8080/api/v1/vistas/resumen-clientes')
    if (!res.ok) throw new Error('Error al obtener resumen')
    resumen.value = await res.json()
    for (const cliente of resumen.value) {
      fetchRiesgo(cliente.rutCliente)
    }
  } catch (e) {
    resumen.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>

.hover\:bg-blue-25:hover {
  background-color: rgba(59, 130, 246, 0.05);
}
.transition-colors {
  transition-property: background-color, border-color, color, fill, stroke;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

html {
  scroll-behavior: smooth;
}
</style>