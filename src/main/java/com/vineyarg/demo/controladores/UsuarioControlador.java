/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.TipoUsuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/registro")
    public String registro() {
        return "registro.html";
    }

    @GetMapping("/registro-admin")
    public String registroAdmin() {
        return "registro-admin.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    @PostMapping("/registrarAdministrador")
    public String registroAdmin(ModelMap modelo, @RequestParam MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String DNI, @RequestParam String correo, @RequestParam Date fechaNacimiento, @RequestParam TipoUsuario tipoUsuario, @RequestParam String clave1, @RequestParam String clave2) throws Excepcion {

        try {

            usuarioServicio.registrarUsuario(archivo, nombre, apellido, DNI, correo, clave1, clave2, fechaNacimiento, TipoUsuario.ADMINISTRADOR);

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("DNI", DNI);
            modelo.put("correo", correo);
            modelo.put("fechaNacimiento", fechaNacimiento);
            modelo.put("tipoUsuario", tipoUsuario.ADMINISTRADOR);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            return "registro-admin.html";
        }

        modelo.put("registrado", "Administrador registrado con éxito");
        return "index.html";
    }
    //FALTA MÉTODO MODIFICAR ADMINSITRADOR - SUGIERO ACEPTAR SOLO MODIFICACIÓN DE MAIL Y CLAVE

    @GetMapping("/registro-usuario")
    public String registroUsuario() {
        return "registro-usuario.html";
    }

    @PostMapping("/registrarUsuarioComun")
    public String registroUsuarioComun(ModelMap modelo, @RequestParam MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String DNI, @RequestParam String correo, @RequestParam Date fechaNacimiento, @RequestParam TipoUsuario tipoUsuario, @RequestParam String clave1, @RequestParam String clave2) throws Excepcion {

        try {

            usuarioServicio.registrarUsuario(archivo, nombre, apellido, DNI, correo, clave1, clave2, fechaNacimiento, TipoUsuario.ADMINISTRADOR);

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("DNI", DNI);
            modelo.put("correo", correo);
            modelo.put("fechaNacimiento", fechaNacimiento);
            modelo.put("tipoUsuario", tipoUsuario.USUARIOCOMUN);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            return "registro-usuario.html";
        }

        modelo.put("registrado", "Usuario registrado con éxito");
        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/editar-usuario")
    public String editarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

//        Agregar esto a HTML editar usuario  <input type="hidden" class="form-control" name="id" th:value="${perfil.id}"/>
        Usuario login = (Usuario) session.getAttribute("UsuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();
            modelo.put("perfil", usuario);
        } else {

            throw new Excepcion("Usuario no reconocido");
        }
        return "editar-usuario.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/editarUsuario")
    public String editarUsuario(ModelMap modelo, @RequestParam MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String DNI, @RequestParam String correo, @RequestParam Date fechaNacimiento, @RequestParam TipoUsuario tipoUsuario, @RequestParam String clave1, @RequestParam String clave2) throws Excepcion {

        try {

            usuarioServicio.modificarUsuario(archivo, nombre, apellido, DNI, correo, clave1, clave2, fechaNacimiento, tipoUsuario);

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("DNI", DNI);
            modelo.put("correo", correo);
            modelo.put("fechaNacimiento", fechaNacimiento);
            modelo.put("tipoUsuario", tipoUsuario.USUARIOCOMUN);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            return "editar-usuario.html";
        }

        modelo.put("registrado", "Usuario registrado con éxito");
        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/eliminar-usuario")
    public String eliminarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("UsuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();
            modelo.put("perfil", usuario);
        } else {

            throw new Excepcion("Usuario no reconocido");
        }

        return "eliminar-usuario.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/borrarUsuario")
    public String eliminarUsuario(ModelMap modelo, @RequestParam String correo, @RequestParam String clave) throws Excepcion {

        try {

            usuarioServicio.eliminarUsuario(correo, clave);

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("mail", correo);
            modelo.put("clave1", clave);

            return "borrar-usuario.html";
        }

        modelo.put("borrado", "Administrador eliminado con éxito");
        return "index.html";
    }
}
