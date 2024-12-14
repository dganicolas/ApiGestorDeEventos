# Gestor de Reserva de Locales para Eventos

## Nombre del Proyecto

**Gestor de Reserva de Locales para Eventos**

## Idea del Proyecto

El objetivo de este proyecto es desarrollar una API REST para gestionar las reservas de locales de eventos. Los usuarios pueden registrar sus propiedades (locales) y los clientes pueden realizar reservas para diferentes tipos de eventos, como bodas, cumpleaños o bautizos. Además, se proporcionará la opción de incluir un menú personalizado dependiendo del tipo de local, automatizando los procesos de gestión y facilitando el control por parte de los propietarios de los locales sobre sus reservas y eventos.

## Justificación del Proyecto

Este proyecto busca mejorar la eficiencia en la gestión de reservas de locales para eventos, brindando a los propietarios una herramienta para controlar las reservas de su local, la disponibilidad para futuros eventos y la opción de ofrecer menús. De esta manera, se facilitará la administración de los eventos y el seguimiento de los clientes.

## Descripción Detallada de las Tablas


---


### Tabla `usuarios`

La tabla de usuarios almacena la información de los usuarios registrados en el sistema, que incluyen tanto propietarios de locales como clientes que realizan reservas.

- **id_usuario**: Identificador único (PK). Tipo: `INT`, AUTO_INCREMENT, NOT NULL.
- **username**: Nombre de usuario. Tipo: `VARCHAR(100)`, UNIQUE, NOT NULL.
- **password**: Contraseña hasheada. Tipo: `VARCHAR(255)`, NOT NULL.
- **roles**: Roles asignados al usuario. Tipo: VARCHAR(255), describe el tipo de acceso como ROLE_USER o ROLE_ADMIN.
- **locales**: Lista de locales asociados a este usuario (propietario). Relación uno a muchos con la tabla locales. Tipo: OneToMany.
- **reservas**: Lista de reservas asociadas a este usuario (cliente). Relación uno a muchos con la tabla reservas. Tipo: OneToMany.

---


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
---
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


---

# `CLASE: USUARIOS`
# 1. `/usuarios/register` - Registrar Usuario

### Método:
`POST`

### Descripción:
Este endpoint permite registrar un nuevo usuario en el sistema.

### Parámetros:
- `username`: Nombre único de usuario.
- `password`: Contraseña del usuario (será cifrada antes de almacenarse).

### Cuerpo de la Solicitud:
El cliente debe enviar un JSON con los siguientes campos como minimo:

```json
{
  "username": "nombre_usuario",
  "password": "contraseña_segura"
}
```

### <span style="color: green;">Casos de éxitos:</span>

1. **Usuario registrado exitosamente:**
  - **Condición:** El `username` no existe en la base de datos y los campos `username` y `password` son válidos.
  - **Respuesta:**  
    Código: `201 Created`  
    Cuerpo:
    ```json
    {
     "id": 4,
	    "username": "usuario",
	    "password": "",
	    "roles": "ADMIN",
	    "locales": []
    }
    ```
 ![imagen de un caso de exito](src%2Fmain%2Fresources%2FCapturas%2FregisterCasoExito.png)

### <span style="color: red;">Casos Fallidos:</span>

1. **Campos vacíos o nulos:**
  - **Condición:** Los campos `username` o `password` están vacíos o no se proporcionan.
  - **Respuesta:**  
    Código: `400 Bad Request`  
    Cuerpo:
    ```json
    {
      "ERROR": "usuario debe tener username, contraseña"
    }
    ```
![imagen de un caso fallido](src%2Fmain%2Fresources%2FCapturas%2FregistercasoFallido1.png)

2. **Nombre de usuario ya registrado:**
  - **Condición:** El `username` proporcionado ya existe en la base de datos.
  - **Respuesta:**  
    Código: `400 Bad Request`  
    Cuerpo:
    ```json
    {
      "ERROR": "Ese nombre ya existe"
    }
    ```
![registerCasoFallido2.png](src%2Fmain%2Fresources%2FCapturas%2FregisterCasoFallido2.png)


---


# 2. `usuarios/login` - Iniciar sesión

### Método:
`POST`

### Descripción:
Este endpoint permite a un usuario autenticarse en el sistema y obtener un token JWT para realizar futuras solicitudes.

### Parámetros:
- `username`: Nombre de usuario del que intenta iniciar sesión.
- `password`: Contraseña del usuario.

