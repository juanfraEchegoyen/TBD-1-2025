# GeoTaskApp - Procedimiento de Inicialización de Base de Datos

Este documento describe el procedimiento para crear e inicializar la base de datos `geotaskapp` usando los scripts SQL provistos en este repositorio. Incluye instrucciones tanto para usuarios de pgAdmin como para quienes prefieren la línea de comandos (`psql`).


## Prerrequisitos

- Tener instalado PostgreSQL
- Tener la extensión PostGIS (se debe habilitar al instalar PostgreSQL, en el stack geoespacial)
- Scripts SQL provistos:
  - `BdCreate.sql`
  - `Ubicacion.sql`
  - `BdPoblate.sql`
  - `runStatements.sql`

---
## Documentación de la Aplicación

Este documento explica cómo funciona la aplicación desarrollada para el Control 2 y describe el proceso de implementación, así como las tecnologías utilizadas.

---

## ¿Cómo funciona la aplicación?

La aplicación GeoTaskApp es un sistema para la gestión de tareas georreferenciadas. Permite a los usuarios crear, consultar y administrar tareas asociadas a ubicaciones geográficas, aprovechando capacidades espaciales proporcionadas por PostGIS. Entre las funcionalidades principales se encuentran:

- Creación y consulta de tareas con ubicación específica.
- Visualización de ubicaciones en el sistema.
- Consultas espaciales para filtrar tareas por proximidad o por criterios geográficos.

El frontend está desarrollada en Vue.js, permitiendo una experiencia interactiva y dinámica, mientras que el backend está construido en Spring Boot, encargado de exponer la API y manejar la lógica de negocio y acceso a la base de datos.

---

## ¿Cómo se realizó la implementación?

### Tecnologías utilizadas

- **Backend:** Java (Spring Boot)
- **Frontend:** Vue.js, TypeScript
- **Base de Datos:** PostgreSQL con extensión PostGIS


### Estructura del proyecto

- Backend: Servicios y lógica de negocio en Java.
- Frontend: Aplicación web en Vue.js.
- BDD: Scripts SQL para creación y poblamiento de la base de datos.

### Proceso de implementación

1. **Modelado de la base de datos:**
    - Diseño del modelo relacional incluyendo tablas para usuarios, tareas y ubicaciones.
    - Utilización de tipos espaciales de PostGIS para almacenar coordenadas.
    - Creación de los scripts `BdCreate.sql`, `Ubicacion.sql` y `BdPoblate.sql` para inicializar y poblar la base de datos.
    NOTA: 'Sqlquerys.sql' es un respaldo de las consultas pedidas para esta entrega.

2. **Desarrollo del Backend:**
    - Implementación de la API REST en Java usando Spring Boot.
    - Conexión a la base de datos PostgreSQL/PostGIS.
    - Definición de endpoints para CRUD de tareas y consultas espaciales.

3. **Desarrollo del Frontend:**
    - Creación de componentes en Vue.js para la interfaz gráfica.
    - Consumo de la API REST para mostrar y administrar tareas y ubicaciones.
    - Integración de mapas o visualización geográfica.

---

## Inicialización y uso

Consulta el procedimiento detallado de inicialización de la base de datos en la sección siguiente, ya sea usando pgAdmin o la línea de comandos (psql).

Para levantar la aplicación:

1. Inicializa la base de datos siguiendo los scripts.
2. Ejecuta el backend Java (`/backend`).
3. Ejecuta el frontend Vue.js (`/frontend`).
4. Accede a la aplicación desde tu navegador.

---

## Notas finales

- Asegurarse de tener PostgreSQL con PostGIS instalado antes de comenzar.
- Modifica las credenciales y parámetros de conexión según tu entorno (En Intellij).
- Para dudas, consulta los archivos fuente y scripts incluidos en el repositorio.


## Opción 1: Utilizando la interfaz gráfica

### 1. Crear la base de datos `geotaskapp`

- Abre **pgAdmin** y conéctate a tu servidor PostgreSQL.
- Haz clic derecho sobre **Databases** > **Create** > **Database…**
- Nombra la base como `geotaskapp` y guarda.


### 2. Crear las tablas y estructuras

- En el **Query Tool**, abre y ejecuta el archivo:
```sql
BdCreate.sql
```


### 3. Cargar los datos de ejemplo

- En el **Query Tool**, abre y ejecuta los archivos:

```bash
Ubicacion.sql
```
```bash
BdPoblate.sql
```


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
psql -h localhost -p 5432 -U postgres -d geotaskapp -f BdCreate.sql
```

### 3. Cargar los datos de ejemplo

```bash
psql -h localhost -p 5432 -U postgres -d geotaskapp -f Ubicacion.sql
psql -h localhost -p 5432 -U postgres -d geotaskapp -f BdPoblate.sql
```

### 4. Consultas de prueba

```bash
psql -h localhost -p 5432 -U postgres -d geotaskapp -f runStatements.sql
```

---

