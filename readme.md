# API REST - Gestor de Reserva de Locales para Eventos

## a. Nombre del Proyecto

**API Gestor de Reserva de Locales para Eventos**

## b. Idea del Proyecto

La idea de este proyecto es proporcionar una API RESTful que permita gestionar eventos,
incluyendo la creación y administración de reservas, usuarios y locales. 
La aplicación tiene un enfoque en la seguridad, utilizando JWT para el control de acceso y asegurando 
que solo los usuarios autorizados puedan realizar ciertas operaciones. 
Además, se implementa un sistema de roles para gestionar los permisos de los usuarios y 
la autenticación utilizando un cifrado asimétrico (RSA).

## c. Justificación del Proyecto

Este proyecto tiene como objetivo ayudar a la gestión eficiente de eventos en un entorno con múltiples usuarios
y locales. A través de la API, los administradores pueden gestionar usuarios y reservas, 
mientras que los clientes pueden realizar reservas para eventos en los locales disponibles. 
La justificación principal es automatizar y asegurar el proceso de reserva de eventos para ofrecer 
una experiencia más ágil y controlada a los usuarios.

## d. Descripción detallada de las Tablas

Las tablas principales que intervienen en el proyecto son:

### 1. **Usuario**

| Campo     | Tipo      | Descripción                                  |
|-----------|-----------|----------------------------------------------|
| `idUsuario` | `Long`    | Identificador único del usuario.            |
| `username`  | `String`  | Nombre de usuario para autenticación.       |
| `password`  | `String`  | Contraseña cifrada del usuario.             |
| `roles`     | `String`  | Roles asignados al usuario (Ej. ADMIN, CLIENTE). |

### 2. **Reserva**

| Campo         | Tipo       | Descripción                                      |
|---------------|------------|--------------------------------------------------|
| `idReserva`   | `Long`     | Identificador único de la reserva.              |
| `idCliente`   | `Long`     | ID del cliente que realiza la reserva.          |
| `local`       | `Long`     | ID del local asociado a la reserva.             |
| `diaEvento`   | `LocalDate`| Fecha del evento.                               |
| `totalPersonas`| `Integer` | Número de personas que asistirán al evento.     |
| `total`       | `BigDecimal`| Total a pagar por la reserva (con o sin menú).  |
| `estado`      | `EstadoReserva` | Estado de la reserva (Ej. PENDIENTE_DE_PAGO).  |

### 3. **Local**

| Campo          | Tipo       | Descripción                                      |
|----------------|------------|--------------------------------------------------|
| `idLocal`      | `Long`     | Identificador único del local.                  |
| `nombre`       | `String`   | Nombre del local.                               |
| `precioInvitados` | `BigDecimal` | Precio por invitado en el local.             |
| `precioMenu`   | `BigDecimal` | Precio por persona si incluye menú.             |

### 4. **EstadoReserva** (Enum)

Este enum representa los diferentes estados de una reserva:

- **PENDIENTE_DE_PAGO**
- **CONFIRMADA**
- **CANCELADA**

---

## 3. Endpoints

### 1. **/usuarios** - Gestión de Usuarios

#### `GET /usuarios`

Obtiene todos los usuarios registrados en el sistema. Solo accesible por administradores.

#### `POST /register`

Crea un nuevo usuario. El usuario debe enviar un `username`, `password` y `roles`. 
La contraseña será cifrada antes de almacenarla en la base de datos.

#### `PUT /actualizarusuario`

Actualiza los datos de un usuario. Solo el administrador o el propio usuario pueden modificar sus datos.

#### `DELETE /usuarios/{username}`

Elimina un usuario del sistema. Solo el administrador o el propio usuario pueden eliminar su cuenta.

---

### 2. **/reservas** - Gestión de Reservas

#### `GET /reservas/tusreservas`

Obtiene todas las reservas asociadas al usuario autenticado.

#### `POST /reservas/register`

Crea una nueva reserva. Requiere información sobre el cliente, local, fecha, número de personas, y si se incluirá menú. Solo accesible por usuarios autenticados.

