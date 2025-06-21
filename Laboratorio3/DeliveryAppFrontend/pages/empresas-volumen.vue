<template>
  <div class="min-h-screen max-h-screen overflow-hidden bg-gradient-to-br from-slate-50 to-slate-100 flex flex-col">
    <!-- Header mejorado -->
    <div class="flex-shrink-0 p-6 text-center">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Empresas con Mayor Volumen</h1>
      <p class="text-gray-600">Análisis de empresas por cantidad de pedidos realizados</p>
      <div class="w-24 h-1 bg-gradient-to-r from-orange-500 to-orange-600 mx-auto mt-3 rounded-full"></div>
    </div>

    <!-- Contenedor principal con scroll -->
    <div class="flex-1 overflow-y-auto px-6 pb-6">
      <div class="max-w-6xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl border border-gray-200">
          <div class="p-6">
            
            <!-- Estado de carga -->
            <div v-if="loading" class="text-center py-12">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto mb-4"></div>
              <p class="text-gray-600 text-lg">Cargando datos de empresas...</p>
            </div>

            <!-- Contenido de la tabla -->
            <div v-else class="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl p-4 border border-orange-200">
              <div class="flex items-center mb-6">
                <div class="w-12 h-12 bg-orange-500 rounded-full flex items-center justify-center mr-3">
                  <span class="text-white text-lg">🏢</span>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-800">Ranking de Empresas por Volumen</h2>
                  <p class="text-gray-600 text-sm">Empresas ordenadas por cantidad total de pedidos</p>
                </div>
              </div>
              
              <div class="bg-white rounded-lg border border-orange-200 overflow-hidden">
                <!-- Contenedor de tabla con scroll -->
                <div class="overflow-x-auto max-h-[60vh] overflow-y-auto">
                  <table class="min-w-full">
                    <thead class="sticky top-0 bg-orange-50 border-b border-orange-200 z-10">
                      <tr>
                        <th class="py-4 px-6 text-left font-bold text-orange-800 text-sm">Posición</th>
                        <th class="py-4 px-6 text-left font-bold text-orange-800 text-sm">RUT Empresa</th>
                        <th class="py-4 px-6 text-left font-bold text-orange-800 text-sm">Nombre de la Empresa</th>
                        <th class="py-4 px-6 text-left font-bold text-orange-800 text-sm">Cantidad de Pedidos</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(empresa, index) in resumen" :key="empresa.rutEmpresa" class="border-b border-orange-100 hover:bg-orange-25 transition-colors duration-200">
                        <td class="py-4 px-6">
                          <div class="flex items-center">
                            <span v-if="index === 0" class="text-2xl mr-2">🥇</span>
                            <span v-else-if="index === 1" class="text-2xl mr-2">🥈</span>
                            <span v-else-if="index === 2" class="text-2xl mr-2">🥉</span>
                            <span v-else class="text-lg font-bold text-gray-600 mr-2">#{{ index + 1 }}</span>
                          </div>
                        </td>
                        <td class="py-4 px-6 font-mono text-xs bg-gray-50 text-gray-700">{{ empresa.rutEmpresa }}</td>
                        <td class="py-4 px-6 font-medium text-gray-800">{{ empresa.nombreEmpresa }}</td>
                        <td class="py-4 px-6">
                          <span class="bg-orange-100 text-orange-800 px-3 py-1 rounded-full text-sm font-bold">
                            {{ empresa.cantidadPedidos }} pedidos
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                
                <!-- Mensaje cuando no hay datos -->
                <div v-if="resumen.length === 0" class="text-center py-12">
                  <div class="text-4xl mb-3">🏢</div>
                  <h3 class="text-xl font-bold text-gray-600 mb-2">No hay datos disponibles</h3>
                  <p class="text-gray-500">No se encontraron empresas para mostrar en el ranking</p>
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

const resumen = ref<any[]>([])
const loading = ref(true)

/**
 * Calcula el total de pedidos de todas las empresas
 * @returns {string} - Total de pedidos formateado con separadores de miles
 */
function calcularTotalPedidos() {
  return resumen.value.reduce((total, empresa) => total + parseInt(empresa.cantidadPedidos || 0), 0).toLocaleString()
}

/**
 * Calcula el promedio de pedidos por empresa
 * @returns {number} - Promedio redondeado de pedidos por empresa
 */
function calcularPromedioPedidos() {
  if (resumen.value.length === 0) return 0
  const total = resumen.value.reduce((sum, empresa) => sum + parseInt(empresa.cantidadPedidos || 0), 0)
  return Math.round(total / resumen.value.length)
}

/**
 * Inicializa el componente cargando los datos de empresas con mayor volumen
 * Se ejecuta al montar el componente
 */
onMounted(async () => {
  try {
    const res = await tokenAutorizacion('http://localhost:8080/api/v1/vistas/empresas-mas-pedidos')
    if (!res.ok) throw new Error('Error al obtener resumen')
    resumen.value = await res.json()
  } catch (e) {
    resumen.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* Hover personalizado para las filas */
.hover\:bg-orange-25:hover {
  background-color: rgba(249, 115, 22, 0.05);
}

/* Transiciones suaves */
.transition-colors {
  transition-property: background-color, border-color, color, fill, stroke;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

/* Personalizar la barra de scroll */
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

/* Scroll suave */
html {
  scroll-behavior: smooth;
}
</style>
