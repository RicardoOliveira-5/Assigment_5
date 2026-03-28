package pt.unl.fct.iadi.bookstore.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
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
@SecuritySchemes(
    SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
    ),
    SecurityScheme(
        name = "apiToken",
        type = SecuritySchemeType.APIKEY,
        `in` = SecuritySchemeIn.HEADER,
        paramName = "X-Api-Token",
    ),
)
class SecurityConfig {

    companion object {
        const val ROLE_EDITOR = "EDITOR"
        const val ROLE_ADMIN = "ADMIN"

        const val BOOKS_ENDPOINT = "/books/**"
        const val SWAGGER_UI = "/swagger-ui/**"
        const val V3_API_DOCS = "/v3/api-docs/**"
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
                // 🔓 Swagger/OpenAPI público
                .requestMatchers(
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/books/**").permitAll()

                .anyRequest().permitAll()
        }

        http.httpBasic {}

        return http.build()
    }
}