#### `DELETE /reservas/eliminarReserva/{id}`

Elimina una reserva por ID. Solo accesible por el propietario de la reserva o un administrador.

---

### 3. **/locales** - Gestión de Locales

#### `GET /locales`

Obtiene todos los locales disponibles.

#### `POST /locales/register`

Crea un nuevo local. Requiere el nombre del local, el precio por invitado y el precio del menú si aplica.

#### `PUT /locales/actualizarlocal`

Actualiza los datos de un local. Solo accesible por el propietario del local o un administrador.

#### `DELETE /locales/eliminarlocal/{nombre}`

Elimina un local del sistema. Solo accesible por el propietario del local o un administrador.

---

## 4. Lógica de Negocio

La lógica de negocio en la aplicación se centra en:

1. **Reserva de Eventos**: 
Los usuarios pueden crear reservas para eventos en locales disponibles. 
La fecha de la reserva debe ser posterior a la fecha actual, 
y la misma fecha no puede estar ocupada en el mismo local.

2. **Gestión de Usuarios**: 
Los administradores pueden gestionar usuarios, incluyendo la creación, actualización y eliminación. 
Los roles asignados a los usuarios determinan los permisos para acceder a ciertos endpoints.

3. **Cálculo del Total de la Reserva**: 
El costo total de la reserva se calcula según el número de personas, el precio por invitado del local y, 
si aplica, el precio del menú.

---

## 5. Excepciones y Códigos de Estado

### Excepciones:

1. **404 Not Found**: Cuando un recurso no es encontrado (usuario, reserva, local).
2. **400 Bad Request**: Cuando los datos enviados por el cliente son incorrectos o faltantes.
3. **401 Unauthorized**: Cuando el usuario no está autenticado.
4. **403 Forbidden**: Cuando el usuario no tiene permisos suficientes para realizar la acción.
5. **500 Internal Server Error**: Cuando ocurre un error inesperado en el servidor.
6. **409 Conflict**: Cuando la solicitud entra en conflicto con el estado actual del recurso (por ejemplo, nombre de local duplicado o reservas vigentes que impiden una acción).

### Códigos de Estado:

- `200 OK`: Operación exitosa.
- `201 Created`: Recurso creado exitosamente.
- `400 Bad Request`: Error en la solicitud.
- `401 Unauthorized`: El usuario no está autenticado.
- `403 Forbidden`: El usuario no tiene permiso para acceder al recurso.
- `404 Not Found`: El recurso no fue encontrado.
- `409 Conflict`: Conflicto con el estado del recurso
- `500 Internal Server Error`: Error en el servidor.
- 

---

## 6. Restricciones de Seguridad

- **Autenticación**: 
La aplicación usa **JWT** para la autenticación de usuarios. 
Cada vez que un usuario inicia sesión, se genera un token JWT firmado con clave pública y privada. 
Este token debe ser incluido en los encabezados de las solicitudes para acceder a los endpoints protegidos.

- **Roles**: 
Los usuarios tienen roles asignados que determinan su nivel de acceso a los recursos. 
Los administradores tienen acceso completo, mientras que los usuarios normales 
tienen acceso limitado a sus propias reservas.

- **Cifrado Asimétrico**: 
La autenticación utiliza un sistema de cifrado asimétrico mediante 
claves públicas y privadas (RSA) para garantizar la seguridad de las credenciales.

---

## Implementación de la API
La implementación de la API está basada en Spring Boot y utiliza Spring Security
para la autenticación y autorización. 
La gestión de usuarios, locales y reservas se realiza a través de servicios 
específicos, mientras que la generación de tokens se maneja mediante un 
servicio dedicado (TokenService).
---
## Pruebas de la API
Las pruebas del funcionamiento de la API se 
realizarán utilizando Insomnia para asegurar que todos 
los endpoints se comportan correctamente según lo esperado.
---
### Plan de Pruebas

### **Pruebas de Endpoints**

#### 1. **Pruebas de Endpoint `POST /usuarios/register`**

