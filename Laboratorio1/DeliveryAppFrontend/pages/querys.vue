<template>
    <div>
        <h1 class="text-2xl font-bold mb-4">Querys Complejas</h1>

        <!-- Contenedor con barra de desplazamiento -->
        <div class="max-h-[80vh] overflow-y-auto p-4 bg-gray-100 rounded shadow">
            <!-- Cliente con mayor gasto -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchClienteMayorGasto" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                    Cliente con mayor gasto
                </button>
                <div v-if="clienteMayorGasto" class="relative bg-white p-4 rounded shadow col-span-3">
                    <button @click="clienteMayorGasto = null" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
                    <p><strong>Nombre:</strong> {{ clienteMayorGasto.nombre }}</p>
                    <p><strong>Total Gastado:</strong> ${{ clienteMayorGasto.totalGastado }}</p>
                </div>
            </div>

            <!-- Producto más vendido -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchProductoMasVendido" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                    Producto/Servicio más vendido
                </button>
                <div v-if="productoMasVendido && productoMasVendido.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="productoMasVendido = []" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2">Productos más vendidos:</h2>
                    <div class="max-h-64 overflow-y-auto">
                        <ul>
                            <li v-for="producto in productoMasVendido" :key="producto.nombreProducto" class="mb-2">
                                <strong>Nombre:</strong> {{ producto.nombreProducto }} - 
                                <strong>Categoría:</strong> {{ producto.categoria }} - 
                                <strong>Total Pedidos:</strong> {{ producto.totalPedidos }}
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <!-- Empresas con más entregas fallidas -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchEmpresasConMasEntregasFallidas" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                    Empresas con más entregas fallidas
                </button>
                <div v-if="empresasConMasEntregasFallidas" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="empresasConMasEntregasFallidas = null" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
                    <ul>
                        <li v-for="empresa in empresasConMasEntregasFallidas" :key="empresa.nombre" class="mb-2">
                            <strong>Nombre:</strong> {{ empresa.nombre }} - <strong>Total Fallidas:</strong> {{ empresa.totalFallidas }}
                        </li>
                    </ul>
                </div>
            </div>

            <!-- Tiempo promedio repartidor -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchTiempoPromedioReapartidor" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                    Tiempo promedio repartidor
                </button>
                <div v-if="tiempoPromedioRepartidor.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="tiempoPromedioRepartidor = []" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
                    <ul>
                        <li v-for="repartidor in tiempoPromedioRepartidor" :key="repartidor.rut" class="mb-2">
                            <strong>Nombre:</strong> {{ repartidor.nombre }} - 
                            <strong>Rut:</strong> {{ repartidor.rut }} - 
                            <strong>Tiempo Promedio:</strong> {{ repartidor.tiempoPromedio }}
                        </li>
                    </ul>
                </div>
            </div>

            <!-- Repartidor mejor rendimiento -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchRepartidorMejorRendimiento" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                    Repartidor mejor rendimiento
                </button>
                <div v-if="repartidorMejorRendimiento.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="repartidorMejorRendimiento = []" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
                    <ul>
                        <li v-for="repartidor in repartidorMejorRendimiento" :key="repartidor.nombre" class="mb-2">
                            <strong>Nombre:</strong> {{ repartidor.nombre }} - 
                            <strong>Puntuación:</strong> {{ repartidor.puntuacion }} - 
                            <strong>Entregas:</strong> {{ repartidor.entregas }}
                        </li>
                    </ul>
                </div>
            </div>

            <!-- Método de pago más utilizado -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchMetodoPagoFrecuente" class="bg-red-500 text-white px-4 py-2 rounded shadow hover:bg-red-600">
                    Método de pago más utilizado
                </button>
                <div v-if="metodoPagoFrecuente" class="mt-4 bg-white p-4 rounded shadow col-span-3">
                    <h2 class="text-xl font-semibold mb-2">Resultado:</h2>
                    <p><strong>Nombre:</strong> {{ metodoPagoFrecuente.nombre }}</p>
                    <p><strong>Cantidad de usos:</strong> {{ metodoPagoFrecuente.usos }}</p>
                </div>
            </div>
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
            empresasConMasEntregasFallidas: null,
            tiempoPromedioRepartidor: [],
            repartidorMejorRendimiento: [],
            metodoPagoFrecuente: null,
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
                const res = await apiClient.get('/api/v1/sentenciassql/empresasEntregasFallidas');
                this.empresasConMasEntregasFallidas = res.data.map(empresa => ({
                    nombre: empresa.nombreEmpresa,
                    totalFallidas: empresa.totalFallidas
                }));
            } catch (e) {
                console.error("Error en fetchEmpresasConMasEntregasFallidas:", e);
            }
        },
        async fetchTiempoPromedioReapartidor(){
            try {
                const response = await apiClient.get('/api/v1/sentenciassql/tiempoPromedioRepartidor');
                this.tiempoPromedioRepartidor = response.data.map(item => ({
                    rut: item.rutRepartidor,
                    nombre: item.nombreRepartidor,
                    tiempoPromedio: item.tiempoPromedio
                }));
            } catch (error) {
                console.error("Error en fetchTiempoPromedioRepartidor:", error);
            }
        },
        async fetchRepartidorMejorRendimiento(){
            try {
                const response = await apiClient.get('/api/v1/sentenciassql/repartidoresMejorRendimiento');
                this.repartidorMejorRendimiento = response.data.map(item => ({
                    nombre: item.nombreRepartidor,
                    puntuacion: item.puntuacion,
                    entregas: item.entregas
                }));
            } catch (error) {
                console.error("Error en fetchRepartidorMejorRendimiento:", error);
            }
        },
        async fetchMetodoPagoFrecuente(){
            try {
                const resp = await apiClient.get('/api/v1/sentenciassql/metodoPagoFrecuente');
                this.metodoPagoFrecuente = {
                    nombre: resp.data.nombreMedioDePago,
                    usos: resp.data.cantidadUsos
                };
            } catch (error) {
                console.error("Error en fetchMetodoPagoFrecuente:", error);
            }
        }
    }
};
</script>

<style scoped>
/* Add your styles here */
h1 {
    color: #333333;
}
</style>