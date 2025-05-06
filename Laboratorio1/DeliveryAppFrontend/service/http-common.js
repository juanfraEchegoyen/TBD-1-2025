import axios from 'axios';

// Crear instancia de axios con URL base y cabeceras predeterminadas
const clienteAPI = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Añadir interceptor de petición para incluir token de autenticación en rutas protegidas
clienteAPI.interceptors.request.use(
  (config) => {
    const tokenAcceso = localStorage.getItem('accessToken');
    if (tokenAcceso) {
      config.headers['Authorization'] = `Bearer ${tokenAcceso}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Añadir interceptor de respuesta para manejar la renovación del token
clienteAPI.interceptors.response.use(
  (respuesta) => {
    return respuesta;
  },
  async (error) => {
    const peticionOriginal = error.config;
    
    // Si el error es 401 y no hemos intentado renovar el token aún
    if (error.response && error.response.status === 401 && !peticionOriginal._intentoRenovacion) {
      peticionOriginal._intentoRenovacion = true;
      
      try {
        const tokenRefresco = localStorage.getItem('refreshToken');
        if (!tokenRefresco) {
          // No hay token de refresco disponible, redirigir al login
          window.location.href = '/login';
          return Promise.reject(error);
        }
        
        // Intentar renovar el token
        const respuesta = await axios.post('http://localhost:8080/auth/refresh', {
          refreshToken: tokenRefresco
        });
        
        // Verificar si la renovación fue exitosa
        if (respuesta.data && respuesta.data.accessToken) {
          // Almacenar nuevos tokens
          localStorage.setItem('accessToken', respuesta.data.accessToken);
          localStorage.setItem('refreshToken', respuesta.data.refreshToken);
          
          // Actualizar la cabecera de autorización
          peticionOriginal.headers['Authorization'] = `Bearer ${respuesta.data.accessToken}`;
          
          // Reintentar la petición original con el nuevo token
          return clienteAPI(peticionOriginal);
        }
      } catch (errorRefresco) {
        // Si el token de refresco es inválido, limpiar almacenamiento y redirigir al login
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(errorRefresco);
      }
    }
    
    return Promise.reject(error);
  }
);

// Función de utilidad para realizar peticiones autenticadas
const tokenAutorizacion = async (url, opciones = {}) => {
  const token = localStorage.getItem('accessToken');
  
  try {
    const respuesta = await fetch(url, {
      ...opciones,
      headers: {
        ...opciones.headers,
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (!respuesta.ok) {
      throw new Error(`Error: ${respuesta.status}`);
    }
    
    return respuesta;
  } catch (error) {
    console.error('Falló la petición:', error);
    
    // Si no está autorizado, redirigir al login
    if (error.message.includes('401')) {
      // Intentar refrescar el token
      try {
        const tokenRefresco = localStorage.getItem('refreshToken');
        if (!tokenRefresco) {
          window.location.href = '/login';
          throw new Error('No hay token de refresco disponible');
        }
        
        const respuestaRefresco = await fetch('http://localhost:8080/auth/refresh', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ refreshToken: tokenRefresco })
        });
        
        if (!respuestaRefresco.ok) {
          throw new Error('Falló el refresco del token');
        }
        
        const datosToken = await respuestaRefresco.json();
        localStorage.setItem('accessToken', datosToken.accessToken);
        localStorage.setItem('refreshToken', datosToken.refreshToken);
        
        // Reintentar la petición original con el nuevo token
        return tokenAutorizacion(url, opciones);
      } catch (errorRefresco) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
      }
    }
    
    throw error;
  }
};

// He notado que la función tokenAutorizacion necesita devolver respuesta, no respuesta.json()
// para poder verificar si respuesta.ok es true en los componentes

export { tokenAutorizacion };
export default clienteAPI;