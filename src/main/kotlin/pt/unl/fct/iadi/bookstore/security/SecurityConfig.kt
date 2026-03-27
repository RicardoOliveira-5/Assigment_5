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
    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()
    @Bean
    fun userDetailsService(encoder: PasswordEncoder) = InMemoryUserDetailsManager (
        User.withUsername("editor1")
            .password(encoder.encode("editor1pass"))
            .roles("EDITOR")
            .build(),

        User.withUsername("editor2")
            .password(encoder.encode("editor2pass"))
            .roles("EDITOR")
            .build(),

        User.withUsername("admin")
            .password(encoder.encode("adminpass"))
            .roles("ADMIN")
            .build())


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }

            .authorizeHttpRequests { auth ->
                auth
                    // Swagger público
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll()

                    // GET requests → qualquer utilizador autenticado
                    .requestMatchers(HttpMethod.GET, "/books/**").authenticated()

                    // Criar / editar livros → EDITOR ou ADMIN
                    .requestMatchers(HttpMethod.POST, "/books/**").hasAnyRole("EDITOR","ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/books/**").hasAnyRole("EDITOR","ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/books/**").hasAnyRole("EDITOR","ADMIN")

                    // DELETE book → ADMIN
                    .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                    // resto autenticado
                    .anyRequest().authenticated()
            }

            .httpBasic {}

        return http.build()
    }
}