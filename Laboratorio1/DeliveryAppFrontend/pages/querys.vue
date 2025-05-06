<template>
    <div>
        <h1 class="text-2xl font-bold mb-4">Querys Complejas</h1>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4"> <!-- Contenedor con dos columnas -->
            <button @click="fetchClienteMayorGasto" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                Cliente con mayor gasto
            </button>
            <div v-if="clienteMayorGasto" class="mt-4 bg-white p-4 rounded shadow col-span-2"> <!-- Ocupa ambas columnas -->
                <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
                <p><strong>Nombre:</strong> {{ clienteMayorGasto.nombre }}</p>
                <p><strong>Total Gastado:</strong> ${{ clienteMayorGasto.totalGastado }}</p>
            </div>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
            <button @click="fetchProductoMasVendido" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                Producto/Servicio más vendido
            </button>
        </div>
        <div v-if="productoMasVendido && productoMasVendido.length" class="mt-4 bg-white p-4 rounded shadow">
            <h2 class="text-xl font-semibold mb-2">Productos más vendidos:</h2>
            <div class="max-h-64 overflow-y-auto"> <!-- Contenedor con barra de desplazamiento -->
                <ul>
                    <li v-for="producto in productoMasVendido" :key="producto.nombreProducto" class="mb-2">
                        <strong>Nombre:</strong> {{ producto.nombreProducto }} - 
                        <strong>Categoría:</strong> {{ producto.categoria }} - 
                        <strong>Total Pedidos:</strong> {{ producto.totalPedidos }}
                    </li>
                </ul>
            </div>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
            <button @click="fetchEmpresasConMasEntregasFallidas" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                Empresas con mas entregas fallidas
            </button>
        </div>
        <div v-if="empresasConMasEntregasFallidas" class="mt-4 bg-white p-4 rounded shadow">
            <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
            <ul>
                <li v-for="empresa in empresasConMasEntregasFallidas" :key="empresa.nombre" class="mb-2">
                    <strong>Nombre:</strong> {{ empresa.nombre }} - <strong>Total Fallidas:</strong> {{ empresa.totalFallidas }}
                </li>
            </ul>
        </div>  
    </div>
</template>

<script>
import apiClient from '../service/http-common';

export default {
    name: "QuerysPage",
    data() {
        return {
            clienteMayorGasto: null,
            productoMasVendido: [], // Inicializar como array
            empresasConMasEntregasFallidas: null
        };
    },
    methods: {
        async fetchClienteMayorGasto() {
            try {
                const res = await apiClient.get('/api/v1/sentenciassql/clienteMayorGastos');
                this.clienteMayorGasto = {
                    nombre: res.data.nombreCliente,
                    totalGastado: res.data.dineroGastado
                };
            } catch (e) {
                console.error("Error en fetchClienteMayorGasto:", e);
            }
        },
        async fetchProductoMasVendido() {
            try {
                const res = await apiClient.get('/api/v1/sentenciassql/productosMasVendidos');
                this.productoMasVendido = res.data; // Guardar el listado completo
            } catch (e) {
                console.error("Error en fetchProductoMasVendido:", e);
            }
        },
        async fetchEmpresasConMasEntregasFallidas() {
            try {
                const res = await apiClient.get('/api/v1/sentenciassql/empresaEntregasFallidas');
                this.empresasConMasEntregasFallidas = res.data.map(empresa => ({
                    nombre: empresa.nombreEmpresa,
                    totalFallidas: empresa.totalFallidas
                }));
            } catch (e) {
                console.error("Error en fetchEmpresasConMasEntregasFallidas:", e);
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