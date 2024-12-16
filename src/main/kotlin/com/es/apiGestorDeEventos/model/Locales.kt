package com.es.apiGestorDeEventos.model

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "local_de_eventos")
data class Locales(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idLocal: Long? = null,

    @Column(nullable = false)
    var nombre: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoDeLocal: TipoDeLocal? = null,

    @Column(nullable = false)
    var direccion: String? = null,

    @Column(nullable = false)
    var descripcion: String? = null,

    @Column(nullable = false)
    var aforoMaximo: Int? = null,

    @Column(nullable = false)
    var precioInvitados: BigDecimal? = null,

    @Column(nullable = false)
    var menuDisponible: Boolean = false,

    @Column()
    var precioMenu: BigDecimal? = null,

    @Column()
    var descripcionMenu: String? = null,

    @Column(name = "id_propetario", nullable = false)
    var propietario: Long? = null,

    @OneToMany(mappedBy = "local", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reservas: MutableList<Reservas> = mutableListOf(),
)

enum class TipoDeLocal {
    BAR, VENTA, SALON_DE_CELEBRACIONES, IGLESIA, RESTAURANTE
}