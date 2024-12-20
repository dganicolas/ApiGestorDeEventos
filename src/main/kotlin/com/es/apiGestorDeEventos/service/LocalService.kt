package com.es.apiGestorDeEventos.service

import com.es.apiGestorDeEventos.model.Locales
import com.es.apiGestorDeEventos.repository.LocalRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class LocalService {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    private lateinit var localRepository: LocalRepository


    fun crearLocal(newLocal: Locales, authentication: Authentication): ResponseEntity<Any>? {
        val propietario = usuarioService.findByUsername(authentication.name)

        if (localRepository.findByNombre(newLocal.nombre).isPresent) return ResponseEntity(
            mapOf("mensaje" to "El nombre del local ya existe"),
            HttpStatus.CONFLICT
        )

        if (newLocal.propietario == propietario.idUsuario || authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {

            val camposErroneos = mutableListOf<String>()

            if (newLocal.nombre.isNullOrBlank()) camposErroneos.add("nombre")
            if (newLocal.tipoDeLocal == null) camposErroneos.add("tipo de local")
            if (newLocal.direccion.isNullOrBlank()) camposErroneos.add("direccion")
            if (newLocal.descripcion.isNullOrBlank()) camposErroneos.add("descripcion")
            if (newLocal.aforoMaximo == null || newLocal.aforoMaximo!! <= 0) camposErroneos.add("aforo maximo")
            if (newLocal.precioInvitados == null || newLocal.precioInvitados!! < BigDecimal.ZERO) camposErroneos.add("precio invitados")
            if (newLocal.menuDisponible && (newLocal.precioMenu == null || newLocal.precioMenu!! < BigDecimal.ZERO)) camposErroneos.add(
                "precio menu"
            )
            if (newLocal.menuDisponible && newLocal.descripcionMenu.isNullOrBlank()) camposErroneos.add("descripcion menu")

            if (camposErroneos.isNotEmpty()) {
                return ResponseEntity(
                    mapOf("Error" to "Campos inválidos o faltantes: ${camposErroneos.joinToString(", ")}"),
                    HttpStatus.BAD_REQUEST
                )
            }

            localRepository.save(newLocal)

            return ResponseEntity(mapOf("mensaje" to "Local creado correctamente"), HttpStatus.CREATED)
        }
        return ResponseEntity(mapOf("mensaje" to "Accion no autorizada"), HttpStatus.FORBIDDEN)
    }

    fun findByNombre(nombre: String): Locales? {
        return localRepository.findByNombre(nombre).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado")
        }
    }

    fun findById(id:Long): Locales? {
        return localRepository.findById(id).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado")
        }
    }

    fun comprobarSiElLocalNoTieneReservas(local: Locales): Boolean {
        local.reservas.forEach { if (it.estado?.name == "CONFIRMADA" || it.estado?.name == "PENDIENTE_DE_PAGO") return false }
        return true
    }

    fun eliminarLocal(nombre: String, authentication: Authentication): ResponseEntity<Any>? {
        val propietario = usuarioService.findByUsername(authentication.name)
        val local =
            findByNombre(nombre) ?: return ResponseEntity(mapOf("mensaje" to "Usuario no existe"), HttpStatus.NOT_FOUND)
        if (local.propietario != propietario.idUsuario && !authentication.authorities.any { it.authority == "ROLE_ADMIN" }) return ResponseEntity(
            mapOf("mensaje" to "Accion no autorizada"),
            HttpStatus.FORBIDDEN
        )

        if (comprobarSiElLocalNoTieneReservas(local)) {
            localRepository.delete(local)
            return ResponseEntity(mapOf("mensaje" to "local eliminado"), HttpStatus.OK)
        } else {
            if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
                localRepository.delete(local)
                return ResponseEntity(
                    mapOf("mensaje" to "local eliminado por privilegios de admin"),
                    HttpStatus.OK
                )
            } else {
                return ResponseEntity(mapOf("mensaje" to "El local tiene reservas vigentes"), HttpStatus.CONFLICT)
            }
        }

    }

    fun updateLocalByName(
        local: Locales,
        authentication: Authentication,
    ): ResponseEntity<Any> {
        val propietario = usuarioService.findByUsername(authentication.name)

        val localExistente = local.nombre?.let { findByNombre(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado")
        if (localExistente.propietario != propietario.idUsuario && !authentication.authorities.any { it.authority == "ROLE_ADMIN" }) return ResponseEntity(
            mapOf("mensaje" to "Accion no autorizada"),
            HttpStatus.FORBIDDEN
        )
        local.tipoDeLocal?.let { localExistente.tipoDeLocal = it }
        local.direccion?.let { localExistente.direccion = it }
        local.descripcion?.let { localExistente.descripcion = it }
        local.aforoMaximo?.let { localExistente.aforoMaximo = it }
        local.precioInvitados?.let { localExistente.precioInvitados = it }
        local.menuDisponible.let { localExistente.menuDisponible = it }
        local.precioMenu?.let { localExistente.precioMenu = it }
        local.descripcionMenu?.let { localExistente.descripcionMenu = it }
        local.propietario?.let {
            if (authentication.authorities.any { admin -> admin.authority == "ROLE_ADMIN" }) localExistente.propietario =
                it else return ResponseEntity(
                mapOf("mensaje" to "Accion no autorizada, solos los admin pueden cambiar el propietario del local"),
                HttpStatus.FORBIDDEN
            )
        }
        localRepository.save(localExistente)
        return ResponseEntity(mapOf("mensaje" to "local actualizado correctamente"), HttpStatus.OK)
    }

    fun getAllLocal(): ResponseEntity<Any> {
        val locales = localRepository.findAll()
        if (locales.isEmpty()) {
            return ResponseEntity(mapOf("mensaje" to "No hay locales registrados"), HttpStatus.OK)
        }
        val localesSoloFecha = locales.map { local ->
            local.reservas.forEach {
                it.estado = null
                it.total = null
                it.idCliente = null
                it.menuIncluido = null
                it.totalPersonas = null
                it.local = null
                it.idCliente = null
                it.tipoDeEvento = null
                it.idReserva = null
            }
            local
        }
        return ResponseEntity(localesSoloFecha, HttpStatus.OK)
    }

}