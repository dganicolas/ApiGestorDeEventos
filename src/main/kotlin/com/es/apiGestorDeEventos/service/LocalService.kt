package com.es.apiGestorDeEventos.service

import com.es.apiGestorDeEventos.model.Locales
import com.es.apiGestorDeEventos.repository.LocalRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class LocalService {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    private lateinit var localRepository: LocalRepository

    fun crearLocal(newLocal: Locales, authentication: Authentication): ResponseEntity<Any>? {
        val propietario = newLocal.propietario?.username?.let { usuarioService.findByUsername(it) } ?: return ResponseEntity(mapOf("mensaje" to "Usuario no existe"), HttpStatus.NOT_FOUND)
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

}