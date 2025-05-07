<template>
    <div>
        <h1 class="text-2xl font-bold mb-4">Querys Complejas</h1>

        <!-- Contenedor con barra de desplazamiento -->
        <div class="max-h-[80vh] overflow-y-auto p-4 bg-gray-100 rounded shadow">
            <!-- Cliente con mayor gasto -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchClienteMayorGasto" class="btn-primary">
                    Cliente con mayor gasto
                </button>
                <div v-if="clienteMayorGasto" class="relative bg-white p-4 rounded shadow col-span-3">
                    <button @click="clienteMayorGasto = null" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <p><strong>Nombre:</strong> {{ clienteMayorGasto.nombre }}</p>
                    <p><strong>Total Gastado:</strong> ${{ clienteMayorGasto.totalGastado }}</p>
                </div>
            </div>

            <!-- Producto más vendido -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchProductoMasVendido" class="btn-primary">
                    Producto más vendido
                </button>
                <div v-if="productoMasVendido && productoMasVendido.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="productoMasVendido = []" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <table class="table-auto w-full border-collapse border border-gray-300 mb-4">
                        <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2">Nombre</th>
                                <th class="border border-gray-300 px-4 py-2">Categoría</th>
                                <th class="border border-gray-300 px-4 py-2">Total Pedidos</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="producto in productoMasVendido" :key="producto.nombreProducto">
                                <td class="border border-gray-300 px-4 py-2">{{ producto.nombreProducto }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ producto.categoria }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ producto.totalPedidos }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Empresas con más entregas fallidas -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchEmpresasConMasEntregasFallidas" class="btn-primary">
                    Empresas con más entregas fallidas
                </button>
                <div v-if="empresasConMasEntregasFallidas" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="empresasConMasEntregasFallidas = null" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <table class="table-auto w-full border-collapse border border-gray-300 mb-4">
                        <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2">Nombre</th>
                                <th class="border border-gray-300 px-4 py-2">Total Entregas Fallidas</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="empresa in empresasConMasEntregasFallidas" :key="empresa.nombre">
                                <td class="border border-gray-300 px-4 py-2">{{ empresa.nombreEmpresa }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ empresa.totalEntregasFallidas }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Tiempo promedio repartidor -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchTiempoPromedioReapartidor" class="btn-primary">
                    Tiempo promedio repartidor
                </button>
                <div v-if="tiempoPromedioRepartidor.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="tiempoPromedioRepartidor = []" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <table class="table-auto w-full border-collapse border border-gray-300">
                        <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2">Rut</th>
                                <th class="border border-gray-300 px-4 py-2">Nombre</th>
                                <th class="border border-gray-300 px-4 py-2">Tiempo Promedio</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="repartidor in tiempoPromedioRepartidor" :key="repartidor.rut">
                                <td class="border border-gray-300 px-4 py-2">{{ repartidor.rut }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ repartidor.nombre }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ repartidor.tiempoPromedio }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Repartidor mejor rendimiento -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchRepartidorMejorRendimiento" class="btn-primary">
                    Repartidor mejor rendimiento
                </button>
                <div v-if="repartidorMejorRendimiento.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="repartidorMejorRendimiento = []" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <table class="table-auto w-full border-collapse border border-gray-300">
                        <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2">Nombre</th>
                                <th class="border border-gray-300 px-4 py-2">Puntuación</th>
                                <th class="border border-gray-300 px-4 py-2">Entregas</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="repartidor in repartidorMejorRendimiento" :key="repartidor.nombre">
                                <td class="border border-gray-300 px-4 py-2">{{ repartidor.nombre }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ repartidor.puntuacion }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ repartidor.entregas }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Método de pago más utilizado -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchMetodoPagoFrecuente" class="btn-primary">
                    Método de pago más utilizado
                </button>
                <div v-if="metodoPagoFrecuente" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="metodoPagoFrecuente = null" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <p><strong>Método:</strong> {{ metodoPagoFrecuente.nombre }}</p>
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
                    nombreEmpresa: empresa.nombreEmpresa,
                    totalEntregasFallidas: empresa.totalEntregasFallidas
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
                const response = await apiClient.get('/api/v1/sentenciassql/metodoPagoFrecuente');
                this.metodoPagoFrecuente = {
                    nombre: response.data.nombreMedioDePago,
                    usos: response.data.cantidadUsos
                };
            } catch (error) {
                console.error("Error en fetchMetodoPagoFrecuente:", error);
            }
        }
    }
};
</script>

<style scoped>
h1 {
    color: #333333;
}

.btn-primary {
    background-color: #ef4444; /* rojo */
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 0.375rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: background-color 0.2s;
}

.btn-primary:hover {
    background-color: #dc2626; /* rojo oscuro */
}

.close-btn {
    position: absolute;
    top: 0.5rem;
    right: 0.5rem;
    color: #6b7280; /* gris */
    cursor: pointer;
    transition: color 0.2s;
}

.close-btn:hover {
    color: #374151; /* gris oscuro */
}

</style>