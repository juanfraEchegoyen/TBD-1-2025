<template>
  <GeoTaskBackground>
    <div class="flex flex-col items-center justify-center min-h-screen">
      <div class="bg-white/80 p-10 rounded-[2.5rem] w-full max-w-sm flex flex-col items-center">
        <GeoTaskLogo />
        <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Iniciar Sesión</h2>
        <form @submit.prevent="login">
          <div class="mb-4">
            <label class="block mb-1 font-semibold text-gray-700">Usuario</label>
            <input v-model="nombre" type="text" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" required />
          </div>
          <div class="mb-6">
            <label class="block mb-1 font-semibold text-gray-700">Contraseña</label>
            <input v-model="password" type="password" class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400" required />
          </div>
          <GeoTaskButton color="green" class="w-full" type="submit">Entrar</GeoTaskButton>
        </form>
        <div v-if="error" class="text-red-500 mt-4 text-center">{{ error }}</div>
        <div class="mt-4 text-center">
          <GeoTaskButton color="yellow" @click="$router.push('/register')">¿No tienes cuenta? Regístrate</GeoTaskButton>
        </div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import GeoTaskLogo from '~/components/GeoTaskLogo.vue'
import GeoTaskButton from '~/components/GeoTaskButton.vue'
import GeoTaskBackground from '~/components/GeoTaskBackground.vue'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const nombre = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

const login = async () => {
  error.value = ''
  try {
    const res = await axios.post('http://localhost:8080/auth/login', { nombre: nombre.value, password: password.value })
    localStorage.setItem('accessToken', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    localStorage.setItem('userId', res.data.userId)
    
    router.push('/tareas')
  } catch (e) {
    error.value = 'Credenciales invalidas'
  }
}
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>