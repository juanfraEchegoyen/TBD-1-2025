<template>
  <GeoTaskBackground>
    <div class="min-h-screen flex flex-col items-center py-10">
      <div class="w-full max-w-2xl bg-white/80 rounded-[2.5rem] p-10 flex flex-col">
        <div class="flex items-center mb-8">
          <GeoTaskLogo />
          <h1 class="text-3xl font-bold text-green-700 tracking-tight ml-3">Editar Tarea</h1>
        </div>
        
        <form @submit.prevent="actualizarTarea">
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Título</label>
            <input v-model="titulo" type="text" class="input-style" required />
          </div>
          
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Descripción</label>
            <textarea v-model="descripcion" class="input-style h-24" required></textarea>
          </div>
          
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Fecha de vencimiento</label>
            <input v-model="fechaVencimiento" type="date" class="input-style" required />
          </div>

          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Estado</label>
            <select v-model="estado" class="input-style">
              <option value="Pendiente">Pendiente</option>
              <option value="Completada">Completada</option>
            </select>
          </div>
          
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Ubicación de la tarea</label>
            <p class="text-sm text-gray-600 mb-2">Busca una dirección o haz clic en el mapa para seleccionar la nueva ubicación</p>
            
            <LocationMapPicker 
              @update:location="updateLocation" 
              :initialLatitude="latitud" 
              :initialLongitude="longitud"
              :key="`${tareaId}-${mapKey}`"
            />
            
            <div class="mt-2">
              <label class="block mb-1 text-sm font-semibold text-gray-700">Dirección seleccionada</label>
              <input v-model="address" type="text" class="input-style" readonly />
            </div>
          </div>
          
          <div class="flex space-x-4">
            <GeoTaskButton color="blue" custom-class="flex-1" type="submit">Guardar Cambios</GeoTaskButton>
            <GeoTaskButton color="red" custom-class="flex-1" type="button" @click="eliminarTarea">Eliminar Tarea</GeoTaskButton>
            <GeoTaskButton color="yellow" custom-class="flex-1" type="button" @click="$router.push('/tareas')">Cancelar</GeoTaskButton>
          </div>
        </form>
        
        <div v-if="error" class="text-red-500 mt-4 text-center">{{ error }}</div>
        <div v-if="success" class="text-green-600 mt-4 text-center">{{ success }}</div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import GeoTaskLogo from '~/components/GeoTaskLogo.vue';
import GeoTaskButton from '~/components/GeoTaskButton.vue';
import GeoTaskBackground from '~/components/GeoTaskBackground.vue';
import LocationMapPicker from '~/components/GeoLocationMap.vue';

// ----- CONSTANTES Y CONFIGURACIÓN -----

// ENTRADA: Ninguna
// PROCEDIMIENTO: Define coordenadas predeterminadas para el centro de Santiago de Chile
// SALIDA: Constantes con coordenadas por defecto
const defaultLat = -33.4489;
const defaultLng = -70.6693;

// ----- VARIABLES DE ESTADO -----
const titulo = ref('');
const descripcion = ref('');
const fechaVencimiento = ref('');
const estado = ref('Pendiente');
const latitud = ref(defaultLat);
const longitud = ref(defaultLng);
const address = ref('');
const comuna = ref('');
const calle = ref('');
const error = ref('');
const success = ref('');
const tareaId = ref(null);
const originalIdSector = ref(null); // Para mantener el sector original
const locationChanged = ref(false); // Para rastrear si se cambió la ubicación
const sectorData = ref(null); // Nueva variable para almacenar datos del sector
const router = useRouter();
const route = useRoute();
const mapKey = ref(0); // Nueva variable para forzar re-renderización

// ----- INICIALIZACIÓN -----

// ENTRADA: Ninguna
// PROCEDIMIENTO: Carga los datos de la tarea cuando se monta el componente
// SALIDA: Formulario inicializado con los datos de la tarea
onMounted(async () => {
  const id = route.query.id;
  if (id) {
    tareaId.value = id;
    await cargarTarea(id);
  } else {
    router.push('/tareas');
  }
});

