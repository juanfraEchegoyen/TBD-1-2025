<template>
  <div class="container">
    <h1 class="titulo">Gestión de Pedidos</h1>
    
    <!-- Navegación entre funcionalidades -->
    <div class="tabs">
      <BaseButton 
        :class="{ 'tab-activa': activeTab === 'register' }"
        @click="activeTab = 'register'"
      >
        Registrar Pedido
      </BaseButton>
      
      <BaseButton 
        :class="{ 'tab-activa': activeTab === 'stock' }"
        @click="activeTab = 'stock'"
      >
        Gestionar Stock
      </BaseButton>

      <BaseButton 
        :class="{ 'tab-activa': activeTab === 'status' }"
        @click="activeTab = 'status'"
      >
        Cambiar Estado
      </BaseButton>
    </div>

    <!-- Contenido según la pestaña activa -->
    <div v-if="activeTab === 'register'" class="panel">
      <h2 class="subtitulo">Registrar Nuevo Pedido</h2>
      
      <!-- Formulario de registro de pedido -->
      <form @submit.prevent="registrarPedido">
        <!-- Datos de pedido -->
        <div class="form-grid">
          <div>
            <label class="label">Cliente (RUT):</label>
            <input 
              v-model="registroPedido.rutCliente" 
              @input="onClienteChange"
              class="input" 
              required 
              placeholder="Ingrese el RUT del cliente" 
            />
          </div>

          <div>
            <label class="label">Prioridad:</label>
            <select
              v-model="registroPedido.prioridadPedido"
              class="input"
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
            <!-- Producto -->
            <div class="producto-select">
              <label class="label">Producto:</label>
              <select
                v-model="registroPedido.idProducto"
                class="input"
                required
                @change="onProductoChange"
              >
                <option value="" disabled selected>Seleccione un producto</option>
                <option v-for="producto in productos" :key="producto.idProducto" :value="producto.idProducto">
                  {{ producto.nombre }} - ${{ producto.precio }} ({{ producto.stock }} disponibles)
                </option>
              </select>
            </div>
            <!-- Cantidad -->
            <div class="w-20">
              <label class="label">Cantidad:</label>
              <input 
                v-model.number="registroPedido.cantidad" 
                type="number" 
                min="1"
                :max= "productoSeleccionado ? productoSeleccionado.stock : 1"
                class="input"
                @change="calcularTotal"
              />
            </div>
            
            <!-- Medio de pago -->
            <div class="w-full">
              <label class="label">Medio de Pago:</label>
              <select
                v-model="registroPedido.nombreMedioPago"
                class="input"
                required
              >
                <option value="" disabled selected>Seleccione un medio de pago</option>
                <option value="Efectivo">Efectivo</option>
                <option value="Crédito">Tarjeta de Crédito</option>
                <option value="Débito">Tarjeta de Débito</option>
                <option value="Transferencia">Transferencia</option>
              </select>
            </div>

            <div class="ml-auto text-right">
              <p class="total">Total: ${{ precioTotal || 0 }}</p>
            </div>

          </div>
        </div>

        <!-- Mapa de ruta -->
        <div v-if="mostrarMapa" class="mb-4">
          <RouteMap 
            :rut-cliente="registroPedido.rutCliente"
            :nombre-producto="productoSeleccionado?.nombre || ''"
            :should-calculate-route="shouldCalculateRoute"
            ref="routeMapRef"
          />
        </div>

        <div class="mt-4">
          <BaseButtonGreen type="submit" class="w-full md:w-auto">
            Registrar Pedido
          </BaseButtonGreen>
        </div>
      </form>
    </div>

    <!-- Pestaña para cambiar estado del pedido -->
    <div v-if="activeTab === 'status'" class="panel">
      <h2 class="subtitulo">Cambiar Estado de Pedido</h2>
      <div class="flex flex-col md:flex-row gap-4 mb-4">
        <div class="producto-select">
          <label class="label">Selecciona un Pedido:</label>
          <select v-model="cambioEstado.idPedido" class="w-full px-2 py-1 border rounded-md">
            <option value="" disabled selected>Selecciona un pedido</option>
            <option v-for="pedido in pedidos.filter(p => (p.estadoEntrega || p.estado) === 'Pendiente')" :key="pedido.idPedido" :value="pedido.idPedido">
              {{ pedido.idPedido }} - {{ pedido.estadoEntrega || pedido.estado }} - {{ pedido.rutCliente || pedido.idCliente }}
            </option>
          </select>
        </div>
        <div class="producto-select">
          <label class="label">Nuevo Estado:</label>
          <select v-model="cambioEstado.nuevoEstado" class="w-full px-2 py-1 border rounded-md">
            <option value="Entrega fallida">Entrega fallida</option>
            <option value="Entregado">Entregado</option>
            <option value="Devolución">Devolución</option>
            <option value="Cancelada">Cancelada</option>
          </select>
        </div>
      </div>
      <BaseButtonGreen @click="cambiarEstadoPedido" :disabled="!cambioEstado.idPedido">
        Actualizar Estado
      </BaseButtonGreen>
    </div>

    <!-- Pestaña para gestionar el stock-->
    <div v-if="activeTab === 'stock'" class="panel">
      <h2 class="subtitulo">Actualizar Stock</h2>
      <div class="mb-4">
        <label class="label">Selecciona un pedido:</label>
        <select v-model="idPedidoConfirmar" class="w-full px-2 py-1 border rounded-md">
          <option value="" disabled selected>Selecciona un pedido</option>
          <option v-for="pedido in pedidos.filter(p => (p.estadoEntrega || p.estado) === 'Pendiente')" :key="pedido.idPedido" :value="pedido.idPedido">
            {{ pedido.idPedido }} - {{ pedido.estadoEntrega || pedido.estado }} - {{ pedido.rutCliente || pedido.idCliente }}
          </option>
        </select>
      </div>
      <BaseButtonGreen @click="confirmarPedido">
        Actualizar Stock
      </BaseButtonGreen>
    </div>

    <!-- Lista de Pedidos -->
    <div v-if="activeTab === 'list'" class="panel">
      <h2 class="subtitulo mb-4">Lista de Pedidos</h2>
      
      <div v-if="loading" class="text-center py-4">
        <p>Cargando pedidos...</p>
      </div>
      
      <div v-else-if="pedidos.length === 0" class="text-center py-4">
        <p>No hay pedidos registrados</p>
      </div>
      
      <div v-else class="overflow-x-auto">
        <table class="tabla">
          <thead>
            <tr class="tabla-header">
              <th >ID</th>
              <th >Cliente</th>
              <th >Empresa</th>
              <th >Dirección</th>
              <th >Estado</th>
              <th >Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pedido in pedidos" :key="pedido.idPedido" class="hover:bg-gray-50">
              <td >{{ pedido.idPedido }}</td>
              <td >{{ pedido.idCliente }}</td>
              <td >{{ pedido.idEmpresa }}</td>
              <td >{{ pedido.direccionEntrega }}</td>
              <td >{{ pedido.estado }}</td>
              <td class="acciones">
                <button 
                  @click="cambioEstado.idPedido = pedido.idPedido; activeTab = 'status'" 
                  class="accion-estado"
                >
                  Cambiar Estado
                </button>
                <button 
                  @click="idPedidoConfirmar = pedido.idPedido; activeTab = 'stock'" 
                  class="accion-confirmar"
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
    <div v-if="mensaje" class="mensaje" :class="mensajeEstilo">
      {{ mensaje }}
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
      // Nuevo objeto simplificado para registro de pedidos
      registroPedido: {
        prioridadPedido: 'Media',
        rutCliente: '',
        cantidad: 1,
        idProducto: '',
        nombreMedioPago: ''
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
      pedidos: [],
      clientes: [],
      precioTotal: 0,
      loading: false,
      // Nuevas variables para el mapa de ruta
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
    async fetchClientes() {
      try {
        const response = await apiClient.get('/api/v1/clientes');
        this.clientes = response.data;
      } catch (error) {
        this.mostrarError('Error al cargar clientes: ' + (error.response?.data || error.message));
      }
    },
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
    async fetchPedidos() {
      this.loading = true;
      try {
        const response = await apiClient.get('/api/v1/pedidos');
        this.pedidos = response.data;
      } catch (error) {
        this.mostrarError('Error al cargar pedidos: ' + (error.response?.data || error.message));
      } finally {
        this.loading = false;
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
    // Nuevos métodos para manejar cambios en cliente y producto
    onClienteChange() {
      this.checkAndShowRoute();
    },
    onProductoChange() {
      this.calcularTotal();
      this.checkAndShowRoute();
    },
    checkAndShowRoute() {
      if (this.registroPedido.rutCliente && this.registroPedido.idProducto) {
        this.mostrarMapa = true;
        this.shouldCalculateRoute = true;
        // Reset para forzar recálculo
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
    async registrarPedido() {
      try {
        const producto = this.productoSeleccionado;
        if (!producto) {
          this.mostrarError('Debe seleccionar un producto válido');
          return;
        }
        // Llamada simplificada a la API con solo los campos necesarios
        console.log(this.registroPedido);
        const response = await apiClient.post('/api/v1/pedidos/registrar', this.registroPedido);

        if (response.status === 200 || response.status === 201) {
          this.mostrarExito('Pedido registrado');
        
          // Restablecer formulario
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
        } else {
          this.mostrarError('Error inesperado al registrar el pedido');
        }
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
        await apiClient.put(`/api/v1/pedidos/${this.cambioEstado.idPedido}/estado`, null, {
          params: { nuevoEstado: this.cambioEstado.nuevoEstado },
          headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}`}
      });
        
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
        console.log(response.data);
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
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem;
  min-height: 100vh;
  box-sizing: border-box;
  overflow-x: hidden; /* Evitar scroll horizontal */
}

/* Asegurar que el body y html permitan scroll */
:global(body) {
  margin: 0;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden; /* Evitar scroll horizontal */
  height: auto;
  min-height: 100vh;
}

:global(html) {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden; /* Evitar scroll horizontal */
}

.titulo {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tab-activa {
  background-color: #b91c1c !important;
  color: #fff !important;
}

.panel {
  background: #fff;
  padding: 1rem;
  border-radius: 0.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
  margin-bottom: 1rem;
  overflow: visible;
  max-width: 100%; /* Evitar que se desborde */
  box-sizing: border-box;
}

/* Contenedor específico para el mapa */
.mb-4 {
  margin-bottom: 1rem;
  max-width: 100%;
  overflow: hidden;
}

/* Mejorar responsividad */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.producto-select {
  flex: 1;
  min-width: 200px;
  max-width: 100%;
}

/* Responsividad mejorada */
@media (max-width: 768px) {
  .container {
    padding: 0.5rem;
    max-width: 100vw; /* Usar todo el ancho de la ventana */
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .producto-select {
    max-width: 100%;
    min-width: unset;
  }
  
  .panel {
    margin: 0 0 1rem 0;
    padding: 0.75rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0.25rem;
  }
  
  .tabs {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .panel {
    padding: 0.5rem;
  }
}

/* Resto de tus estilos existentes... */
</style>