package com.es.apiGestorDeEventos.controller

import com.es.apiGestorDeEventos.model.Usuario
import com.es.apiGestorDeEventos.service.TokenService
import com.es.apiGestorDeEventos.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var tokenService: TokenService
    /*
    MÉTODO PARA INSERTAR UN USUARIO
     */

    //funcion funciona correctamente y comprobada DOCUMENTADO EN EL README
    @PostMapping("/register")
    fun register(
        @RequestBody newUsuario: Usuario
    ) : ResponseEntity<Any>? {
        // Comprobación mínima
        // -> La obviamos por ahora
        if(newUsuario.password.isNullOrBlank() || newUsuario.username.isNullOrBlank()){
            return ResponseEntity(mapOf("ERROR" to "usuario debe tener username,contraseña"),HttpStatus.BAD_REQUEST)
        }
        // Llamar al UsuarioService para insertar un usuario
        return usuarioService.registerUsuario(newUsuario)

    }

    //metodo funcional y comprobado DOCUMENTADO EN EL README
    //METODO (ENDPOINT) PARA HACER UN LOGIN
    @PostMapping("/login")
    fun login(@RequestBody usuario: Usuario):ResponseEntity<Any>?{
        val authentication : Authentication
        try{
            authentication= authenticationManager.authenticate(UsernamePasswordAuthenticationToken(usuario.username,usuario.password))
        }catch (e: AuthenticationException){
            return ResponseEntity(mapOf("mensajes" to "Credenciales incorrecta dude"), HttpStatus.UNAUTHORIZED)
        }

        // si pasamos la autenticacion, significa que estamos bien autenticados Dxs
        //paamos a generar el token
        var token = ""
        token = tokenService.generarToken(authentication)


        println(authentication)
        return ResponseEntity(mapOf("token" to token),HttpStatus.CREATED)
    }

    //metodo funcional y comprobado DOCUMENTADO EN EL README
    @DeleteMapping("/eliminarusuario/{nombre}")
    fun deleteUser(
        @PathVariable nombre: String, authentication: Authentication
    ): ResponseEntity<Any>? {
        if (authentication.name == nombre || authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            return usuarioService.deleteUserByUsername(nombre,authentication)
        }
            return ResponseEntity(mapOf("mensajes" to "Accion no autorizada"), HttpStatus.FORBIDDEN)
    }

    //metodo funcional y comprobado Documenytado en el readme
    @PutMapping("/actualizarusuario")
    fun updateUser(
        @RequestBody updatedUser: Usuario,
        authentication: Authentication
    ): ResponseEntity<Any>? {
        // Verificar si el usuario está autorizado a actualizar sus datos
        println(authentication.name + updatedUser.username)
        return if (authentication.name == updatedUser.username || authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            usuarioService.updateUserByUsername(updatedUser)
        }else{
            ResponseEntity(mapOf("mensaje" to "Acción no autorizada"), HttpStatus.FORBIDDEN)
        }
    }

    //metodo funcional y comprobado DOCUMENTADO EN EL README
    @GetMapping("/alluser")
    fun allUser(authentication: Authentication): ResponseEntity<Any> {
       return usuarioService.getAllUsers(authentication.name)
    }
}