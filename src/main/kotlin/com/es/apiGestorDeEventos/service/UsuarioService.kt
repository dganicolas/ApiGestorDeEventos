package com.es.apiGestorDeEventos.service

import com.es.apiGestorDeEventos.model.Usuario
import com.es.apiGestorDeEventos.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

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
        if (usuarioRepository.findByUsername(newUsuario.username).isPresent) {
            return ResponseEntity(mapOf("ERROR" to "Ese nombre ya existe"), HttpStatus.BAD_REQUEST)
        }

        newUsuario.password = passwordEncoder.encode(newUsuario.password)
        usuarioRepository.save(newUsuario)
        newUsuario.password = ""
        return ResponseEntity(newUsuario, HttpStatus.CREATED)
    }

    fun deleteUserByUsername(nombre: String, authentication: Authentication): ResponseEntity<Any>? {
        val usuario = usuarioRepository.findByUsername(nombre).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
        }
        usuarioRepository.delete(usuario)
        if (authentication.name == nombre) {
            return ResponseEntity(
                mapOf("mensaje" to "Usuario eliminado. Su sesión se ha cerrado."),
                HttpStatus.UNAUTHORIZED
            )
        }
        return ResponseEntity(mapOf("eliminado" to "Usuario eliminado"), HttpStatus.NO_CONTENT)
    }

    fun updateUserByUsername(nombre: String, updatedUser: Usuario): ResponseEntity<Any>? {
        val usuario = usuarioRepository.findByUsername(nombre).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
        }
        updatedUser.username?.let { nuevoUsername -> usuario.username = nuevoUsername }
        updatedUser.password?.let { nuevoPassword -> usuario.password = passwordEncoder.encode(nuevoPassword) }
        updatedUser.roles?.let { nuevosRoles -> usuario.roles = nuevosRoles }
        usuarioRepository.save(usuario)
        return ResponseEntity(mapOf("mensaje" to "Usuario actualizado correctamente"), HttpStatus.OK)
    }

    fun getAllUsers(nombre: String): ResponseEntity<Any> {
        val detallesUser = loadUserByUsername(nombre)
        println(detallesUser.authorities)
        if (detallesUser.authorities.any { it.authority == "ROLE_ADMIN" }) {
            val usuarios = usuarioRepository.findAll() // Obtener todos los usuarios de la base de datos
            if (usuarios.isEmpty()) {
                return ResponseEntity(mapOf("mensaje" to "No hay usuarios registrados"), HttpStatus.NO_CONTENT)
            }

            val usuariosSinPasswords = usuarios.map { usuario ->
                usuario.password = ""
                usuario
            }
            return ResponseEntity(usuariosSinPasswords, HttpStatus.OK)
        }
        return ResponseEntity(mapOf("mensaje" to "Acción no autorizada"), HttpStatus.FORBIDDEN)
    }

    fun findByUsername(nombre: String): Usuario? {
        return usuarioRepository.findByUsername(nombre).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
        }
    }
}