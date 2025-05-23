# GeoTaskApp - Procedimiento de Inicialización de Base de Datos

Este documento describe el procedimiento para crear e inicializar la base de datos `geotaskapp` usando los scripts SQL provistos en este repositorio. Incluye instrucciones tanto para usuarios de **pgAdmin** como para quienes prefieren la **línea de comandos (`psql`)**.


## Prerrequisitos

- Tener instalado PostgreSQL
- Tener la extensión PostGIS (se debe habilitar al instalar PostgreSQL, en el stack geoespacial)
- Scripts SQL provistos:
  - `dbCreate.sql`
  - `Ubicacion.sql`
  - `dbPoblate.sql`
  - `runStatements.sql`

---

## Opción 1: Utilizando la interfaz gráfica

### 1. Crear la base de datos `geotaskapp`

- Abre **pgAdmin** y conéctate a tu servidor PostgreSQL.
- Haz clic derecho sobre **Databases** > **Create** > **Database…**
- Nombra la base como `geotaskapp` y guarda.

---

### 2. Crear las tablas y estructuras

- En el **Query Tool**, abre y ejecuta el archivo:
```sql
dbCreate.sql
```
---

### 3. Cargar los datos de ejemplo

- En el **Query Tool**, abre y ejecuta los archivos:

```bash
Ubicacion.sql
```
```bash
dbPoblate.sql
```
---

### 4. Consultas de prueba

- Ejecuta en el **Query Tool**:
```bash
runStatements.sql
```

---
## Opción 2: Usando línea de comandos (psql)

### 1. Crear la base de datos

Abre una terminal y ejecuta:

    ```bash
    psql -h localhost -p 5432 -U postgres
    ```

Dentro de la consola de `psql`, crea la base de datos:

```sql
CREATE DATABASE geotaskapp;
\q
```

### 2. Crear las tablas y estructuras

Ejecuta el script para crear todas las tablas y relaciones:

```bash
psql -h localhost -p 5432 -U postgres -d geotaskapp -f dbCreate.sql
```

### 3. Cargar los datos de ejemplo

```bash
psql -h localhost -p 5432 -U postgres -d geotaskapp -f Ubicacion.sql
psql -h localhost -p 5432 -U postgres -d geotaskapp -f dbPoblate.sql
```

### 4. Consultas de prueba

```bash
psql -h localhost -p 5432 -U postgres -d geotaskapp -f runStatements.sql
```

---
