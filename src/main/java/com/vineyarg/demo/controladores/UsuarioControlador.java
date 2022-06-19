/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.EstadoCompra;
import com.vineyarg.demo.enumeraciones.TipoUsuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    @Autowired
    private CompraRepositorio compraRepositorio;
    @Autowired
    private ProductoRepositorio productoRepositorio;

//    @GetMapping("/registro")
//    public String registro() {
//        return "registro.html";
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/registro-admin")
    public String registroAdmin() {
        return "registro-admin.html";
    }
    
    @GetMapping("/logueo")
    public String login() {
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    @PostMapping("/registrarAdministrador")
    public String registroAdmin(ModelMap modelo, @RequestParam MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, 
            @RequestParam String DNI, @RequestParam String correo, @RequestParam Date fechaNacimiento, 
            @RequestParam TipoUsuario tipoUsuario, @RequestParam String clave1, @RequestParam String clave2) throws Excepcion {

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
        return "login.html";
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
        return "login.html";
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
        return "usuarioweb.html";
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
            modelo.put("clae1", clave);

            return "borrar-usuario.html";
        }

        modelo.put("borrado", "Administrador eliminado con éxito");
        return "registro.html";
    }

    @GetMapping("/administradorweb")
    public String administradorWeb(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

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

        return "administradorweb.html";
    }

    @GetMapping("/verNuevasCompras")
    public String verNuevasCompras(ModelMap modelo) {

        List<Compra> comprasNuevasPre = compraRepositorio.buscarComprasNuevas();
        List<Compra> comprasNuevas = new ArrayList();

        for (Compra compraNueva : comprasNuevasPre) {

            if (compraNueva.getFechaCompra() != null) {
                comprasNuevas.add(compraNueva);
            }

        }
        modelo.put("comprasNuevas", comprasNuevas);

        return "administradorweb.html";
    }

    @GetMapping("/verComprasHistoricas")
    public String verComprasHistoricas(ModelMap modelo) {

        List<Compra> comprasHistoricas = compraRepositorio.buscarComprasHistoricas();

        
        
        modelo.put("comprasHistoricas", comprasHistoricas);
        return "administrador.html";
    }

    @PostMapping("/aceptarCompra")
    public String aceptarCompra(ModelMap modelo, @RequestParam String decision, @RequestParam String observaciones, @RequestParam String id) {

        Compra compra = compraRepositorio.buscarPorId(id);

        if (decision.equalsIgnoreCase("ACEPTAR")) {

            compra.setEstadoCompra(EstadoCompra.ACEPTADA);
            compra.setObservacionesCompra(observaciones);

        } else if (decision.equalsIgnoreCase("RECHAZAR")) {
            compra.setEstadoCompra(EstadoCompra.RECHAZADA);
            compra.setObservacionesCompra(observaciones);

            //RESTITUIMOS EL STOCK DE PRODUCTOS (COMO EN EL SERVICIO ANULAR COMPRA PERO SIN ELIMINAR LA COMPRA DE LA BASE DE DATOS):
            List<Producto> listaProductos = compra.getListaProductos();
            List<Integer> listaCantidades = compra.getCantidades();

            for (int i = 0; i < listaProductos.size(); i++) {
                for (int j = 0; j < listaCantidades.size(); j++) {

                    if (i == j) {

                        Producto producto = productoRepositorio.buscarPorId(listaProductos.get(i).getId());

                        producto.setCantidad(producto.getCantidad() + listaCantidades.get(j));

                        productoRepositorio.save(producto);

                    }
                }

            }

        }
        return "administrador.html";
    }

    @GetMapping("/usuarioweb")
    public String usuarioWeb(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

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

        return "usuarioweb.html";
    }

    @GetMapping("/verCompras")
    public String verCompras(ModelMap modelo, HttpSession session, @RequestParam String id) {

        List<Compra> comprasUsuario = compraRepositorio.buscarComprasTotalesPorUsuario(id);

        modelo.put("compras", comprasUsuario);

        return "usuarioweb.html";
    }

     
}
