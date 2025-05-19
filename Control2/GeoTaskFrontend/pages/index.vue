<template>
  <GeoTaskBackground>
    <div class="flex flex-col items-center justify-center min-h-screen">
      <div class="bg-white/80 p-10 rounded-[2.5rem] w-full max-w-lg flex flex-col items-center">
        <GeoTaskLogo />
        <h1 class="text-4xl font-extrabold mb-4 text-green-700 text-center drop-shadow">Bienvenido a GeoTask</h1>
        <p class="text-lg text-gray-700 text-center mb-8 font-medium">Gestion de tareas</p>
        <div class="flex gap-6 w-full justify-center">
          <GeoTaskButton color="green" class="text-lg px-8 py-3" @click="$router.push('/login')">Iniciar sesion</GeoTaskButton>
          <GeoTaskButton color="yellow" class="text-lg px-8 py-3" @click="$router.push('/register')">Registrarse</GeoTaskButton>
        </div>
      </div>
    </div>
  </GeoTaskBackground>
</template>

<script setup>
import GeoTaskLogo from '~/components/GeoTaskLogo.vue'
import GeoTaskButton from '~/components/GeoTaskButton.vue'
import GeoTaskBackground from '~/components/GeoTaskBackground.vue'
</script>

<style scoped>
body {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}
</style>
