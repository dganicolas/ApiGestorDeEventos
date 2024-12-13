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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

    //funcion funciona correctamente y comprobada
    @PostMapping("/register")
    fun register(
        @RequestBody newUsuario: Usuario
    ) : ResponseEntity<Any>? {
        // Comprobación mínima
        // -> La obviamos por ahora
        if(newUsuario.password.isNullOrBlank() || newUsuario.username.isNullOrBlank()){
            return ResponseEntity(mapOf("ERROR" to "usuario debe tener email,contraseña"),HttpStatus.BAD_REQUEST)
        }
        // Llamar al UsuarioService para insertar un usuario
        return usuarioService.registerUsuario(newUsuario)

    }

    //metodo funcional y comprobado
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

}