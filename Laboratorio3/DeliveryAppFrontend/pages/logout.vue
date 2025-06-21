<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex items-center justify-center p-6">
    <div class="max-w-md w-full">
      <div class="bg-white rounded-2xl shadow-xl border border-gray-200 p-8">
        
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-gray-800 mb-2">Cerrar Sesión</h1>
          <p class="text-gray-600 text-sm">¿Estás seguro que deseas salir del sistema?</p>
          <div class="w-16 h-1 bg-gradient-to-r from-red-400 to-red-500 mx-auto mt-3 rounded-full"></div>
        </div>

        <div class="space-y-4">
          <div class="bg-gradient-to-br from-red-50 to-red-100 rounded-xl p-4 border border-red-200">
            <div class="text-center">
              <div class="text-sm text-gray-600 mb-4">
                Tu sesión se cerrará completamente y deberás iniciar sesión nuevamente.
              </div>
              
              <div class="flex flex-col gap-3">
                <BaseButtonGreen 
                  @click="logout" 
                  class="w-full bg-red-500 hover:bg-red-600 transition-colors duration-200"
                >
                  Salir
                </BaseButtonGreen>
                
                <button 
                  @click="$router.go(-1)"
                  class="w-full px-4 py-2 text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors duration-200 font-medium"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import BaseButtonGreen from '@/components/BaseButtonGreen.vue'
import authService from '@/service/authService'

const router = useRouter()

/**
 * Procesa el cierre de sesión del usuario limpiando tokens y redirigiendo al login
 * Utiliza el servicio de autenticación para limpiar el estado y redirige a la página de login
 */
const logout = () => {
  authService.logout()
  router.push('/login')
}
</script>

<style scoped>
h1 {
  color: #333333;
}
</style>