package com.es.apiGestorDeEventos.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "reservas")
data class Reservas(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idReserva: Long? = null, // Identificador único de la reserva

    @Column(nullable = false)
    var diaEvento: LocalDate, // Fecha del evento (debe ser >= CURRENT_DATE)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoDeEvento: TipoDeEvento, // Tipo de evento (Enum)

    @ManyToOne()
    @JoinColumn(name = "id_usuario", nullable = false)
    var usuario: Usuario, // Usuario que creó la reserva

    @ManyToOne()
    @JoinColumn(name = "id_local", nullable = false)
    var local: Locales, // Local asociado a la reserva

    @Column(nullable = false)
    var totalPersonas: Int, // Número total de personas para la reserva

    @Column(nullable = false)
    var menuIncluido: Boolean, // Indica si se incluye el menú

    @Column(nullable = false)
    var total: BigDecimal, // Total calculado de la reserva

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var estado: EstadoReserva // Estado de la reserva (Enum)
)

enum class TipoDeEvento {
    CUMPLEANOS, BODA, DESPEDIDA_DE_SOLTERO, BAUTIZO
}

enum class EstadoReserva {
    REALIZADA, PENDIENTE_DE_PAGO, CONFIRMADA, DENEGADA
}