package br.com.ope.security.jwt.provider

import br.com.ope.security.UserDetailsServiceImpl
import br.com.ope.security.jwt.exceptions.UsuarioDesabilitadoException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class LoginAuthenticationProvider @Autowired
constructor(private val usuarioNameRepository: UserDetailsServiceImpl) : AuthenticationProvider {
    private val encoder: BCryptPasswordEncoder

    init {
        this.encoder = BCryptPasswordEncoder()
    }

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        Assert.notNull(authentication, "Dados de autenticação não inseridos")

        val login = authentication.principal as String
        val senha = authentication.credentials as String

        //TODO UsuarioAuth
        val usuario = usuarioNameRepository.loadUserByUsername(login)

        if (!encoder.matches(senha, usuario.password)) {
            throw BadCredentialsException("Usuário ou senha estão incorretos")
        }

        if (!usuario.isEnabled) {
            throw UsuarioDesabilitadoException("Usuario nao esta habilitado")
        }

        return UsernamePasswordAuthenticationToken(usuario, null, usuario.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
