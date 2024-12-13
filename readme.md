# Gestor de Reserva de Locales para Eventos

## Nombre del Proyecto

**Gestor de Reserva de Locales para Eventos**

## Idea del Proyecto

El objetivo de este proyecto es desarrollar una API REST para gestionar las reservas de locales de eventos. Los usuarios pueden registrar sus propiedades (locales) y los clientes pueden realizar reservas para diferentes tipos de eventos, como bodas, cumpleaños o bautizos. Además, se proporcionará la opción de incluir un menú personalizado dependiendo del tipo de local, automatizando los procesos de gestión y facilitando el control por parte de los propietarios de los locales sobre sus reservas y eventos.

## Justificación del Proyecto

Este proyecto busca mejorar la eficiencia en la gestión de reservas de locales para eventos, brindando a los propietarios una herramienta para controlar las reservas de su local, la disponibilidad para futuros eventos y la opción de ofrecer menús. De esta manera, se facilitará la administración de los eventos y el seguimiento de los clientes.

## Descripción Detallada de las Tablas

### Tabla `usuarios`

La tabla de usuarios almacena la información de los usuarios registrados en el sistema, que incluyen tanto propietarios de locales como clientes que realizan reservas.

- **id_usuario**: Identificador único (PK). Tipo: `INT`, AUTO_INCREMENT, NOT NULL.
- **username**: Nombre de usuario. Tipo: `VARCHAR(100)`, UNIQUE, NOT NULL.
- **password**: Contraseña hasheada. Tipo: `VARCHAR(255)`, NOT NULL.

### Tabla `local_de_eventos`

Esta tabla gestiona los locales de eventos disponibles para reservas. Cada local está asociado a un propietario y tiene detalles específicos como el tipo de local y la posibilidad de ofrecer un menú.

- **id_local**: Identificador único (PK). Tipo: `INT`, AUTO_INCREMENT, NOT NULL.
- **nombre**: Nombre o título. Tipo: `VARCHAR(100)`, NOT NULL.
- **tipo_de_local**: Tipo de local disponible para eventos (Bar, Salón de Celebraciones, Iglesia, Restaurante, etc.). Tipo: `ENUM`, NOT NULL.
- **direccion**: Dirección física. Tipo: `TEXT`, NOT NULL.
- **descripcion**: Descripción detallada. Tipo: `TEXT`.
- **aforo_maximo**: Número máximo de invitados. Tipo: `DECIMAL(4), NOT NULL`.
- **precio_invitados**: Precio por persona que será cobrado al cliente. Tipo: `DECIMAL(5,2), NOT NULL`.
- **menu_disponible**: Indica si el local ofrece un menú para los eventos (BOOLEAN). Tipo: `NOT NULL`.
- **precio_menu**: Precio del menú/persona, si el local ofrece menú. Tipo: `DECIMAL(5,2)`.
- **descripcion_menu**: Descripción del menú disponible. Tipo: `TEXT`.
- **id_propietario**: Identificador del propietario. Referencia a `usuarios.id_usuario`, NOT NULL.

### Tabla `reservas`

Esta tabla registra las reservas realizadas por los usuarios, asociando las propiedades reservadas a los eventos programados.

- **id_reserva**: Identificador único (PK). Tipo: `INT`, AUTO_INCREMENT, NOT NULL.
- **dia_evento**: Fecha del evento programado. Tipo: `DATE`, NOT NULL.
- **tipo_de_evento**: Tipo de evento reservado (Cumpleaños, Boda, Despedida de Soltero, Bautizo, etc.). Tipo: `ENUM`, NOT NULL.
- **id_usuario**: Identificador del usuario que realizó la reserva. Referencia a `usuarios.id_usuario`, NOT NULL.
- **id_local**: Identificador del local reservado. Referencia a `local_de_eventos.id_local`, NOT NULL.
- **total_personas**: Número total de personas para la reserva. Tipo: `DECIMAL(4), NOT NULL`.
- **menu_incluido**: Indica si se incluye el menú. Tipo: `BOOLEAN`, NOT NULL.
- **total**: Monto total de la reserva. Tipo: `DECIMAL(12,2), NOT NULL`.
- **estado**: Estado de la reserva (Realizada, Pendiente de pago, Confirmada, Denegada). Tipo: `ENUM`, NOT NULL.

## Endpoints a Desarrollar

### 1. `/usuarios/register` - Registrar Usuario
- **Método**: `POST`
- **Descripción**: Este endpoint permite registrar un nuevo usuario en el sistema.
- **Parámetros**:
    - `username`: Nombre de usuario.
    - `password`: Contraseña (debe ser cifrada).

### 2. `/usuarios/login` - Login de Usuario
- **Método**: `POST`
- **Descripción**: Este endpoint permite a un usuario autenticarse mediante sus credenciales y obtener un token JWT.
- **Parámetros**:
    - `username`: Nombre de usuario.
    - `password`: Contraseña.

### 3. `/usuarios/eliminarusuario/{nombre}` - Eliminar Usuario
- **Método**: `DELETE`
- **Descripción**: Este endpoint permite eliminar un usuario por su nombre, solo si el usuario autenticado tiene permisos de administrador o es el mismo usuario.
- **Parámetros**:
    - `nombre`: Nombre de usuario del usuario a eliminar.

## Lógica de Negocio

La aplicación gestionará las siguientes funcionalidades principales:
- Registro y login de usuarios.
- Gestión de locales para eventos, permitiendo a los propietarios registrar sus locales y establecer características como tipo de evento, menú disponible y capacidad.
- Reservas de eventos, donde los usuarios pueden seleccionar un local, definir el tipo de evento y el número de personas, y calcular automáticamente el precio total.
- Eliminación de usuarios, con controles de seguridad para asegurar que solo los administradores o el propio usuario puedan eliminar su cuenta.

## Excepciones y Códigos de Estado

Las excepciones generadas por la API y sus códigos de estado incluyen:

- **400 Bad Request**: Cuando los parámetros proporcionados son incorrectos o incompletos.
- **401 Unauthorized**: Cuando las credenciales de un usuario no son válidas.
- **403 Forbidden**: Cuando un usuario intenta realizar una acción para la cual no tiene permisos (por ejemplo, eliminar otro usuario sin ser administrador).
- **404 Not Found**: Cuando no se encuentra un recurso solicitado.
- **500 Internal Server Error**: Cuando ocurre un error inesperado en el servidor.

## Restricciones de Seguridad

Se aplicarán las siguientes restricciones de seguridad en la API:
- **Autenticación mediante JWT**: Los endpoints que requieren acceso restringido serán protegidos mediante un token JWT, el cual se debe enviar en los encabezados de la solicitud.
- **Autorización por roles**: Algunos endpoints solo estarán disponibles para administradores. Otros estarán disponibles para usuarios comunes.
- **Cifrado de Contraseñas**: Las contraseñas de los usuarios se almacenarán cifradas en la base de datos.

## Repositorio de GitHub

El código fuente de este proyecto estará disponible en el repositorio de GitHub:

[https://github.com/dganicolas/ApiGestorDeEventos](https://github.com/dganicolas/ApiGestorDeEventos)

## Conclusión

Este proyecto tiene como objetivo optimizar la administración de eventos y mejorar la interacción entre los diferentes actores involucrados, asegurando un proceso más fluido y accesible.
