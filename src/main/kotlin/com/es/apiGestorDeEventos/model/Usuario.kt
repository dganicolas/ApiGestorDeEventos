package com.es.apiGestorDeEventos.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
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

    var roles: String? = null, // e.g., "ROLE_USER,ROLE_ADMIN"

    @OneToMany(mappedBy = "propietario", cascade = [CascadeType.ALL], orphanRemoval = true)
    //@JsonBackReference // Esto previene la serialización de la relación hacia atrás
    var locales: MutableList<Locales> = mutableListOf(), // Lista de locales asociados a este usuario, pero solo con los IDs de los locales

    @OneToMany(mappedBy = "idCliente", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reservas: MutableList<Reservas> = mutableListOf(),
)

