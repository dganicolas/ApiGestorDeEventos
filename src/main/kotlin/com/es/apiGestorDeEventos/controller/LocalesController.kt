package com.es.apiGestorDeEventos.controller

import com.es.apiGestorDeEventos.model.Locales
import com.es.apiGestorDeEventos.model.Usuario
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locales")
class LocalesController {

//    @PostMapping("/register")
//    fun register(
//        @RequestBody newLocal: Locales
//    ) : ResponseEntity<Any>? {
//
//    }
//
//    @DeleteMapping("/eliminarusuario/{nombre}")
//    fun deleteUser(
//        @PathVariable nombre: String, authentication: Authentication
//    ): ResponseEntity<Any>? {
//    }
//
//    @PutMapping("/actualizarusuario/{nombre}")
//    fun updateUser(
//        @PathVariable nombre: String,
//        @RequestBody updatedUser: Usuario,
//        authentication: Authentication
//    ): ResponseEntity<Any>? {
//
//    }
//
//    @GetMapping("/allUser")
//    fun allUser(authentication: Authentication): ResponseEntity<Any> {
//        if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
//            usuarioService.getAllUsers()
//        }
//        return ResponseEntity(mapOf("mensaje" to "Acción no autorizada"), HttpStatus.FORBIDDEN)
//    }
}