### Cuerpo de la Solicitud:
El cliente debe enviar un JSON con los siguientes campos:

```json
{
  "username": "nombre_usuario",
  "password": "contraseña_usuario"
}
```

### :

### <span style="color: green;">Casos de éxitos:</span>

1. **Login exitoso:**
    - **Condición:** Las credenciales proporcionadas (username y password) son correctas y coinciden con un usuario registrado.
    - **Respuesta:**  
      Código: 201 Created  
      Cuerpo:

```json
{
  "token": "aqui-va-tu-token-jwt"
}
```
![imagen de un caso de exito](src%2Fmain%2Fresources%2FCapturas%2FloginCasoExito.png)

### <span style="color: red;">Casos Fallidos:</span>

1. **Credenciales incorrectas:**
    - **Condición:** Las credenciales proporcionadas (username y password) no coinciden con los registros de la base de datos.
    - **Respuesta:**  
      Código: 401 Unauthorized  
      Cuerpo:

```json
{
  "mensajes": "Credenciales incorrectas dude"
}
```

![imagen de un caso fallido](src%2Fmain%2Fresources%2FCapturas%2FloginCasoFallido.png)


---


# 3. `/eliminarusuario/{nombre}` - Eliminar Usuario

### Método:
`DELETE`

### Descripción:
Este endpoint permite eliminar un usuario del sistema. Solo el usuario autenticado con el nombre correspondiente o un administrador puede realizar esta acción.

### Parámetros:
- nombre: El nombre de usuario del usuario que se desea eliminar.
- authentication: El token de autenticación del usuario que realiza la solicitud.

### Cuerpo de la Solicitud:
No se requiere cuerpo de la solicitud para este endpoint.



### <span style="color: green;">Casos de éxitos:</span>
1. **Usuario eliminado exitosamente por un administrador:**
    - **Condición:** Un usuario administrador elimina a otro usuario.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:

```json
{
  "mensaje": "Usuario eliminado"
}
```
![eliminarUsuarioCasoDeExitoAdmin.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsuarioCasoDeExitoAdmin.png)

2. **Usuario eliminado exitosamente por el mismo usuario autenticado:**
    - **Condición:** El usuario autenticado elimina su propio usuario.
    - **Respuesta:**  
      Código: 401 Unauthorized  
      Cuerpo:

```json
{
  "mensaje": "Usuario eliminado. Su sesión se ha cerrado."
}
```

![imagen de un caso de exito](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsuarioCasoExito.png)

### <span style="color: red;">Casos Fallidos:</span>

1. **Acción no autorizada:**
    - **Condición:** El usuario autenticado no tiene permisos para eliminar al usuario especificado (ni es el mismo usuario ni un administrador).
    - **Respuesta:**  
      Código: 403 Forbidden  
      Cuerpo:

```json
{
  "mensajes": "Acción no autorizada"
}
```

![imagen de un caso fallido](src%2Fmain%2Fresources%2FCapturas%2FeliminarUsuarioCasoFallido.png)

---


# 4. `/actualizarusuario` - Actualizar Usuario

### Método:
`PUT`

### Descripción:
Este endpoint permite actualizar los datos de un usuario en el sistema. Solo el usuario autenticado con el nombre correspondiente o un administrador puede realizar esta acción.

### Parámetros:
- `updatedUser`: El objeto con los datos que se desean actualizar (username, password, roles).
- `authentication`: El token de autenticación del usuario que realiza la solicitud.

### Cuerpo de la Solicitud:
El cliente debe enviar un JSON con los campos que desea actualizar. Al menos uno de los siguientes campos debe estar presente:

