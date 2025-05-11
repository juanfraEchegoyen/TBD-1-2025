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
        :class="{ 'tab-activa': activeTab === 'status' }"
        @click="activeTab = 'status'"
      >
        Cambiar Estado
      </BaseButton>
      <BaseButton 
        :class="{ 'tab-activa': activeTab === 'stock' }"
        @click="activeTab = 'stock'"
      >
        Gestionar Stock
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
            <label class="label">RUT Cliente:</label>
            <input 
              v-model="registroPedido.rutCliente" 
              type="text" 
              class="input"
              required
              placeholder="Ej: 12345678-9"
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
                @change="calcularTotal"
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
          <label class="label">ID del Pedido:</label>
          <input 
            v-model="cambioEstado.idPedido" 
            type="number" 
            class="w-full px-2 py-1 border rounded-md"
          />
        </div>

        <div class="producto-select">
          <label class="label">Nuevo Estado:</label>
          <select 
            v-model="cambioEstado.nuevoEstado" 
            class="w-full px-2 py-1 border rounded-md"
          >
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
      <h2 class="subtitulo">Confirmar Pedido y Actualizar Stock</h2>
      
      <div class="mb-4">
        <label class="label">ID del Pedido a confirmar:</label>
        <input 
          v-model="idPedidoConfirmar" 
          type="number" 
          class="w-full px-2 py-1 border rounded-md"
        />
      </div>

      <BaseButtonGreen @click="confirmarPedido">
        Confirmar Pedido y Actualizar Stock
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
      precioTotal: 0,
      loading: false
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
        const producto = this.productoSeleccionado;
        if (!producto) {
          this.mostrarError('Debe seleccionar un producto válido');
          return;
        }
        // Llamada simplificada a la API con solo los campos necesarios
        console.log(this.registroPedido);
        const response = await apiClient.post('/api/v1/pedidos/registrar', this.registroPedido);
        console.log(response.data);
        
        this.mostrarExito('Pedido registrado correctamente');
        
        // Restablecer formulario
        this.registroPedido = {
          prioridadPedido: 'Media',
          rutCliente: '',
          cantidad: 1,
          idProducto: '',
          nombreMedioPago: ''
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
}
.subtitulo {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}
.label {
  font-size: 0.95rem;
  margin-bottom: 0.25rem;
  display: block;
}
.input {
  width: 100%;
  padding: 0.4rem 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.95rem;
}
.producto-select {
  width: 100%;
  max-width: 415px;
}
.cantidad-input {
  width: 80px;
}
.total {
  font-size: 1rem;
  font-weight: bold;
}
.tabla {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #d1d5db;
  margin-bottom: 1rem;
}
.tabla th,
.tabla td {
  border: 1px solid #d1d5db;
  padding: 0.5rem;
  text-align: left;
}
.tabla-header {
  background-color: #f3f4f6;
}
.acciones {
  display: flex;
  justify-content: space-around;
  gap: 0.5rem;
}
.accion-estado {
  color: #2563eb;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}
.accion-estado:hover {
  color: #1d4ed8;
}
.accion-confirmar {
  color: #16a34a;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}
.accion-confirmar:hover {
  color: #166534;
}
.mensaje {
  margin-top: 1rem;
  padding: 0.5rem;
  border-radius: 0.375rem;
  font-size: 0.95rem;
}
</style>