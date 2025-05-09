<template>
  <div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">Gestión de Pedidos</h1>
    
    <!-- Navegación entre funcionalidades -->
    <div class="flex flex-wrap gap-2 mb-4">
      <BaseButton 
        :class="{ 'bg-red-700': activeTab === 'register' }"
        @click="activeTab = 'register'"
      >
        Registrar Pedido
      </BaseButton>
      <BaseButton 
        :class="{ 'bg-red-700': activeTab === 'status' }"
        @click="activeTab = 'status'"
      >
        Cambiar Estado
      </BaseButton>
      <BaseButton 
        :class="{ 'bg-red-700': activeTab === 'stock' }"
        @click="activeTab = 'stock'"
      >
        Gestionar Stock
      </BaseButton>
    </div>

    <!-- Contenido según la pestaña activa -->
    <div v-if="activeTab === 'register'" class="bg-white p-4 rounded-lg shadow-md">
      <h2 class="text-lg font-semibold mb-2">Registrar Nuevo Pedido</h2>
      
      <!-- Formulario simplificado de registro de pedido -->
      <form @submit.prevent="registrarPedido">
        <!-- Datos de pedido -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-2 mb-2">
          <div>
            <label class="block text-sm mb-1">RUT Cliente:</label>
            <input 
              v-model="registroPedido.rutCliente" 
              type="text" 
              class="w-full px-2 py-1 border rounded-md text-sm"
              required
              placeholder="Ej: 12345678-9"
            />
          </div>

          <div>
            <label class="block text-sm mb-1">Prioridad:</label>
            <select
              v-model="registroPedido.prioridadPedido"
              class="w-full px-2 py-1 border rounded-md text-sm"
              required
            >
              <option value="Baja">Baja</option>
              <option value="Media">Media</option>
              <option value="Alta">Alta</option>
              <option value="Urgente">Urgente</option>
            </select>
          </div>
        </div>

        <!-- Datos del producto -->
        <div class="mb-3 mt-2">
          <div class="flex flex-wrap items-end gap-2">
            <div class="w-full md:w-1/2">
              <label class="block text-sm mb-1">Producto:</label>
              <select
                v-model="registroPedido.idProducto"
                class="w-full px-2 py-1 border rounded-md text-sm"
                required
                @change="calcularTotal"
              >
                <option value="" disabled selected>Seleccione un producto</option>
                <option v-for="producto in productos" :key="producto.idProducto" :value="producto.idProducto">
                  {{ producto.nombre }} - ${{ producto.precio }} ({{ producto.stock }} disponibles)
                </option>
              </select>
            </div>
            <div class="w-20">
              <label class="block text-sm mb-1">Cantidad:</label>
              <input 
                v-model.number="registroPedido.cantidad" 
                type="number" 
                min="1"
                class="w-full px-2 py-1 border rounded-md text-sm"
                @change="calcularTotal"
              />
            </div>
            <div class="ml-auto text-right">
              <p class="text-sm font-bold">Total: ${{ precioTotal || 0 }}</p>
            </div>
          </div>
        </div>

        <div class="mt-4">
          <BaseButtonGreen type="submit" class="w-full md:w-auto">
            Registrar Pedido
          </BaseButtonGreen>
        </div>
      </form>
    </div>

    <!-- Las demás pestañas permanecen sin cambios -->
    <div v-if="activeTab === 'status'" class="bg-white p-4 rounded-lg shadow-md">
      <h2 class="text-lg font-semibold mb-2">Cambiar Estado de Pedido</h2>
      
      <div class="flex flex-col md:flex-row gap-4 mb-4">
        <div class="w-full md:w-1/2">
          <label class="block text-sm mb-1">ID del Pedido:</label>
          <input 
            v-model="cambioEstado.idPedido" 
            type="number" 
            class="w-full px-2 py-1 border rounded-md"
          />
        </div>

        <div class="w-full md:w-1/2">
          <label class="block text-sm mb-1">Nuevo Estado:</label>
          <select 
            v-model="cambioEstado.nuevoEstado" 
            class="w-full px-2 py-1 border rounded-md"
          >
            <option value="Entrega fallida">Entrega fallida</option>
            <option value="Entregado">Entregado</option>
            <option value="Devolución">Devolución</option>
          </select>
        </div>
      </div>

      <BaseButtonGreen @click="cambiarEstadoPedido" :disabled="!cambioEstado.idPedido">
        Actualizar Estado
      </BaseButtonGreen>
    </div>

    <div v-if="activeTab === 'stock'" class="bg-white p-4 rounded-lg shadow-md">
      <h2 class="text-lg font-semibold mb-2">Confirmar Pedido y Actualizar Stock</h2>
      
      <div class="mb-4">
        <label class="block text-sm mb-1">ID del Pedido a confirmar:</label>
        <input 
          v-model="idPedidoConfirmar" 
          type="number" 
          class="w-full px-2 py-1 border rounded-md"
        />
      </div>

      <BaseButtonGreen @click="confirmarPedido" :disabled="!idPedidoConfirmar">
        Confirmar Pedido y Actualizar Stock
      </BaseButtonGreen>
    </div>

    <!-- Lista de Pedidos -->
    <div v-if="activeTab === 'list'" class="bg-white p-6 rounded-lg shadow-md">
      <h2 class="text-xl font-semibold mb-4">Lista de Pedidos</h2>
      
      <div v-if="loading" class="text-center py-4">
        <p>Cargando pedidos...</p>
      </div>
      
      <div v-else-if="pedidos.length === 0" class="text-center py-4">
        <p>No hay pedidos registrados</p>
      </div>
      
      <div v-else class="overflow-x-auto">
        <table class="w-full border-collapse border border-gray-300">
          <thead>
            <tr class="bg-gray-100">
              <th class="border border-gray-300 px-4 py-2">ID</th>
              <th class="border border-gray-300 px-4 py-2">Cliente</th>
              <th class="border border-gray-300 px-4 py-2">Empresa</th>
              <th class="border border-gray-300 px-4 py-2">Dirección</th>
              <th class="border border-gray-300 px-4 py-2">Estado</th>
              <th class="border border-gray-300 px-4 py-2">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pedido in pedidos" :key="pedido.idPedido" class="hover:bg-gray-50">
              <td class="border border-gray-300 px-4 py-2">{{ pedido.idPedido }}</td>
              <td class="border border-gray-300 px-4 py-2">{{ pedido.idCliente }}</td>
              <td class="border border-gray-300 px-4 py-2">{{ pedido.idEmpresa }}</td>
              <td class="border border-gray-300 px-4 py-2">{{ pedido.direccionEntrega }}</td>
              <td class="border border-gray-300 px-4 py-2">{{ pedido.estado }}</td>
              <td class="border border-gray-300 px-4 py-2 flex justify-around">
                <button 
                  @click="cambioEstado.idPedido = pedido.idPedido; activeTab = 'status'" 
                  class="text-blue-500 hover:text-blue-700"
                >
                  Cambiar Estado
                </button>
                <button 
                  @click="idPedidoConfirmar = pedido.idPedido; activeTab = 'stock'" 
                  class="text-green-500 hover:text-green-700"
                >
                  Confirmar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Mensajes de estado -->
    <div v-if="mensaje" class="mt-4 p-2 rounded text-sm" :class="mensajeEstilo">
      {{ mensaje }}
    </div>
  </div>
