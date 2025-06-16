<template>
  <div class="min-h-screen max-h-screen overflow-hidden bg-gradient-to-br from-slate-50 to-slate-100 flex items-center justify-center p-4">
    <div class="max-w-md w-full h-full max-h-[95vh] overflow-y-auto">
      
      <div class="mb-8 text-center pt-4">
        <h1 class="text-3xl font-bold text-gray-800 mb-2">Crear cuenta</h1>
        <p class="text-gray-600">Únete a nuestro sistema de delivery</p>
        <div class="w-16 h-1 bg-gradient-to-r from-blue-500 to-blue-600 mx-auto mt-3 rounded-full"></div>
      </div>

      <div class="bg-white rounded-2xl shadow-xl border border-gray-200 p-6 mb-4">
        
        <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 text-center">
          <div class="flex items-center justify-center">
            <span class="text-red-500 mr-2">⚠️</span>
            {{ errorMessage }}
          </div>
        </div>

        <div v-if="successMessage" class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-6 text-center">
          <div class="flex items-center justify-center">
            <span class="text-green-500 mr-2">✅</span>
            {{ successMessage }}
          </div>
        </div>

        <div class="mb-6">
          <label class="block mb-3 font-semibold text-gray-700">Tipo de cuenta</label>
          <div class="relative">
            <select 
              v-model="formData.tipoUsuario" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-white"
            >
              <option value="CLIENTE">👤 Cliente</option>
              <option value="REPARTIDOR">🚴 Repartidor</option>
            </select>
          </div>
        </div>

        <div class="mb-6">
          <label class="block mb-3 font-semibold text-gray-700">RUT</label>
          <input
            v-model="formData.rut"
            type="text"
            placeholder="12345678-9"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>
        
        <div class="mb-6">
          <label class="block mb-3 font-semibold text-gray-700">Nombre completo</label>
          <input
            v-model="formData.nombre"
            type="text"
            placeholder="Nombre completo"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>

        <div class="mb-6">
          <label class="block mb-3 font-semibold text-gray-700">Teléfono</label>
          <input
            v-model="formData.telefono"
            type="text"
            placeholder="+56912345678"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>
        
        <div class="mb-6">
          <label class="block mb-3 font-semibold text-gray-700">Contraseña</label>
          <input
            v-model="formData.password"
            type="password"
            placeholder="Contraseña"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>

        <template v-if="formData.tipoUsuario === 'CLIENTE' && (formData.direccion || formData.comuna)">
          <div class="mb-6 p-4 bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-200 rounded-xl">
            <div class="flex items-center mb-3">
              <h4 class="font-semibold text-blue-800">Información de ubicación</h4>
            </div>
            <div class="bg-white rounded-lg p-3 border border-blue-200">
              <p v-if="formData.direccion" class="text-sm text-blue-700 mb-1">
                <strong>Dirección:</strong> {{ formData.direccion }}
              </p>
              <p v-if="formData.comuna" class="text-sm text-blue-700">
                <strong>Comuna:</strong> {{ formData.comuna }}
              </p>
            </div>
          </div>
        </template>

        <div class="mb-6">
          <label class="block mb-3 font-semibold text-gray-700">
            {{ formData.tipoUsuario === 'CLIENTE' ? ' Ubicación de tu domicilio' : ' Ubicación inicial de trabajo' }}
          </label>
          <div class="bg-gray-50 rounded-xl p-4 border border-gray-200">
            <p class="text-sm text-gray-600 mb-3">
              Selecciona tu ubicación en el mapa o busca una dirección en Chile
            </p>
            <div class="bg-white rounded-lg border border-gray-300 overflow-hidden">
              <GeoLocationMap 
                @update:location="updateLocation" 
                :initial-latitude="-33.4489"
                :initial-longitude="-70.6693"
              />
            </div>
          </div>
        </div>
        
        <BaseButtonGreen
          @click="registrar"
          :disabled="isLoading"
          class="w-full mb-6 py-3 text-lg font-semibold rounded-lg transition-all duration-200 transform hover:scale-105"
        >
          <span v-if="isLoading" class="flex items-center justify-center">
            <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
            Procesando...
          </span>
          <span v-else class="flex items-center justify-center">
            Registrarse
          </span>
        </BaseButtonGreen>
        
        <div class="text-center text-sm bg-gray-50 rounded-lg p-3">
          <span class="text-gray-600">¿Ya tienes una cuenta?</span>
          <NuxtLink to="/login" class="text-blue-600 hover:text-blue-800 font-medium ml-1 transition-colors duration-200">
            Iniciar sesión
          </NuxtLink>
        </div>
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
  ubicacion: ''
})

/**
 * Actualiza los datos de ubicación del formulario con la información obtenida del mapa
 * @param {Object} location - Objeto con datos de ubicación (lat, lng, address, addressData)
 */
const updateLocation = (location) => {
  console.log('Ubicación actualizada:', location)
  
  if (location.address) {
    formData.direccion = location.address
  }
  
  if (location.addressData) {
    formData.comuna = location.addressData.suburb || 
                     location.addressData.city_district || 
                     location.addressData.municipality || 
                     location.addressData.county ||
                     ''
  }
  
  formData.ubicacion = `POINT(${location.longitude} ${location.latitude})`
}

/**
 * Valida todos los campos del formulario antes de enviar el registro
 * @returns {boolean} - True si la validación es exitosa, false en caso contrario
 */
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

/**
 * Procesa el registro del usuario enviando los datos al backend
 * Maneja la validación, envío de datos, respuestas exitosas y errores
 */
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
      tipoUsuario: formData.tipoUsuario
    }
    
    await apiClient.post('/auth/registro', payload)
    
    successMessage.value = `${formData.tipoUsuario.toLowerCase()} registrado exitosamente. Redirigiendo...`
    
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

<style scoped>
h1 {
  color: #333333;
}

input:focus, select:focus {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>