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

      <!-- Pedidos con Cambios Rápidos -->
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-semibold text-gray-800">
            Pedidos con Más de 3 Cambios de Estado en Menos de 10 Minutos
          </h2>
          <button 
            @click="fetchPedidosCambiosRapidos"
            :disabled="loadingPedidosCambios"
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-4 py-2 rounded-lg flex items-center gap-2"
          >
            <svg v-if="loadingPedidosCambios" class="animate-spin h-4 w-4" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loadingPedidosCambios ? 'Cargando...' : 'Actualizar Pedidos' }}
          </button>
        </div>

        <div v-if="errorPedidosCambios" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          <div class="flex">
            <svg class="w-5 h-5 mr-2 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <strong>Error:</strong> {{ errorPedidosCambios }}
            </div>
          </div>
        </div>

        <div v-if="!loadingPedidosCambios && pedidosCambiosRapidos.length > 0">
          <div class="overflow-x-auto mb-8">
            <table class="min-w-full bg-white">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Pedido</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hora</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Cambios</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="pedido in pedidosCambiosRapidos" :key="pedido.pedido_id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ pedido.pedido_id }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ pedido.fecha }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ pedido.hora }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                      {{ pedido.historial_estados.length }} cambios
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="mt-4 text-sm text-gray-600">
            Total pedidos con cambios rápidos: <span class="font-bold">{{ pedidosCambiosRapidos.length }}</span>
          </div>
        </div>
        <div v-else-if="!loadingPedidosCambios" class="text-center py-8 text-gray-500">
          No hay pedidos con más de 3 cambios de estado en menos de 10 minutos.
        </div>
      </div>

      <!-- SECCIÓN ACTUALIZADA: Rutas Frecuentes de Repartidores CON MAPA Y TABLA -->
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-semibold text-gray-800">
            Rutas Más Frecuentes de Repartidores (Últimos 7 días)
          </h2>
          <div class="flex gap-2">
            <button 
              @click="fetchRutasFrecuentes"
              :disabled="loadingRutasFrecuentes"
              class="bg-purple-600 hover:bg-purple-700 disabled:bg-purple-400 text-white px-4 py-2 rounded-lg flex items-center gap-2"
            >
              <svg v-if="loadingRutasFrecuentes" class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ loadingRutasFrecuentes ? 'Cargando...' : 'Analizar Zonas' }}
            </button>
          </div>
        </div>

        <div v-if="errorRutasFrecuentes" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          <div class="flex">
            <svg class="w-5 h-5 mr-2 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <strong>Error:</strong> {{ errorRutasFrecuentes }}
            </div>
          </div>
        </div>

        <div v-if="!loadingRutasFrecuentes && rutasFrecuentes.length > 0">
          <!-- MAPA DE ZONAS FRECUENTES -->
          <div class="mb-8">
            <RutasFrecuentesMap :rutas-frecuentes="rutasFrecuentes" />
          </div>

          <!-- TABLA DETALLADA DE ZONAS -->
          <div class="mb-8">
            <div class="overflow-x-auto">
              <table class="min-w-full bg-white">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Zona</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Coordenadas</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Repartidores Únicos</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Visitas</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Promedio Visitas/Repartidor</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr 
                    v-for="(zona, index) in rutasFrecuentes" 
                    :key="index" 
                    class="hover:bg-gray-50"
                  >
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {{ zona.nombreZona || `Zona ${zona.latitudZona.toFixed(5)}, ${zona.longitudZona.toFixed(5)}` }}
                      <div v-if="index < 3" class="text-xs text-yellow-600 font-semibold">
                        🏆 Top {{ index + 1 }}
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <div class="text-xs text-gray-600">
                        <div>{{ zona.latitudZona.toFixed(5) }}</div>
                        <div>{{ zona.longitudZona.toFixed(5) }}</div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ zona.cantidadRepartidores }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ zona.visitasFrecuentes }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ (zona.visitasFrecuentes / zona.cantidadRepartidores).toFixed(1) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Estados sin datos (existente) -->
        <div v-else-if="!loadingRutasFrecuentes && rutasFrecuentes.length === 0 && rutasFrecuentesBuscado" class="text-center py-12">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">No hay zonas frecuentes</h3>
          <p class="mt-1 text-sm text-gray-500">No se encontraron zonas con múltiples repartidores en los últimos 7 días.</p>
        </div>

        <div v-else-if="!loadingRutasFrecuentes && !rutasFrecuentesBuscado" class="text-center py-12">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">Analizar Zonas Frecuentes</h3>
          <p class="mt-1 text-sm text-gray-500">Haz clic en "Analizar Zonas" para encontrar las áreas más visitadas por múltiples repartidores.</p>
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

      <!-- Clientes que no han comprado -->
      <div class="bg-white rounded-lg shadow-sm border p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-semibold text-gray-800">
            Clientes sin Eventos de Compra
          </h2>
          <button 
            @click="fetchClientesSinCompra"
            :disabled="loadingClientesSinCompra"
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-4 py-2 rounded-lg flex items-center gap-2"
          >
            <svg v-if="loadingClientesSinCompra" class="animate-spin h-4 w-4" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loadingClientesSinCompra ? 'Cargando...' : 'Actualizar Clientes' }}
          </button>
        </div>

        <div v-if="errorClientesSinCompra" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          <div class="flex">
            <svg class="w-5 h-5 mr-2 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <strong>Error:</strong> {{ errorClientesSinCompra }}
            </div>
          </div>
        </div>

        <div v-if="!loadingClientesSinCompra && clientesSinCompra.length > 0">
          <!-- Lista de clientes en formato de tarjetas -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 mb-6">
            <div 
              v-for="clienteId in clientesSinCompra" 
              :key="clienteId" 
              class="bg-gradient-to-br from-red-50 to-red-100 border border-red-200 rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-2">
                  <svg class="w-5 h-5 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"></path>
                  </svg>
                  <span class="text-sm font-medium text-red-900">Cliente</span>
                </div>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                  Sin compras
                </span>
              </div>
              <div class="mt-2">
                <p class="text-sm font-mono text-red-700 break-all">{{ clienteId }}</p>
              </div>
            </div>
          </div>

          <!-- Resumen estadísticas -->
          <div class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-red-50 p-4 rounded-lg border border-red-200">
              <div class="text-red-600 text-sm font-medium">Total Clientes Sin Compras</div>
              <div class="text-2xl font-bold text-red-900">{{ clientesSinCompra.length }}</div>
            </div>
          </div>
        </div>

        <div v-else-if="!loadingClientesSinCompra && clientesSinCompra.length === 0 && clientesSinCompraBuscado" class="text-center py-12">
          <svg class="mx-auto h-12 w-12 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-green-900">¡Excelente!</h3>
          <p class="mt-1 text-sm text-green-600">Todos los clientes han realizado al menos una compra.</p>
        </div>

        <div v-else-if="!loadingClientesSinCompra && !clientesSinCompraBuscado" class="text-center py-12">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">Buscar Clientes Inactivos</h3>
          <p class="mt-1 text-sm text-gray-500">Haz clic en "Buscar Clientes" para encontrar usuarios que no han realizado compras.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import clienteAPI from '~/service/http-common.js'
