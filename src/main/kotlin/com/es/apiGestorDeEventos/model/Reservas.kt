package com.es.apiGestorDeEventos.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "reservas")
data class Reservas(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idReserva: Long? = null,

    @Column(nullable = false)
    var diaEvento: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoDeEvento: TipoDeEvento?,

    @Column(name = "id_cliente", nullable = false)
    var idCliente: Long?,

    @Column(name = "id_local", nullable = false)
    var local: Long?,

    @Column(nullable = false)
    var totalPersonas: Int?,

    @Column(nullable = false)
    var menuIncluido: Boolean?,

    @Column(nullable = false)
    var total: BigDecimal?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var estado: EstadoReserva?
)

enum class TipoDeEvento {
    CUMPLEANOS, BODA, DESPEDIDA_DE_SOLTERO, BAUTIZO
}

enum class EstadoReserva {
    REALIZADA, PENDIENTE_DE_PAGO, CONFIRMADA, DENEGADA
}