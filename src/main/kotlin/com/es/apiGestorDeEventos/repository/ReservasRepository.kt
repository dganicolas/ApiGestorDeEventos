package com.es.apiGestorDeEventos.repository

import com.es.apiGestorDeEventos.model.Reservas
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ReservasRepository: JpaRepository<Reservas, Long> {

    fun findByDiaEventoAndLocal(diaEvento: LocalDate, local: Long): List<Reservas>

    fun findByIdCliente(idCliente: Long): List<Reservas>
}