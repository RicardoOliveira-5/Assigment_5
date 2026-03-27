package pt.unl.fct.iadi.bookstore.security

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    companion object {
        const val ROLE_EDITOR = "EDITOR"
        const val ROLE_ADMIN = "ADMIN"
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(encoder: PasswordEncoder) = InMemoryUserDetailsManager(
        User.withUsername("editor1")
            .password(encoder.encode("editor1pass"))
            .roles(ROLE_EDITOR)
            .build(),

        User.withUsername("editor2")
            .password(encoder.encode("editor2pass"))
            .roles(ROLE_EDITOR)
            .build(),

        User.withUsername("admin")
            .password(encoder.encode("adminpass"))
            .roles(ROLE_ADMIN)
            .build()
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }

        http.authorizeHttpRequests { auth ->
            auth
                // Swagger público
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // GET requests → qualquer utilizador autenticado
                .requestMatchers(HttpMethod.GET, "/books/**").authenticated()

                // Criar / editar livros → EDITOR ou ADMIN
                .requestMatchers(HttpMethod.POST, "/books/**").hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, "/books/**").hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
                .requestMatchers(HttpMethod.PATCH, "/books/**").hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)

                // DELETE book → ADMIN
                .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole(ROLE_ADMIN)

                // resto autenticado
                .anyRequest().authenticated()
        }

        http.httpBasic {}
        return http.build()
    }
}