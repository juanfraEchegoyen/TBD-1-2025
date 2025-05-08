<template>
  <div class="h-screen overflow-y-auto bg-gray-100 p-4">
    <h3 class="text-lg font-semibold mb-4">Registrar Pedido</h3>
    <form @submit.prevent="registrarPedido">
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

      <!-- Botón de Enviar -->
      <button type="submit" class="btn-primary-sm">Registrar Pedido</button>
    </form>
  </div>
</template>

<script>
import apiClient from '../service/http-common';

export default {
  data() {
    return {
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
      productos: [],
      repartidores: [],
      clientes: [],
      empresas: [],
      productoSeleccionado: null
    };
  },
  methods: {
    async registrarPedido() {
      try {
        const response = await apiClient.post('/api/v1/pedidos/registrar', {
          pedido: this.pedido,
          detalle: this.detalle
        });

        if (response.status === 200) {
          alert('Pedido registrado correctamente.');

          const idPedido = response.data.idPedido;
          await apiClient.put(`/api/v1/pedidos/${idPedido}/estado?nuevoEstado=Confirmado`);
          await apiClient.put(`/api/v1/pedidos/${idPedido}/confirmar`);

          alert('Estado actualizado a "Confirmado" y stock descontado.');
        }
      } catch (error) {
        console.error('Error al registrar el pedido:', error);
        alert('Error al registrar el pedido.');
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
</style>