- **Prueba 1**: Registrar un usuario con `username` y `password` válidos.
    - **Entrada**: JSON con un `username` y `password`.
    - **Esperado**:
        - Código: `201 Created`.
        - Respuesta: Objeto JSON del usuario creado (sin incluir el campo `password`).
    - **Resultado esperado**: Respuesta con el usuario creado.
    - ![registerCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FregisterCasoExito.png)
- **Prueba 2**: Intentar registrar un usuario sin `username` o `password`.
    - **Entrada**: JSON incompleto (por ejemplo, sin `username` o sin `password`).
    - **Esperado**:
        - Código: `400 Bad Request`.
        - Respuesta: `{"ERROR": "usuario debe tener username,contraseña"}`.
    - **Resultado esperado**: Respuesta con mensaje de error.
    - ![registercasoFallido1.png](src%2Fmain%2Fresources%2FCapturas%2FregistercasoFallido1.png)
- **Prueba 3**: Intentar registrar un usuario con un `username` que ya existe.
    - **Entrada**: JSON con un `username` ya registrado.
    - **Esperado**:
        - Código: `400 Bad Request`.
        - Respuesta: `{"ERROR": "Ese nombre ya existe"}`.
    - **Resultado esperado**: Respuesta con mensaje de error.
    - ![registerCasoFallido2.png](src%2Fmain%2Fresources%2FCapturas%2FregisterCasoFallido2.png)
---

#### 2. **Pruebas de Endpoint `POST /usuarios/login`**

- **Prueba 1**: Hacer login con credenciales correctas.
    - **Entrada**: JSON con `username` y `password` correctos.
    - **Esperado**:
        - Código: `201 Created`.
        - Respuesta: `{"token": "<JWT_TOKEN>"}` (un token válido generado).
    - **Resultado esperado**: Respuesta con el token JWT.
    - ![loginCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FloginCasoExito.png)

- **Prueba 2**: Intentar login con credenciales incorrectas.
    - **Entrada**: JSON con `username` o `password` incorrectos.
    - **Esperado**:
        - Código: `401 Unauthorized`.
        - Respuesta: `{"mensajes": "Credenciales incorrecta dude"}`.
    - **Resultado esperado**: Respuesta con mensaje de error.
    - ![loginCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FloginCasoFallido.png)

---

#### 3. **Pruebas de Endpoint `DELETE /usuarios/eliminarusuario/{nombre}`**

- **Prueba 1**: Eliminar un usuario autenticado.
    - **Entrada**: Usuario autenticado que coincide con el `nombre` o tiene rol `ROLE_ADMIN`.
    - **Esperado**:
        - Código:
            - `200 OK` si el usuario es eliminado.
            - `401 Unauthorized` si el usuario elimina su propia cuenta.
        - Respuesta:
            - `{"mensaje": "Usuario eliminado"}`.
            - `{"mensaje": "Usuario eliminado. Su sesión se ha cerrado."}`.
    - **Resultado esperado**: Respuesta adecuada según el caso.
    - ![eliminarUsuarioCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsuarioCasoExito.png)
    - ![eliminarUsuarioCasoDeExitoAdmin.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsuarioCasoDeExitoAdmin.png)
- **Prueba 2**: Intentar eliminar un usuario sin permisos.
    - **Entrada**: Usuario autenticado pero sin permisos de administrador o que no coincide con el `nombre`.
    - **Esperado**:
        - Código: `403 Forbidden`.
        - Respuesta: `{"mensajes": "Accion no autorizada"}`.
    - **Resultado esperado**: Respuesta con mensaje de error.
    - ![eliminarUsuarioCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsuarioCasoFallido.png)

- **Prueba 3**: Intentar eliminar un usuario inexistente.
    - **Entrada**: `nombre` de un usuario no registrado.
    - **Esperado**:
        - Código: `404 Not Found`.
        - Respuesta: `{"mensaje": "Usuario no encontrado"}`.
    - **Resultado esperado**: Respuesta con mensaje de error.
    - ![eliminarUsaurioCasoFallido2.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsaurioCasoFallido2.png)

