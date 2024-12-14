package com.es.apiGestorDeEventos.security



import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private lateinit var rsaKeys: RSAKeysProperties

    @Bean
    fun securityFilterChain(http:HttpSecurity):SecurityFilterChain{

        return http.csrf { csfr -> csfr.disable() }// cross-site Forgery
            .authorizeHttpRequests{ auth -> auth
                .requestMatchers("/usuarios/login","/usuarios/register").permitAll()
                .requestMatchers(HttpMethod.GET,"/usuarios/alluser").hasRole("ADMIN")
                .requestMatchers("/locales/*").authenticated()
                .anyRequest().authenticated()
            }// los recursos protegidos y publicos
            .oauth2ResourceServer{ oauth2-> oauth2.jwt(Customizer.withDefaults()) }
            .sessionManagement{ session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            .httpBasic(Customizer.withDefaults())
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    //metodo que inicializa un objeto de tipo authenticationmanager
    @Bean
    fun authenticationManger(authenticationConfiguration: AuthenticationConfiguration):AuthenticationManager{
        return authenticationConfiguration.authenticationManager
    }

    //meotod para codificar un JWT
    @Bean
    fun jwtEncoder():JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeys.publicKey).privateKey(rsaKeys.privateKey).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
        }

    //metodo para decodificar el JWT
    @Bean
    fun jwtDecoder(): JwtDecoder{
        return  NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey).build()
    }
}