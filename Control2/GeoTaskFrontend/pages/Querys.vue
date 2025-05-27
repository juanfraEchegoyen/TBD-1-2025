<template>
  <GeoTaskBackground>
    <div class="min-h-screen flex flex-col items-center py-10">
      <div class="w-full max-w-2xl bg-white/80 rounded-[2.5rem] p-10 flex flex-col">
        <div class="flex items-center mb-8">
          <GeoTaskLogo />
          <h1 class="text-3xl font-bold text-green-700 tracking-tight ml-3">Consultas de Tareas</h1>
          <GeoTaskButton color="gray" class="ml-auto" @click="router.push('/tareas')">Volver</GeoTaskButton>
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
                  <span v-html="renderResultado(item.consulta, item.resultado)"></span>
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
import { useRouter } from 'vue-router';
import axios from 'axios';
import GeoTaskLogo from '~/components/GeoTaskLogo.vue';
import GeoTaskButton from '~/components/GeoTaskButton.vue';
import GeoTaskBackground from '~/components/GeoTaskBackground.vue';

const router = useRouter();

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
    const mapUnicos = new Map();
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

function renderResultado(consulta, resultado) {
  if (!resultado || resultado === "No hay datos disponibles") {
    return `<span class="text-gray-500 italic">No hay datos disponibles</span>`;
  }

  let data = resultado;
  if (typeof resultado === "string") {
    try {
      data = JSON.parse(resultado);
    } catch {
      // Si no es JSON, lo dejamos como está
    }
  }

  // Tareas por sector (array)
  if (consulta === 'Tareas por sector' && Array.isArray(data)) {
    return `
      <table class="min-w-full text-xs border">
        <thead><tr><th>Usuario</th><th>ID Sector</th><th>Cantidad</th></tr></thead>
        <tbody>
          ${data.map(item => `<tr>
            <td>${item.nombre}</td>
            <td>${item.idSector}</td>
            <td>${item.cantidadTareas}</td>
          </tr>`).join('')}
        </tbody>
      </table>
    `;
  }

  // Tarea pendiente más cercana (objeto)
  if (consulta.includes('más cercana') && data && typeof data === 'object' && data.idTarea !== undefined) {
    return `
      <table class="min-w-full text-xs border">
        <thead><tr><th>Usuario</th><th>ID Tarea</th><th>Título</th><th>Distancia (m)</th></tr></thead>
        <tbody>
          <tr>
            <td>${data.nombre}</td>
            <td>${data.idTarea}</td>
            <td>${data.titulo}</td>
            <td>${data.distancia?.toFixed(2) ?? ''}</td>
          </tr>
        </tbody>
      </table>
    `;
  }

  // Sector con más completadas (objeto)
  if (consulta.includes('Sector con más completadas') && data && typeof data === 'object' && data.idSector !== undefined) {
    return `
      <table class="min-w-full text-xs border">
        <thead><tr><th>Usuario</th><th>ID Sector</th><th>Cantidad tareas</th></tr></thead>
        <tbody>
          <tr>
            <td>${data.nombre}</td>
            <td>${data.idSector}</td>
            <td>${data.cantidadTareas}</td>
          </tr>
        </tbody>
      </table>
    `;
  }

  // Distancia promedio de completadas (objeto)
  if (consulta.includes('Distancia promedio') && data && typeof data === 'object' && data.promedioDistancia !== undefined) {
    return `
      <table class="min-w-full text-xs border">
        <thead><tr><th>Usuario</th><th>Promedio distancia (m)</th></tr></thead>
        <tbody>
          <tr>
            <td>${data.nombre}</td>
            <td>${data.promedioDistancia?.toFixed(2) ?? ''}</td>
          </tr>
        </tbody>
      </table>
    `;
  }

  // Sectores con más tareas pendientes (array)
  if (consulta.includes('Sectores con más tareas pendientes') && Array.isArray(data)) {
    if (data.length === 0) {
      return `<span class="text-gray-500 italic">No hay datos disponibles</span>`;
    }
    return `
      <table class="min-w-full text-xs border">
        <thead><tr><th>Usuario</th><th>ID Sector</th><th>Asignación</th><th>Comuna</th><th>Calle</th><th>Longitud</th><th>Latitud</th><th>Cantidad</th></tr></thead>
        <tbody>
          ${data.map(item => `<tr>
            <td>${item.nombre}</td>
            <td>${item.idSector}</td>
            <td>${item.asignacion}</td>
            <td>${item.comuna}</td>
            <td>${item.calle}</td>
            <td>${item.longitud}</td>
            <td>${item.latitud}</td>
            <td>${item.cantidadTareasPendientes}</td>
          </tr>`).join('')}
        </tbody>
      </table>
    `;
  }

  // Tareas usuario por sector (array)
  if (consulta.includes('Tareas usuario por sector') && Array.isArray(data)) {
    return `
      <table class="min-w-full text-xs border">
        <thead><tr><th>Usuario</th><th>ID Usuario</th><th>ID Sector</th><th>Cantidad</th></tr></thead>
        <tbody>
          ${data.map(item => `<tr>
            <td>${item.nombre}</td>
            <td>${item.idUsuario}</td>
            <td>${item.idSector}</td>
            <td>${item.cantidadTareas}</td>
          </tr>`).join('')}
        </tbody>
      </table>
    `;
  }

  // Por defecto, muestra como JSON
  return `<pre>${JSON.stringify(data, null, 2)}</pre>`;
}
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>