import RutasFrecuentesMap from '~/components/RutasFrecuentesMap.vue'

definePageMeta({
  title: 'Consultas NoSQL'
})

// Estados existentes
const promedioData = ref([])
const loading = ref(false)
const error = ref(null)

const opinionesDemoraError = ref([])
const loadingOpiniones = ref(false)
const errorOpiniones = ref(null)

const opinionesPorHora = ref([])
const loadingPorHora = ref(false)
const errorPorHora = ref(null)

const clientesSinCompra = ref([])
const loadingClientesSinCompra = ref(false)
const errorClientesSinCompra = ref(null)
const clientesSinCompraBuscado = ref(false)

const pedidosCambiosRapidos = ref([])
const loadingPedidosCambios = ref(false)
const errorPedidosCambios = ref(null)

// NUEVOS ESTADOS para rutas frecuentes
const rutasFrecuentes = ref([])
const loadingRutasFrecuentes = ref(false)
const errorRutasFrecuentes = ref(null)
const rutasFrecuentesBuscado = ref(false)

// Computed properties existentes
const promedioGeneral = computed(() => {
  if (promedioData.value.length === 0) return 0
  const suma = promedioData.value.reduce((acc, empresa) => acc + empresa.promedio_puntuacion, 0)
  return suma / promedioData.value.length
})

