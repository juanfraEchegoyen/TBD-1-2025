<template>
  <div class="flex flex-col items-center justify-center h-full">
    <div class="text-2xl font-bold mb-6">Iniciar sesión</div>
    <div>Email</div>
    <input type="text" placeholder="Email" class="mb-3 px-4 py-2 border rounded-lg" />
    <div>Contraseña</div>
    <input type="password" placeholder="Contraseña" class="mb-6 px-4 py-2 border rounded-lg" />
    <BaseButtonGreen>Acceder</BaseButtonGreen>
  </div>
</template>

<script setup>
import BaseButtonGreen from '@/components/BaseButtonGreen.vue'
</script>