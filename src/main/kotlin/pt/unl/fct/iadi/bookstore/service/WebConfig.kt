package pt.unl.fct.iadi.bookstore.service

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.unl.fct.iadi.bookstore.security.LoggingInterceptor

@Configuration
class WebConfig(private val loggingInterceptor: LoggingInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loggingInterceptor)
    }
}