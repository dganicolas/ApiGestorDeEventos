package com.es.apiGestorDeEventos.model


import jakarta.persistence.*

@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idUsuario: Long? = null,

    @Column(unique = true, nullable = false)
    var username: String? = null,

    @Column(nullable = false)
    var password: String? = null,

    var roles: String? = null,

    @OneToMany(mappedBy = "propietario", cascade = [CascadeType.ALL], orphanRemoval = true)

    var locales: MutableList<Locales> = mutableListOf(),

    @OneToMany(mappedBy = "idCliente", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reservas: MutableList<Reservas> = mutableListOf(),
)

