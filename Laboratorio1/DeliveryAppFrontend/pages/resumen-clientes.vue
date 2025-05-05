<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Resumen de pedidos por cliente</h1>
    <div v-if="loading" class="text-gray-500">Cargando resumen...</div>
    <div v-else>
      <table class="min-w-full bg-white shadow rounded">
        <thead>
          <tr>
            <th class="py-2 px-4 border-b">RUT Cliente</th>
            <th class="py-2 px-4 border-b">Nombre</th>
            <th class="py-2 px-4 border-b">Cantidad de Pedidos</th>
            <th class="py-2 px-4 border-b">Monto Total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cliente in resumen" :key="cliente.rutCliente">
            <td class="py-2 px-4 border-b">{{ cliente.rutCliente }}</td>
            <td class="py-2 px-4 border-b">{{ cliente.nombreCliente }}</td>
            <td class="py-2 px-4 border-b">{{ cliente.cantidadPedidos }}</td>
            <td class="py-2 px-4 border-b">${{ cliente.montoTotal }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="resumen.length === 0" class="text-gray-500 mt-4">No hay datos para mostrar</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const resumen = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/v1/vistas/resumen-clientes')
    if (!res.ok) throw new Error('Error al obtener resumen')
    resumen.value = await res.json()
  } catch (e) {
    resumen.value = []
  } finally {
    loading.value = false
  }
})
</script>