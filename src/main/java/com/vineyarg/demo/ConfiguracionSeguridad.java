/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo;


import com.vineyarg.demo.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ConfiguracionSeguridad extends WebSecurityConfigurerAdapter {

    @Autowired
   UsuarioServicio usuarioServicio = new UsuarioServicio();
     
    

    @Autowired
    public void ConfigureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usuarioServicio)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests().anyRequest().permitAll();

       http.authorizeRequests().antMatchers("/css/*", "/js/*", "/img/*", "/**").permitAll().and().formLogin()
				.loginPage("/").loginProcessingUrl("/logincheck").usernameParameter("correo")
				.passwordParameter("clave").defaultSuccessUrl("/inicio").failureUrl("/?error=error")
				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/?logout=logout").permitAll().and().csrf()
				.disable();
       
       
       //PRUEBA DIFERENTES USUARIOS:
       
//       http.authorizeRequests().antMatchers("/css/*", "/js/*", "/img/*", "/**").access("hasRole('ROLE_ADMINISTRADOR')").and().formLogin()
//				.loginPage("/").loginProcessingUrl("/logincheck").usernameParameter("correo")
//				.passwordParameter("clave").defaultSuccessUrl("/administradorweb").failureUrl("/?error=error")
//				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/?logout=logout").permitAll().and()
//               .authorizeRequests().antMatchers("/css/*", "/js/*", "/img/*", "/**").access("hasRole('ROLE_USUARIO_COMUN')").and().formLogin()
//				.loginPage("/").loginProcessingUrl("/logincheck").usernameParameter("correo")
//				.passwordParameter("clave").defaultSuccessUrl("/usuarioweb").failureUrl("/?error=error")
//				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/?logout=logout").permitAll().and()
//               .authorizeRequests().antMatchers("/css/*", "/js/*", "/img/*", "/**").access("hasRole('ROLE_PRODUCTOR')").and().formLogin()
//				.loginPage("/").loginProcessingUrl("/logincheck").usernameParameter("correo")
//				.passwordParameter("clave").defaultSuccessUrl("/productorweb").failureUrl("/?error=error")
//				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/?logout=logout").permitAll()
//                                .and().csrf().disable();
    }
}
