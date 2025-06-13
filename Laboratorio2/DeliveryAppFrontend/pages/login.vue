<template>
  <div class="flex flex-col items-center justify-center h-full">
    <div class="text-2xl font-bold mb-6">Iniciar sesión</div>
    <div v-if="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 w-80 text-center">
      {{ errorMessage }}
    </div>
    
    <div>RUT</div>
    <input 
      v-model="formData.rut" 
      type="text" 
      placeholder="12345678-9" 
      class="mb-3 px-4 py-2 border rounded-lg w-80" 
    />
    
    <div>Contraseña</div>
    <input
      v-model="formData.password"
      type="password"
      placeholder="Contraseña"
      class="mb-6 px-4 py-2 border rounded-lg w-80"
      @keyup.enter="login"
    />
    
    <BaseButtonGreen
      @click="login"
      :disabled="isLoading"
      class="w-80"
    >
      {{ isLoading ? 'Cargando...' : 'Acceder' }}
    </BaseButtonGreen>
    
    <div class="text-center text-sm mt-4">
      ¿No tienes una cuenta? 
      <NuxtLink to="/registro" class="text-green-600 hover:text-green-800">Registrarse</NuxtLink>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import BaseButtonGreen from '@/components/BaseButtonGreen.vue'
import apiClient from '@/service/http-common'
import authService from '@/service/authService'  

const router = useRouter()
const isLoading = ref(false)
const errorMessage = ref('')

const formData = reactive({
  rut: '',
  password: ''
})

const login = async () => {
  // Reset error message
  errorMessage.value = ''
  
  // Form validation
  if (!formData.rut || !formData.password) {
    errorMessage.value = 'Por favor, complete todos los campos'
    return
  }
  
  try {
    isLoading.value = true
    
    // Send login request to backend with only RUT and password
    const response = await apiClient.post('/auth/login', {
      rut: formData.rut,
      password: formData.password
    })
    
    // Handle successful login
    if (response.data && response.data.accessToken) {
      authService.login(response.data.accessToken, response.data.refreshToken)
      // Guarda el rut del usuario autenticado
      localStorage.setItem('user', JSON.stringify({ rut: formData.rut }))
      router.push('/')
    } else {
      errorMessage.value = 'Error en la respuesta del servidor'
    }
  } catch (error) {
    console.error('Error during login:', error)
    
    // Handle different error scenarios
    if (error.response) {
      // Server responded with error status
      if (error.response.status === 401) {
        errorMessage.value = 'Credenciales inválidas'
      } else {
        errorMessage.value = error.response.data?.error || 'Error al iniciar sesión'
      }
    } else if (error.request) {
      // No response received
      errorMessage.value = 'No se pudo conectar con el servidor'
    } else {
      // Other errors
      errorMessage.value = 'Error al procesar su solicitud'
    }
  } finally {
    isLoading.value = false
  }
}

const userStr = localStorage.getItem('user')
const user = userStr ? JSON.parse(userStr) : null
const rutCliente = user?.rut
</script>