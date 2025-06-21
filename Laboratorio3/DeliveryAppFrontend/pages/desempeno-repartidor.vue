<template>
  <div class="min-h-screen max-h-screen overflow-hidden bg-gradient-to-br from-slate-50 to-slate-100 flex flex-col">
    <!-- Header mejorado -->
    <div class="flex-shrink-0 p-6 text-center">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Desempeño de Repartidores</h1>
      <p class="text-gray-600">Análisis de rendimiento y puntuaciones de repartidores</p>
      <div class="w-24 h-1 bg-gradient-to-r from-green-500 to-green-600 mx-auto mt-3 rounded-full"></div>
    </div>

    <!-- Contenedor principal con scroll -->
    <div class="flex-1 overflow-y-auto px-6 pb-6">
      <div class="max-w-6xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl border border-gray-200">
          <div class="p-6">
            
            <!-- Estado de carga -->
            <div v-if="loading" class="text-center py-12">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500 mx-auto mb-4"></div>
              <p class="text-gray-600 text-lg">Cargando datos de repartidores...</p>
            </div>

            <!-- Contenido de la tabla -->
            <div v-else class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4 border border-green-200">
              <div class="flex items-center mb-6">
                <div class="w-12 h-12 bg-green-500 rounded-full flex items-center justify-center mr-3">
                  <span class="text-white text-lg">🚴‍♂️</span>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-800">Dashboard de Repartidores</h2>
                  <p class="text-gray-600 text-sm">Métricas de rendimiento y evaluaciones</p>
                </div>
              </div>
              
              <div class="bg-white rounded-lg border border-green-200 overflow-hidden">
                <!-- Contenedor de tabla con scroll -->
                <div class="overflow-x-auto max-h-[60vh] overflow-y-auto">
                  <table class="min-w-full">
                    <thead class="sticky top-0 bg-green-50 border-b border-green-200 z-10">
                      <tr>
                        <th class="py-4 px-6 text-left font-bold text-green-800 text-sm">Ranking</th>
                        <th class="py-4 px-6 text-left font-bold text-green-800 text-sm">RUT Repartidor</th>
                        <th class="py-4 px-6 text-left font-bold text-green-800 text-sm">Nombre</th>
                        <th class="py-4 px-6 text-left font-bold text-green-800 text-sm">Entregas Realizadas</th>
                        <th class="py-4 px-6 text-left font-bold text-green-800 text-sm">Puntuación Promedio</th>
                        <th class="py-4 px-6 text-left font-bold text-green-800 text-sm">Estado</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(repartidor, index) in resumen" :key="repartidor.rutRepartidor" class="border-b border-green-100 hover:bg-green-25 transition-colors duration-200">
                        <td class="py-4 px-6">
                          <div class="flex items-center">
                            <span v-if="index === 0" class="text-2xl mr-2">🏆</span>
                            <span v-else-if="index === 1" class="text-2xl mr-2">🥈</span>
                            <span v-else-if="index === 2" class="text-2xl mr-2">🥉</span>
                            <span v-else class="text-lg font-bold text-gray-600 mr-2">#{{ index + 1 }}</span>
                          </div>
                        </td>
                        <td class="py-4 px-6 font-mono text-xs bg-gray-50 text-gray-700">{{ repartidor.rutRepartidor }}</td>
                        <td class="py-4 px-6 font-medium text-gray-800">{{ repartidor.nombreRepartidor }}</td>
                        <td class="py-4 px-6">
                          <span class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                            {{ repartidor.entregasRealizadas }} entregas
                          </span>
                        </td>
                        <td class="py-4 px-6">
                          <div class="flex items-center space-x-2">
                            <span :class="getPuntuacionClass(repartidor.puntuacionPromedio)" class="px-3 py-1 rounded-full text-sm font-bold">
                              ⭐ {{ repartidor.puntuacionPromedio.toFixed(2) }}
                            </span>
                          </div>
                        </td>
                        <td class="py-4 px-6">
                          <span :class="getEstadoClass(repartidor.puntuacionPromedio)" class="px-3 py-1 rounded-full text-xs font-bold">
                            {{ getEstadoTexto(repartidor.puntuacionPromedio) }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                
                <!-- Mensaje cuando no hay datos -->
                <div v-if="resumen.length === 0" class="text-center py-12">
                  <div class="text-4xl mb-3">🚴‍♂️</div>
                  <h3 class="text-xl font-bold text-gray-600 mb-2">No hay datos disponibles</h3>
                  <p class="text-gray-500">No se encontraron repartidores para mostrar en el desempeño</p>
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

function getPuntuacionClass(puntuacion: number) {
  if (puntuacion >= 4.5) {
    return 'bg-green-100 text-green-800'
  } else if (puntuacion >= 3.5) {
    return 'bg-yellow-100 text-yellow-800'
  } else {
    return 'bg-red-100 text-red-800'
  }
}

function getEstadoClass(puntuacion: number) {
  if (puntuacion >= 4.5) {
    return 'bg-green-100 text-green-800'
  } else if (puntuacion >= 3.5) {
    return 'bg-yellow-100 text-yellow-800'
  } else {
    return 'bg-red-100 text-red-800'
  }
}

function getEstadoTexto(puntuacion: number) {
  if (puntuacion >= 4.5) {
    return 'EXCELENTE'
  } else if (puntuacion >= 3.5) {
    return 'BUENO'
  } else {
    return 'NECESITA MEJORAR'
  }
}

function calcularTotalEntregas() {
  return resumen.value.reduce((total, repartidor) => total + parseInt(repartidor.entregasRealizadas || 0), 0).toLocaleString()
}

function calcularPromedioEntregas() {
  if (resumen.value.length === 0) return 0
  const total = resumen.value.reduce((sum, repartidor) => sum + parseInt(repartidor.entregasRealizadas || 0), 0)
  return Math.round(total / resumen.value.length)
}

function calcularPromedioPuntuacion() {
  if (resumen.value.length === 0) return '0.00'
  const total = resumen.value.reduce((sum, repartidor) => sum + parseFloat(repartidor.puntuacionPromedio || 0), 0)
  return (total / resumen.value.length).toFixed(2)
}

onMounted(async () => {
  try {
    const res = await tokenAutorizacion('http://localhost:8080/api/v1/vistas/desempeno-repartidores')
    if (!res.ok) throw new Error('Error al obtener resumen')
    const data = await res.json()
    // Ordenar por puntuación promedio de mayor a menor
    resumen.value = data.sort((a, b) => parseFloat(b.puntuacionPromedio) - parseFloat(a.puntuacionPromedio))
  } catch (e) {
    resumen.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* Hover personalizado para las filas */
.hover\:bg-green-25:hover {
  background-color: rgba(34, 197, 94, 0.05);
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