// ----- FUNCIONES DE CARGA DE DATOS -----

// ENTRADA: ID del sector
// PROCEDIMIENTO: Obtiene los datos del sector del servidor
// SALIDA: Datos del sector cargados en el estado
const cargarSector = async (idSector) => {
  try {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      router.push('/login');
      return;
    }
    
    const response = await axios.get(`http://localhost:8080/api/sectores/${idSector}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    
    sectorData.value = response.data;
    
    // Actualizar coordenadas con los datos del sector
    latitud.value = response.data.latitud;
    longitud.value = response.data.longitud;
    comuna.value = response.data.comuna;
    calle.value = response.data.calle;
    address.value = `${response.data.calle}, ${response.data.comuna}`;
    
    // Forzar re-renderización del mapa
    mapKey.value++;
    
    console.log('Sector cargado:', sectorData.value);
    console.log('Coordenadas del sector:', latitud.value, longitud.value);
    
  } catch (e) {
    console.error('Error al cargar sector:', e);
  }
};

// ENTRADA: ID de la tarea a cargar
// PROCEDIMIENTO: Obtiene los datos de la tarea del servidor y carga el sector asociado
// SALIDA: Formulario inicializado con los datos de la tarea y ubicación del sector
const cargarTarea = async (id) => {
  try {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      router.push('/login');
      return;
    }
    const response = await axios.get(`http://localhost:8080/api/tareas/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const tarea = response.data;
    
    // Debug: ver qué estado viene del servidor
    console.log('Estado recibido del servidor:', tarea.estado);
    console.log('Tipo de estado:', typeof tarea.estado);
    
    titulo.value = tarea.titulo;
    descripcion.value = tarea.descripcion;
    
    // Asegurar que el estado se establezca correctamente desde el servidor
    // Normalizar el estado para que coincida con las opciones del select
    if (tarea.estado === 'Completada' || tarea.estado === 'completada') {
      estado.value = 'Completada';
    } else {
      estado.value = 'Pendiente';
    }
    
    console.log('Estado asignado:', estado.value);
    
    // Guardar el idSector original
    originalIdSector.value = tarea.idSector; 
    
    // Formatear fecha para el input date
    if (tarea.fechaVencimiento) {
      const fecha = new Date(tarea.fechaVencimiento);
      fechaVencimiento.value = fecha.toISOString().split('T')[0];
    }
    
    // Cargar datos del sector si existe idSector
    if (tarea.idSector) {
      await cargarSector(tarea.idSector);
    } else {
      // Si hay ubicación en formato WKT, extraer coordenadas (fallback)
      if (tarea.ubicacion) {
        const match = tarea.ubicacion.match(/POINT\(([\d.-]+) ([\d.-]+)\)/);
        if (match) {
          longitud.value = parseFloat(match[1]);
          latitud.value = parseFloat(match[2]);
        }
      }
      comuna.value = tarea.comuna || '';
      calle.value = tarea.calle || '';
      address.value = tarea.calle ? `${tarea.calle}, ${tarea.comuna}` : '';
    }
    
  } catch (e) {
    error.value = 'No se pudo cargar la tarea';
    console.error('Error al cargar tarea:', e);
  }
};

// ----- FUNCIONES DE ACTUALIZACIÓN -----

// ENTRADA: Objeto location con latitude, longitude, address y addressData
// PROCEDIMIENTO: Actualiza las variables de estado con la ubicación seleccionada
// SALIDA: Variables de estado actualizadas con la nueva ubicación
const updateLocation = (location) => {
  latitud.value = location.latitude;
  longitud.value = location.longitude;
  address.value = location.address || 'Dirección no disponible';
  locationChanged.value = true; // Marcar que la ubicación fue cambiada
  
  // Extraer comuna y calle de los datos de dirección si están disponibles
  if (location.addressData) {
    comuna.value = location.addressData.county || 
                  location.addressData.city_district || 
                  location.addressData.suburb || 
                  location.addressData.municipality || 
                  ''; 
    calle.value = location.addressData.road || 
                 location.addressData.street || 
                 '';
  }
};

