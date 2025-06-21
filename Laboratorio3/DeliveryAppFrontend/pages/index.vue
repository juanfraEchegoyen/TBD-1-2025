<template>
  <div class="flex flex-col items-center justify-center min-h-[70vh]">
    <img src="/assets/imgs/altiro-logo-red.png" alt="Logo AltiroDelivery" class="w-40 mb-6 drop-shadow-lg" />
    <h1 class="text-4xl font-extrabold text-primary mb-2">Bienvenido a AltiroDelivery</h1>
    <p class="text-lg text-gray-700 mb-6 text-center max-w-xl">
      Gestion de pedidos, clientes y empresas.
    </p>    <div class="flex gap-4 flex-wrap justify-center" v-if="isAuth">
      <nuxt-link to="/crear-pedido" class="bg-primary text-white px-6 py-2 rounded-lg font-semibold shadow hover:bg-red-700 transition-colors">
        Crear Pedido
      </nuxt-link>
      <nuxt-link to="/resumen-clientes" class="bg-tertiary text-white px-6 py-2 rounded-lg font-semibold shadow hover:bg-black/80 transition-colors">
        Resumen Clientes
      </nuxt-link>
      <nuxt-link to="/querys" class="bg-secondary text-primary px-6 py-2 rounded-lg font-semibold shadow hover:bg-gray-300 transition-colors">
        Consultas SQL
      </nuxt-link>      <nuxt-link to="/consultas-mongo" class="bg-green-600 text-white px-6 py-2 rounded-lg font-semibold shadow hover:bg-green-700 transition-colors">
        Consultas NoSQL
      </nuxt-link>
      <nuxt-link to="/AddressSearchMap" class="bg-blue-600 text-white px-6 py-2 rounded-lg font-semibold shadow hover:bg-blue-700 transition-colors">
        Consultas Geoespaciales
      </nuxt-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import authService from '@/service/authService'

const isAuth = computed(() => authService.isAuthenticated.value)
</script>