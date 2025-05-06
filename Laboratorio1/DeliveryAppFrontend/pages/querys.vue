<template>
    <div>
        <h1 class="text-2xl font-bold mb-4">Querys Complejas</h1>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <button @click="fetchClienteMayorGasto" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
        Cliente con mayor gasto
      </button>
      <div v-if="clienteMayorGasto" class="mt-4 bg-white p-4 rounded shadow">
      <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
      <p><strong>Nombre:</strong> {{ clienteMayorGasto.nombre }}</p>
      <p><strong>Total Gastado:</strong> ${{ clienteMayorGasto.totalGastado }}</p>
    </div>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
            <button @click="" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                Producto/Servicio más vendido
            </button>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
            <button @click="" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                Empresas con mmas entregas fallidas
            </button>
            </div>
    </div>
</template>

<script>
import apiClient from '../service/http-common';

export default {
    name: "QuerysPage",
    data() {
        return {
            clienteMayorGasto: null
        };
    },
    methods: {
        async fetchClienteMayorGasto() {
            try {
                const res = await apiClient.get('/api/v1/sentenciassql/clienteMayorGastos');
                // Ajustar los datos para que coincidan con las claves del servidor
                this.clienteMayorGasto = {
                    nombre: res.data.nombreCliente,
                    totalGastado: res.data.dineroGastado
                };
            } catch (e) {
                console.error("Error en fetchClienteMayorGasto:", e);
            }
        }
    }
};
</script>

<style scoped>
/* Add your styles here */
h1 {
    color: #333;
}
</style>