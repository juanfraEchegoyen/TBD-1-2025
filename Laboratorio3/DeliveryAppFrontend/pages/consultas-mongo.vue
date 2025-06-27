<template>
  <div class="min-h-screen bg-gray-50 p-6 overflow-y-auto">
    <div class="max-w-7xl mx-auto"> 
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <h1 class="text-3xl font-bold text-gray-900">Consultas NoSQL</h1>
        <p class="text-gray-600 mt-2">Consultas y análisis de datos desde MongoDB</p>
      </div>

      <!-- Promedio de Puntuación por Empresas -->
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-semibold text-gray-800">
            Promedio de Puntuación por Empresas
          </h2>
          <button 
            @click="fetchPromedioPuntuacion"
            :disabled="loading"
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-4 py-2 rounded-lg flex items-center gap-2"
          >
            <svg v-if="loading" class="animate-spin h-4 w-4" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loading ? 'Cargando...' : 'Actualizar Datos' }}
          </button>
        </div>

        <!-- Error  -->
        <div v-if="error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          <div class="flex">
            <svg class="w-5 h-5 mr-2 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <strong>Error:</strong> {{ error }}
            </div>
          </div>
        </div>

        
        <div v-if="!loading && promedioData.length > 0">
          <!-- Tabla -->
          <div class="overflow-x-auto mb-8">
            <table class="min-w-full bg-white">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    ID Empresa
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Promedio Puntuación
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Estrellas
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="empresa in promedioData" :key="empresa._id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {{ empresa._id }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ formatPromedio(empresa.promedio_puntuacion) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-yellow-400">
                    {{ getStars(empresa.promedio_puntuacion) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Resumen -->
          <div class="mt-8 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="bg-blue-50 p-4 rounded-lg">
              <div class="text-blue-600 text-sm font-medium">Total Empresas</div>
              <div class="text-2xl font-bold text-blue-900">{{ promedioData.length }}</div>
            </div>
            <div class="bg-green-50 p-4 rounded-lg">
              <div class="text-green-600 text-sm font-medium">Promedio General</div>
              <div class="text-2xl font-bold text-green-900">{{ formatPromedio(promedioGeneral) }}</div>
            </div>
          </div>
        </div>

        <!-- Si no carga (o vacio) -->
        <div v-else-if="!loading" class="text-center py-12">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">No hay datos disponibles</h3>
          <p class="mt-1 text-sm text-gray-500">Haz clic en "Actualizar Datos" para cargar la información.</p>
        </div>
      </div>

      <!-- Opiniones con Demora o Error -->
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-semibold text-gray-800">
            Opiniones con Demora o Error
          </h2>
          <button 
            @click="fetchOpinionesDemoraError"
            :disabled="loadingOpiniones"
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-4 py-2 rounded-lg flex items-center gap-2"
          >
            <svg v-if="loadingOpiniones" class="animate-spin h-4 w-4" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loadingOpiniones ? 'Cargando...' : 'Actualizar Opiniones' }}
          </button>
        </div>

        <div v-if="errorOpiniones" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          <div class="flex">
            <svg class="w-5 h-5 mr-2 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <strong>Error:</strong> {{ errorOpiniones }}
            </div>
          </div>
        </div>

        <div v-if="!loadingOpiniones && opinionesDemoraError.length > 0">
          <div class="overflow-x-auto mb-8">
            <table class="min-w-full bg-white">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cliente</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Empresa</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Comentario</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Puntuación</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="op in opinionesDemoraError" :key="op.clienteId + op.empresaId + op.fecha" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.clienteId }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.empresaId }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.comentario }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.puntuacion }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.fechaFormateada }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.horaFormateada }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="mt-4 text-sm text-gray-600">
            Total opiniones: <span class="font-bold">{{ opinionesDemoraError.length }}</span>
          </div>
        </div>
        <div v-else-if="!loadingOpiniones" class="text-center py-8 text-gray-500">
          No hay opiniones con demora o error.
        </div>
      </div>

      <!-- Opiniones agrupadas por hora -->
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-semibold text-gray-800">
            Opiniones agrupadas por hora
          </h2>
          <button 
            @click="fetchOpinionesPorHora"
            :disabled="loadingPorHora"
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-4 py-2 rounded-lg flex items-center gap-2"
          >
            <svg v-if="loadingPorHora" class="animate-spin h-4 w-4" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loadingPorHora ? 'Cargando...' : 'Actualizar' }}
          </button>
        </div>

        <div v-if="errorPorHora" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          <div class="flex">
            <svg class="w-5 h-5 mr-2 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <strong>Error:</strong> {{ errorPorHora }}
            </div>
          </div>
        </div>

        <div v-if="!loadingPorHora && opinionesPorHora.length > 0">
          <div class="overflow-x-auto mb-8">
            <table class="min-w-full bg-white">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Promedio Puntaje</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cantidad Opiniones</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="op in opinionesPorHora" :key="op.hora" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.hora }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.promedio_puntaje }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ op.cantidad_opiniones }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="mt-4 text-sm text-gray-600">
            Total horas: <span class="font-bold">{{ opinionesPorHora.length }}</span>
          </div>
        </div>
        <div v-else-if="!loadingPorHora" class="text-center py-8 text-gray-500">
          No hay datos agrupados por hora.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import clienteAPI from '~/service/http-common.js'

