import { ref } from 'vue'

// Estado reactivo para el estado de autenticación
const isAuthenticated = ref(false)

// Comprobar si hay token al iniciar la app
const checkAuth = () => {
  const token = localStorage.getItem('accessToken')
  isAuthenticated.value = !!token
  return isAuthenticated.value
}

// Inicializar al cargar
checkAuth()

export default {
  isAuthenticated,
  checkAuth,
  
  login(token, refreshToken) {
    localStorage.setItem('accessToken', token)
    localStorage.setItem('refreshToken', refreshToken)
    isAuthenticated.value = true
  },
  
  logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    isAuthenticated.value = false
  }
}