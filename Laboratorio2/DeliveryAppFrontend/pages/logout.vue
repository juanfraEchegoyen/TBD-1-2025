<template>
  <div class="flex flex-col items-center justify-center h-full">
    <div class="text-2xl font-bold mb-6">¿Quieres cerrar sesión?</div>
    <BaseButtonGreen @click="logout" class="w-80">Cerrar sesión</BaseButtonGreen>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import BaseButtonGreen from '@/components/BaseButtonGreen.vue'
import authService from '@/service/authService'

const router = useRouter()

const logout = () => {
  // Use auth service to clear tokens and update state
  authService.logout()
  
  // Redirect to login page
  router.push('/login')
}
</script>