<template>
  <div class="h-screen overflow-y-auto bg-gray-100 p-4">
    <h3 class="text-lg font-semibold mb-4">Gestión de Pedidos</h3>
    
    <!-- Menú de navegación superior -->
    <div class="flex flex-wrap gap-2 mb-6">
      <button @click="opcionActiva = 'registrar'" 
              :class="['btn-nav', opcionActiva === 'registrar' ? 'btn-nav-active' : '']">
        Registrar un pedido completo
      </button>
      <button @click="opcionActiva = 'cambiarEstado'" 
              :class="['btn-nav', opcionActiva === 'cambiarEstado' ? 'btn-nav-active' : '']">
        Cambiar estado de pedido
      </button>
      <button @click="opcionActiva = 'confirmarStock'" 
              :class="['btn-nav', opcionActiva === 'confirmarStock' ? 'btn-nav-active' : '']">
        Descontar stock al confirmar
      </button>
    </div>

    <!-- Formulario de registro de pedido -->
    <form v-if="opcionActiva === 'registrar'">
      <h4 class="text-md font-semibold mb-3">Nuevo Pedido</h4>
      
      <!-- Estado de Entrega -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Estado de Entrega</label>
        <input v-model="pedido.estadoEntrega" type="text" class="input-sm" required />
      </div>

      <!-- Prioridad -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Prioridad</label>
        <input v-model="pedido.prioridadPedido" type="text" class="input-sm" required />
      </div>

      <!-- Problema Crítico -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Problema Crítico</label>
        <input v-model="pedido.problemaCritico" type="checkbox" />
      </div>

      <!-- Cliente -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Cliente</label>
        <div class="relative">
          <select v-model="pedido.rutCliente" class="input-sm appearance-none" required>
            <option v-for="cliente in clientes" :key="cliente.rut" :value="cliente.rut">
              {{ cliente.nombre }} ({{ cliente.rut }})
            </option>
          </select>
          <span class="absolute right-2 top-2 text-gray-500 pointer-events-none">▼</span>
        </div>
      </div>

      <!-- Empresa -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Empresa</label>
        <div class="relative">
          <select v-model="pedido.rutEmpresa" class="input-sm appearance-none" required>
            <option v-for="empresa in empresas" :key="empresa.rut" :value="empresa.rut">
              {{ empresa.nombre }} ({{ empresa.rut }})
            </option>
          </select>
          <span class="absolute right-2 top-2 text-gray-500 pointer-events-none">▼</span>
        </div>
      </div>

      <!-- Repartidor -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Repartidor</label>
        <div class="relative">
          <select v-model="pedido.rutRepartidor" class="input-sm appearance-none" required>
            <option v-for="repartidor in repartidores" :key="repartidor.rut" :value="repartidor.rut">
              {{ repartidor.nombreRepartidor }} ({{ repartidor.rut }})
            </option>
          </select>
          <span class="absolute right-2 top-2 text-gray-500 pointer-events-none">▼</span>
        </div>
      </div>

      <!-- Producto -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Producto</label>
        <div class="relative">
          <select v-model="detalle.idProducto" @change="actualizarProductoSeleccionado" class="input-sm appearance-none" required>
            <option v-for="producto in productos" :key="producto.id" :value="producto.id">
              {{ producto.nombre }}
            </option>
          </select>
          <span class="absolute right-2 top-2 text-gray-500 pointer-events-none">▼</span>
        </div>
      </div>

      <!-- Cantidad -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Cantidad</label>
        <input v-model.number="detalle.cantidad" type="number" class="input-sm" @input="calcularPrecioTotal" required />
      </div>

      <!-- Precio Total -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Precio Total</label>
        <input v-model="detalle.precioTotal" type="number" class="input-sm" readonly />
      </div>

      <!-- Tiempo de Entrega -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Tiempo de Entrega (minutos)</label>
        <input v-model="detalle.tiempoEntrega" type="number" class="input-sm" required />
      </div>

      <!-- Fecha de Entrega -->
      <div class="mb-3">
        <label class="block font-medium mb-1">Fecha de Entrega</label>
        <input v-model="detalle.fechaEntrega" type="date" class="input-sm" required />
      </div>

      <div class="mt-4">
        <button type="button" @click="registrarPedidoCompleto" class="btn-primary-sm w-full">
          Registrar Pedido
        </button>
      </div>
    </form>

    <!-- Formulario para cambiar estado de pedido -->
    <form v-if="opcionActiva === 'cambiarEstado'">
      <h4 class="text-md font-semibold mb-3">Cambiar Estado de Pedido</h4>
      
      <div class="mb-3">
        <label class="block font-medium mb-1">ID del Pedido</label>
        <input v-model="idPedidoExistente" type="number" class="input-sm" required />
      </div>
      
      <div class="mb-3">
        <label class="block font-medium mb-1">Nuevo Estado</label>
        <select v-model="nuevoEstado" class="input-sm">
          <option value="Confirmado">Confirmado</option>
          <option value="En Preparación">En Preparación</option>
          <option value="En Camino">En Camino</option>
          <option value="Entregado">Entregado</option>
          <option value="Cancelado">Cancelado</option>
        </select>
      </div>
      
      <div class="mt-4">
        <button type="button" @click="cambiarEstadoPedido" class="btn-secondary-sm w-full">
          Actualizar Estado
        </button>
      </div>
    </form>

    <!-- Formulario para confirmar stock -->
    <form v-if="opcionActiva === 'confirmarStock'">
      <h4 class="text-md font-semibold mb-3">Confirmar Pedido y Descontar Stock</h4>
      
      <div class="mb-3">
        <label class="block font-medium mb-1">ID del Pedido</label>
        <input v-model="idPedidoExistente" type="number" class="input-sm" required />
      </div>
      
      <div class="mt-4">
        <button type="button" @click="descontarStock" class="btn-tertiary-sm w-full">
          Confirmar y Descontar Stock
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import apiClient from '../service/http-common';

