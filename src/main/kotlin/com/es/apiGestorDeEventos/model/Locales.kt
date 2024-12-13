package com.es.apiGestorDeEventos.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "local_de_eventos")
data class Locales(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idLocal: Long? = null, // Identificador único del local

    @Column(nullable = false)
    var nombre: String? = null, // Nombre o título del local

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoDeLocal: TipoDeLocal? = null, // Tipo de local (Enum)

    @Column(nullable = false)
    var direccion: String? = null, // Dirección del local

    @Column(nullable = false)
    var descripcion: String? = null, // Descripción del local

    @Column(nullable = false)
    var aforoMaximo: Int? = null, // Aforo máximo s

    @Column(nullable = false)
    var precioInvitados: BigDecimal? = null, // Precio por invitado

    @Column(nullable = false)
    var menuDisponible: Boolean = false, // Indica si el local ofrece menú

    @Column()
    var precioMenu: BigDecimal? = null, // Precio del menú por persona

    @Column()
    var descripcionMenu: String? = null, // Descripción del menú f

    @ManyToOne()
    @JoinColumn(name = "id_propietario", nullable = false)
    var propietario: Usuario? = null, // Propietario del local

    @OneToMany(mappedBy = "local", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reservas: MutableList<Reservas> = mutableListOf(), // Reservas asociadas al local
)

enum class TipoDeLocal {
    BAR, VENTA, SALON_DE_CELEBRACIONES, IGLESIA, RESTAURANTE
}