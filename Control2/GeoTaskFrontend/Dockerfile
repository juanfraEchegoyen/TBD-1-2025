# Etapa de construcción: usa Node.js para compilar la aplicación
FROM node:20-alpine AS build

# Copia los archivos de dependencias
COPY package*.json ./

# Instala las dependencias
RUN npm install

# Copia el resto del código fuente
COPY . .

# Construye la aplicación para producción
RUN npm run build

# Etapa de producción: usa Nginx para servir los archivos estáticos
FROM nginx:latest

# Copia los archivos generados desde la etapa anterior
COPY --from=build .output/public/_nuxt /usr/share/nginx/html

# Expone el puerto 80 para servir la aplicación
EXPOSE 3000

# Inicia Nginx al levantar el contenedor
CMD ["nginx", "-g", "daemon off;"]