export default {
  data() {
    return {
      opcionActiva: 'registrar', // Opción por defecto
      pedido: {
        estadoEntrega: '',
        prioridadPedido: '',
        problemaCritico: false,
        rutCliente: '',
        rutEmpresa: '',
        rutRepartidor: ''
      },
      detalle: {
        idProducto: null,
        cantidad: 0,
        precioTotal: 0,
        tiempoEntrega: 0,
        fechaEntrega: ''
      },
      idPedidoExistente: null,
      nuevoEstado: 'Confirmado',
      idPedidoRegistrado: null,
      productos: [],
      repartidores: [],
      clientes: [],
      empresas: [],
      productoSeleccionado: null
    };
  },
  methods: {
    // 1. Registrar pedido completo
    async registrarPedidoCompleto() {
      try {
        const response = await apiClient.post('/api/v1/pedidos/registrar', {
          pedido: this.pedido,
          detalle: this.detalle
        });

        if (response.status === 200) {
          this.idPedidoRegistrado = response.data.idPedido;
          alert(`Pedido registrado correctamente con ID: ${this.idPedidoRegistrado}`);
          this.resetForm();
        }
      } catch (error) {
        console.error('Error al registrar el pedido:', error);
        alert('Error al registrar el pedido: ' + (error.response?.data || error.message));
      }
    },
    
    // 2. Cambiar estado de un pedido con validación
    async cambiarEstadoPedido() {
      if (!this.idPedidoExistente) {
        alert('Por favor, ingrese el ID del pedido.');
        return;
      }
      
      try {
        const response = await apiClient.put(
          `/api/v1/pedidos/${this.idPedidoExistente}/estado?nuevoEstado=${this.nuevoEstado}`
        );
        
        if (response.status === 200) {
          alert(`Estado del pedido ${this.idPedidoExistente} cambiado a "${this.nuevoEstado}" correctamente.`);
        }
      } catch (error) {
        console.error('Error al cambiar el estado del pedido:', error);
        alert('Error al cambiar el estado: ' + (error.response?.data || error.message));
      }
    },
    
    // 3. Descontar stock al confirmar pedido
    async descontarStock() {
      if (!this.idPedidoExistente) {
        alert('Por favor, ingrese el ID del pedido.');
        return;
      }
      
      try {
        const response = await apiClient.put(`/api/v1/pedidos/${this.idPedidoExistente}/confirmar`);
        
        if (response.status === 200) {
          alert(`Stock descontado correctamente para el pedido ${this.idPedidoExistente}.`);
        }
      } catch (error) {
        console.error('Error al descontar stock:', error);
        alert('Error al descontar stock: ' + (error.response?.data || error.message));
      }
    },
    
    async obtenerProductos() {
      try {
        const response = await apiClient.get('/api/v1/productos');
        this.productos = response.data;
      } catch (error) {
        console.error('Error al obtener productos:', error);
      }
    },
    
    async obtenerRepartidores() {
      try {
        const response = await apiClient.get('/api/v1/repartidores');
        this.repartidores = response.data;
      } catch (error) {
        console.error('Error al obtener repartidores:', error);
      }
    },
    
    async obtenerClientes() {
      try {
        const response = await apiClient.get('/api/v1/clientes');
        this.clientes = response.data;
      } catch (error) {
        console.error('Error al obtener clientes:', error);
      }
    },
    
    async obtenerEmpresas() {
      try {
        const response = await apiClient.get('/api/v1/empresas');
        this.empresas = response.data;
      } catch (error) {
        console.error('Error al obtener empresas:', error);
      }
    },
    
    // Actualiza el producto seleccionado al cambiar el dropdown
    actualizarProductoSeleccionado() {
      const producto = this.productos.find(p => p.id === this.detalle.idProducto);
      if (producto) {
        this.productoSeleccionado = producto; // Guarda el producto seleccionado
        this.calcularPrecioTotal(); // Recalcula el precio total
      }
    },

    // Calcula el precio total basado en el producto seleccionado y la cantidad
    calcularPrecioTotal() {
      if (this.productoSeleccionado && this.detalle.cantidad > 0) {
        this.detalle.precioTotal = this.productoSeleccionado.precio * this.detalle.cantidad;
      } else {
        this.detalle.precioTotal = 0; // Resetea el precio total si no hay cantidad o producto seleccionado
      }
    },
    
    // Resetea el formulario después de crear un pedido
    resetForm() {
      this.pedido = {
        estadoEntrega: '',
        prioridadPedido: '',
        problemaCritico: false,
        rutCliente: '',
        rutEmpresa: '',
        rutRepartidor: ''
      };
      
      this.detalle = {
        idProducto: null,
        cantidad: 0,
        precioTotal: 0,
        tiempoEntrega: 0,
        fechaEntrega: ''
      };
      
      this.productoSeleccionado = null;
    }
  },
  mounted() {
    this.obtenerProductos();
    this.obtenerRepartidores();
    this.obtenerClientes();
    this.obtenerEmpresas();
  }
};
</script>

<style>
.input-sm {
  width: 100%;
  padding: 6px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

/* Estilos para los botones de navegación */
.btn-nav {
  padding: 10px 16px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #f3f4f6;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-nav:hover {
  background-color: #e5e7eb;
}

.btn-nav-active {
  background-color: #3b82f6;
  border-color: #2563eb;
  color: white;
}

.btn-nav-active:hover {
  background-color: #2563eb;
}

.btn-primary-sm {
  background-color: #ef4444;
  color: white;
  padding: 8px 16px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn-primary-sm:hover {
  background-color: #dc2626;
}

.btn-secondary-sm {
  background-color: #3b82f6;
  color: white;
  padding: 8px 16px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn-secondary-sm:hover {
  background-color: #2563eb;
}

.btn-tertiary-sm {
  background-color: #10b981;
  color: white;
  padding: 8px 16px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn-tertiary-sm:hover {
  background-color: #059669;
}
</style>