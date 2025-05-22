<template>
  <GeoTaskBackground>
    <div class="flex flex-col items-center justify-center min-h-screen">
      <div class="bg-white/80 p-10 rounded-[2.5rem] w-full max-w-2xl flex flex-col items-center">
        <GeoTaskLogo />
        <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Crear cuenta</h2>
        <form @submit.prevent="register">
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Usuario</label>
            <input v-model="nombre" type="text" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" required />
          </div>
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Contraseña</label>
            <input v-model="password" type="password" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" required />
          </div>
          
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Selecciona tu ubicación en Santiago</label>
            <p class="text-sm text-gray-600 mb-2">Busca una dirección o haz clic en el mapa para seleccionar tu ubicación dentro de la Región Metropolitana</p>
            
            <LocationMapPicker 
              @update:location="updateLocation" 
              :initialLatitude="defaultLat" 
              :initialLongitude="defaultLng" 
            />
            
            <div class="mt-2">
              <label class="block mb-1 text-sm font-semibold text-gray-700">Dirección seleccionada</label>
              <input 
                v-model="address" 
                type="text" 
                class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" 
                readonly 
              />
            </div>
          </div>
          
          <GeoTaskButton color="yellow" class="w-full" type="submit">Registrarse</GeoTaskButton>
        </form>
        <div v-if="error" class="text-red-500 mt-4 text-center">{{ error }}</div>
        <div v-if="success" class="text-green-600 mt-4 text-center">{{ success }}</div>
        <div class="mt-4 text-center">
          <GeoTaskButton color="green" @click="$router.push('/login')">¿Ya tienes cuenta? Inicia sesión</GeoTaskButton>
        </div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
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
const nombre = ref('');
const password = ref('');
const latitud = ref(defaultLat);
const longitud = ref(defaultLng);
const address = ref('');
const comuna = ref('');
const calle = ref('');
const addressData = ref(null);
const error = ref('');
const success = ref('');
const router = useRouter();

// ----- FUNCIONES DE ACTUALIZACIÓN ----

// ENTRADA: Objeto location con latitude, longitude, address y addressData
// PROCEDIMIENTO: Actualiza las variables de estado con la ubicación seleccionada
// SALIDA: Variables de estado actualizadas con la nueva ubicación
const updateLocation = (location) => {
  latitud.value = location.latitude;
  longitud.value = location.longitude;
  address.value = location.address || 'Dirección no disponible';
  addressData.value = location.addressData;
  
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

// ENTRADA: Datos del formulario (nombre, password, ubicación)
// PROCEDIMIENTO: Envía los datos al servidor para registrar un nuevo usuario
// SALIDA: Mensaje de éxito o error, y redirección a login si es exitoso
const register = async () => {
  error.value = '';
  success.value = '';
  
  // Validar que hay coordenadas seleccionadas
  if (!latitud.value || !longitud.value) {
    error.value = 'Por favor selecciona tu ubicación en el mapa';
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
    
    // Prevenir envíos múltiples
    const submitButton = document.querySelector('button[type="submit"]');
    if (submitButton) {
      submitButton.disabled = true;
      submitButton.textContent = 'Registrando...';
    }
    
    // Enviar datos al servidor SOLO cuando se presiona el botón de registro
    await axios.post('http://localhost:8080/auth/registro', {
      nombre: nombre.value,
      password: password.value,
      asignacion: 'usuario', // Valor por defecto
      comuna: comuna.value,
      calle: calle.value,
      ubicacion: wkt
    });
    
    // Mostrar mensaje de éxito y redirigir
    success.value = 'Usuario registrado correctamente. Redirigiendo...';
    setTimeout(() => router.push('/login'), 1500);
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al registrar usuario';
    // Reactivar el botón en caso de error
    const submitButton = document.querySelector('button[type="submit"]');
    if (submitButton) {
      submitButton.disabled = false;
      submitButton.textContent = 'Registrarse';
    }
  }
};
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>