// ----- FUNCIONES DE COMUNICACIÓN CON API -----

// ENTRADA: Datos del formulario (título, descripción, fecha, ubicación)
// PROCEDIMIENTO: Envía los datos al servidor para actualizar una tarea
// SALIDA: Mensaje de éxito o error, y redirección a la lista de tareas
const actualizarTarea = async () => {
  error.value = '';
  success.value = '';
  // Validaciones básicas
  if (!titulo.value.trim() || !descripcion.value.trim() || !fechaVencimiento.value) {
    error.value = 'Por favor completa todos los campos requeridos';
    return;
  }
  // Validar que hay coordenadas seleccionadas
  if (!latitud.value || !longitud.value) {
    error.value = 'Por favor selecciona la ubicación en el mapa';
    return;
  }
  // Validar que tenemos datos mínimos de dirección
  if (!comuna.value || !calle.value) {
    error.value = 'No se pudo determinar la comuna o calle de la ubicación seleccionada';
    return;
  }
  try {
    // Convertir coordenadas a formato WKT para PostGIS
    const wkt = `POINT(${longitud.value} ${latitud.value})`;
    const token = localStorage.getItem('accessToken');
    const userId = localStorage.getItem('userId');
    if (!token || !userId) {
      router.push('/login');
      return;
    }
    // Prevenir envíos múltiples
    const submitButton = document.querySelector('button[type="submit"]');
    if (submitButton) {
      submitButton.disabled = true;
      submitButton.textContent = 'Guardando...';
    }
    // Asegurar formato de fecha correcto
    const fechaFormateada = new Date(fechaVencimiento.value + 'T00:00:00').toISOString();
    const tareaData = {
      titulo: titulo.value,
      descripcion: descripcion.value,
      fechaVencimiento: fechaFormateada,
      estado: estado.value,
      asignacion: 'tarea',
      comuna: comuna.value,
      calle: calle.value,
      ubicacion: wkt,
      idUsuario: userId
    };
    
    // Si la ubicación no cambió, mantener el idSector original
    if (!locationChanged.value && originalIdSector.value) {
      tareaData.idSector = originalIdSector.value;
    }
    
    // Actualizar tarea existente
    await axios.put(`http://localhost:8080/api/tareas/${tareaId.value}`, tareaData, {
      headers: { Authorization: `Bearer ${token}` }
    });
    success.value = 'Tarea actualizada correctamente. Redirigiendo...';
    // Redireccionar después de un breve momento
    setTimeout(() => router.push('/tareas'), 1500);
  } catch (e) {
    console.error('Error al actualizar tarea:', e); 
    error.value = e.response?.data?.error || 'Error al actualizar la tarea';
    // Reactivar el botón en caso de error
    const submitButton = document.querySelector('button[type="submit"]');
    if (submitButton) {
      submitButton.disabled = false;
      submitButton.textContent = 'Guardar Cambios';
    }
  }
};

// ENTRADA: Ninguna
// PROCEDIMIENTO: Elimina la tarea actual
// SALIDA: Tarea eliminada y redirección a la lista
const eliminarTarea = async () => {
  if (!confirm('¿Estás seguro que deseas eliminar esta tarea?')) {
    return;
  }
  try {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      router.push('/login');
      return;
    }
    await axios.delete(`http://localhost:8080/api/tareas/${tareaId.value}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    success.value = 'Tarea eliminada correctamente. Redirigiendo...';
    setTimeout(() => router.push('/tareas'), 1500);
  } catch (e) {
    error.value = 'Error al eliminar la tarea';
    console.error('Error al eliminar tarea:', e);
  }
};
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}

</style>