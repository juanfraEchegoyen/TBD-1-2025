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
        
        <!-- Filtros de tareas -->
        <div class="mb-6 flex space-x-4">
          <button 
            @click="filtroActual = 'todas'" 
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium',
              filtroActual === 'todas' 
                ? 'bg-green-600 text-white' 
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            Todas
          </button>
          <button 
            @click="filtroActual = 'pendientes'" 
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium',
              filtroActual === 'pendientes' 
                ? 'bg-yellow-500 text-white' 
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            Pendientes
          </button>
          <button 
            @click="filtroActual = 'completadas'" 
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium',
              filtroActual === 'completadas' 
                ? 'bg-blue-600 text-white' 
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            Completadas
          </button>
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
              <div class="text-xs text-gray-500 mb-1">{{ tarea.calle }}, {{ tarea.comuna }}</div>
              <span 
                :class="tarea.estado === 'Completada' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'" 
                class="px-2 py-1 rounded text-xs font-semibold"
              >
                {{ tarea.estado }}
              </span>
            </div>
            
            <div class="flex flex-col space-y-2 ml-4">
              <!-- Botones de acción -->
              <button 
                @click="editarTarea(tarea.idTarea)" 
                class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm"
              >
                Editar
              </button>
              
              <button 
                v-if="tarea.estado !== 'Completada'"
                @click="marcarComoCompletada(tarea.idTarea)" 
                class="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-sm"
              >
                Completar
              </button>
              
              <button 
                @click="eliminarTarea(tarea.idTarea)" 
                class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm"
              >
                Eliminar
              </button>
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

// ----- VARIABLES DE ESTADO -----
const tareas = ref([])
const error = ref('')
const filtroActual = ref('todas')
const router = useRouter()

// ----- PROPIEDADES COMPUTADAS ----

// ENTRADA: Lista de tareas y filtro actual
// PROCEDIMIENTO: Filtra las tareas según el criterio seleccionado
// SALIDA: Lista de tareas filtrada
const tareasFiltradas = computed(() => {
  if (filtroActual.value === 'todas') return tareas.value
  if (filtroActual.value === 'pendientes') return tareas.value.filter(t => t.estado !== 'Completada')
  if (filtroActual.value === 'completadas') return tareas.value.filter(t => t.estado === 'Completada')
  return tareas.value
})

// ----- INICIALIZACIÓN ----

// ENTRADA: Ninguna
// PROCEDIMIENTO: Inicializa la página al montarse
// SALIDA: Carga las tareas desde la API
onMounted(async () => {
  await cargarTareas()
  // Debug: ver qué formato tienen las fechas y toda la estructura
  if (tareas.value.length > 0) {
    console.log("Estructura completa de la tarea:", tareas.value[0])
    console.log("Ejemplo de fechaVencimiento:", tareas.value[0].fechaVencimiento, typeof tareas.value[0].fechaVencimiento)
  }
})

// ----- FUNCIONES DE COMUNICACIÓN CON API -----

// ENTRADA: Ninguna
// PROCEDIMIENTO: Carga las tareas del usuario desde la API
// SALIDA: Actualiza la lista de tareas
const cargarTareas = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      router.push('/login')
      return
    }
    
    const response = await axios.get('http://localhost:8080/api/tareas', { 
      headers: { Authorization: `Bearer ${token}` } 
    })
    tareas.value = response.data
  } catch (e) {
    error.value = 'Error al cargar las tareas'
    console.error('Error al cargar tareas:', e)
    if (e.response?.status === 401) {
      localStorage.removeItem('accessToken')
      router.push('/login')
    }
  }
}

// ENTRADA: ID de la tarea a editar
// PROCEDIMIENTO: Redirecciona al formulario de edición de la tarea
// SALIDA: Navegación a la página de edición
const editarTarea = (id) => {
  router.push({
    path: '/editarTarea',
    query: { id: id }
  });
}

// ENTRADA: ID de la tarea a marcar como completada
// PROCEDIMIENTO: Actualiza el estado de la tarea a "Completada"
// SALIDA: Tarea actualizada y lista refrescada
const marcarComoCompletada = async (id) => {
  try {
    const token = localStorage.getItem('accessToken')
    
    // Encontrar la tarea actual
    const tarea = tareas.value.find(t => t.idTarea === id)
    if (!tarea) return
    
    // Clonar la tarea y cambiar su estado
    const tareaActualizada = { ...tarea, estado: 'Completada' }
    
    // Enviar actualización a la API
    await axios.put(`http://localhost:8080/api/tareas/${id}`, tareaActualizada, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    // Actualizar localmente
    await cargarTareas()
    
  } catch (e) {
    error.value = 'Error al actualizar la tarea'
    console.error('Error al actualizar tarea:', e)
  }
}

// ENTRADA: ID de la tarea a eliminar
// PROCEDIMIENTO: Elimina la tarea del servidor
// SALIDA: Tarea eliminada y lista refrescada
const eliminarTarea = async (id) => {
  if (!confirm('¿Estás seguro que deseas eliminar esta tarea?')) {
    return
  }
  
  try {
    const token = localStorage.getItem('accessToken')
    
    await axios.delete(`http://localhost:8080/api/tareas/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    // Actualizar lista de tareas
    await cargarTareas()
    
  } catch (e) {
    error.value = 'Error al eliminar la tarea'
    console.error('Error al eliminar tarea:', e)
  }
}

// ----- FUNCIONES DE UTILIDAD -----

// ENTRADA: Fecha en cualquier formato válido
// PROCEDIMIENTO: Formatea la fecha para mostrarla de manera más amigable
// SALIDA: Fecha formateada como DD/MM/YYYY
const formatearFecha = (fecha) => {
  if (!fecha) return 'Sin fecha definida'
  
  try {
    // Intentar crear objeto Date desde cualquier formato que envíe el backend
    const date = new Date(fecha)
    
    // Verificar si es una fecha válida
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