definePageMeta({
  title: 'Consultas NoSQL'
})


const promedioData = ref([])
const loading = ref(false)
const error = ref(null)

const opinionesDemoraError = ref([])
const loadingOpiniones = ref(false)
const errorOpiniones = ref(null)

const opinionesPorHora = ref([])
const loadingPorHora = ref(false)
const errorPorHora = ref(null)

const promedioGeneral = computed(() => {
  if (promedioData.value.length === 0) return 0
  const suma = promedioData.value.reduce((acc, empresa) => acc + empresa.promedio_puntuacion, 0)
  return suma / promedioData.value.length
})

// Metodos
const fetchPromedioPuntuacion = async () => {
  loading.value = true
  error.value = null
  
  // Token
  const token = localStorage.getItem('accessToken')
  if (!token) {
    error.value = 'Debes iniciar sesión para acceder a estos datos'
    loading.value = false
    return
  }
  
  try {
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/promedio-por-empresa')
    // Ordenar por puntuación de mayor a menor
    promedioData.value = response.data.sort((a, b) => b.promedio_puntuacion - a.promedio_puntuacion)
  } catch (err) {
    console.error('Error fetching promedio puntuacion:', err)
    if (err.response?.status === 401) {
      error.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
      // Optionally redirect to login
      // await navigateTo('/login')
    } else {
      error.value = err.response?.data?.message || 'Error al cargar los datos'
    }
  } finally {
    loading.value = false
  }
}

const fetchOpinionesDemoraError = async () => {
  loadingOpiniones.value = true
  errorOpiniones.value = null

  const token = localStorage.getItem('accessToken')
  if (!token) {
    errorOpiniones.value = 'Debes iniciar sesión para acceder a estos datos'
    loadingOpiniones.value = false
    return
  }

  try {
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/OpinionesConDemoraOError')
    // Procesar para quitar id y separar fecha/hora
    opinionesDemoraError.value = response.data.map(op => {
      const fechaObj = new Date(op.fecha)
      const fechaFormateada = fechaObj.toISOString().slice(0, 10)
      const horaFormateada = fechaObj.toISOString().slice(11, 16)
      return {
        clienteId: op.clienteId,
        empresaId: op.empresaId,
        comentario: op.comentario,
        puntuacion: op.puntuacion,
        fechaFormateada,
        horaFormateada
      }
    })
  } catch (err) {
    console.error('Error fetching opiniones:', err)
    if (err.response?.status === 401) {
      errorOpiniones.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
    } else {
      errorOpiniones.value = err.response?.data?.message || 'Error al cargar las opiniones'
    }
  } finally {
    loadingOpiniones.value = false
  }
}

const fetchOpinionesPorHora = async () => {
  loadingPorHora.value = true
  errorPorHora.value = null

  const token = localStorage.getItem('accessToken')
  if (!token) {
    errorPorHora.value = 'Debes iniciar sesión para acceder a estos datos'
    loadingPorHora.value = false
    return
  }

  try {
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/OpinionesAgrupadasPorHora')
    // Ignorar _id y solo dejar hora, promedio_puntaje, cantidad_opiniones
    opinionesPorHora.value = response.data.map(({ hora, promedio_puntaje, cantidad_opiniones }) => ({
      hora, promedio_puntaje, cantidad_opiniones
    }))
  } catch (err) {
    console.error('Error fetching opiniones por hora:', err)
    if (err.response?.status === 401) {
      errorPorHora.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
    } else {
      errorPorHora.value = err.response?.data?.message || 'Error al cargar los datos agrupados por hora'
    }
  } finally {
    loadingPorHora.value = false
  }
}

const formatPromedio = (promedio) => {
  return Number(promedio).toFixed(2)
}


const getCalificacionClass = (promedio) => {
  if (promedio >= 4.5) return 'bg-green-100 text-green-800'
  if (promedio >= 4.0) return 'bg-blue-100 text-blue-800'
  if (promedio >= 3.5) return 'bg-yellow-100 text-yellow-800'
  if (promedio >= 3.0) return 'bg-orange-100 text-orange-800'
  if (promedio >= 2.0) return 'bg-red-100 text-red-800'
  return 'bg-gray-100 text-gray-800'
}

const getBarColor = (promedio) => {
  if (promedio >= 4.5) return 'bg-green-500'
  if (promedio >= 4.0) return 'bg-blue-500'
  if (promedio >= 3.5) return 'bg-yellow-500'
  if (promedio >= 3.0) return 'bg-orange-500'
  if (promedio >= 2.0) return 'bg-red-500'
  return 'bg-gray-500'
}

const getStars = (promedio) => {
  const fullStars = Math.floor(promedio)
  const hasHalfStar = promedio % 1 >= 0.5
  let stars = '★'.repeat(fullStars)
  if (hasHalfStar) {
    stars += '☆'
    const emptyStars = 5 - fullStars - 1
    stars += '☆'.repeat(emptyStars)
  } else {
    const emptyStars = 5 - fullStars
    stars += '☆'.repeat(emptyStars)
  }
  return stars
}

// Load data on component mount
onMounted(() => {
  fetchPromedioPuntuacion()
  fetchOpinionesDemoraError()
  fetchOpinionesPorHora()
})
</script>