### 4. **Pruebas de Endpoint PUT `/usuarios/actualizarusuario`**

#### **Prueba 1**: Actualizar los datos del usuario con credenciales correctas.
- **Descripción**: Un usuario autenticado actualiza su password.
- **Entrada**:
  ```json
  {
      "username": "usuario",
      "password": "nuevaPassword"
  }
  ```
- **Esperado**:
    - Código: 200 OK.
    - Respuesta:
      ```json
      {
          "mensaje": "Usuario actualizado correctamente"
      }
      ```
- **Resultado esperado**: El username y/o password del usuario se actualizan correctamente en la base de datos.
- **Captura**:  
  ![actualizarUsuarioCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarUsuarioCasoExito.png)

---

#### **Prueba 2**: Intentar actualizar otro usuario sin permisos de administrador.
- **Descripción**: Un usuario intenta actualizar los datos de otro usuario sin tener el rol `ROLE_ADMIN`.
- **Entrada**:
  ```json
  {
      "username": "otroUsuario",
      "password": "nuevaPassword"
  }
  ```
- **Esperado**:
    - Código: 403 Forbidden.
    - Respuesta:
      ```json
      {
          "mensaje": "Acción no autorizada"
      }
      ```
- **Resultado esperado**: La operación es denegada y no se realizan cambios en la base de datos.
- **Captura**:  
  ![actualizarUsuarioCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarUsuarioCasoFallido.png)

---

#### **Prueba 3**: Intentar actualizar un usuario que no existe.
- **Descripción**: Se intenta actualizar un usuario con un username no registrado en la base de datos.
- **Entrada**:
  ```json
  {
      "username": "usuarioInexistente",
      "password": "nuevaPassword"
  }
  ```
- **Esperado**:
    - Código: 404 Not Found.
    - Respuesta:
      ```json
      {
          "mensaje": "Usuario no encontrado"
      }
      ```
- **Resultado esperado**: La operación falla con un mensaje de error indicando que el usuario no existe.
- **Captura**:  
  ![actUsuaCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FactUsuaCasoFallido.png)

---

### 5. **Pruebas de Endpoint `POST /locales/register`**

- **Prueba 1**: Crear un local con datos válidos.
    - **Entrada**: JSON con los campos obligatorios (`nombre`, `tipoDeLocal`, `direccion`, etc.).
    - **Esperado**:
        - Código: `201 Created`.
        - Respuesta: `{"mensaje": "Local creado correctamente"}`.
    - **Resultado esperado**: Creación exitosa del local.
    - ![crearLocalCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FcrearLocalCasoExito.png)

- **Prueba 2**: Crear un local con campos inválidos o faltantes.
    - **Entrada**: JSON incompleto o con valores no válidos (por ejemplo, `aforoMaximo` <= 0).
    - **Esperado**:
        - Código: `400 Bad Request`.
        - Respuesta: `{"Error": "Campos inválidos o faltantes: <lista de campos>"}`.
    - **Resultado esperado**: Error con lista de campos inválidos.
    - ![crearLocalCasoFallido2.png](src%2Fmain%2Fresources%2FCapturas%2FcrearLocalCasoFallido2.png)

- **Prueba 3**: Crear un local con un nombre ya existente.
    - **Entrada**: JSON con un `nombre` ya registrado.
    - **Esperado**:
        - Código: `409 Conflict`.
        - Respuesta: `{"mensaje": "El nombre del local ya existe"}`.
    - **Resultado esperado**: Error indicando que el nombre ya está registrado.
    - ![crearLoclaCasoFallido1.png](src%2Fmain%2Fresources%2FCapturas%2FcrearLoclaCasoFallido1.png)

---

### 6. **Pruebas de Endpoint `DELETE /locales/eliminarlocal/{nombre}`**

