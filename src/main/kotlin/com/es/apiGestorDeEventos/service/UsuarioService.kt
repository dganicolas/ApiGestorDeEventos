package com.es.apiGestorDeEventos.service

import com.es.apiGestorDeEventos.model.Usuario
import com.es.apiGestorDeEventos.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService : UserDetailsService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    /*
    TODO
     */

    //aqui decimos como tratamos los usuarios
    override fun loadUserByUsername(username: String?): UserDetails {
        var usuario: Usuario = usuarioRepository.findByUsername(username!!).orElseThrow()

        return User
            .builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    /*
    MÉTODO PARA INSERTAR UN USUARIO
     */
    fun registerUsuario(usuario: Usuario): ResponseEntity<Any>? {


        val newUsuario = usuario
        // Comprobamos que el usuario no existe en la base de datos
        if(usuarioRepository.findByUsername(newUsuario.username).isPresent){
            return ResponseEntity(mapOf("ERROR" to "ese correo/nombre ya existe"), HttpStatus.BAD_REQUEST)
        }

        newUsuario.password = passwordEncoder.encode(newUsuario.password)
        usuarioRepository.save(newUsuario)
        newUsuario.password = ""
        return ResponseEntity(newUsuario, HttpStatus.CREATED)
        // Creamos la instancia de Usuario


        /*
         La password del newUsuario debe estar hasheada, así que usamos el passwordEncoder que tenemos definido.
         ¿De dónde viene ese passwordEncoder?
         El objeto passwordEncoder está definido al principio de esta clase.
         */


        // Guardamos el newUsuario en la base de datos... igual que siempre


        // Devolvemos el Usuario insertado en la BDD

    }


}