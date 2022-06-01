/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.TipoUsuario;
import static com.vineyarg.demo.enumeraciones.TipoUsuario.ADMINISTRADOR;
import static com.vineyarg.demo.enumeraciones.TipoUsuario.USUARIOCOMUN;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author joaqu
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void registrarUsuario(MultipartFile archivo, String nombre, String apellido, String DNI, String correo, String clave1, String clave2, Date fechaNacimiento, TipoUsuario tipoUsuario) throws Excepcion {

        validar(nombre, apellido, DNI, correo, clave1, clave2, fechaNacimiento);

        if (tipoUsuario.equals(ADMINISTRADOR)) {
            Usuario admin = new Usuario();

            admin.setNombre(nombre);
            admin.setApellido(apellido);
            admin.setDNI(DNI);
            admin.setCorreo(correo);
            String encriptada = new BCryptPasswordEncoder().encode(clave1);
            admin.setClave(encriptada);
            admin.setFechaNacimiento(fechaNacimiento);
            admin.setAlta(true);
            admin.setTipoUsuario(tipoUsuario);
            
            usuarioRepositorio.save(admin);

        }

        if (tipoUsuario.equals(USUARIOCOMUN)) {
            Usuario usuario = new Usuario();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDNI(DNI);
            usuario.setCorreo(correo);
            String encriptada = new BCryptPasswordEncoder().encode(clave1);
            usuario.setClave(encriptada);
            usuario.setFechaNacimiento(fechaNacimiento);
            usuario.setAlta(true);
            usuario.setTipoUsuario(tipoUsuario);
            usuario.setTotalComprasEfectuadas(0);
            usuario.setTotalDineroComprado(0.0);
            
            usuarioRepositorio.save(usuario);
        }

    }

    @Transactional
    public void modificarUsuario(MultipartFile archivo, String nombre, String apellido, String DNI, String correo, String clave1, String clave2, Date fechaNacimiento, TipoUsuario tipoUsuario) throws Excepcion {

        Usuario verificacionUsuario = usuarioRepositorio.BuscarUsuarioPorCorreoYClave(correo, clave1);

        if (verificacionUsuario == null) {
            throw new Excepcion("Usuario no encontrado");

        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(usuarioRepositorio.BuscarUsuarioPorCorreoYClave(correo, clave1).getId());

        if (respuesta.isPresent()) {

            validar(nombre, apellido, DNI, correo, clave1, clave2, fechaNacimiento);

            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDNI(DNI);
            usuario.setCorreo(correo);
            String encriptada = new BCryptPasswordEncoder().encode(clave1);
            usuario.setClave(encriptada);
            usuario.setFechaNacimiento(fechaNacimiento);
            usuario.setAlta(true);
            usuario.setTipoUsuario(tipoUsuario);

            usuarioRepositorio.save(usuario);
        } else {

            throw new Excepcion("Usuario o clave no hallada");
        }

    }

    @Transactional
    public void eliminarUsuario(@RequestParam String correo, @RequestParam String clave) throws Excepcion {

        Usuario verificacionUsuario = usuarioRepositorio.BuscarUsuarioPorCorreoYClave(correo, clave);

        if (verificacionUsuario == null) {
            throw new Excepcion("Usuario no encontrado");

        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(usuarioRepositorio.BuscarUsuarioPorCorreoYClave(correo, clave).getId());

        if (respuesta.isPresent()) {

            Usuario usuarioOAdmin = respuesta.get();

            usuarioRepositorio.delete(usuarioOAdmin);
        } else {

            throw new Excepcion("Usuario o clave no hallada");
        }

    }

    public void validar(String nombre, String apellido, String DNI, String correo, String clave1, String clave2, Date fechaNacimiento) throws Excepcion {

        if (nombre.trim() == null || nombre.trim().isEmpty()) {
            throw new Excepcion("nombre inválido");
        }
        if (apellido.trim() == null || apellido.trim().isEmpty()) {
            throw new Excepcion("apellido inválido");

        }
        if (DNI.trim() == null || nombre.trim().isEmpty() || DNI.length() < 8 || DNI.length() > 8) {

            throw new Excepcion("DNI inválido. Debe tener 8 dígitos. Si tiene menos, por favor inicie con 0");

        }
        if (!correo.contains("@") || !correo.contains(".") || correo.trim() == null || correo.trim().isEmpty()) {
            throw new Excepcion("E-mail inválido");
        }
        if (clave1.trim() == null || clave1.trim().isEmpty() || clave1.trim().length() < 5) {
            throw new Excepcion("La contraseña no puede ser nula");
        }

        char ch;
        boolean verificacionClaveMayuscula = false;
        boolean verificacionClaveNumero = false;

        for (int i = 0; i < clave1.length(); i++) {

            ch = (char) i;
            if (Character.isUpperCase(ch)) {
                verificacionClaveMayuscula = true;
                break;
            };
        }
        for (int i = 0; i < clave1.length(); i++) {

            ch = (char) i;
            if (Character.isDigit(ch)) {
                verificacionClaveNumero = true;
                break;
            };
        }

        if (verificacionClaveMayuscula == false || verificacionClaveNumero == false || clave1.trim().length() < 5) {
            throw new Excepcion("La contraseña no cumple con los requisitos especificados");
        }

        if (!clave2.trim().equalsIgnoreCase(clave1.trim())) {
            throw new Excepcion("Las contraseñas ingresadas no son iguales");
        }

        int edad;
        Date fechaActual = new Date();
        Calendar calendario = new GregorianCalendar();
        calendario.setTime(fechaActual);
        edad = calendario.get(Calendar.YEAR) - (fechaNacimiento.getYear() + 1900);

        if ((calendario.get(Calendar.MONTH) < fechaNacimiento.getMonth())
                || (calendario.get(Calendar.MONTH) == fechaNacimiento.getMonth())
                && (calendario.get(Calendar.DAY_OF_MONTH) < fechaNacimiento.getDay())) {
            edad--;
        }
        if (edad < 18) {
            throw new Excepcion("Debés tener más de 18 años para crear un Usuario y poder adquirir productos");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.BuscarUsuarioPorCorreo(correo);

        if (usuario != null) {

            if (usuario.getTipoUsuario() == ADMINISTRADOR) {

                List<GrantedAuthority> permisosAdministrador = new ArrayList<>();

                GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_ADMINISTRADOR");
                GrantedAuthority p2 = new SimpleGrantedAuthority("ROLE_USUARIO_COMUN");

                permisosAdministrador.add(p1);
                permisosAdministrador.add(p2);

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);
                session.setAttribute("AdminSession", usuario);

                User user = new User(usuario.getCorreo(), usuario.getClave(), permisosAdministrador);

                return user;
            }

            if (usuario.getTipoUsuario() == USUARIOCOMUN) {

                List<GrantedAuthority> permisosUsuarioComun = new ArrayList<>();

                GrantedAuthority p2 = new SimpleGrantedAuthority("ROLE_USUARIO_COMUN");

                permisosUsuarioComun.add(p2);

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);
                session.setAttribute("UsuarioSession", usuario);

                User user = new User(usuario.getCorreo(), usuario.getClave(), permisosUsuarioComun);

                return user;
            }

        } else {

            throw new UsernameNotFoundException("ADMIN USER NOT FOUND");
        }
        User user = null;
        return user;
    }

}
