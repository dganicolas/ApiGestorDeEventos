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
        val propietario = usuarioService.findByUsername(authentication.name) ?: return ResponseEntity(mapOf("mensaje" to "Usuario no existe"), HttpStatus.NOT_FOUND)

        if(localRepository.findByNombre(newLocal.nombre).isPresent) return ResponseEntity(mapOf("mensaje" to "El nombre del local ya existe"), HttpStatus.CONFLICT)

        if (newLocal.propietario == propietario || authentication.authorities.any { it.authority == "ROLE_ADMIN" }){

            val camposErroneos = mutableListOf<String>()

            if (newLocal.nombre.isNullOrBlank()) camposErroneos.add("nombre")
            if (newLocal.tipoDeLocal == null) camposErroneos.add("tipo de local")
            if (newLocal.direccion.isNullOrBlank()) camposErroneos.add("direccion")
            if (newLocal.descripcion.isNullOrBlank()) camposErroneos.add("descripcion")
            if (newLocal.aforoMaximo == null || newLocal.aforoMaximo!! <= 0) camposErroneos.add("aforo maximo")
            if (newLocal.precioInvitados == null || newLocal.precioInvitados!! < BigDecimal.ZERO) camposErroneos.add("precio invitados")
            if (newLocal.menuDisponible && (newLocal.precioMenu == null || newLocal.precioMenu!! < BigDecimal.ZERO)) camposErroneos.add("precio menu")
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
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
        }
    }
    fun comprobarSiElLocalNoTieneReservas(local: Locales):Boolean{
        local.reservas.forEach { if(it.estado.name == "CONFIRMADA"||it.estado.name == "PENDIENTE_DE_PAGO")return false }
        return true
    }

    fun eliminarLocal(nombre: String, authentication: Authentication): ResponseEntity<Any>? {
        val propietario = usuarioService.findByUsername(authentication.name) ?: return ResponseEntity(mapOf("mensaje" to "Usuario no existe"), HttpStatus.NOT_FOUND)
        val local = findByNombre(nombre) ?: return ResponseEntity(mapOf("mensaje" to "Usuario no existe"), HttpStatus.NOT_FOUND)

        if (local.propietario == propietario || authentication.authorities.any { it.authority == "ROLE_ADMIN" }){

            if(comprobarSiElLocalNoTieneReservas(local)){
                localRepository.delete(local)
                return ResponseEntity(mapOf("mensaje" to "local eliminado"), HttpStatus.NO_CONTENT)
            }
            else{
                if(authentication.authorities.any { it.authority == "ROLE_ADMIN" }){
                    localRepository.delete(local)
                    return ResponseEntity(mapOf("mensaje" to "local eliminado por privilegios de admin"), HttpStatus.NO_CONTENT)
                }
                return ResponseEntity(mapOf("mensaje" to "El local tiene reservas vigentes"), HttpStatus.CONFLICT)
            }
        }
        return ResponseEntity(mapOf("mensaje" to "Accion no autorizada"), HttpStatus.FORBIDDEN)
    }

}