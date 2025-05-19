<template>
  <GeoTaskBackground>
    <div class="flex flex-col items-center justify-center min-h-screen">
      <div class="bg-white/80 p-10 rounded-[2.5rem] w-full max-w-sm flex flex-col items-center">
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
          <div class="mb-6 grid grid-cols-2 gap-2">
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Latitud</label>
              <input v-model="latitud" type="number" step="any" placeholder="40.7127837" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" required />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Longitud</label>
              <input v-model="longitud" type="number" step="any" placeholder="-74.0059413" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" required />
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import GeoTaskLogo from '~/components/GeoTaskLogo.vue'
import GeoTaskButton from '~/components/GeoTaskButton.vue'
import GeoTaskBackground from '~/components/GeoTaskBackground.vue'

const nombre = ref('')
const password = ref('')
const latitud = ref('')
const longitud = ref('')
const error = ref('')
const success = ref('')
const router = useRouter()

const register = async () => {
  error.value = ''
  success.value = ''
  try {
    const wkt = `POINT(${longitud.value} ${latitud.value})`
    await axios.post('http://localhost:8080/auth/registro', {
      nombre: nombre.value,
      password: password.value,
      ubicacion: wkt
    })
    success.value = 'Usuario registrado correctamente. Redirigiendo...'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al registrar usuario'
  }
}
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>