```json
{
  "username": "nuevo_nombre_usuario",
  "password": "nueva_contraseña_segura",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

### <span style="color: green;">Casos de éxitos:</span>
1. **Usuario actualizado correctamente:**
    - **Condición:** El usuario autenticado actualiza sus propios datos o un administrador actualiza los datos de otro usuario.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:

```json
{
  "mensaje": "Usuario actualizado correctamente"
}
```

![imagen de un caso de exito](src%2Fmain%2Fresources%2FCapturas%2FactualizarUsuarioCasoExito.png)

### <span style="color: red;">Casos Fallidos:</span>
1. **Acción no autorizada:**
    - **Condición:** El usuario autenticado no tiene permisos para actualizar los datos del usuario especificado (ni es el mismo usuario ni un administrador).
    - **Respuesta:**  
      Código: 403 Forbidden  
      Cuerpo:

```json
{
  "mensaje": "Acción no autorizada"
}
```

![imagen de un caso fallido](src%2Fmain%2Fresources%2FCapturas%2FactualizarUsuarioCasoFallido.png)


---


# 5. `/usuarios/alluser` - Obtener Todos los Usuarios

### Método:
`GET`

### Descripción:
Este endpoint permite a un usuario con rol de administrador obtener la lista de todos los usuarios registrados en el sistema, excluyendo las contraseñas. Solo los administradores pueden acceder a este recurso.

### Parámetros:
- `authentication`: El token de autenticación del usuario que realiza la solicitud.

### Cuerpo de la Solicitud:
No se requiere cuerpo de la solicitud para este endpoint.


### <span style="color: green;">Casos de éxitos:</span>
1. **Lista de usuarios obtenida exitosamente:**
    - **Condición:** El usuario autenticado es un administrador y hay usuarios registrados en el sistema.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:

```json
[
  {
    "id": 1,
    "username": "usuario1",
    "password": "",
    "roles": ["ROLE_USER"],
    "locales": []
  },
  {
    "id": 2,
    "username": "usuario2",
    "password": "",
    "roles": ["ROLE_USER"],
    "locales": []
  }
]
```
![allUserCasoDeExito.png](src%2Fmain%2Fresources%2FCapturas%2FallUserCasoDeExito.png)
2. **No hay usuarios registrados:**
    - **Condición:** El usuario autenticado es un administrador, pero no hay usuarios registrados en el sistema.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:

```json
{
  "mensaje": "No hay usuarios registrados"
}
```

![imagen de un caso de exito](src%2Fmain%2Fresources%2FCapturas%2FtodosUsuariosCasoExito.png)

### <span style="color: red;">Casos Fallidos:</span>
1. **Acción no autorizada:**
    - **Condición:** El usuario autenticado no tiene el rol de administrador.
    - **Respuesta:**  
      Código: 403 Forbidden

![imagen de un caso fallido](src%2Fmain%2Fresources%2FCapturas%2FtodosUsuariosCasoFallido.png)


---

# `CLASE: LOCALES`
# 1. `/locales/register` - Registrar Local

### Método:
`POST`

### Descripción:
Este endpoint permite registrar un nuevo local en el sistema. Solo el propietario del local o un administrador puede realizar esta acción.

### Parámetros:
- `newLocal`: El objeto `Locales` que contiene la información del nuevo local.
- `authentication`: El token de autenticación del usuario que realiza la solicitud.

### Cuerpo de la Solicitud:
El cliente debe enviar un JSON con los siguientes campos mínimos:

```json
{
  "nombre": "nombre_local",
  "tipoDeLocal": "RESTAURANTE",
  "direccion": "Calle ejemplo 123",
  "descripcion": "Descripción breve del local",
  "aforoMaximo": 100,
  "precioInvitados": 50.00,
  "menuDisponible": true,
  "precioMenu": 25.50,
  "descripcionMenu": "Menú dos platos y un postre",
  "propietario": 1
}
```

### :

### <span style="color: green;">Casos de éxitos:</span>
1. **Local creado correctamente:**
   - **Condición:** El propietario es el usuario autenticado o el usuario tiene rol de administrador, y todos los campos necesarios son válidos.
   - **Respuesta:**  
     Código: 201 Created  
     Cuerpo:

```json
{
  "mensaje": "Local creado correctamente"
}
```

![imagen de un caso de exito](src%2Fmain%2Fresources%2FCapturas%2FcrearLocalCasoExito.png)

### <span style="color: red;">Casos Fallidos:</span>
1. **Nombre de local ya registrado:**
   - **Condición:** El nombre del local proporcionado ya existe en la base de datos.
   - **Respuesta:**  
     Código: 409 Conflict  
     Cuerpo:

```json
{
  "mensaje": "El nombre del local ya existe"
}
```
![crearLoclaCasoFallido1.png](src%2Fmain%2Fresources%2FCapturas%2FcrearLoclaCasoFallido1.png)
2. **Campos inválidos o faltantes:**
   - **Condición:** Los campos necesarios para la creación del local están vacíos, nulos o no válidos (como aforo máximo negativo, precio de menú inválido, etc.).
   - **Respuesta:**  
     Código: 400 Bad Request  
     Cuerpo:

```json
{
  "Error": "Campos inválidos o faltantes: nombre, tipo de local"
}
```
![crearLocalCasoFallido2.png](src%2Fmain%2Fresources%2FCapturas%2FcrearLocalCasoFallido2.png)
3. **Acción no autorizada:**
   - **Condición:** El usuario autenticado no es el propietario del local ni tiene rol de administrador.
   - **Respuesta:**  
     Código: 403 Forbidden  
     Cuerpo:

```json
{
  "mensaje": "Accion no autorizada"
}
```

![imagen de un caso fallido](src%2Fmain%2Fresources%2FCapturas%2FcrearLocalCasoFallido.png)

Aquí tienes la documentación para el endpoint `/locales/eliminarlocal/{nombre}`, siguiendo el formato solicitado:

---

# 2. `/locales/eliminarlocal/{nombre}` - Eliminar Local

### Método:
`DELETE`

### Descripción:
Este endpoint permite eliminar un local del sistema. Solo el propietario del local o un administrador puede realizar esta acción. Si el local tiene reservas vigentes, solo un administrador podrá eliminarlo.

### Parámetros:
- `nombre`: El nombre del local que se desea eliminar.
- `authentication`: El token de autenticación del usuario que realiza la solicitud.

### Cuerpo de la Solicitud:
No es necesario incluir un cuerpo en la solicitud para este endpoint.

### :

### <span style="color: green;">Casos de éxitos:</span>
1. **Local eliminado correctamente:**
   - **Condición:** El usuario autenticado es el propietario del local o tiene rol de administrador, y el local no tiene reservas vigentes.
   - **Respuesta:**  
     Código: 200 OK  
     Cuerpo:

```json
{
  "mensaje": "local eliminado"
}
```
![eliminarocalCasoExtio.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarocalCasoExtio.png)
2. **Local eliminado por privilegios de administrador:**
   - **Condición:** El usuario autenticado es un administrador y el local tiene reservas vigentes.
   - **Respuesta:**  
     Código: 200 OK 
     Cuerpo:

```json
{
  "mensaje": "local eliminado por privilegios de admin"
}
```
![eliminarLoclaCasoAdminExito.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarLoclaCasoAdminExito.png)
### <span style="color: red;">Casos Fallidos:</span>
1. **Local no encontrado:**
   - **Condición:** El local con el nombre proporcionado no existe en la base de datos.
   - **Respuesta:**  
     Código: 404 Not Found  
     Cuerpo:

```json
{
  "mensaje": "Local no existe"
}
```
![eliminarlocalcasofallido2.png](src%2Fmain%2Fresources%2FCapturas%2Feliminarlocalcasofallido2.png)
2. **Acción no autorizada:**
   - **Condición:** El usuario autenticado no es el propietario del local ni tiene rol de administrador.
   - **Respuesta:**  
     Código: 403 Forbidden  
     Cuerpo:

```json
{
  "mensaje": "Accion no autorizada"
}
```
![eliminarLoclaCasoFallido1.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarLoclaCasoFallido1.png)
3. **El local tiene reservas vigentes:**
   - **Condición:** El local tiene reservas activas y no es un administrador el que está intentando eliminarlo.
   - **Respuesta:**  
     Código: 409 Conflict  
     Cuerpo:

```json
{
  "mensaje": "El local tiene reservas vigentes"
}
```

![eliminarLocalCasoFallido.png](src%2Fmain%2Fresources%2FCapturas%2FeliminarLocalCasoFallido.png)


Aquí tienes la documentación para el endpoint `/locales/actualizarlocal` actualizado:


---


Aquí tienes la documentación del endpoint actualizada para reflejar que el nombre del local no se puede modificar:

---

# 3. `/locales/actualizarlocal` - Actualizar Local

### Método:
`PUT`

### Descripción:
Este endpoint permite actualizar los datos de un local existente, excepto el nombre del local, ya que es inmutable. Solo el propietario del local o un administrador tiene permiso para realizar esta acción. Además, cambiar el propietario del local está restringido únicamente a usuarios con rol de administrador.

---

### Parámetros:

#### Cuerpo de la Solicitud (*JSON*):
Debe incluir el nombre del local (como referencia) y los campos que se deseen actualizar. Los campos que no se incluyan permanecerán sin cambios.

```json
{
  "nombre": "string (obligatorio, solo como referencia, no se puede modificar)",
  "tipoDeLocal": "enum (BAR, VENTA, SALON_DE_CELEBRACIONES, IGLESIA, RESTAURANTE)",
  "direccion": "string",
  "descripcion": "string",
  "aforoMaximo": "integer",
  "precioInvitados": "number",
  "menuDisponible": "boolean",
  "precioMenu": "number",
  "descripcionMenu": "string",
  "propietario": "long (opcional, solo admins pueden cambiarlo)"
}
```

---

### Respuestas:

#### Éxito:
1. **Local actualizado correctamente:**
    - **Condición:** El usuario autenticado es el propietario del local o un administrador, y los datos proporcionados son válidos.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:
      ```json
      {
        "mensaje": "local actualizado correctamente"
      }
      ```  
![actualizarLocalcasoExito.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarLocalcasoExito.png)
---

#### Errores:
1. **Acción no autorizada:**
    - **Condición:** El usuario autenticado no es el propietario del local ni tiene rol de administrador.
    - **Respuesta:**  
      Código: 403 Forbidden  
      Cuerpo:
      ```json
      {
        "mensaje": "Accion no autorizada"
      }
      ```  
![actualizarLoclaCasoFallo1.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarLoclaCasoFallo1.png)
2. **Usuario no encontrado:**
    - **Condición:** El usuario autenticado no existe en el sistema.
    - **Respuesta:**  
      Código: 404 Not Found  
      Cuerpo:
      ```json
      {
        "mensaje": "Usuario no encontrado"
      }
      ```  
No puedo llegar a ser este caso, por que todos los token son ususarios, que estan en el sistemas, pero lo tengo por si acaso :D
3. **Local no encontrado:**
    - **Condición:** El local con el nombre proporcionado no existe en el sistema.
    - **Respuesta:**  
      Código: 404 Not Found  
      Cuerpo:
      ```json
      {
        "mensaje": "Local no encontrado"
      }
      ```  
![actualizarlocalCasoFallo2.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarlocalCasoFallo2.png)
4. **Intento de cambiar el propietario sin ser administrador:**
    - **Condición:** El usuario autenticado intenta cambiar el propietario del local, pero no tiene rol de administrador.
    - **Respuesta:**  
      Código: 403 Forbidden  
      Cuerpo:
      ```json
      {
        "mensaje": "Accion no autorizada, solos los admin pueden cambiar el propietario del local"
      }
      ```  
![actualizarlocalCasoFallo4.png](src%2Fmain%2Fresources%2FCapturas%2FactualizarlocalCasoFallo4.png)

Aquí tienes la documentación para el endpoint `GET /alllocal`:

---

# 4. `/locales/alllocal` - Listar Todos los Locales

### Método:
`GET`

### Descripción:
Este endpoint permite obtener una lista de todos los locales registrados en el sistema. Si no hay locales registrados, se devuelve un mensaje indicando la ausencia de datos.

---

### Parámetros:
Este endpoint no requiere parámetros adicionales.

---

### Cuerpo de la Solicitud:
N/A

---

### <span style="color: green;">Casos de Éxito:</span>
1. **Locales obtenidos exitosamente:**
    - **Condición:** Hay locales registrados en la base de datos.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:

```json
[
  {
    "id": 1,
    "nombre": "RestauranteCentral",
    "tipoDeLocal": "RESTAURANTE",
    "direccion": "Calle Nueva 123",
    "descripcion": "Restaurante moderno",
    "aforoMaximo": 100,
    "precioInvitados": 50.0,
    "menuDisponible": true,
    "precioMenu": 30.0,
    "descripcionMenu": "Menú de 3 platos",
    "propietario": 3
  },
  {
    "id": 2,
    "nombre": "BarLosAmigos",
    "tipoDeLocal": "BAR",
    "direccion": "Calle Vieja 456",
    "descripcion": "Bar acogedor",
    "aforoMaximo": 50,
    "precioInvitados": 20.0,
    "menuDisponible": false,
    "precioMenu": null,
    "descripcionMenu": null,
    "propietario": 5
  }
]
```
![casoExitoAllLocal.png](src%2Fmain%2Fresources%2FCapturas%2FcasoExitoAllLocal.png)

---

### <span style="color: red;">Casos Fallidos:</span>
1. **No hay locales registrados:**
    - **Condición:** No existen registros de locales en la base de datos.
    - **Respuesta:**  
      Código: 200 OK  
      Cuerpo:

```json
{
  "mensaje": "No hay locales registrados"
}
```

---


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
