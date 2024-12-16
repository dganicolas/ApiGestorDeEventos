package com.es.apiGestorDeEventos.controller

import com.es.apiGestorDeEventos.model.Reservas
import com.es.apiGestorDeEventos.service.ReservasService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reservas")
class ReservasController {

    @Autowired
    private lateinit var reservaService: ReservasService

    @PostMapping("/register")
    fun register(
        @RequestBody newReserva: Reservas,
        authentication: Authentication,
    ): ResponseEntity<Any>? {

        if (newReserva.local == null) {
            return ResponseEntity(mapOf("ERROR" to "la reserva debe de tener un local"), HttpStatus.BAD_REQUEST)
        }
        return reservaService.registerReserva(authentication, newReserva)

    }


    @DeleteMapping("/eliminarReserva/{id}")
    fun deleteUser(
        @PathVariable id: String, authentication: Authentication,
    ): ResponseEntity<Any>? {
        return reservaService.deleteReserva(id, authentication)
    }


    @PutMapping("/actualizarreserva")
    fun updateUser(
        @RequestBody updateReservas: Reservas,
        authentication: Authentication,
    ): ResponseEntity<Any> {

        return reservaService.updateReserva(updateReservas,authentication)
    }


    @GetMapping("/tusreservas")
    fun verTusReserva(authentication: Authentication): ResponseEntity<Any>  {
        return reservaService.verTusReserva(authentication)
    }
}