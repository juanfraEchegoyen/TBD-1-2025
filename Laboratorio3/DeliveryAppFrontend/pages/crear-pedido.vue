<template>
  <div class="min-h-screen max-h-screen overflow-hidden bg-gradient-to-br from-slate-50 to-slate-100 flex flex-col">
    <div class="flex-shrink-0 p-6 text-center">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Gestión de Pedidos</h1>
      <p class="text-gray-600">Sistema integral de gestión y seguimiento de pedidos</p>
      <div class="w-24 h-1 bg-gradient-to-r from-green-500 to-green-600 mx-auto mt-3 rounded-full"></div>
    </div>

    <div class="flex-shrink-0 px-6 pb-4">
      <div class="max-w-7xl mx-auto">
        <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-3">
          <div class="flex flex-wrap gap-2 justify-center">
            <button 
              @click="activeTab = 'register'"
              :class="[
                'px-4 py-2 rounded-lg font-medium text-sm transition-all duration-200',
                activeTab === 'register' 
                  ? 'bg-green-500 text-white shadow-lg' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              📝 Registrar Pedido
            </button>
            
            <button 
              @click="activeTab = 'stock'"
              :class="[
                'px-4 py-2 rounded-lg font-medium text-sm transition-all duration-200',
                activeTab === 'stock' 
                  ? 'bg-blue-500 text-white shadow-lg' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              📦 Gestionar Stock
            </button>

            <button 
              @click="activeTab = 'status'"
              :class="[
                'px-4 py-2 rounded-lg font-medium text-sm transition-all duration-200',
                activeTab === 'status' 
                  ? 'bg-orange-500 text-white shadow-lg' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              🔄 Cambiar Estado
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto px-6 pb-6">
      <div class="max-w-7xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl border border-gray-200">
          <div class="p-6">

            <div v-if="activeTab === 'register'">
              <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4 border border-green-200">
                <div class="flex items-center mb-6">
                  <div class="w-12 h-12 bg-green-500 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">📝</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Registrar Nuevo Pedido</h2>
                    <p class="text-gray-600 text-sm">Crea un nuevo pedido en el sistema</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-6 border border-green-200">
                  <form @submit.prevent="registrarPedido">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Cliente (RUT)</label>
                        <input 
                          v-model="registroPedido.rutCliente" 
                          @input="onClienteChange"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200" 
                          required 
                          placeholder="Ingrese el RUT del cliente" 
                        />
                      </div>

                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Prioridad</label>
                        <select
                          v-model="registroPedido.prioridadPedido"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
                          required
                        >
                          <option value="Baja">🟢 Baja</option>
                          <option value="Media">🟡 Media</option>
                          <option value="Alta">🟠 Alta</option>
                          <option value="Urgente">🔴 Urgente</option>
                        </select>
                      </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                      <div class="md:col-span-2">
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Producto</label>
                        <select
                          v-model="registroPedido.idProducto"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
                          required
                          @change="onProductoChange"
                        >
                          <option value="" disabled selected>Seleccione un producto</option>
                          <option v-for="producto in productos" :key="producto.idProducto" :value="producto.idProducto">
                            {{ producto.nombre }} - ${{ producto.precio }} ({{ producto.stock }} disponibles)
                          </option>
                        </select>
                      </div>
                      
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Cantidad</label>
                        <input 
                          v-model.number="registroPedido.cantidad" 
                          type="number" 
                          min="1"
                          :max="productoSeleccionado ? productoSeleccionado.stock : 1"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
                          @change="calcularTotal"
                        />
                      </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Medio de Pago</label>
                        <select
                          v-model="registroPedido.nombreMedioPago"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
                          required
                        >
                          <option value="" disabled selected>Seleccione un medio de pago</option>
                          <option value="Efectivo">💵 Efectivo</option>
                          <option value="Crédito">💳 Tarjeta de Crédito</option>
                          <option value="Débito">💳 Tarjeta de Débito</option>
                          <option value="Transferencia">🏦 Transferencia</option>
                        </select>
                      </div>

                      <div class="flex items-end">
                        <div class="bg-green-50 rounded-lg p-4 border border-green-200 w-full">
                          <div class="text-sm font-medium text-gray-600">Total a Pagar</div>
                          <div class="text-2xl font-bold text-green-600">${{ precioTotal || 0 }}</div>
                        </div>
                      </div>
                    </div>

                    <div v-if="mostrarMapa" class="mb-6">
                      <div class="bg-blue-50 rounded-lg p-4 border border-blue-200">
                        <h4 class="font-semibold text-blue-800 mb-3 flex items-center">
                          <span class="mr-2">🗺️</span>
                          Visualización de Ruta
                        </h4>
                        <div class="bg-white rounded-lg border border-blue-200 overflow-hidden">
                          <RouteMap 
                            :rut-cliente="registroPedido.rutCliente"
                            :nombre-producto="productoSeleccionado?.nombre || ''"
                            :should-calculate-route="shouldCalculateRoute"
                            ref="routeMapRef"
                          />
                        </div>
                      </div>
                    </div>

                    <button
                      type="submit"
                      class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 transform hover:scale-105 flex items-center justify-center"
                    >
                      <span class="mr-2">✨</span>
                      Registrar Pedido
                    </button>
                  </form>
                </div>
              </div>
            </div>

            <div v-if="activeTab === 'stock'">
              <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4 border border-blue-200">
                <div class="flex items-center mb-6">
                  <div class="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">📦</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Actualizar Stock</h2>
                    <p class="text-gray-600 text-sm">Confirma pedidos y actualiza el inventario</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-6 border border-blue-200">
                  <div class="mb-6">
                    <label class="block text-sm font-semibold text-gray-700 mb-2">Selecciona un pedido</label>
                    <select 
                      v-model="idPedidoConfirmar" 
                      class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                    >
                      <option value="" disabled selected>Selecciona un pedido</option>
                      <option v-for="pedido in pedidosPendientes" :key="pedido.idPedido" :value="pedido.idPedido">
                        🆔 {{ pedido.idPedido }} - 📋 {{ pedido.estadoEntrega || pedido.estado }} - 👤 {{ pedido.rutCliente || pedido.idCliente }}
                      </option>
                    </select>
                  </div>
                  <button
                    @click="confirmarPedido"
                    :disabled="!idPedidoConfirmar"
                    class="w-full bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 transform hover:scale-105 flex items-center justify-center"
                  >
                    <span class="mr-2">📦</span>
                    Actualizar Stock
                  </button>
                </div>
              </div>
            </div>

            <div v-if="activeTab === 'status'">
              <div class="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl p-4 border border-orange-200">
                <div class="flex items-center mb-6">
                  <div class="w-12 h-12 bg-orange-500 rounded-full flex items-center justify-center mr-3">
                    <span class="text-white text-lg">🔄</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-bold text-gray-800">Cambiar Estado de Pedido</h2>
                    <p class="text-gray-600 text-sm">Actualiza el estado de los pedidos en proceso</p>
                  </div>
                </div>
                
                <div class="bg-white rounded-lg p-6 border border-orange-200">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-2">Selecciona un Pedido</label>
                      <select 
                        v-model="cambioEstado.idPedido" 
                        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all duration-200"
                      >
                        <option value="" disabled selected>Selecciona un pedido</option>
                        <option v-for="pedido in pedidosPendientes" :key="pedido.idPedido" :value="pedido.idPedido">
                          🆔 {{ pedido.idPedido }} - 📋 {{ pedido.estadoEntrega || pedido.estado }} - 👤 {{ pedido.rutCliente || pedido.idCliente }}
                        </option>
                      </select>
                    </div>
                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-2">Nuevo Estado</label>
                      <select 
                        v-model="cambioEstado.nuevoEstado" 
                        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all duration-200"
                      >
                        <option value="Entrega fallida">❌ Entrega fallida</option>
                        <option value="Entregado">✅ Entregado</option>
                        <option value="Devolución">↩️ Devolución</option>
                        <option value="Cancelada">🚫 Cancelada</option>
                      </select>
                    </div>
                  </div>
                  <button
                    @click="cambiarEstadoPedido" 
                    :disabled="!cambioEstado.idPedido"
                    class="w-full bg-orange-500 hover:bg-orange-600 disabled:bg-gray-400 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 transform hover:scale-105 flex items-center justify-center"
                  >
                    <span class="mr-2">🔄</span>
                    Actualizar Estado
                  </button>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>

    <div v-if="mensaje" class="fixed bottom-4 right-4 max-w-md z-50">
      <div :class="[
        'p-4 rounded-lg shadow-lg border',
        mensajeEstilo === 'bg-green-100 text-green-800' 
          ? 'bg-green-50 text-green-800 border-green-200' 
          : 'bg-red-50 text-red-800 border-red-200'
      ]">
        <div class="flex items-center">
          <span class="mr-2">
            {{ mensajeEstilo === 'bg-green-100 text-green-800' ? '✅' : '❌' }}
          </span>
          {{ mensaje }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '../service/http-common';
import BaseButton from '../components/BaseButton.vue';
import BaseButtonGreen from '../components/BaseButtonGreen.vue';
import RouteMap from '../components/RouteMap.vue';

export default {
  name: 'CrearPedidoPage',
  components: {
    BaseButton,
    BaseButtonGreen,
    RouteMap
  },
  data() {
    return {
      activeTab: 'register',
      registroPedido: {
        prioridadPedido: 'Media',
        rutCliente: '',
        cantidad: 1,
        idProducto: '',
        nombreMedioPago: ''
      },
      cambioEstado: {
        idPedido: null,
        nuevoEstado: 'Entregado'
      },
      idPedidoConfirmar: null,
      mensaje: '',
      mensajeEstilo: 'bg-green-100 text-green-800',
      productos: [],
      empresas: [],
      repartidores: [],
      pedidos: [],
      clientes: [],
      precioTotal: 0,
      loading: false,
      mostrarMapa: false,
      shouldCalculateRoute: false
    };
  },
  computed: {
    fechaHoy() {
      return new Date().toISOString().split('T')[0];
    },
    productoSeleccionado() {
      return this.productos.find(p => p.idProducto === this.registroPedido.idProducto);
    },
    pedidosPendientes() {
      if (!Array.isArray(this.pedidos)) {
        return [];
      }
      return this.pedidos.filter(p => (p.estadoEntrega || p.estado) === 'Pendiente');
    }
  },
  created() {
    this.fetchClientes();
    this.fetchProductos();
    this.fetchEmpresas();
    this.fetchRepartidores();
    this.fetchPedidos();
  },
  methods: {
    /**
     * Obtiene la lista de clientes desde la API y maneja errores
     */
    async fetchClientes() {
      try {
        const response = await apiClient.get('/api/v1/clientes');
        this.clientes = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.clientes = [];
        this.mostrarError('Error al cargar clientes: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Obtiene la lista de productos disponibles desde la API
     */
    async fetchProductos() {
      try {
        const response = await apiClient.get('/api/v1/productos');
        this.productos = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.productos = [];
        this.mostrarError('Error al cargar productos: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Obtiene la lista de empresas registradas desde la API
     */
    async fetchEmpresas() {
      try {
        const response = await apiClient.get('/api/v1/empresas');
        this.empresas = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.empresas = [];
        this.mostrarError('Error al cargar empresas: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Obtiene la lista de repartidores disponibles desde la API
     */
    async fetchRepartidores() {
      try {
        const response = await apiClient.get('/api/v1/repartidores');
        this.repartidores = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.repartidores = [];
        this.mostrarError('Error al cargar repartidores: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Obtiene la lista de pedidos existentes desde la API
     */
    async fetchPedidos() {
      this.loading = true;
      try {
        const response = await apiClient.get('/api/v1/pedidos');
        this.pedidos = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.pedidos = [];
        this.mostrarError('Error al cargar pedidos: ' + (error.response?.data || error.message));
      } finally {
        this.loading = false;
      }
    },

    /**
     * Calcula el precio total del pedido basado en el producto y cantidad seleccionados
     */
    calcularTotal() {
      if (this.registroPedido.idProducto && this.registroPedido.cantidad) {
        const producto = this.productos.find(p => p.idProducto === this.registroPedido.idProducto);
        if (producto) {
          this.precioTotal = producto.precio * this.registroPedido.cantidad;
        }
      } else {
        this.precioTotal = 0;
      }
    },

    /**
     * Maneja el cambio de cliente y actualiza la visualización del mapa
     */
    onClienteChange() {
      this.checkAndShowRoute();
    },

    /**
     * Maneja el cambio de producto, recalcula el total y actualiza el mapa
     */
    onProductoChange() {
      this.calcularTotal();
      this.checkAndShowRoute();
    },

    /**
     * Verifica si debe mostrar el mapa de ruta basado en datos del pedido
     */
    checkAndShowRoute() {
      if (this.registroPedido.rutCliente && this.registroPedido.idProducto) {
        this.mostrarMapa = true;
        this.shouldCalculateRoute = true;
        this.$nextTick(() => {
          this.shouldCalculateRoute = false;
          this.$nextTick(() => {
            this.shouldCalculateRoute = true;
          });
        });
      } else {
        this.mostrarMapa = false;
        this.shouldCalculateRoute = false;
      }
    },

    /**
     * Registra un nuevo pedido en el sistema enviando los datos al backend
     */
    async registrarPedido() {
      try {
        const producto = this.productoSeleccionado;
        if (!producto) {
          this.mostrarError('Debe seleccionar un producto válido');
          return;
        }

        console.log(this.registroPedido);
        const response = await apiClient.post('/api/v1/pedidos/registrar', this.registroPedido);

        if (response.status === 200 || response.status === 201) {
          this.mostrarExito('Pedido registrado');
        
          this.registroPedido = {
            prioridadPedido: 'Media',
            rutCliente: '',
            cantidad: 1,
            idProducto: '',
            nombreMedioPago: ''
          };
          this.precioTotal = 0;
          this.mostrarMapa = false;
          this.shouldCalculateRoute = false;
          
          this.fetchPedidos();
        } else {
          this.mostrarError('Error inesperado al registrar el pedido');
        }
      } catch (error) {
        this.mostrarError('Error al registrar el pedido: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Cambia el estado de un pedido existente a través de la API
     */
    async cambiarEstadoPedido() {
      if (!this.cambioEstado.idPedido) {
        this.mostrarError('Debe ingresar el ID del pedido');
        return;
      }
      
      try {
        await apiClient.put(`/api/v1/pedidos/${this.cambioEstado.idPedido}/estado`, null, {
          params: { nuevoEstado: this.cambioEstado.nuevoEstado },
          headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}`}
        });
        
        this.mostrarExito(`Estado del pedido ${this.cambioEstado.idPedido} actualizado a ${this.cambioEstado.nuevoEstado}`);
        this.cambioEstado.idPedido = null;
        
        this.fetchPedidos();
      } catch (error) {
        this.mostrarError('Error al actualizar el estado: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Confirma un pedido y actualiza el stock del producto correspondiente
     */
    async confirmarPedido() {
      if (!this.idPedidoConfirmar) {
        this.mostrarError('Debe ingresar el ID del pedido a confirmar');
        return;
      }
      
      try {
        const response = await apiClient.put(`/api/v1/pedidos/${this.idPedidoConfirmar}/confirmar`);
        console.log(response.data);
        this.mostrarExito(`Pedido ${this.idPedidoConfirmar} confirmado y stock actualizado`);
        this.idPedidoConfirmar = null;
        
        this.fetchProductos();
        this.fetchPedidos();
      } catch (error) {
        this.mostrarError('Error al confirmar el pedido: ' + (error.response?.data || error.message));
      }
    },

    /**
     * Muestra un mensaje de éxito al usuario
     * @param {string} mensaje - Mensaje a mostrar
     */
    mostrarExito(mensaje) {
      this.mensaje = mensaje;
      this.mensajeEstilo = 'bg-green-100 text-green-800';
      setTimeout(() => {
        this.mensaje = '';
      }, 5000);
    },

    /**
     * Muestra un mensaje de error al usuario
     * @param {string} mensaje - Mensaje de error a mostrar
     */
    mostrarError(mensaje) {
      this.mensaje = mensaje;
      this.mensajeEstilo = 'bg-red-100 text-red-800';
      setTimeout(() => {
        this.mensaje = '';
      }, 5000);
    }
  }
};
</script>

<style scoped>
h1 {
  color: #333333;
}

input:focus, select:focus {
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
}

.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>