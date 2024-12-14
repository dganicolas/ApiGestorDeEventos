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


    //DOCUMENTADO EN EL README
    @PostMapping("/register")
    fun register(
        @RequestBody newLocal: Locales,
        authentication: Authentication,
    ): ResponseEntity<Any>? {
        return localService.crearLocal(newLocal, authentication)
    }

    //DOCUMENTADO EN EL README
    @DeleteMapping("/eliminarlocal/{nombre}")
    fun deleteLocal(
        @PathVariable nombre: String, authentication: Authentication,
    ): ResponseEntity<Any>? {
        return localService.eliminarLocal(nombre, authentication)
    }

    //DOCUMENTADO EN EL README
    @PutMapping("/actualizarlocal")
    fun updateUser(
        @RequestBody local: Locales,
        authentication: Authentication,
    ): ResponseEntity<Any>? {
        return localService.updateLocalByName(local, authentication)
    }

    @GetMapping("/alllocal")
    fun allLocal(authentication: Authentication): ResponseEntity<Any> {
        return localService.getAllLocal()
    }
}