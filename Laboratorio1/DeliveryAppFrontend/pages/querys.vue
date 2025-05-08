<template>
    <div>
        <h1 class="text-2xl font-bold mb-4">Consulta de datos</h1>

        <!-- Contenedor con barra de desplazamiento -->
        <div class="max-h-[80vh] overflow-y-auto p-4 bg-gray-100 rounded shadow">
            <!-- Cliente con mayor gasto -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchClienteMayorGasto" class="btn-primary">
                    Cliente con mayor gasto
                </button>
                <div v-if="clienteMayorGasto && consultaActiva === 'clienteMayorGasto'" class="relative bg-white p-4 rounded shadow col-span-3">
                    <button @click="clienteMayorGasto = null; consultaActiva = null" class="close-btn">
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
                <div v-if="productoMasVendido && productoMasVendido.length && consultaActiva === 'productoMasVendido'" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="productoMasVendido = []; consultaActiva = null" class="close-btn">
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
                <div v-if="empresasConMasEntregasFallidas && consultaActiva === 'empresasConMasEntregasFallidas'" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="empresasConMasEntregasFallidas = null; consultaActiva = null" class="close-btn">
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
                <div v-if="tiempoPromedioRepartidor.length && consultaActiva === 'tiempoPromedioRepartidor'" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="tiempoPromedioRepartidor = []; consultaActiva = null" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <table class="table-auto w-full border-collapse border border-gray-300">
                        <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2">Rut</th>
                                <th class="border border-gray-300 px-4 py-2">Nombre</th>
                                <th class="border border-gray-300 px-4 py-2">Tiempo Promedio (minutos)</th>
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
                <div v-if="repartidorMejorRendimiento.length && consultaActiva === 'repartidorMejorRendimiento'" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="repartidorMejorRendimiento = []; consultaActiva = null" class="close-btn">
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
                <div v-if="metodoPagoFrecuente && consultaActiva === 'metodoPagoFrecuente'" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="metodoPagoFrecuente = null; consultaActiva = null" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2"</h2>
                    <p><strong>Método:</strong> {{ metodoPagoFrecuente.nombre }}</p>
                    <p><strong>Cantidad de usos:</strong> {{ metodoPagoFrecuente.usos }}</p>
                </div>
            </div>
            
            <!-- Separador de secciones -->
            <hr class="my-8 border-t-2 border-gray-300" />
            
            <!-- Título de la nueva sección -->
            <h1 class="text-2xl font-bold mb-4">Actividades Bonus</h1>
            
            <!-- Ranking de devoluciones o cancelaciones -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                <button @click="fetchRankingDevolucionesOCancelaciones" class="btn-primary btn-bonus">
                    Ranking de devoluciones o cancelaciones
                </button>
                <div v-if="rankingDevolucionesCancelaciones.length" class="relative mt-4 bg-white p-4 rounded shadow col-span-3">
                    <button @click="rankingDevolucionesCancelaciones = []" class="close-btn">
                        ✖
                    </button>
                    <h2 class="text-xl font-semibold mb-2">Ranking de productos por devoluciones y cancelaciones</h2>
                    <table class="table-auto w-full border-collapse border border-gray-300">
                        <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2">Producto</th>
                                <th class="border border-gray-300 px-4 py-2">Categoría</th>
                                <th class="border border-gray-300 px-4 py-2">Devoluciones</th>
                                <th class="border border-gray-300 px-4 py-2">Cancelaciones</th>
                                <th class="border border-gray-300 px-4 py-2">Total Problemas</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="producto in rankingDevolucionesCancelaciones" :key="producto.nombre_producto">
                                <td class="border border-gray-300 px-4 py-2">{{ producto.producto }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ producto.categoria }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ producto.devoluciones }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ producto.cancelaciones }}</td>
                                <td class="border border-gray-300 px-4 py-2">{{ producto.problemas }}</td>
                            </tr>
                        </tbody>
                    </table>
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
            consultaActiva: null, // Estado centralizado para la consulta activa
            clienteMayorGasto: null,
            productoMasVendido: [],
            empresasConMasEntregasFallidas: null,
            tiempoPromedioRepartidor: [],
            repartidorMejorRendimiento: [],
            metodoPagoFrecuente: null,
            // Agregamos la nueva variable para el ranking
            rankingDevolucionesCancelaciones: [],
        };
    },
    methods: {
        async fetchClienteMayorGasto() {
            this.consultaActiva = 'clienteMayorGasto'; // Activar esta consulta
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
            this.consultaActiva = 'productoMasVendido'; // Activar esta consulta
            try {
                const res = await apiClient.get('/api/v1/sentenciassql/productosMasVendidos');
                this.productoMasVendido = res.data;
            } catch (e) {
                console.error("Error en fetchProductoMasVendido:", e);
            }
        },
        async fetchEmpresasConMasEntregasFallidas() {
            this.consultaActiva = 'empresasConMasEntregasFallidas'; // Activar esta consulta
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
        async fetchTiempoPromedioReapartidor() {
            this.consultaActiva = 'tiempoPromedioRepartidor'; // Activar esta consulta
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
        async fetchRepartidorMejorRendimiento() {
            this.consultaActiva = 'repartidorMejorRendimiento'; // Activar esta consulta
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
        async fetchMetodoPagoFrecuente() {
            this.consultaActiva = 'metodoPagoFrecuente'; // Activar esta consulta
            try {
                const response = await apiClient.get('/api/v1/sentenciassql/metodoPagoFrecuente');
                this.metodoPagoFrecuente = {
                    nombre: response.data.nombreMedioDePago,
                    usos: response.data.cantidadUsos
                };
            } catch (error) {
                console.error("Error en fetchMetodoPagoFrecuente:", error);
            }
        },
        // Nuevo método para buscar el ranking de devoluciones o cancelaciones
        async fetchRankingDevolucionesOCancelaciones() {
            try {
                // Llamada a la API
                const response = await apiClient.get('/api/v1/sentenciassql/rankingDevolucionesOCancelaciones');
                
                // Asignar los datos a la variable
                this.rankingDevolucionesCancelaciones = response.data;
            } catch (error) {
                console.error("Error en fetchRankingDevolucionesOCancelaciones:", error);
                
                // Manejo del error 404 o cualquier otro error
                if (error.response && error.response.status === 404) {
                    alert("La API de ranking de devoluciones no está disponible o el endpoint no existe");
                } else {
                    alert("Error al obtener el ranking de devoluciones: " + error.message);
                }
                
                // Inicializar con array vacío para evitar errores
                this.rankingDevolucionesCancelaciones = [];
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

/* Estilo especial para el botón de bonus */
.btn-bonus {
    background-color: #8b5cf6; /* púrpura */
}

.btn-bonus:hover {
    background-color: #7c3aed; /* púrpura oscuro */
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