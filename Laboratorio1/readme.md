# Comandos psql para Cargar Base de Datos DeliveryApp

Esta guía muestra los comandos `psql` para ejecutar los scripts SQL y configurar la base de datos. Se asume que te encuentras en la línea de comandos **dentro del directorio que contiene los archivos `.sql`**.

## Prerrequisitos

* Tener `psql` accesible desde la línea de comandos.
* La base de datos `deliveryapp` debe existir previamente en el servidor PostgreSQL.
* Conocer los detalles de conexión (host, puerto, usuario, contraseña) a tu servidor PostgreSQL.

## Ejecución de Scripts

1.  **Crear Estructura (Tablas):**
    Ejecuta el script `dbCreateDelivery.sql` para crear todas las tablas. Estando en el directorio del script, el comando es:
    ```bash
    psql -h localhost -p 5432 -U postgres -d deliveryapp -f dbCreateDelivery.sql
    ```
    * Introduce la contraseña del usuario `postgres` cuando se solicite.
    * Verifica que la salida muestre mensajes `CREATE TABLE` y no errores.

2.  **Cargar Datos Iniciales:**
    **Solo si el paso anterior fue exitoso**, ejecuta el script `CargaDatos.sql` para insertar los datos de ejemplo:
    ```bash
    psql -h localhost -p 5432 -U postgres -d deliveryapp -f CargaDatos.sql
    ```
    * Introduce la contraseña de `postgres` cuando se solicite.
    * Verifica que la salida muestre mensajes `INSERT 0 X` y no errores.

---

Si ejecutas los comandos `psql` desde un directorio diferente, deberás reemplazar `dbCreateDelivery.sql` y `CargaDatos.sql` con las **rutas completas** a esos archivos.