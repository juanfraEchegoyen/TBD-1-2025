<template>
   <div>
     <h1 class="text-2xl font-bold mb-4">Empresas con Mayor Volumen de Pedidos</h1>
     <div v-if="loading" class="text-gray-500">Cargando resumen...</div>
     <div v-else>
       <table class="min-w-full bg-white shadow rounded">
         <thead>
           <tr>
             <th class="py-2 px-4 border-b">RUT Empresa</th>
             <th class="py-2 px-4 border-b">Nombre</th>
             <th class="py-2 px-4 border-b">Cantidad de Pedidos</th>
           </tr>
         </thead>
         <tbody>
           <tr v-for="empresa in resumen" :key="empresa.rutEmpresa">
             <td class="py-2 px-4 border-b">{{ empresa.rutEmpresa }}</td>
             <td class="py-2 px-4 border-b">{{ empresa.nombreEmpresa }}</td>
             <td class="py-2 px-4 border-b">{{ empresa.cantidadPedidos }}</td>
           </tr>
         </tbody>
       </table>
       <div v-if="resumen.length === 0" class="text-gray-500 mt-4">No hay datos para mostrar</div>
     </div>
   </div>
 </template>
 
 <script setup lang="ts">
 import { ref, onMounted } from 'vue'
 import { tokenAutorizacion } from '~/service/http-common'
 
 const resumen = ref<any[]>([])
 const loading = ref(true)
 
 onMounted(async () => {
   try {
     const res = await tokenAutorizacion('http://localhost:8080/api/v1/vistas/empresas-mas-pedidos')
     if (!res.ok) throw new Error('Error al obtener resumen')
     resumen.value = await res.json()
   } catch (e) {
     resumen.value = []
   } finally {
     loading.value = false
   }
 })
 </script>
 