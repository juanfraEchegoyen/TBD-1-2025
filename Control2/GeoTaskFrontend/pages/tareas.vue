<template>
  <GeoTaskBackground>
    <div class="min-h-screen flex flex-col items-center py-10">
      <div class="w-full max-w-2xl bg-white/80 rounded-[2.5rem] p-10 flex flex-col">
        <div class="flex items-center mb-8">
          <GeoTaskLogo />
          <h1 class="text-3xl font-bold text-green-700 tracking-tight ml-3">Mis Tareas</h1>
        </div>
        <ul>
          <li v-for="tarea in tareas" :key="tarea.idTarea" class="bg-yellow-50 hover:bg-green-50 transition p-5 mb-4 rounded-xl flex justify-between items-center">
            <div>
              <div class="font-semibold text-lg text-gray-800">{{ tarea.titulo }}</div>
              <div class="text-gray-600 text-sm mb-1">{{ tarea.descripcion }}</div>
              <div class="text-xs text-gray-400 mb-1">Vence: {{ tarea.fechaVencimiento }}</div>
              <span :class="tarea.estado === 'Completada' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'" class="px-2 py-1 rounded text-xs font-semibold">
                {{ tarea.estado }}
              </span>
            </div>
          </li>
        </ul>
        <div v-if="tareas.length === 0" class="text-center text-gray-500 mt-8">No hay tareas para mostrar.</div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import GeoTaskLogo from '~/components/GeoTaskLogo.vue'
import GeoTaskBackground from '~/components/GeoTaskBackground.vue'
import { ref, onMounted } from 'vue'
import axios from 'axios'

const tareas = ref([])

const cargarTareas = async () => {
  const token = localStorage.getItem('token')
  const url = 'http://localhost:8080/api/tareas'
  const res = await axios.get(url, { headers: { Authorization: `Bearer ${token}` } })
  tareas.value = res.data
}

onMounted(cargarTareas)
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>