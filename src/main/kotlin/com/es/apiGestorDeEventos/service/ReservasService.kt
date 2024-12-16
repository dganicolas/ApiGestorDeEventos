package com.es.apiGestorDeEventos.service

import com.es.apiGestorDeEventos.model.EstadoReserva
import com.es.apiGestorDeEventos.model.Reservas
import com.es.apiGestorDeEventos.model.TipoDeEvento
import com.es.apiGestorDeEventos.model.Usuario
import com.es.apiGestorDeEventos.repository.ReservasRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ReservasService {
    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    private lateinit var localService: LocalService

    @Autowired
    private lateinit var reservaRepository: ReservasRepository

    fun validaciones(newReserva: Reservas): ResponseEntity<Any>? {
        val camposErroneos = mutableListOf<String>()

        if (newReserva.diaEvento != null && newReserva.diaEvento!!.isBefore(LocalDate.now())) {
            camposErroneos.add("la fecha del evento no puede ser anterior a la fecha de hoy")
        }

        val comprobarDia = newReserva.diaEvento?.let { diaEvento ->
            newReserva.local?.let { idLocal ->
                reservaRepository.findByDiaEventoAndLocal(diaEvento, idLocal)
            }
        }

        if (!comprobarDia.isNullOrEmpty()) {
            camposErroneos.add("La fecha seleccionada ya está reservada para este local")
        }

        if (newReserva.tipoDeEvento == null || !TipoDeEvento.values().contains(newReserva.tipoDeEvento)) {
            camposErroneos.add("El tipo de evento es erroneo")
        }

        if (newReserva.idCliente == null) {
            camposErroneos.add("El id cliente es erroneo")
        }

        if (newReserva.local == null) {
            camposErroneos.add("el id local es erroneo")
        }

        if (newReserva.totalPersonas == null || newReserva.totalPersonas!! <= 0) {
            camposErroneos.add("el total de persona es erroneo")
        }

        if (camposErroneos.isNotEmpty()) {
            return ResponseEntity(
                mapOf("Error" to "Campos inválidos o faltantes: ${camposErroneos.joinToString(", ")}"),
                HttpStatus.BAD_REQUEST
            )
        }
        return null
    }

    fun registerReserva(authentication: Authentication, newReserva: Reservas): ResponseEntity<Any>? {

        val error = validaciones(newReserva)
        if (error != null) return error

        val usuario = usuarioService.findByUsername(authentication.name)

        val local = newReserva.local?.let { localService.findById(it) } ?: return ResponseEntity(
            mapOf("ERROR" to "El id local no existe"),
            HttpStatus.BAD_REQUEST
        )


        newReserva.idCliente = usuario.idUsuario

        newReserva.local = local.idLocal


        val total = local.precioInvitados?.multiply(newReserva.totalPersonas?.let { BigDecimal(it) })

        val totalConMenu =
            if (newReserva.menuIncluido == true) {
                total?.add(
                    local.precioMenu?.multiply(
                        newReserva.totalPersonas?.let { BigDecimal(it) }) ?: BigDecimal(0)
                )
            } else {
                total
            }
        if (total == BigDecimal(0)) return ResponseEntity(
            mapOf("ERROR" to "Si ves este error, contacta a soporte tecnico"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )

        newReserva.total = totalConMenu

        newReserva.estado = EstadoReserva.PENDIENTE_DE_PAGO

        reservaRepository.save(newReserva)

        return ResponseEntity(newReserva, HttpStatus.CREATED)
    }

    fun deleteReserva(id: String, authentication: Authentication): ResponseEntity<Any>? {
        val idReserva = id.toLongOrNull() ?: return ResponseEntity(
            mapOf("ERROR" to "El id reserva debe de ser un numero"),
            HttpStatus.BAD_REQUEST
        )

        val usuario = usuarioService.findByUsername(authentication.name)
        val reservas = reservaRepository.findById(idReserva).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "id reserva no encontrada")
        } ?: return ResponseEntity(
            mapOf("ERROR" to "id reserva no encontrada"),
            HttpStatus.BAD_REQUEST
        )

        if (reservas.idReserva != usuario.idUsuario && !authentication.authorities.any { it.authority == "ROLE_ADMIN" }) return ResponseEntity(
            mapOf("mensaje" to "Accion no autorizada"),
            HttpStatus.FORBIDDEN
        )
        reservaRepository.delete(reservas)

        return ResponseEntity(mapOf("mensaje" to "Reserva eliminada"), HttpStatus.OK)
    }

    fun validacionesActualizar(
        updateReservas: Reservas,
        usuario: Usuario,
        authentication: Authentication,
        reservaExistente: Reservas
    ): ResponseEntity<Any>? {

        if (reservaExistente.idCliente != usuario.idUsuario &&
            !authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            return ResponseEntity(mapOf("mensaje" to "Acción no autorizada"), HttpStatus.FORBIDDEN)
        }

        if (updateReservas.diaEvento != null && updateReservas.diaEvento != reservaExistente.diaEvento) {
            return ResponseEntity(mapOf("mensaje" to "La fecha del evento no puede ser modificada"), HttpStatus.BAD_REQUEST)
        }

        if (updateReservas.local != null && updateReservas.local != reservaExistente.local) {
            return ResponseEntity(mapOf("mensaje" to "No se puede cambiar el local asociado a la reserva"), HttpStatus.BAD_REQUEST)
        }

        if (updateReservas.estado != null && updateReservas.estado != reservaExistente.estado) {
            return ResponseEntity(mapOf("mensaje" to "El estado de la reserva no puede ser modificado"), HttpStatus.BAD_REQUEST)
        }
        return null
    }

    fun updateReserva(updateReservas: Reservas, authentication: Authentication): ResponseEntity<Any> {
        val usuario = usuarioService.findByUsername(authentication.name)
        val reservaExistente = reservaRepository.findById(updateReservas.idReserva ?: 0L).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada")
        }
        val error = validacionesActualizar(updateReservas,usuario,authentication,reservaExistente)
        if (error != null) return error

        val local = localService.findById(reservaExistente.local ?: 0L)
        val total = local?.precioInvitados?.multiply(updateReservas.totalPersonas?.let { BigDecimal(it) })

        val totalConMenu = if (updateReservas.menuIncluido == true) {
            total?.add(local.precioMenu?.multiply(
                updateReservas.totalPersonas?.let { BigDecimal(it) }
            ) ?: BigDecimal(0))
        } else {
            total
        }
        if (total == BigDecimal(0)) {
            return ResponseEntity(mapOf("ERROR" to "Si ves este error, contacta a soporte técnico"), HttpStatus.INTERNAL_SERVER_ERROR)
        }

        reservaExistente.total = totalConMenu

        if (updateReservas.idCliente != null && authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            reservaExistente.idCliente = updateReservas.idCliente
        }

        reservaRepository.save(reservaExistente)

        return ResponseEntity(mapOf("mensaje" to "Reserva actualizada correctamente"), HttpStatus.OK)
    }

    fun verTusReserva(authentication: Authentication): ResponseEntity<Any> {
        val idCliente = usuarioService.findByUsername(authentication.name)

        val reservas = idCliente.idUsuario?.let { reservaRepository.findByIdCliente(it) }

        if (reservas != null) {
            return if (reservas.isEmpty()) {
                ResponseEntity(mapOf("mensaje" to "No tienes reservas registradas"), HttpStatus.OK)
            } else {
                ResponseEntity(reservas, HttpStatus.OK)
            }
        }else{
            return ResponseEntity(mapOf("ERROR" to "Si ves este error, contacta a soporte técnico"), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}