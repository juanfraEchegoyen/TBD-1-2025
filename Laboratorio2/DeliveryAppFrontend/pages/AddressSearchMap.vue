<template>
  <div class="address-page">
    <h2 class="text-xl font-bold mb-4">Selecciona tu dirección</h2>
    <GeoLocationMap @update:location="onLocationUpdate" />
    <div v-if="selectedLocation" class="mt-4 p-3 bg-gray-100 rounded">
      <div><strong>Dirección seleccionada:</strong> {{ selectedLocation.address }}</div>
      <div><strong>Lat:</strong> {{ selectedLocation.latitude }} | <strong>Lng:</strong> {{ selectedLocation.longitude }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import GeoLocationMap from '@/components/GeoLocationMap.vue'

const selectedLocation = ref(null)

function onLocationUpdate(location) {
  selectedLocation.value = location
}
</script>

<style scoped>
.address-page {
  max-width: 500px;
  margin: 0 auto;
  padding: 2rem 0;
}
</style>