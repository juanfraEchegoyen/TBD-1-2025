<template>
  <GeoTaskBackground>
    <div class="min-h-screen flex flex-col items-center py-10">
      <div class="w-full max-w-2xl bg-white/80 rounded-[2.5rem] p-10 flex flex-col">
        <div class="flex items-center mb-8">
          <GeoTaskLogo />
          <h1 class="text-3xl font-bold text-green-700 tracking-tight ml-3">Consultas de Tareas</h1>
        </div>

        <!-- Seleccionar usuario -->
        <div class="mb-4">
          <label class="block font-semibold text-gray-700">Selecciona un usuario</label>
          <select v-model="idUsuarioSeleccionado" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400">
            <option v-for="usuario in listaUsuarios" :key="usuario.idUsuario" :value="usuario.idUsuario">
              {{ usuario.nombre }}
            </option>
          </select>
        </div>

        <!-- Botón para cargar las consultas -->
        <GeoTaskButton color="green" class="mb-4" @click="ejecutarConsultas">
          Cargar Consultas
        </GeoTaskButton>

        <!-- Tabla con resultados -->
        <div v-if="resultados.length > 0">
          <table class="w-full border border-gray-300 rounded-lg">
            <thead>
              <tr class="bg-green-200 text-gray-700 font-semibold">
                <th class="px-3 py-2">Consulta</th>
                <th class="px-3 py-2">Resultado</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in resultados" :key="index" class="border-t border-gray-200">
                <td class="px-3 py-2 font-bold text-green-700">{{ item.consulta }}</td>
                <td class="px-3 py-2">
                  <template v-if="item.resultado && item.resultado.length > 2">
                    <pre class="text-gray-800 bg-gray-100 p-2 rounded-lg">{{ item.resultado }}</pre>
                  </template>
                  <template v-else>
                    <span class="text-gray-500 italic">No hay datos disponibles</span>
                  </template>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="error" class="text-red-500 mt-4 text-center">{{ error }}</div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import GeoTaskLogo from '~/components/GeoTaskLogo.vue';
import GeoTaskButton from '~/components/GeoTaskButton.vue';
import GeoTaskBackground from '~/components/GeoTaskBackground.vue';

// Variables de estado
const API_BASE_URL = "http://localhost:8080/api/querys";
const resultados = ref([]);
const error = ref('');
const idUsuarioSeleccionado = ref(null);
const listaUsuarios = ref([]);

// Función para cargar usuarios disponibles
const cargarUsuarios = async () => {
  try {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      error.value = "No hay token de autenticación. Por favor, inicia sesión.";
      return;
    }

    const response = await axios.get("http://localhost:8080/api/usuarios", {
      headers: {
        "Authorization": `Bearer ${token}`,
        "Accept": "application/json"
      }
    });

    listaUsuarios.value = response.data;
  } catch (err) {
    error.value = "Error al obtener usuarios.";
    console.error("Error en API:", err);
  }
};

// Función para ejecutar consultas con autenticación y usuario seleccionado
const ejecutarConsultas = async () => {
  try {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      error.value = "No hay token de autenticación. Por favor, inicia sesión.";
      return;
    }

    if (!idUsuarioSeleccionado.value) {
      error.value = "Por favor, selecciona un usuario.";
      return;
    }

    resultados.value = [];
    const consultasAgregadas = new Set();
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    axios.defaults.headers.common["Accept"] = "application/json";

    const consultas = [
      { consulta: 'Tareas por sector', endpoint: `${API_BASE_URL}/tareas-por-sector/${idUsuarioSeleccionado.value}` },
      { consulta: 'Tarea pendiente más cercana', endpoint: `${API_BASE_URL}/tarea-pendiente-mas-cercana/${idUsuarioSeleccionado.value}` },
      { consulta: 'Sector con más completadas en 2km', endpoint: `${API_BASE_URL}/sector-mas-completadas-2km/${idUsuarioSeleccionado.value}` },
      { consulta: 'Distancia promedio de completadas', endpoint: `${API_BASE_URL}/distancia-promedio-completadas/${idUsuarioSeleccionado.value}` },
      { consulta: 'Sectores con más tareas pendientes', endpoint: `${API_BASE_URL}/sectores-mas-pendientes` },
      { consulta: 'Tareas usuario por sector', endpoint: `${API_BASE_URL}/tareas-usuario-por-sector` },
      { consulta: 'Sector con más completadas en 5km', endpoint: `${API_BASE_URL}/sector-mas-completadas-5km/${idUsuarioSeleccionado.value}` },
    ];

    const resultadosTemp = [];
    for (const consulta of consultas) {
      const response = await axios.get(consulta.endpoint);
      resultadosTemp.push({
        consulta: consulta.consulta,
        resultado: Array.isArray(response.data) && response.data.length > 0 ? JSON.stringify(response.data, null, 2) : (typeof response.data === 'object' && Object.keys(response.data).length > 0 ? JSON.stringify(response.data, null, 2) : "No hay datos disponibles")
      });
    }
   
    for (const item of resultadosTemp) {
      mapUnicos.set(item.consulta, item);
    }
    resultados.value = Array.from(mapUnicos.values());
  } catch (err) {
    error.value = "Error al ejecutar consultas: " + err.message;
    console.error("Error en API:", err);
  }
};

// Cargar usuarios al montar el componente
onMounted(() => {
  cargarUsuarios();
});
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>