</template>

<script>
import apiClient from '../service/http-common';
import BaseButton from '../components/BaseButton.vue';
import BaseButtonGreen from '../components/BaseButtonGreen.vue';

export default {
  name: 'CrearPedidoPage',
  components: {
    BaseButton,
    BaseButtonGreen
  },
  data() {
    return {
      activeTab: 'register',
      // Nuevo objeto simplificado para registro de pedidos
      registroPedido: {
        prioridadPedido: 'Media',
        rutCliente: '',
        cantidad: 1,
        idProducto: ''
      },
      // Mantenemos los objetos originales para otras funcionalidades
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
      precioTotal: 0,
      loading: false
    };
  },
  computed: {
    fechaHoy() {
      return new Date().toISOString().split('T')[0];
    }
  },
  created() {
    this.fetchProductos();
    this.fetchEmpresas();
    this.fetchRepartidores();
  },
  methods: {
    async fetchProductos() {
      try {
        const response = await apiClient.get('/api/v1/productos');
        this.productos = response.data;
      } catch (error) {
        this.mostrarError('Error al cargar productos: ' + (error.response?.data || error.message));
      }
    },
    async fetchEmpresas() {
      try {
        const response = await apiClient.get('/api/v1/empresas');
        this.empresas = response.data;
      } catch (error) {
        this.mostrarError('Error al cargar empresas: ' + (error.response?.data || error.message));
      }
    },
    async fetchRepartidores() {
      try {
        const response = await apiClient.get('/api/v1/repartidores');
        this.repartidores = response.data;
      } catch (error) {
        this.mostrarError('Error al cargar repartidores: ' + (error.response?.data || error.message));
      }
    },
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
    async registrarPedido() {
      try {
        if (!this.registroPedido.idProducto || this.registroPedido.cantidad <= 0) {
          this.mostrarError('Debe seleccionar un producto y especificar una cantidad válida');
          return;
        }
        
        // Llamada simplificada a la API con solo los campos necesarios
        const response = await apiClient.post('/api/v1/pedidos/registrar', this.registroPedido);
        
        this.mostrarExito('Pedido registrado correctamente');
        
        // Restablecer formulario
        this.registroPedido = {
          prioridadPedido: 'Media',
          rutCliente: '',
          cantidad: 1,
          idProducto: ''
        };
        this.precioTotal = 0;
        
      } catch (error) {
        this.mostrarError('Error al registrar el pedido: ' + (error.response?.data || error.message));
      }
    },
    async cambiarEstadoPedido() {
      if (!this.cambioEstado.idPedido) {
        this.mostrarError('Debe ingresar el ID del pedido');
        return;
      }
      
      try {
        await apiClient.put(
          `/api/v1/pedidos/${this.cambioEstado.idPedido}/${this.cambioEstado.nuevoEstado}/estado`
        );
        
        this.mostrarExito(`Estado del pedido ${this.cambioEstado.idPedido} actualizado a ${this.cambioEstado.nuevoEstado}`);
        this.cambioEstado.idPedido = null;
        
        // Actualizar la lista si estamos viendo la pestaña de listado
        if (this.activeTab === 'list') {
          this.fetchPedidos();
        }
      } catch (error) {
        this.mostrarError('Error al actualizar el estado: ' + (error.response?.data || error.message));
      }
    },
    async confirmarPedido() {
      if (!this.idPedidoConfirmar) {
        this.mostrarError('Debe ingresar el ID del pedido a confirmar');
        return;
      }
      
      try {
        const response = await apiClient.put(`/api/v1/pedidos/${this.idPedidoConfirmar}/confirmar`);
        
        this.mostrarExito(`Pedido ${this.idPedidoConfirmar} confirmado y stock actualizado`);
        this.idPedidoConfirmar = null;
        
        // Recargar productos para reflejar el cambio en el stock
        this.fetchProductos();
      } catch (error) {
        this.mostrarError('Error al confirmar el pedido: ' + (error.response?.data || error.message));
      }
    },
    mostrarExito(mensaje) {
      this.mensaje = mensaje;
      this.mensajeEstilo = 'bg-green-100 text-green-800';
      setTimeout(() => {
        this.mensaje = '';
      }, 5000);
    },
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
/* Estilos adicionales específicos para esta página */
</style>