<template>
    <div class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 p-6">
        <!-- Header mejorado -->
        <div class="mb-6 text-center">
            <h1 class="text-3xl font-bold text-gray-800 mb-2">Panel de Consultas</h1>
            <p class="text-gray-600">Análisis de datos del sistema de delivery</p>
            <div class="w-24 h-1 bg-gradient-to-r from-red-500 to-red-600 mx-auto mt-3 rounded-full"></div>
        </div>

        <!-- Menú de navegación -->
        <div class="max-w-7xl mx-auto mb-4">
            <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-3">
                <div class="flex flex-wrap gap-2 justify-center">
                    <button 
                        v-for="(consulta, key) in consultasDisponibles" 
                        :key="key"
                        @click="mostrarConsulta(key)"
                        :class=" [
                            'px-3 py-2 rounded-lg font-medium text-sm',
                            consultaActiva === key 
                                ? 'bg-red-500 text-white shadow-lg' 
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                        ]"
                    >
                        {{ consulta.titulo }}
                    </button>
                </div>
            </div>
        </div>

        <!-- Contenedor principal mejorado -->
        <div class="max-w-7xl mx-auto">
            <div class="bg-white rounded-2xl shadow-xl border border-gray-200">
                <div class="p-6">
                    
                    <!-- Contenido dinámico basado en la consulta activa -->
                    <div v-if="!consultaActiva" class="text-center py-8">
                        <div class="text-4xl mb-3">📊</div>
                        <h3 class="text-xl font-bold text-gray-600 mb-2">Selecciona una consulta</h3>
                        <p class="text-gray-500">Usa el menú superior para ver los diferentes análisis disponibles</p>
                    </div>

                    <!-- Cliente con mayor gasto -->
                    <div v-if="consultaActiva === 'clienteMayorGasto'">
                        <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4 border border-blue-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">👤</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta 1</h2>
                                    <p class="text-gray-600 text-sm">Cliente con mayor gasto total</p>
                                </div>
                            </div>
                            
                            <div v-if="clienteMayorGasto" class="bg-white rounded-lg p-4 border border-blue-200">
                                <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    <div class="text-center p-3 bg-blue-50 rounded-lg">
                                        <div class="text-sm font-medium text-gray-600">Nombre del Cliente</div>
                                        <div class="text-lg font-bold text-blue-600">{{ clienteMayorGasto.nombre }}</div>
                                    </div>
                                    <div class="text-center p-3 bg-green-50 rounded-lg">
                                        <div class="text-sm font-medium text-gray-600">Total Gastado</div>
                                        <div class="text-lg font-bold text-green-600">${{ clienteMayorGasto.totalGastado }}</div>
                                    </div>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>

                    <!-- Producto más vendido -->
                    <div v-if="consultaActiva === 'productoMasVendido'">
                        <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4 border border-green-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-green-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">🏆</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta 2</h2>
                                    <p class="text-gray-600 text-sm">Productos más vendidos</p>
                                </div>
                            </div>
                            
                            <div v-if="productoMasVendido.length" class="bg-white rounded-lg p-4 border border-green-200">
                                <div class="overflow-x-auto">
                                    <table class="w-full text-sm">
                                        <thead>
                                            <tr class="bg-green-50">
                                                <th class="text-left p-3 font-bold text-green-800">Producto</th>
                                                <th class="text-left p-3 font-bold text-green-800">Categoría</th>
                                                <th class="text-left p-3 font-bold text-green-800">Total Pedidos</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(producto, index) in productoMasVendido" :key="index" class="border-t border-green-100">
                                                <td class="p-3 font-medium">{{ producto.nombreProducto }}</td>
                                                <td class="p-3">
                                                    <span class="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-medium">
                                                        {{ producto.categoria }}
                                                    </span>
                                                </td>
                                                <td class="p-3 font-bold text-green-600">{{ producto.totalPedidos }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>

                    <!-- Empresas con entregas fallidas -->
                    <div v-if="consultaActiva === 'empresasConMasEntregasFallidas'">
                        <div class="bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl p-4 border border-yellow-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-yellow-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">⚠️</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta 3</h2>
                                    <p class="text-gray-600 text-sm">Empresas con más entregas fallidas</p>
                                </div>
                            </div>
                            
                            <div v-if="empresasConMasEntregasFallidas" class="bg-white rounded-lg p-4 border border-yellow-200">
                                <div class="overflow-x-auto">
                                    <table class="w-full text-sm">
                                        <thead>
                                            <tr class="bg-yellow-50">
                                                <th class="text-left p-3 font-bold text-yellow-800">Empresa</th>
                                                <th class="text-left p-3 font-bold text-yellow-800">Entregas Fallidas</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(empresa, index) in empresasConMasEntregasFallidas" :key="index" class="border-t border-yellow-100">
                                                <td class="p-3 font-medium">{{ empresa.nombreEmpresa }}</td>
                                                <td class="p-3 font-bold text-red-600">{{ empresa.totalEntregasFallidas }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-yellow-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>

                    <!-- Tiempo promedio repartidor -->
                    <div v-if="consultaActiva === 'tiempoPromedioRepartidor'">
                        <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-4 border border-purple-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-purple-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">⏱️</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta 4</h2>
                                    <p class="text-gray-600 text-sm">Tiempo promedio de entrega por repartidor</p>
                                </div>
                            </div>
                            
                            <div v-if="tiempoPromedioRepartidor.length" class="bg-white rounded-lg p-4 border border-purple-200">
                                <div class="overflow-x-auto">
                                    <table class="w-full text-sm">
                                        <thead>
                                            <tr class="bg-purple-50">
                                                <th class="text-left p-3 font-bold text-purple-800">RUT</th>
                                                <th class="text-left p-3 font-bold text-purple-800">Nombre</th>
                                                <th class="text-left p-3 font-bold text-purple-800">Tiempo Promedio (min)</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(repartidor, index) in tiempoPromedioRepartidor" :key="index" class="border-t border-purple-100">
                                                <td class="p-3 font-mono text-xs bg-gray-50">{{ repartidor.rut }}</td>
                                                <td class="p-3 font-medium">{{ repartidor.nombre }}</td>
                                                <td class="p-3 font-bold text-purple-600">{{ repartidor.tiempoPromedio }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>

                    <!-- Repartidor mejor rendimiento -->
                    <div v-if="consultaActiva === 'repartidorMejorRendimiento'">
                        <div class="bg-gradient-to-br from-indigo-50 to-indigo-100 rounded-xl p-4 border border-indigo-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-indigo-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">🌟</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta 5</h2>
                                    <p class="text-gray-600 text-sm">Repartidores con mejor desempeño</p>
                                </div>
                            </div>
                            
                            <div v-if="repartidorMejorRendimiento.length" class="bg-white rounded-lg p-4 border border-indigo-200">
                                <div class="overflow-x-auto">
                                    <table class="w-full text-sm">
                                        <thead>
                                            <tr class="bg-indigo-50">
                                                <th class="text-left p-3 font-bold text-indigo-800">Nombre</th>
                                                <th class="text-left p-3 font-bold text-indigo-800">Puntuación</th>
                                                <th class="text-left p-3 font-bold text-indigo-800">Total Entregas</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(repartidor, index) in repartidorMejorRendimiento" :key="index" class="border-t border-indigo-100">
                                                <td class="p-3 font-medium">{{ repartidor.nombre }}</td>
                                                <td class="p-3">
                                                    <span class="bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full text-xs font-bold">
                                                        ⭐ {{ repartidor.puntuacion }}
                                                    </span>
                                                </td>
                                                <td class="p-3 font-bold text-indigo-600">{{ repartidor.entregas }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>

                    <!-- Método de pago más utilizado -->
                    <div v-if="consultaActiva === 'metodoPagoFrecuente'">
                        <div class="bg-gradient-to-br from-teal-50 to-teal-100 rounded-xl p-4 border border-teal-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-teal-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">💳</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta 6</h2>
                                    <p class="text-gray-600 text-sm">Método de pago más utilizado</p>
                                </div>
                            </div>
                            
                            <div v-if="metodoPagoFrecuente" class="bg-white rounded-lg p-4 border border-teal-200">
                                <div class="text-center">
                                    <div class="mb-4">
                                        <div class="text-2xl font-bold text-teal-600 mb-1">{{ metodoPagoFrecuente.nombre }}</div>
                                        <div class="text-sm text-gray-600">Método más popular</div>
                                    </div>
                                    <div class="bg-teal-50 rounded-xl p-4 inline-block">
                                        <div class="text-xl font-bold text-teal-800">{{ metodoPagoFrecuente.usos }}</div>
                                        <div class="text-teal-600 font-medium text-sm">veces utilizado</div>
                                    </div>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>

                    <!-- Ranking de devoluciones -->
                    <div v-if="consultaActiva === 'rankingDevolucionesCancelaciones'">
                        <div class="bg-gradient-to-br from-red-50 to-red-100 rounded-xl p-4 border border-red-200">
                            <div class="flex items-center mb-4">
                                <div class="w-12 h-12 bg-red-500 rounded-full flex items-center justify-center mr-3">
                                    <span class="text-white text-lg">📊</span>
                                </div>
                                <div>
                                    <h2 class="text-xl font-bold text-gray-800">Consulta Bonus</h2>
                                    <p class="text-gray-600 text-sm">Productos con más devoluciones y cancelaciones</p>
                                </div>
                            </div>
                            
                            <div v-if="rankingDevolucionesCancelaciones.length" class="bg-white rounded-lg p-4 border border-red-200">
                                <div class="overflow-x-auto">
                                    <table class="w-full text-sm">
                                        <thead>
                                            <tr class="bg-red-50">
                                                <th class="text-left p-3 font-bold text-red-800">Producto</th>
                                                <th class="text-left p-3 font-bold text-red-800">Categoría</th>
                                                <th class="text-left p-3 font-bold text-red-800">Devoluciones</th>
                                                <th class="text-left p-3 font-bold text-red-800">Cancelaciones</th>
                                                <th class="text-left p-3 font-bold text-red-800">Total Problemas</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(producto, index) in rankingDevolucionesCancelaciones" :key="index" class="border-t border-red-100">
                                                <td class="p-3 font-medium">{{ producto.producto }}</td>
                                                <td class="p-3">
                                                    <span class="bg-gray-100 text-gray-800 px-2 py-1 rounded-full text-xs">
                                                        {{ producto.categoria }}
                                                    </span>
                                                </td>
                                                <td class="p-3 font-bold text-orange-600">{{ producto.devoluciones }}</td>
                                                <td class="p-3 font-bold text-red-600">{{ producto.cancelaciones }}</td>
                                                <td class="p-3 font-bold text-red-800">{{ producto.problemas }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <div v-else class="text-center py-6">
                                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-red-500 mx-auto"></div>
                                <p class="text-gray-600 mt-2 text-sm">Cargando datos...</p>
                            </div>
                        </div>
                    </div>
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
            consultaActiva: null,
            clienteMayorGasto: null,
            productoMasVendido: [],
            empresasConMasEntregasFallidas: null,
            tiempoPromedioRepartidor: [],
            repartidorMejorRendimiento: [],
            metodoPagoFrecuente: null,
            rankingDevolucionesCancelaciones: [],
            
            // Configuración del menú
            consultasDisponibles: {
                clienteMayorGasto: {
                    titulo: 'Consulta 1'
                },
                productoMasVendido: {
                    titulo: 'Consulta 2'
                },
                empresasConMasEntregasFallidas: {
                    titulo: 'Consulta 3'
                },
                tiempoPromedioRepartidor: {
                    titulo: 'Consulta 4'
                },
                repartidorMejorRendimiento: {
                    titulo: 'Consulta 5'
                },
                metodoPagoFrecuente: {
                    titulo: 'Consulta 6'
                },
                rankingDevolucionesCancelaciones: {
                    titulo: 'Consulta Bonus'
                }
            }
        };
    },
    methods: {
        async mostrarConsulta(tipoConsulta) {
            // Si ya está activa la misma consulta, la ocultamos
            if (this.consultaActiva === tipoConsulta) {
                this.consultaActiva = null;
                return;
            }
            
            // Activamos la nueva consulta
            this.consultaActiva = tipoConsulta;
            
            // Llamamos al método correspondiente
            switch(tipoConsulta) {
                case 'clienteMayorGasto':
                    await this.fetchClienteMayorGasto();
                    break;
                case 'productoMasVendido':
                    await this.fetchProductoMasVendido();
                    break;
                case 'empresasConMasEntregasFallidas':
                    await this.fetchEmpresasConMasEntregasFallidas();
                    break;
                case 'tiempoPromedioRepartidor':
                    await this.fetchTiempoPromedioReapartidor();
                    break;
                case 'repartidorMejorRendimiento':
                    await this.fetchRepartidorMejorRendimiento();
                    break;
                case 'metodoPagoFrecuente':
                    await this.fetchMetodoPagoFrecuente();
                    break;
                case 'rankingDevolucionesCancelaciones':
                    await this.fetchRankingDevolucionesOCancelaciones();
                    break;
            }
        },

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
                this.productoMasVendido = res.data;
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

        async fetchTiempoPromedioReapartidor() {
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

        async fetchRankingDevolucionesOCancelaciones() {
            try {
                const response = await apiClient.get('/api/v1/sentenciassql/rankingDevolucionesOCancelaciones');
                this.rankingDevolucionesCancelaciones = response.data;
            } catch (error) {
                console.error("Error en fetchRankingDevolucionesOCancelaciones:", error);
                if (error.response && error.response.status === 404) {
                    alert("La API de ranking de devoluciones no está disponible o el endpoint no existe");
                } else {
                    alert("Error al obtener el ranking de devoluciones: " + error.message);
                }
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
</style>