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
