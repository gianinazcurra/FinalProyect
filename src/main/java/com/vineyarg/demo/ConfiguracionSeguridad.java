/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo;

import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.java.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ConfiguracionSeguridad extends WebSecurityConfigurerAdapter {

    @Autowired
    UsuarioServicio usuarioServicio = new UsuarioServicio();

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    public void ConfigureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usuarioServicio)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MySimpleUrlAuthenticationSuccessHandler();
    }

    public class MySimpleUrlAuthenticationSuccessHandler
            implements AuthenticationSuccessHandler {

        protected org.apache.commons.logging.Log logger = LogFactory.getLog(this.getClass());

        private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                HttpServletResponse response, Authentication authentication)
                throws IOException {

            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        }

        protected void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication
        ) throws IOException {

            String targetUrl = determineTargetUrl(authentication);

            if (response.isCommitted()) {
                logger.debug(
                        "Response has already been committed. Unable to redirect to "
                        + targetUrl);
                return;
            }

            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    protected String determineTargetUrl(final Authentication authentication) {

        User usuario = (User) authentication.getPrincipal();
        Usuario usuario1 = usuarioRepositorio.BuscarUsuarioPorCorreo(usuario.getUsername());
        String id = usuario1.getId();

        Map<String, String> roleTargetUrlMap = new HashMap<>();

        roleTargetUrlMap.put("ROLE_ADMINISTRADOR", "/administradorweb?id=" + id);
        roleTargetUrlMap.put("ROLE_USUARIO_COMUN", "/tienda");
        roleTargetUrlMap.put("ROLE_PRODUCTOR", "/productorweb?id=" + id);

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }

        throw new IllegalStateException();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests().anyRequest().permitAll();

        http.authorizeRequests().antMatchers("/css/*", "/js/*", "/img/*", "/**").permitAll().and().formLogin()
                .loginPage("/logueo").loginProcessingUrl("/logincheck").usernameParameter("correo")
                .passwordParameter("clave").successHandler(myAuthenticationSuccessHandler()).failureUrl("/logueo/?error=error")
                .permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/?logout=logout").permitAll().and().csrf()
                .disable();
                http.sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/?error='Cambió la sesión'");
      
    }

}
