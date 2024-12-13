package com.es.apiGestorDeEventos.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.Date

@Service
class TokenService {

//prueba132
    @Autowired
    private lateinit var jwtEncoder:JwtEncoder


    fun generarToken(authentication: Authentication): String {
        val roles: String = authentication
            .authorities
            .map { it.authority }
            .joinToString(" ")//contiene los roles del usuario

        val payLoad: JwtClaimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(Instant.now())
            .expiresAt(Date().toInstant().plus(Duration.ofHours(1)))
            .subject(authentication.name)
            .claim("roles",roles)
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(payLoad)).tokenValue
    }
}