- **Prueba 1**: Eliminar un local con reservas vigentes sin permisos de administrador.
    - **Entrada**: Nombre del local con reservas activas.
    - **Esperado**:
        - Código: `409 Conflict`.
        - Respuesta: `{"mensaje": "El local tiene reservas vigentes"}`.
    - **Resultado esperado**: No se elimina el local.
    - ![eliminarLocalCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarLocalCasoFallido.png)

- **Prueba 2**: Eliminar un local como administrador, ignorando reservas.
    - **Entrada**: Nombre del local a eliminar.
    - **Esperado**:
        - Código: `200 OK`.
        - Respuesta: `{"mensaje": "local eliminado por privilegios de admin"}`.
    - **Resultado esperado**: El local es eliminado con éxito.
    - ![eliminarLoclaCasoAdminExito.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarLoclaCasoAdminExito.png)

- **Prueba 3**: Eliminar un local inexistente.
    - **Entrada**: Nombre de un local no registrado.
    - **Esperado**:
        - Código: `404 Not Found`.
        - Respuesta: `{"mensaje": "Usuario no existe"}`.
    - **Resultado esperado**: Error indicando que el local no se encontró.
    - ![eliminarlocalcasofallido2.png](src%2Fmain%2Fresources%2FCapturas%2Feliminarlocalcasofallido2.png)

---

### 7. **Pruebas de Endpoint `PUT /locales/actualizarlocal`**

- **Prueba 1**: Actualizar datos de un local válido.
    - **Entrada**: JSON con datos nuevos y válidos.
    - **Esperado**:
        - Código: `200 OK`.
        - Respuesta: `{"mensaje": "local actualizado correctamente"}`.
    - **Resultado esperado**: Los datos del local se actualizan correctamente.
    - ![actualizarLocalcasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarLocalcasoExito.png)

- **Prueba 2**: Intentar actualizar un local inexistente.
    - **Entrada**: JSON con el `nombre` de un local no registrado.
    - **Esperado**:
        - Código: `404 Not Found`.
        - Respuesta: `{"mensaje": "Local no encontrado"}`.
    - **Resultado esperado**: Error indicando que el local no existe.
    - ![actualizarlocalCasoFallo2.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarlocalCasoFallo2.png)

- **Prueba 3**: Intentar actualizar como usuario no autorizado.
    - **Entrada**: JSON con datos válidos pero enviado por un usuario no propietario o sin permisos de administrador.
    - **Esperado**:
        - Código: `403 Forbidden`.
        - Respuesta: `{"mensaje": "Accion no autorizada,solo los admin pueden cambiar el propietario del local"}`.
    - **Resultado esperado**: Error indicando que no tiene permisos para actualizar.
    - ![actualizarlocalCasoFallo4.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarlocalCasoFallo4.png)

---

### 8. **Pruebas de Endpoint `GET /locales/alllocal`**

- **Prueba 1**: Obtener la lista de locales registrados.
    - **Entrada**: No requiere parámetros.
    - **Esperado**:
        - Código: `200 OK`.
        - Respuesta: JSON con la lista de locales y datos filtrados.
    - **Resultado esperado**: Lista de locales correctamente devuelta.
    - ![casoExitoAllLocal.png](src%2Fmain%2Fresources%2FCapturas%2FcasoExitoAllLocal.png)

- **Prueba 2**: Obtener lista de locales cuando no hay registros.
    - **Entrada**: No requiere parámetros.
    - **Esperado**:
        - Código: `200 OK`.
        - Respuesta: `{"mensaje": "No hay locales registrados"}`.
    - **Resultado esperado**: Mensaje indicando que no hay locales disponibles.
    - ![getAllLocalCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FgetAllLocalCasoFallido.png)
      Aquí está la documentación de los endpoints basada en el formato establecido:

---

### Documentación de los Endpoints

#### 9. **POST /reservas/register**
- **Prueba 1**: Registrar una reserva válida.
    - **Entrada**: JSON con `local`, `diaEvento`, `tipoDeEvento`, `totalPersonas`, y `menuIncluido`.
    - **Esperado**:
        - **Código**: `201 Created`.
        - **Respuesta**: Objeto JSON de la reserva creada.
    - **Resultado esperado**: Reserva registrada con estado `PENDIENTE_DE_PAGO`.
    - ![registerReservaCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FregisterReservaCasoExito.png)
