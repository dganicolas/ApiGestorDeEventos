package com.es.apiGestorDeEventos

import com.es.apiGestorDeEventos.security.RSAKeysProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RSAKeysProperties::class)
class apiGestorDeEventos

fun main(args: Array<String>) {
	runApplication<apiGestorDeEventos>(*args)
}
