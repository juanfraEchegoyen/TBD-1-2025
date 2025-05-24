<template>
  <GeoTaskBackground>
    <div class="min-h-screen flex flex-col items-center py-10">
      <div class="w-full max-w-2xl bg-white/80 rounded-[2.5rem] p-10 flex flex-col">
        <div class="flex items-center justify-between mb-8">
          <div class="flex items-center">
            <GeoTaskLogo />
            <h1 class="text-3xl font-bold text-green-700 tracking-tight ml-3">Mis Tareas</h1>
          </div>
          <GeoTaskButton color="green" @click="$router.push('/registroTareas')">
            Nueva Tarea
          </GeoTaskButton>
        </div>
        
        <!-- AVISOS DE TAREAS POR EXPIRAR -->
        <div v-if="avisosExpiracion.length > 0" class="mb-4">
          <div 
            v-for="aviso in avisosExpiracion" 
            :key="aviso.idTarea"
            class="bg-red-100 text-red-700 px-4 py-2 rounded-lg mb-2 text-sm font-semibold flex items-center"
          >
             La tarea <span class="mx-1 font-bold">"{{ aviso.titulo }}"</span> vencerá en 24 horas o menos.
          </div>
        </div>

        <!-- Filtros de tareas -->
        <div class="mb-6 flex space-x-4">
          <GeoTaskButton 
            :color="filtroActual === 'todas' ? 'green' : 'gray'"
            custom-class="px-4 py-2 rounded-full text-sm"
            @click="filtroActual = 'todas'"
          >
            Todas
          </GeoTaskButton>
          
          <GeoTaskButton 
            :color="filtroActual === 'pendientes' ? 'yellow' : 'gray'"
            custom-class="px-4 py-2 rounded-full text-sm"
            @click="filtroActual = 'pendientes'"
          >
            Pendientes
          </GeoTaskButton>
          
          <GeoTaskButton 
            :color="filtroActual === 'completadas' ? 'blue' : 'gray'"
            custom-class="px-4 py-2 rounded-full text-sm"
            @click="filtroActual = 'completadas'"
          >
            Completadas
          </GeoTaskButton>
        </div>

        <!-- Buscador por palabra clave -->
        <div class="mb-6">
          <input
            v-model="busqueda"
            type="text"
            placeholder="Buscar por título o descripción..."
            class="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-400"
          />
        </div>
        
        <!-- Lista de tareas -->
        <ul>
          <li 
            v-for="tarea in tareasFiltradas" 
            :key="tarea.idTarea" 
            class="bg-yellow-50 hover:bg-green-50 transition p-5 mb-4 rounded-xl flex justify-between items-start"
          >
            <div class="flex-grow">
              <div class="font-semibold text-lg text-gray-800">{{ tarea.titulo }}</div>
              <div class="text-gray-600 text-sm mb-1">{{ tarea.descripcion }}</div>
              <div class="text-xs text-gray-400 mb-1">Vence: {{ formatearFecha(tarea.fechaVencimiento || tarea.fecha_vencimiento) }}</div>
              
              <!-- Ubicación mejorada mostrando sector si existe -->
              <div class="text-xs text-gray-500 mb-1">
                <span v-if="tarea.sectorInfo" class="text-blue-600 font-semibold">
                  📍 Sector: {{ tarea.sectorInfo.calle }}, {{ tarea.sectorInfo.comuna }}
                </span>
                <span v-else>
                  📍 {{ tarea.calle }}, {{ tarea.comuna }}
                </span>
              </div>
              
              <span 
                :class="tarea.estado === 'Completada' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'" 
                class="px-2 py-1 rounded text-xs font-semibold"
              >
                {{ tarea.estado }}
              </span>
            </div>
            
            <div class="flex flex-col space-y-2 ml-4">
              <!-- Botones de acción -->
              <GeoTaskButton 
                color="blue"
                custom-class="px-3 py-1 text-sm"
                @click="editarTarea(tarea.idTarea)"
              >
                Editar
              </GeoTaskButton>
              
              <GeoTaskButton 
                v-if="tarea.estado !== 'Completada'"
                color="green"
                custom-class="px-3 py-1 text-sm"
                @click="marcarComoCompletada(tarea.idTarea)"
              >
                Completada
              </GeoTaskButton>
              
              <GeoTaskButton 
                color="red"
                custom-class="px-3 py-1 text-sm"
                @click="eliminarTarea(tarea.idTarea)"
              >
                Eliminar
              </GeoTaskButton>
            </div>
          </li>
        </ul>
        
        <div v-if="tareasFiltradas.length === 0" class="text-center text-gray-500 mt-8">
          No hay tareas {{ filtroActual !== 'todas' ? filtroActual : '' }} para mostrar.
        </div>
        
        <div v-if="error" class="text-red-500 mt-4 text-center">{{ error }}</div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import GeoTaskLogo from '~/components/GeoTaskLogo.vue'
