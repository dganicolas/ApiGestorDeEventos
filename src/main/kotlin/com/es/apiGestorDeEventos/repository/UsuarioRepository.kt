package com.es.apiGestorDeEventos.repository

import com.es.apiGestorDeEventos.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UsuarioRepository : JpaRepository<Usuario, Long> {

    // Implementar una derived query para obtener a un usuario por su nombre
    fun findByUsername(username:String?):Optional<Usuario>
}