- **Prueba 2**: Intentar registrar una reserva sin datos obligatorios (`local`).
    - **Entrada**: JSON incompleto.
    - **Esperado**:
        - **Código**: `400 Bad Request`.
        - **Respuesta**: {"ERROR": "la reserva debe de tener un local"}.
    - **Resultado esperado**: Mensaje de error.
    - ![crearReservaCasoFallo.png](src%2Fmain%2Fresources%2FCapturas%2FcrearReservaCasoFallo.png)
- **Prueba 3**: Registrar una reserva con conflicto (fecha ya ocupada).
    - **Entrada**: JSON con `diaEvento` y `local` duplicados.
    - **Esperado**:
        - **Código**: `400 Bad Request`.
        - **Respuesta**: {"Error": "La fecha seleccionada ya está reservada para este local"}.
    - **Resultado esperado**: Mensaje de error.
    - ![crearReservaCasoFallido2.png](src%2Fmain%2Fresources%2FCapturas%2FcrearReservaCasoFallido2.png)

---

#### 10. **DELETE /reservas/eliminarReserva/{id}**
- **Prueba 1**: Eliminar una reserva válida.
    - **Entrada**: `id` de una reserva existente.
    - **Esperado**:
        - **Código**: `200 OK`.
        - **Respuesta**: {"mensaje": "Reserva eliminada"}.
    - **Resultado esperado**: Reserva eliminada correctamente.
    - ![eliminarReservaCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarReservaCasoExito.png)
- **Prueba 2**: Intentar eliminar una reserva inexistente.
    - **Entrada**: `id` de reserva no encontrada.
    - **Esperado**:
        - **Código**: `404 Not Found`.
        - **Respuesta**: {"ERROR": "id reserva no encontrada"}.
    - **Resultado esperado**: Mensaje de error.
    - ![eliminarReervaCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarReervaCasoFallido.png)
- **Prueba 3**: Eliminar una reserva sin autorización.
    - **Entrada**: `id` de una reserva perteneciente a otro usuario.
    - **Esperado**:
        - **Código**: `403 Forbidden`.
        - **Respuesta**: {"mensaje": "Accion no autorizada"}.
    - **Resultado esperado**: Mensaje de error.
    - ![eliminarReservaCasoFallido3.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarReservaCasoFallido3.png)

---

#### 11. **PUT /reservas/actualizarreserva**
- **Prueba 1**: Actualizar una reserva válida.
    - **Entrada**: JSON con los campos actualizables de una reserva existente.
    - **Esperado**:
        - **Código**: `200 OK`.
        - **Respuesta**: {"mensaje": "Reserva actualizada correctamente"}.
    - **Resultado esperado**: Actualización exitosa.
    - ![img.png](src%2Fmain%2Fresources%2FCapturas%2Fimg.png)
- **Prueba 2**: Intentar actualizar una reserva con cambios no permitidos (fecha).
    - **Entrada**: JSON modificando la fecha del evento.
    - **Esperado**:
        - **Código**: `400 Bad Request`.
        - **Respuesta**: {"mensaje": "La fecha del evento no puede ser modificada"}.
    - **Resultado esperado**: Mensaje de error.
    - ![actualizaReservaCasoFalilido.png](src%2Fmain%2Fresources%2FCapturas%2FactualizaReservaCasoFalilido.png)
- **Prueba 3**: Actualizar sin autorización.
    - **Entrada**: JSON de una reserva que pertenece a otro usuario.
    - **Esperado**:
        - **Código**: `403 Forbidden`.
        - **Respuesta**: {"mensaje": "Acción no autorizada"}.
    - **Resultado esperado**: Mensaje de error.
    - ![actualizarReservaCasoFallido1.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarReservaCasoFallido1.png)

---

