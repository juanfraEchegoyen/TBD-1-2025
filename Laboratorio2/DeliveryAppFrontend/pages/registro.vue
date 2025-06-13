<template>
  <div class="min-h-screen overflow-auto py-8">
    <div class="max-w-md w-full mx-auto p-6">
      <div class="text-2xl font-bold mb-6 text-center">Crear cuenta</div>
      
      <!-- Error message display -->
      <div v-if="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 text-center">
        {{ errorMessage }}
      </div>

      <!-- Selector de tipo de usuario -->
      <div class="mb-4">
        <label class="block mb-2 font-semibold">Tipo de cuenta</label>
        <select v-model="formData.tipoUsuario" class="w-full px-4 py-2 border rounded-lg">
          <option value="CLIENTE">Cliente</option>
          <option value="REPARTIDOR">Repartidor</option>
        </select>
      </div>

      <!-- Campo de RUT -->
      <div class="mb-4">
        <label class="block mb-2 font-semibold">RUT</label>
        <input
          v-model="formData.rut"
          type="text"
          placeholder="12345678-9"
          class="w-full px-4 py-2 border rounded-lg"
        />
      </div>
      
      <div class="mb-4">
        <label class="block mb-2 font-semibold">Nombre completo</label>
        <input
          v-model="formData.nombre"
          type="text"
          placeholder="Nombre completo"
          class="w-full px-4 py-2 border rounded-lg"
        />
      </div>

      <div class="mb-4">
        <label class="block mb-2 font-semibold">Teléfono</label>
        <input
          v-model="formData.telefono"
          type="text"
          placeholder="+56912345678"
          class="w-full px-4 py-2 border rounded-lg"
        />
      </div>
      
      <div class="mb-4">
        <label class="block mb-2 font-semibold">Contraseña</label>
        <input
          v-model="formData.password"
          type="password"
          placeholder="Contraseña"
          class="w-full px-4 py-2 border rounded-lg"
        />
      </div>

      <!-- Mostrar dirección y comuna obtenidas del mapa (solo para Cliente) -->
      <template v-if="formData.tipoUsuario === 'CLIENTE' && (formData.direccion || formData.comuna)">
        <div class="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
          <h4 class="font-semibold text-blue-800 mb-2">Información de ubicación:</h4>
          <p v-if="formData.direccion" class="text-sm text-blue-700">
            <strong>Dirección:</strong> {{ formData.direccion }}
          </p>
          <p v-if="formData.comuna" class="text-sm text-blue-700">
            <strong>Comuna:</strong> {{ formData.comuna }}
          </p>
        </div>
      </template>

      <!-- Mapa de ubicación -->
      <div class="mb-6">
        <label class="block mb-2 font-semibold">
          {{ formData.tipoUsuario === 'CLIENTE' ? 'Ubicación de tu domicilio' : 'Ubicación inicial de trabajo' }}
        </label>
        <p class="text-sm text-gray-600 mb-2">
          Selecciona tu ubicación en el mapa o busca una dirección en Chile
        </p>
        <GeoLocationMap 
          @update:location="updateLocation" 
          :initial-latitude="-33.4489"
          :initial-longitude="-70.6693"
        />
      </div>
      
      <BaseButtonGreen
        @click="registrar"
        :disabled="isLoading"
        class="w-full mb-4"
      >
        {{ isLoading ? 'Procesando...' : 'Registrarse' }}
      </BaseButtonGreen>
      
      <div class="text-center text-sm">
        ¿Ya tienes una cuenta? 
        <NuxtLink to="/login" class="text-green-600 hover:text-green-800">Iniciar sesión</NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import BaseButtonGreen from '@/components/BaseButtonGreen.vue'
import GeoLocationMap from '@/components/GeoLocationMap.vue'
import apiClient from '@/service/http-common'

const router = useRouter()
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const formData = reactive({
  tipoUsuario: 'CLIENTE',
  rut: '',
  nombre: '',
  telefono: '',
  password: '',
  direccion: '',
  comuna: '',
  ubicacion: '' // WKT format: "POINT(lng lat)"
})

const updateLocation = (location) => {
  console.log('Ubicación actualizada:', location)
  
  // Siempre actualizar la dirección y comuna desde el mapa
  if (location.address) {
    formData.direccion = location.address
  }
  
  if (location.addressData) {
    formData.comuna = location.addressData.county || 
                     location.addressData.city_district || 
                     location.addressData.municipality || 
                     location.addressData.suburb ||
                     ''
  }
  
  // Guardar ubicación en formato WKT
  formData.ubicacion = `POINT(${location.longitude} ${location.latitude})`
}

const validarFormulario = () => {
  if (!formData.rut.trim()) {
    errorMessage.value = 'El RUT es obligatorio'
    return false
  }
  if (!formData.nombre.trim()) {
    errorMessage.value = 'El nombre es obligatorio'
    return false
  }
  if (!formData.telefono.trim()) {
    errorMessage.value = 'El teléfono es obligatorio'
    return false
  }
  if (!formData.password || formData.password.length < 6) {
    errorMessage.value = 'La contraseña debe tener al menos 6 caracteres'
    return false
  }
  if (!formData.ubicacion) {
    errorMessage.value = 'Debes seleccionar una ubicación en el mapa'
    return false
  }
  
  // Validaciones específicas para cliente
  if (formData.tipoUsuario === 'CLIENTE') {
    if (!formData.direccion.trim()) {
      errorMessage.value = 'Debes seleccionar una ubicación válida con dirección en el mapa'
      return false
    }
    if (!formData.comuna.trim()) {
      errorMessage.value = 'No se pudo determinar la comuna. Intenta con otra ubicación'
      return false
    }
  }
  
  return true
}

const registrar = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!validarFormulario()) {
    return
  }
  
  try {
    isLoading.value = true
    
    const payload = {
      rut: formData.rut,
      nombre: formData.nombre,
      telefono: formData.telefono,
      password: formData.password,
      direccion: formData.direccion,
      comuna: formData.comuna,
      ubicacion: formData.ubicacion,
      tipoUsuario: formData.tipoUsuario // Agregar tipoUsuario al payload
    }
    
    await apiClient.post('/auth/registro', payload)
    
    successMessage.value = `${formData.tipoUsuario.toLowerCase()} registrado exitosamente. Redirigiendo...`
    
    // Reset form
    Object.keys(formData).forEach(key => {
      if (key === 'tipoUsuario') formData[key] = 'CLIENTE'
      else formData[key] = ''
    })
    
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    
  } catch (error) {
    console.error('Error durante el registro:', error)
    
    if (error.response) {
      if (error.response.status === 409) {
        errorMessage.value = 'El RUT ya está registrado'
      } else {
        errorMessage.value = error.response.data?.error || 'Error al registrar usuario'
      }
    } else if (error.request) {
      errorMessage.value = 'No se pudo conectar con el servidor'
    } else {
      errorMessage.value = 'Error al procesar su solicitud'
    }
  } finally {
    isLoading.value = false
  }
}
</script>