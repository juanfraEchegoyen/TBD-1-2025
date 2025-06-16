<template>
  <div class="min-h-screen max-h-screen overflow-hidden bg-gradient-to-br from-slate-50 to-slate-100 flex flex-col">
    <!-- Header mejorado -->
    <div class="flex-shrink-0 p-6 pb-4 text-center">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Panel de Consultas Geoespaciales</h1>
      <p class="text-gray-600">Análisis espacial del sistema de delivery</p>
      <div class="w-24 h-1 bg-gradient-to-r from-blue-500 to-blue-600 mx-auto mt-3 rounded-full"></div>
    </div>

    <!-- Menú de navegación -->
    <div class="flex-shrink-0 px-6 pb-4">
      <div class="max-w-7xl mx-auto">
        <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-3">
          <div class="flex flex-wrap gap-2 justify-center">
            <button 
              v-for="(consulta, key) in consultasDisponibles" 
              :key="key"
              @click="mostrarConsulta(key)"
              :class="[
                'px-3 py-2 rounded-lg font-medium text-sm transition-all duration-200',
                consultaActiva === key 
                  ? consulta.activeClass + ' text-white shadow-lg' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              {{ consulta.titulo }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Contenedor principal con scroll -->
    <div class="flex-1 overflow-y-auto px-6 pb-6">
      <div class="max-w-7xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl border border-gray-200 h-full">
          <div class="p-6 h-full overflow-y-auto">
            
            <!-- Contenido dinámico basado en la consulta activa -->
            <div v-if="!consultaActiva" class="text-center py-8">
              <div class="text-4xl mb-3">🗺️</div>
              <h3 class="text-xl font-bold text-gray-600 mb-2">Selecciona una consulta geoespacial</h3>
              <p class="text-gray-500">Usa el menú superior para ver los diferentes análisis espaciales disponibles</p>
            </div>

            <!-- Entregas Cercanas -->
            <div v-if="consultaActiva === 'entregasCercanas'" class="h-full">
              <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4 border border-blue-200 h-full flex flex-col">
                <div class="flex items-center mb-4 flex-shrink-0">
                  <div class="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">📍</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Puntos de Entrega Cercanos</h2>
                    <p class="text-gray-600 text-sm">Los 5 puntos de entrega más cercanos por empresa</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-4 border border-blue-200 flex-1 min-h-0">
                  <Mapquery ref="geoMap" :initialLatitude="-33.426" :initialLongitude="-70.6118" />
                </div>
              </div>
            </div>

            <!-- Zona de Cobertura -->
            <div v-if="consultaActiva === 'zonaCobertura'" class="h-full">
              <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4 border border-green-200 h-full flex flex-col">
                <div class="flex items-center mb-4 flex-shrink-0">
                  <div class="w-12 h-12 bg-green-600 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">🌍</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Zona de Cobertura</h2>
                    <p class="text-gray-600 text-sm">Zona de cobertura y ubicación del cliente</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-4 border border-green-200 flex-1 min-h-0">
                  <Mapquery ref="geoMap" :initialLatitude="-33.426" :initialLongitude="-70.6118" />
                </div>
              </div>
            </div>

            <!-- Distancia Repartidor (Solo tabla) -->
            <div v-if="consultaActiva === 'distanciaRepartidor'" class="h-full">
              <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-4 border border-purple-200 h-full flex flex-col">
                <div class="flex items-center mb-4 flex-shrink-0">
                  <div class="w-12 h-12 bg-purple-600 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">🚴</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Distancia Recorrida</h2>
                    <p class="text-gray-600 text-sm">Distancia total recorrida por repartidor</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-4 border border-purple-200 flex-1 min-h-0">
                  <Mapquery ref="geoMapDistancia" :showMap="false" />
                </div>
              </div>
            </div>

            <!-- Entregas Más Lejanas -->
            <div v-if="consultaActiva === 'entregasLejanas'" class="h-full">
              <div class="bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl p-4 border border-yellow-200 h-full flex flex-col">
                <div class="flex items-center mb-4 flex-shrink-0">
                  <div class="w-12 h-12 bg-yellow-600 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">📏</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Entregas Más Lejanas</h2>
                    <p class="text-gray-600 text-sm">Punto de entrega más lejano a cada empresa</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-4 border border-yellow-200 flex-1 min-h-0">
                  <Mapquery ref="geoMap" :initialLatitude="-33.426" :initialLongitude="-70.6118" />
                </div>
              </div>
            </div>

            <!-- Pedidos Cruzan Zonas (Solo tabla) -->
            <div v-if="consultaActiva === 'pedidosZonas'" class="h-full">
              <div class="bg-gradient-to-br from-pink-50 to-pink-100 rounded-xl p-4 border border-pink-200 h-full flex flex-col">
                <div class="flex items-center mb-4 flex-shrink-0">
                  <div class="w-12 h-12 bg-pink-600 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">🔄</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Pedidos Multi-Zona</h2>
                    <p class="text-gray-600 text-sm">Pedidos que cruzan más de 2 zonas de reparto</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-4 border border-pink-200 flex-1 min-h-0">
                  <Mapquery ref="geoMapPedidos" :showMap="false" />
                </div>
              </div>
            </div>

            <!-- Clientes Lejanos -->
            <div v-if="consultaActiva === 'clientesLejanos'" class="h-full">
              <div class="bg-gradient-to-br from-red-50 to-red-100 rounded-xl p-4 border border-red-200 h-full flex flex-col">
                <div class="flex items-center mb-4 flex-shrink-0">
                  <div class="w-12 h-12 bg-red-600 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">🏃‍♂️</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Clientes Lejanos</h2>
                    <p class="text-gray-600 text-sm">Clientes a más de 5km de distancia</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-4 border border-red-200 flex-1 min-h-0">
                  <Mapquery ref="geoMap" :initialLatitude="-33.426" :initialLongitude="-70.6118" />
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import Mapquery from '@/components/Mapquery.vue'

const consultaActiva = ref(null)
const geoMap = ref(null)
const geoMapDistancia = ref(null)
const geoMapPedidos = ref(null)

// Configuración del menú basado en los botones de Mapquery
const consultasDisponibles = {
  entregasCercanas: {
    titulo: '1. Entregas Cercanas',
    activeClass: 'bg-blue-600'
  },
  zonaCobertura: {
    titulo: '2. Zona de Cobertura',
    activeClass: 'bg-green-600'
  },
  distanciaRepartidor: {
    titulo: '3. Distancia Repartidor',
    activeClass: 'bg-purple-600'
  },
  entregasLejanas: {
    titulo: '4. Entregas Lejanas',
    activeClass: 'bg-yellow-600'
  },
  pedidosZonas: {
    titulo: '5. Pedidos Multi-Zona',
    activeClass: 'bg-pink-600'
  },
  clientesLejanos: {
    titulo: '6. Clientes Lejanos',
    activeClass: 'bg-red-600'
  }
}

/**
 * Controla la visualización de consultas geoespaciales activando la seleccionada
 * @param {string} tipoConsulta - Tipo de consulta geoespacial a mostrar
 */
const mostrarConsulta = async (tipoConsulta) => {
  // Si ya está activa la misma consulta, la ocultamos
  if (consultaActiva.value === tipoConsulta) {
    consultaActiva.value = null
    return
  }
  
  // Activamos la nueva consulta
  consultaActiva.value = tipoConsulta
  
  // Esperamos a que el componente se renderice
  await nextTick()
  
  // Ejecutamos la función correspondiente del componente Mapquery
  if (tipoConsulta === 'distanciaRepartidor') {
    // Para distancia repartidor usamos el componente sin mapa
    if (geoMapDistancia.value && geoMapDistancia.value.mostrarSelectorRepartidorSinMapa) {
      geoMapDistancia.value.mostrarSelectorRepartidorSinMapa()
    }
  } else if (tipoConsulta === 'pedidosZonas') {
    // Para pedidos en zonas cruzadas, usamos el componente sin mapa
    if (geoMapPedidos.value && geoMapPedidos.value.mostrarBotonPedidosZonasSinMapa) {
      geoMapPedidos.value.mostrarBotonPedidosZonasSinMapa()
    }
  } else {
    // Para el resto usamos el componente con mapa
    if (geoMap.value) {
      switch(tipoConsulta) {
        case 'entregasCercanas':
          if (geoMap.value.mostrarEmpresasYPreparar) {
            geoMap.value.mostrarEmpresasYPreparar()
          }
          break
        case 'zonaCobertura':
          if (geoMap.value.fetchZonaCobertura) {
            geoMap.value.fetchZonaCobertura()
          }
          break
        case 'entregasLejanas':
          if (geoMap.value.mostrarEntregasMasLejanas) {
            geoMap.value.mostrarEntregasMasLejanas()
          }
          break
        case 'clientesLejanos':
          if (geoMap.value.mostrarClientesLejanos) {
            geoMap.value.mostrarClientesLejanos()
          }
          break
      }
    }
  }
}
</script>

<style scoped>
h1 {
  color: #333333;
}
</style>