import GeoTaskButton from '~/components/GeoTaskButton.vue'
import GeoTaskBackground from '~/components/GeoTaskBackground.vue'
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const tareas = ref([])
const error = ref('')
const filtroActual = ref('todas')
const busqueda = ref('')
const router = useRouter()

// Función auxiliar para obtener headers de autenticación
const getAuthHeaders = () => {
  const token = localStorage.getItem('accessToken')
  if (!token) {
    router.push('/login')
    return null
  }
  return { Authorization: `Bearer ${token}` }
}

// Función para cargar datos del sector
const cargarSector = async (idSector) => {
  try {
    const headers = getAuthHeaders()
    if (!headers) return null
    
    const { data } = await axios.get(`http://localhost:8080/api/sectores/${idSector}`, { headers })
    return data
  } catch (e) {
    console.error('Error al cargar sector:', e)
    return null
  }
}

const tareasFiltradas = computed(() => {
  let lista = tareas.value
  if (filtroActual.value === 'pendientes') lista = lista.filter(t => (t.estado || '').toLowerCase() !== 'completada' && (t.estado || '').toLowerCase() !== 'completado')
  if (filtroActual.value === 'completadas') lista = lista.filter(t => (t.estado || '').toLowerCase() === 'completada' || (t.estado || '').toLowerCase() === 'completado')
  if (busqueda.value.trim() !== '') {
    const texto = busqueda.value.trim().toLowerCase()
    lista = lista.filter(t =>
      (t.titulo && t.titulo.toLowerCase().includes(texto)) ||
      (t.descripcion && t.descripcion.toLowerCase().includes(texto))
    )
  }
  return lista
})

const avisosExpiracion = computed(() => {
  const avisos = []
  const ahora = new Date()
  tareas.value.forEach(tarea => {
    if (tarea.estado !== 'Completada') {
      const fechaVenc = new Date(tarea.fechaVencimiento || tarea.fecha_vencimiento)
      if (!isNaN(fechaVenc.getTime())) {
        const diffMs = fechaVenc - ahora
        const diffHoras = diffMs / (1000 * 60 * 60)
        if (diffHoras <= 48 && diffHoras > 0) {
          avisos.push({
            idTarea: tarea.idTarea,
            titulo: tarea.titulo,
            horasRestantes: diffHoras
          })
        }
      }
    }
  })
  return avisos
})

onMounted(async () => {
  await cargarTareas()
  if (tareas.value.length > 0) {
    console.log("Estructura completa de la tarea:", tareas.value[0])
    console.log("Ejemplo de fechaVencimiento:", tareas.value[0].fechaVencimiento, typeof tareas.value[0].fechaVencimiento)
  }
})

const cargarTareas = async () => {
  try {
    const headers = getAuthHeaders()
    if (!headers) return
    
    const { data } = await axios.get('http://localhost:8080/api/tareas', { headers })
    
    // Cargar datos del sector para cada tarea que tenga idSector
    const tareasConSector = await Promise.all(
      data.map(async (tarea) => {
        if (tarea.idSector) {
          const sectorInfo = await cargarSector(tarea.idSector)
          return { ...tarea, sectorInfo }
        }
        return tarea
      })
    )
    
    tareas.value = tareasConSector
  } catch (e) {
    error.value = 'Error al cargar las tareas'
    console.error('Error al cargar tareas:', e)
    if (e.response?.status === 401) {
      localStorage.removeItem('accessToken')
      router.push('/login')
    }
  }
}

const editarTarea = (id) => {
  router.push({
    path: '/editarTarea',
    query: { id: id }
  })
}

const marcarComoCompletada = async (id) => {
  try {
    const headers = getAuthHeaders()
    if (!headers) return
    
    await axios.patch(`http://localhost:8080/api/tareas/${id}/completar`, {}, { headers })    
    await cargarTareas()
  } catch (e) {
    error.value = 'Error al completar la tarea'
    console.error('Error al completar tarea:', e)
  }
}

const eliminarTarea = async (id) => {
  if (!confirm('¿Estás seguro que deseas eliminar esta tarea?')) return
  
  try {
    const headers = getAuthHeaders()
    if (!headers) return
    
    await axios.delete(`http://localhost:8080/api/tareas/${id}`, { headers })
    await cargarTareas()
  } catch (e) {
    error.value = 'Error al eliminar la tarea'
    console.error('Error al eliminar tarea:', e)
  }
}

const formatearFecha = (fecha) => {
  if (!fecha) return 'Sin fecha definida'
  
  try {
    const date = new Date(fecha)
    if (isNaN(date.getTime())) {
      console.log('Fecha inválida:', fecha)
      return 'Fecha inválida'
    }
    
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    })
  } catch (e) {
    console.error('Error al formatear fecha:', e, fecha)
    return 'Error de formato'
  }
}
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>