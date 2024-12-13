package com.es.apiGestorDeEventos.controller

import com.es.apiGestorDeEventos.model.Locales
import com.es.apiGestorDeEventos.service.LocalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locales")
class LocalesController {

    @Autowired
    private lateinit var localService: LocalService


    @PostMapping("/register")
    fun register(
        @RequestBody newLocal: Locales,
        authentication: Authentication
    ): ResponseEntity<Any>? {
        return localService.crearLocal(newLocal,authentication)
    }

    @DeleteMapping("/eliminarlocal/{nombre}")
    fun deleteLocal(
        @PathVariable nombre: String, authentication: Authentication
    ): ResponseEntity<Any>? {
        if(nombre.isBlank()) return ResponseEntity(mapOf("mensajes" to "el nombre no debe estar vacio"), HttpStatus.BAD_REQUEST)
        return localService.eliminarLocal(nombre,authentication)
    }

    @PutMapping("/actualizarlocal/{nombre}")
    fun updateUser(
        @PathVariable nombre: String,
        @RequestBody local: Locales,
        authentication: Authentication
    ): ResponseEntity<Any>? {

        if (authentication.name == nombre || authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            localService.updateLocalByName(nombre, local,authentication)
        }
        return ResponseEntity(mapOf("mensaje" to "Acción no autorizada"), HttpStatus.FORBIDDEN)
    }

    @GetMapping("/alllocal")
    fun allLocal(authentication: Authentication): ResponseEntity<Any> {
        return localService.getAllLocal()
    }
}