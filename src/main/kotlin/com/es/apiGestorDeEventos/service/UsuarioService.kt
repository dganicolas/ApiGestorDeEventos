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

    override fun loadUserByUsername(username: String?): UserDetails {
        var usuario: Usuario = usuarioRepository.findByUsername(username!!)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado") }

        return User
            .builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    fun registerUsuario(usuario: Usuario): ResponseEntity<Any>? {

        val newUsuario = usuario
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
        return ResponseEntity(mapOf("mensaje" to "Usuario eliminado"), HttpStatus.OK)
    }

    fun updateUserByUsername(updatedUser: Usuario): ResponseEntity<Any>? {
        val usuario = usuarioRepository.findByUsername(updatedUser.username).orElseThrow {
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
        val usuarios = usuarioRepository.findAll()
        if (usuarios.isEmpty()) {
            return ResponseEntity(mapOf("mensaje" to "No hay usuarios registrados"), HttpStatus.OK)
        }

        val usuariosSinPasswords = usuarios.map { usuario ->
            usuario.password = ""
            usuario
        }
        return ResponseEntity(usuariosSinPasswords, HttpStatus.OK)
    }

    fun findByUsername(nombre: String): Usuario {
        return usuarioRepository.findByUsername(nombre).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
        }
    }
}