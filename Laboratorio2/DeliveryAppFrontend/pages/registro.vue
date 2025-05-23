<template>
  <div class="flex flex-col items-center justify-center h-full">
    <div class="text-2xl font-bold mb-6">Crear cuenta</div>
    
    <!-- Error message display -->
    <div v-if="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 w-80 text-center">
      {{ errorMessage }}
    </div>
    
    <!-- Success message display -->
    <div v-if="successMessage" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4 w-80 text-center">
      {{ successMessage }}
    </div>
    
    <div>Nombre de usuario</div>
    <input
      v-model="formData.nombre"
      type="text"
      placeholder="Nombre de usuario"
      class="mb-3 px-4 py-2 border rounded-lg w-80"
    />
    
    <div>Correo electrónico</div>
    <input
      v-model="formData.email"
      type="email"
      placeholder="ejemplo@correo.com"
      class="mb-3 px-4 py-2 border rounded-lg w-80"
    />
    
    <div>Contraseña</div>
    <input
      v-model="formData.password"
      type="password"
      placeholder="Contraseña"
      class="mb-6 px-4 py-2 border rounded-lg w-80"
    />
    
    <BaseButtonGreen
      @click="registrar"
      :disabled="isLoading"
      class="w-80"
    >
      {{ isLoading ? 'Procesando...' : 'Registrarse' }}
    </BaseButtonGreen>
    
    <div class="mt-4 text-sm">
      ¿Ya tienes una cuenta? 
      <NuxtLink to="/login" class="text-green-600 hover:text-green-800">Iniciar sesión</NuxtLink>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import BaseButtonGreen from '@/components/BaseButtonGreen.vue'
import apiClient from '@/service/http-common'

const router = useRouter()
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const formData = reactive({
  nombre: '',
  email: '',
  password: ''
})

const validarFormulario = () => {
  // Validación de email con expresión regular básica
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  
  if (!formData.nombre) {
    errorMessage.value = 'El nombre de usuario es obligatorio'
    return false
  } else if (!formData.email) {
    errorMessage.value = 'El correo electrónico es obligatorio'
    return false
  } else if (!emailRegex.test(formData.email)) {
    errorMessage.value = 'El formato del correo electrónico no es válido'
    return false
  } else if (!formData.password) {
    errorMessage.value = 'La contraseña es obligatoria'
    return false
  } else if (formData.password.length < 6) {
    errorMessage.value = 'La contraseña debe tener al menos 6 caracteres'
    return false
  }
  
  return true
}

const registrar = async () => {
  // Reset messages
  errorMessage.value = ''
  successMessage.value = ''
  
  // Validate form
  if (!validarFormulario()) {
    return
  }
  
  try {
    isLoading.value = true
    
    // Send registration request to backend
    const response = await apiClient.post('/auth/registro', {
      nombre: formData.nombre,
      email: formData.email,
      password: formData.password
    })
    
    // Handle successful registration
    successMessage.value = 'Registro exitoso. Ahora puedes iniciar sesión.'
    
    // Reset form
    formData.nombre = ''
    formData.email = ''
    formData.password = ''
    
    // Redirect to login after a short delay
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    
  } catch (error) {
    console.error('Error durante el registro:', error)
    
    // Handle different error scenarios
    if (error.response) {
      // Server responded with error status
      if (error.response.status === 409) {
        errorMessage.value = 'El usuario o correo ya existe'
      } else {
        errorMessage.value = error.response.data?.error || 'Error al registrar usuario'
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