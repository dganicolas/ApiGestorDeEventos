package com.es.apiGestorDeEventos.repository

import com.es.apiGestorDeEventos.model.Locales
import com.es.apiGestorDeEventos.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LocalRepository : JpaRepository<Locales, Long> {
    fun findByNombre(nombre:String?): Optional<Locales>
}