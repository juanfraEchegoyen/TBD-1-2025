# DeliveryApp


### PostgreSQL

1. **Crear la base de datos:**
   ```bash
   psql -h localhost -p 5432 -U postgres
   ```
   ```sql
   CREATE DATABASE deliveryapp;
   \q
   ```

2. **Ejecutar scripts en orden:**
   ```bash
   # Crear tablas y estructuras
   psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbCreateDelivery.sql
   
   # Crear vistas
   psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbVistas.sql
   
   # Crear procedimientos almacenados
   psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbProcedimientos.sql
   
   # Crear triggers
   psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbTriggers.sql
   
   # Cargar datos de ejemplo
   psql -h localhost -p 5432 -U postgres -d deliveryapp -f CargaDatos.sql
   ```

### MongoDB

1. **Asegúrate de que MongoDB esté corriendo:**
   ```bash
   # Windows
   net start MongoDB
   ```

2. **Cargar datos de ejemplo (ejecutar desde la raíz del proyecto):**
   ```powershell
   # Windows PowerShell
   .\carga_datos_mongo.ps1
   ```

## Ejecución del Backend

### Variables de Entorno

Configura las siguientes variables de entorno:

```bash
# PostgreSQL
ADDRESS=localhost
PORT=5432
PASS=tu_contraseña_postgres

# MongoDB (opcional ya que usa valores por defecto)
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DATABASE=deliveryapp_mongo
```

### Ejecutar Backend

#### Opción 1: Línea de comandos
```bash
cd DeliveryAppBackend
mvn clean install
mvn spring-boot:run
```

#### Opción 2: IntelliJ IDEA
1. Abrir el proyecto `DeliveryAppBackend`
2. Configurar variables de entorno en `Run > Edit Configurations`
3. Ejecutar `DeliveryAppApplication.java`

El backend estará disponible en: **http://localhost:8080**

## Ejecución del Frontend

### Instalar y Ejecutar

```bash
cd DeliveryAppFrontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

El frontend estará disponible en: **http://localhost:3000**

## Configuración adicional

### Backend - application.properties

El archivo de configuración principal está en:
`DeliveryAppBackend/src/main/resources/application.properties`

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://${ADDRESS}:${PORT}/deliveryapp
spring.datasource.username=postgres
spring.datasource.password=${PASS}

# MongoDB
spring.data.mongodb.host=${MONGO_HOST:localhost}
spring.data.mongodb.port=${MONGO_PORT:27017}
spring.data.mongodb.database=${MONGO_DATABASE:deliveryapp_mongo}

# Servidor
server.port=8080
```