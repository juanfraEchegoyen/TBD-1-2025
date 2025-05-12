# Guía rápida para ejecutar el Backend y Frontend

## 1. Configuración de variables de entorno

Antes de ejecutar la aplicación, se deben definir las variables de entorno necesarias para la conexión a la base de datos.

Ejemplo de estructura para las variables de entorno

```
ADDRESS=localhost
PORT=5432
PASS=*contraseña_postgres*
```

Estas se pueden configurar directamente en IntelliJ en el apartado environment variables.

## 2. Ejecutar el Backend

### Opción A: Desde la terminal

1. Abre una terminal y navega a la carpeta del backend:
   ```powershell
   cd DeliveryAppBackend
   ```
2. Compila y ejecuta el backend con Maven:
   ```powershell
   mvn spring-boot:run
   ```
3. El backend estará disponible por defecto en: http://localhost:8080

### Opción B: Desde IntelliJ IDEA

1. Abre el proyecto `DeliveryAppBackend` en IntelliJ IDEA.
2. Ve a la clase principal `DeliveryAppApplication.java`.
3. Haz clic derecho y selecciona `Run 'DeliveryAppApplication'`.
4. Si necesitas agregar variables de entorno:
   - Ve a `Run > Edit Configurations...`
   - En el campo `Environment variables`, agrega:
     ```
     ADDRESS=localhost;PORT=5432;PASS=*contraseña_postgres*
     ```
   - Guarda y ejecuta.

## 3. Ejecutar el Frontend

1. Abre otra terminal y navega a la carpeta del frontend:
   ```powershell
   cd DeliveryAppFrontend
   ```
2. Instala las dependencias:
   ```powershell
   npm install
   ```
3. Ejecuta el frontend:
   ```powershell
   npm run dev
   ```
4. El frontend estará disponible por defecto en: http://localhost:3000

---

# DeliveryApp - Procedimiento de Inicialización de Base de Datos

Este documento describe el procedimiento para crear e inicializar la base de datos `deliveryapp` usando los scripts SQL provistos en este repositorio.

## Prerrequisitos

- Tener instalado PostgreSQL y el comando `psql` disponible en la terminal.
- Conocer el usuario y contraseña de acceso a PostgreSQL (por defecto: usuario `postgres`).
- Estar ubicado en el directorio donde se encuentran los archivos `.sql`.

## Pasos para la Inicialización

### 1. Crear la base de datos

Abre una terminal y ejecuta:

```bash
psql -h localhost -p 5432 -U postgres
```

Dentro de la consola de `psql`, crea la base de datos:

```sql
CREATE DATABASE deliveryapp;
\q
```

### 2. Crear las tablas y estructuras

Ejecuta el script para crear todas las tablas y relaciones:

```bash
psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbCreateDelivery.sql
```

### 3. Crear las vistas

```bash
psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbVistas.sql
```

### 4. Crear los procedimientos almacenados

```bash
psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbProcedimientos.sql
```

### 5. Crear los triggers

```bash
psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbTriggers.sql
```

### 6. Cargar los datos de ejemplo

```bash
psql -h localhost -p 5432 -U postgres -d deliveryapp -f CargaDatos.sql
```

### 7. Consultas de prueba

```bash
psql -h localhost -p 5432 -U postgres -d deliveryapp -f runStatementsDelivery.sql
```

---