// NUEVOS COMPUTED PROPERTIES para rutas frecuentes
const repartidoresUnicos = computed(() => {
  const repartidores = new Set(rutasFrecuentes.value.map(ruta => ruta.repartidorId))
  return Array.from(repartidores)
})

const zonaMasPopular = computed(() => {
  if (rutasFrecuentes.value.length === 0) return 'N/A'
  const zonaMasPopular = rutasFrecuentes.value.reduce((max, zona) => {
    const scoreMax = max.cantidadRepartidores * max.visitasFrecuentes
    const scoreZona = zona.cantidadRepartidores * zona.visitasFrecuentes
    return scoreZona > scoreMax ? zona : max
  })
  return getZonaName(zonaMasPopular.latitudZona, zonaMasPopular.longitudZona)
})

const totalVisitas = computed(() => {
  return rutasFrecuentes.value.reduce((total, zona) => total + zona.visitasFrecuentes, 0)
})

const promedioRepartidoresPorZona = computed(() => {
  if (rutasFrecuentes.value.length === 0) return '0.0'
  const totalRepartidores = rutasFrecuentes.value.reduce((total, zona) => total + zona.cantidadRepartidores, 0)
  return (totalRepartidores / rutasFrecuentes.value.length).toFixed(1)
})

// Métodos existentes
const fetchPromedioPuntuacion = async () => {
  loading.value = true
  error.value = null
  
  const token = localStorage.getItem('accessToken')
  if (!token) {
    error.value = 'Debes iniciar sesión para acceder a estos datos'
    loading.value = false
    return
  }
  
  try {
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/promedio-por-empresa')
    promedioData.value = response.data.sort((a, b) => b.promedio_puntuacion - a.promedio_puntuacion)
  } catch (err) {
    console.error('Error fetching promedio puntuacion:', err)
    if (err.response?.status === 401) {
      error.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
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

const fetchPedidosCambiosRapidos = async () => {
  loadingPedidosCambios.value = true
  errorPedidosCambios.value = null

  const token = localStorage.getItem('accessToken')
  if (!token) {
    errorPedidosCambios.value = 'Debes iniciar sesión para acceder a estos datos'
    loadingPedidosCambios.value = false
    return
  }

  try {
    console.log('Llamando a /api/v1/sentenciasnosql/pedidos-cambios-rapidos')
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/pedidos-cambios-rapidos')
    console.log('Respuesta recibida:', response.data)
    
    pedidosCambiosRapidos.value = response.data.map(pedido => {
      const primerTimestamp = pedido.historial_estados[0]?.timestamp
      if (primerTimestamp) {
        const fecha = new Date(primerTimestamp)
        return {
          pedido_id: pedido.pedido_id,
          historial_estados: pedido.historial_estados,
          fecha: fecha.toISOString().slice(0, 10), // YYYY-MM-DD
          hora: fecha.toISOString().slice(11, 16)  // HH:MM
        }
      }
      return {
        pedido_id: pedido.pedido_id,
        historial_estados: pedido.historial_estados,
        fecha: 'N/A',
        hora: 'N/A'
      }
    })
    console.log('Datos procesados:', pedidosCambiosRapidos.value)
  } catch (err) {
    console.error('Error fetching pedidos cambios rapidos:', err)
    if (err.response?.status === 401) {
      errorPedidosCambios.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
    } else {
      errorPedidosCambios.value = err.response?.data?.message || 'Error al cargar los pedidos con cambios rápidos'
    }
  } finally {
    loadingPedidosCambios.value = false
  }
}

// NUEVO MÉTODO para rutas frecuentes
const fetchRutasFrecuentes = async () => {
  loadingRutasFrecuentes.value = true
  errorRutasFrecuentes.value = null

  const token = localStorage.getItem('accessToken')
  if (!token) {
    errorRutasFrecuentes.value = 'Debes iniciar sesión para acceder a estos datos'
    loadingRutasFrecuentes.value = false
    return
  }

  try {
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/rutas-frecuentes-ultimos-7-dias')    
    rutasFrecuentes.value = response.data || []
    rutasFrecuentesBuscado.value = true
    
    // Obtener nombres de zona para cada ubicación
    await updateZonaNames()
    
  } catch (err) {
    if (err.response?.status === 401) {
      errorRutasFrecuentes.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
    } else {
      errorRutasFrecuentes.value = err.response?.data?.message || 'Error al cargar las rutas frecuentes'
    }
  } finally {
    loadingRutasFrecuentes.value = false
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

const fetchClientesSinCompra = async () => {
  loadingClientesSinCompra.value = true
  errorClientesSinCompra.value = null

  const token = localStorage.getItem('accessToken')
  if (!token) {
    errorClientesSinCompra.value = 'Debes iniciar sesión para acceder a estos datos'
    loadingClientesSinCompra.value = false
    return
  }

  try {
    const response = await clienteAPI.get('/api/v1/sentenciasnosql/clientes-sin-evento-compra')
    clientesSinCompra.value = response.data || []
    clientesSinCompraBuscado.value = true
  } catch (err) {
    console.error('Error fetching clientes sin compra:', err)
    if (err.response?.status === 401) {
      errorClientesSinCompra.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
    } else {
      errorClientesSinCompra.value = err.response?.data?.message || 'Error al cargar los clientes sin compras'
    }
  } finally {
    loadingClientesSinCompra.value = false
  }
}

// Métodos de utilidad existentes
const formatPromedio = (promedio) => {
  return Number(promedio).toFixed(2)
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




// MÉTODOS de utilidad actualizados para obtener nombres de zona desde el mapa
const getZonaName = async (lat, lng) => {
  try {
    // Usar API de geocodificación inversa de Nominatim (OpenStreetMap)
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=14&addressdetails=1`
    )
    const data = await response.json()
    
    // Priorizar diferentes niveles de ubicación
    const address = data.address || {}
    
    // Intentar obtener el barrio/comuna primero
    if (address.suburb) return address.suburb
    if (address.neighbourhood) return address.neighbourhood
    if (address.city_district) return address.city_district
    if (address.municipality) return address.municipality
    if (address.town) return address.town
    if (address.city) return address.city
    
    // Fallback a coordenadas si no se encuentra nombre
    return `Zona ${lat.toFixed(5)}, ${lng.toFixed(5)}`
  } catch (error) {
    console.error('Error obteniendo nombre de zona:', error)
    // Fallback en caso de error
    return `Zona ${lat.toFixed(5)}, ${lng.toFixed(5)}`
  }
}

// Cache para nombres de zona para evitar múltiples llamadas a la API
const zonaNamesCache = ref(new Map())

const getZonaNameCached = async (lat, lng) => {
  const key = `${lat.toFixed(5)},${lng.toFixed(5)}`
  
  if (zonaNamesCache.value.has(key)) {
    return zonaNamesCache.value.get(key)
  }
  
  const nombre = await getZonaName(lat, lng)
  zonaNamesCache.value.set(key, nombre)
  return nombre
}

// Método para actualizar los nombres de zona en los datos
const updateZonaNames = async () => {
  if (rutasFrecuentes.value.length === 0) return
  
  for (let zona of rutasFrecuentes.value) {
    zona.nombreZona = await getZonaNameCached(zona.latitudZona, zona.longitudZona)
  }
}

// Load data on component mount
onMounted(() => {
  fetchPromedioPuntuacion()
  fetchOpinionesDemoraError()
  fetchOpinionesPorHora()
  fetchClientesSinCompra()
  fetchPedidosCambiosRapidos()
  fetchRutasFrecuentes()
})
</script>
