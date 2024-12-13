package com.es.apiGestorDeEventos.model

import jakarta.persistence.*

@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var username: String? = null,

    @Column(nullable = false)
    var password: String? = null,

    var roles: String? = null, // e.g., "ROLE_USER,ROLE_ADMIN"

    @OneToMany(mappedBy = "propietario", cascade = [CascadeType.ALL], orphanRemoval = true)
    var locales: MutableList<Locales> = mutableListOf(), // Locales asociados al usuario

    @OneToMany(mappedBy = "usuario", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reservas: MutableList<Reservas> = mutableListOf(), // Reservas asociadas al usuario
)

