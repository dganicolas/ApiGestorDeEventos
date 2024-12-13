package com.es.apiGestorDeEventos.repository

import com.es.apiGestorDeEventos.model.Locales
import org.springframework.data.jpa.repository.JpaRepository

interface LocalRepository : JpaRepository<Locales, Long> {
}