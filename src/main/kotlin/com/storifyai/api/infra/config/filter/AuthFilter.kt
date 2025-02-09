package com.storifyai.api.infra.config.filter

import com.storifyai.api.infra.config.wrapper.CustomHttpServletRequestWrapper
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import io.jsonwebtoken.security.SignatureException

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestCachingFilter", urlPatterns = ["/*"])
class AuthFilter : OncePerRequestFilter() {

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHttpConfig = request.getHeader("Authorization")

        if (authorizationHttpConfig == "" || authorizationHttpConfig == null) {
            throw ServletException("Authorization header is missing")
        }

        val cleanToken = authorizationHttpConfig.trim().replace("Bearer ", "").trim()
        if (cleanToken.isEmpty()) {
            throw ServletException("Authorization bearer is missing")
        }

        val customWrappedRequest = CustomHttpServletRequestWrapper(request as HttpServletRequest)

        try {
            val claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(cleanToken)
                .body

            val user = claims.entries.associate { it.key to it.value }
            customWrappedRequest.addHeader("userId", user["id"].toString())

        } catch (ex: SignatureException) {
            throw ServletException("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            throw ServletException("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            throw ServletException("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            throw ServletException("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            throw ServletException("JWT claims string is empty")
        }

        filterChain.doFilter(customWrappedRequest, response)
    }

    private fun getSigningKey(): SecretKey {
        if (jwtSecret.toByteArray(StandardCharsets.UTF_8).size < 32) {
            throw IllegalArgumentException("JWT secret must be at least 32 bytes")
        }
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
    }

}

data class User(val id : String, val email: String)