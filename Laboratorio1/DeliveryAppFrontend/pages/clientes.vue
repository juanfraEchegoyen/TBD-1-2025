<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Clientes</h1>
    <div v-if="loading" class="text-gray-500">Cargando clientes...</div>
    <div v-else>
      <table class="min-w-full bg-white shadow rounded">
        <thead>
          <tr>
            <th class="py-2 px-4 border-b">RUT</th>
            <th class="py-2 px-4 border-b">Nombre</th>
            <th class="py-2 px-4 border-b">Dirección</th>
            <th class="py-2 px-4 border-b">Comuna</th>
            <th class="py-2 px-4 border-b">Teléfono</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cliente in clientes" :key="cliente.rut">
            <td class="py-2 px-4 border-b">{{ cliente.rut }}</td>
            <td class="py-2 px-4 border-b">{{ cliente.nombre }}</td>
            <td class="py-2 px-4 border-b">{{ cliente.direccion }}</td>
            <td class="py-2 px-4 border-b">{{ cliente.comuna }}</td>
            <td class="py-2 px-4 border-b">{{ cliente.telefono }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="clientes.length === 0" class="text-gray-500 mt-4">No hay clientes registrados</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const clientes = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/v1/clientes')
    if (!res.ok) throw new Error('Error al obtener clientes')
    clientes.value = await res.json()
  } catch (e) {
    clientes.value = []
  } finally {
    loading.value = false
  }
})
</script>