#### 12. **GET /reservas/tusreservas**
- **Prueba 1**: Consultar reservas existentes.
    - **Entrada**: Autenticación válida.
    - **Esperado**:
        - **Código**: `200 OK`.
        - **Respuesta**: Lista de reservas del usuario autenticado.
    - **Resultado esperado**: Lista de reservas devuelta correctamente.
    - ![verReservasCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FverReservasCasoExito.png)
- **Prueba 2**: Consultar reservas sin registros.
    - **Entrada**: Autenticación válida.
    - **Esperado**:
        - **Código**: `200 OK`.
        - **Respuesta**: {"mensaje": "No tienes reservas registradas"}.
    - **Resultado esperado**: Mensaje de que no hay reservas.
    - ![verTusReservasCasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FverTusReservasCasoExito.png)
---
#### **a. Tecnologías utilizadas**

##### i. **Dependencias incluidas en el proyecto**
- **Spring Boot**: Para implementar los controladores REST y manejar peticiones HTTP.
- **Spring Boot Security**: Para la autenticación y autorización de usuarios.
- **Xammp**: Base de datos para almacenar la información del sistema.
- **PasswordEncoder (BCrypt)**: Para encriptar las contraseñas de los usuarios.

##### ii. **Software utilizado**
- **IntelliJ IDEA**: IDE para escribir, depurar y gestionar el código del proyecto.
- **Insomnia**: Para probar los endpoints de la API REST con distintas solicitudes y verificar su funcionamiento.
- **GitHub**: Para el control de versiones y almacenamiento del repositorio.

##### iii. **Tecnologías y su propósito**
- **Spring Boot**: Framework principal para el desarrollo del backend, 
que simplifica la configuración y permite una rápida implementación de la API REST.
- **Hibernate (JPA)**: Herramienta ORM para mapear las tablas de la base de datos en clases Java
y facilitar las operaciones CRUD.
- **Xammp**: Almacenar de manera persistente la información de usuarios, reservas y configuraciones.
- **Spring Security**: Proporciona autenticación, autorización y protección de la API frente a accesos no autorizados.
- **Insomnia**: Envios de solicitudes HTTP a los endpoints y validar las respuestas.

---

#### **b. API REST**

##### **¿Qué es una API REST?**
Una interfaz que permite la comunicación entre sistemas utilizando el protocolo HTTP.
utiliza operaciones estándar como GET, POST, PUT y DELETE.

##### **Principios de una API REST**
1. **Cliente-Servidor**: Los cliente solicitan recursos al servidor central.
2. **Stateless**: Cada petición es independiente y no depende de estados previos.
3. **Cacheable**: Las respuestas pueden ser cacheadas para optimizar el rendimiento.
4. **Uniform Interface**: Uso de una interfaz estándar y consistente.

##### **Identificación de principios en la implementación**
- **Client-Server**: El cliente realiza peticiones a la API y recibe respuestas JSON sin interferir en la lógica del servidor.
- **Stateless**: Cada solicitud incluye la información necesaria (como credenciales en los headers) y no depende del estado del servidor.
- **Cacheable**: No esta configurado un sistema de cacheo, las respuestas GET podrían beneficiarse de ello en el futuro.
- **Uniform Interface**: Todos los endpoints siguen convenciones estándar (por ejemplo, `/reservas/register` para crear reservas).

---

#### **c. Ventajas de la separación de responsabilidades entre cliente y servidor**

1. **Independencia en el desarrollo**: Los equipos pueden trabajar de forma paralela en el frontend y backend sin bloquearse mutuamente.
2. **Escalabilidad**: Permite actualizar o escalar el cliente o el servidor de forma independiente según las necesidades.
3. **Reutilización**: Una API puede ser consumida por múltiples clientes (aplicaciones web, móviles, etc.).
4. **Seguridad**: La lógica sensible y los datos críticos se gestionan en el servidor, mientras que el cliente solo interactúa con los recursos necesarios.
5. **Mantenimiento más sencillo**: Los cambios en una capa no afectan directamente a la otra, reduciendo los riesgos.  