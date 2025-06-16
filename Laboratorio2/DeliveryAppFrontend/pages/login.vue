<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex items-center justify-center p-6">
    <!-- Contenedor principal -->
    <div class="w-full max-w-md">
      <!-- Header sutil -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-2">Iniciar Sesión</h1>
        <p class="text-gray-600">Accede a tu cuenta del sistema de delivery</p>
        <div class="w-16 h-1 bg-gradient-to-r from-blue-500 to-blue-600 mx-auto mt-3 rounded-full"></div>
      </div>

      <!-- Tarjeta de login -->
      <div class="bg-white rounded-2xl shadow-xl border border-gray-200 p-8">
        <!-- Mensaje de error -->
        <div v-if="errorMessage" class="bg-gradient-to-br from-red-50 to-red-100 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 text-center">
          <div class="flex items-center justify-center">
            <span class="text-red-500 mr-2">⚠️</span>
            {{ errorMessage }}
          </div>
        </div>

        <!-- Formulario -->
        <div class="space-y-6">
          <!-- Campo RUT -->
          <div>
            <label for="rut" class="block text-sm font-medium text-gray-700 mb-2">
              RUT
            </label>
            <input 
              id="rut"
              v-model="formData.rut" 
              type="text" 
              placeholder="12345678-9" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white" 
            />
          </div>
          
          <!-- Campo Contraseña -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              Contraseña
            </label>
            <input
              id="password"
              v-model="formData.password"
              type="password"
              placeholder="Contraseña"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              @keyup.enter="login"
            />
          </div>
          
          <!-- Botón de acceso -->
          <div class="pt-2">
            <button
              @click="login"
              :disabled="isLoading"
              :class="[
                'w-full py-3 px-4 rounded-lg font-medium text-white transition-all duration-200',
                isLoading 
                  ? 'bg-gray-400 cursor-not-allowed' 
                  : 'bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5'
              ]"
            >
              <span v-if="isLoading" class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Cargando...
              </span>
              <span v-else>Acceder</span>
            </button>
          </div>
        </div>

        <!-- Footer del formulario -->
        <div class="mt-8 pt-6 border-t border-gray-200">
          <div class="text-center">
            <p class="text-sm text-gray-600">
              ¿No tienes una cuenta? 
              <NuxtLink 
                to="/registro" 
                class="font-medium text-blue-600 hover:text-blue-800 transition-colors duration-200"
              >
                Registrarse
              </NuxtLink>
            </p>
          </div>
        </div>
      </div>

      <!-- Footer sutil -->
      <div class="text-center mt-8">
        <p class="text-xs text-gray-500">
          Sistema de Delivery - Acceso Seguro
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/service/http-common'
import authService from '@/service/authService'  

const router = useRouter()
const isLoading = ref(false)
const errorMessage = ref('')

const formData = reactive({
  rut: '',
  password: ''
})

/**
 * Procesa el inicio de sesión del usuario validando credenciales y redirigiendo al dashboard
 * Maneja la autenticación, almacenamiento de tokens y manejo de errores
 */
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
</script>

<style scoped>
h1 {
  color: #333333;
}

input:focus {
  transform: translateY(-1px);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>