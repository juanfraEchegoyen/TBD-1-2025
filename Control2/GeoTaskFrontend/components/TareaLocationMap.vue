<template>
  <div ref="mapContainer" class="h-full w-full"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

const props = defineProps({
  latitude: {
    type: Number,
    required: true
  },
  longitude: {
    type: Number,
    required: true
  },
  addressText: {
    type: String,
    default: ''
  }
});

const mapContainer = ref(null);

onMounted(() => {
  // Arreglar iconos de Leaflet en Vue
  fixLeafletIcon();
  
  // Inicializar mapa
  const map = L.map(mapContainer.value).setView([props.latitude, props.longitude], 15);
  
  L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    maxZoom: 19
  }).addTo(map);
  
  // Añadir marcador
  const marker = L.marker([props.latitude, props.longitude]).addTo(map);
  if (props.addressText) {
    marker.bindPopup(props.addressText).openPopup();
  }
});

// Función para arreglar el problema de iconos en Leaflet
function fixLeafletIcon() {
  delete L.Icon.Default.prototype._getIconUrl;
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
    iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
    shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png'
  });
}
</script>