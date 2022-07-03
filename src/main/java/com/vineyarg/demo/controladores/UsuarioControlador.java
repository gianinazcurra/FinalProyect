/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.ItemCompra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.EstadoCompra;
import com.vineyarg.demo.enumeraciones.TipoUsuario;
import static com.vineyarg.demo.enumeraciones.TipoUsuario.ADMINISTRADOR;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ItemCompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.ItemCompraServicio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
    @Autowired
    private ItemCompraServicio itemCompraServicio;
    @Autowired
    private ItemCompraRepositorio itemCompraRepositorio;

    @GetMapping("/registro")
    public String registro(HttpSession session) {
        return "registro.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/registro-admin")
    public String registroAdmin() {
        return "registro-admin.html";
    }

    @GetMapping("/logueo")
    public String login(@RequestParam(required = false) String error, ModelMap modelo, HttpSession session) {

        //lo de abajo impide que si existe una sesión iniciada se puede acceder a otro login
        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login != null) {
            
            return "redirect:/";
        }
        
        
        if (error != null) {
            modelo.put("error", "Nombre de usuario o clave incorrectos");
        }

//        if(faltaLogin.equalsIgnoreCase("aa")) {
//             modelo.put("debeLoguearse", "Debes ingresar a tu cuenta para poder comprar");
//        }
        return "login.html";

    }

    //El de abajo es para manejar el input DATE que reciben los formularios de registro
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                dateFormat, false));
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = dateFormat.format(fechaNacimiento);
            modelo.put("fechaNacimiento", dateString);

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

            usuarioServicio.registrarUsuario(archivo, nombre, apellido, DNI, correo, clave1, clave2, fechaNacimiento, TipoUsuario.USUARIOCOMUN);

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("DNI", DNI);
            modelo.put("correo", correo);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = dateFormat.format(fechaNacimiento);
            modelo.put("fechaNacimiento", dateString);
            modelo.put("tipoUsuario", tipoUsuario.USUARIOCOMUN);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            return "registro-usuario.html";
        }

        modelo.put("registrado", "Usuario registrado con éxito. Ahora podés ingresar con tus datos");
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/editar-usuario")
    public String editarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
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
    public String editarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam MultipartFile archivo, @RequestParam String correo, @RequestParam String clave1, @RequestParam String clave2) throws Excepcion {

        try {

            Usuario login = (Usuario) session.getAttribute("usuarioSession");
            if (login == null || !login.getId().equalsIgnoreCase(id)) {
                return "redirect:/index.html";
            }

            Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

            if (respuesta.isPresent()) {

                Usuario usuario = new Usuario();
                usuario = respuesta.get();

                modelo.put("perfil", usuario);
                usuarioServicio.modificarUsuario(id, archivo, correo, clave1, clave2);

                if (usuario.getTipoUsuario().equals(ADMINISTRADOR)) {
                    modelo.put("registrado", "Usuario modificado con éxito");

                    return "administradorweb.html";

                }

            }
        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("correo", correo);

            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            return "editar-usuario.html";
        }

        modelo.put("registrado", "Usuario modificado con éxito");

        return "usuarioweb.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/eliminarUsuario")
    public String eliminarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String correo, @RequestParam String clave) throws Excepcion {

        try {

            Usuario login = (Usuario) session.getAttribute("usuarioSession");
            if (login == null || !login.getId().equalsIgnoreCase(id)) {
                return "redirect:/";
            }

            Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

            if (respuesta.isPresent()) {

                Usuario usuario = new Usuario();
                usuario = respuesta.get();

                modelo.put("perfil", usuario);

                usuarioServicio.eliminarUsuario(id, correo, clave);

                return "redirect:/logout";

            }
        } catch (Excepcion ex) {
            modelo.put("error1", ex.getMessage());

            modelo.put("mail", correo);
            modelo.put("clave", clave);

            return "editar-usuario.html";
        }

        modelo.put("registrado", "Baja exitosa");
        return "registro.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/administradorweb")
    public String administradorWeb(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {
            return "redirect:/";
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

        if (comprasNuevas.isEmpty()) {
            modelo.put("sinNuevas", "sinNuevas");
        }

        return "administradorweb.html";
    }

    @GetMapping("/verComprasHistoricas")
    public String verComprasHistoricas(ModelMap modelo) {

        List<Compra> comprasHistoricas = compraRepositorio.buscarComprasHistoricas();

        modelo.put("comprasHistoricas", comprasHistoricas);

        return "administradorweb.html";
    }

    @PostMapping("/aceptarCompra")
    public String aceptarCompra(ModelMap modelo, @RequestParam String decision, @RequestParam String observaciones, @RequestParam String idCompra) {

        Compra compra = compraRepositorio.buscarPorId(idCompra);
        System.out.println(decision);
        if (decision.equalsIgnoreCase("ACEPTAR")) {

            compra.setEstadoCompra(EstadoCompra.ACEPTADA);
            compra.setObservacionesCompra(observaciones);

            Usuario usuarioComprador = usuarioRepositorio.getById(compra.getUsuario().getId());
            usuarioComprador.setTotalComprasEfectuadas(usuarioComprador.getTotalComprasEfectuadas() + 1);
            usuarioComprador.setTotalDineroComprado(usuarioComprador.getTotalDineroComprado() + compra.getMontoFinal());

            compraRepositorio.save(compra);
            usuarioRepositorio.save(usuarioComprador);

            List<Compra> comprasNuevasPre = compraRepositorio.buscarComprasNuevas();
            List<Compra> comprasNuevas = new ArrayList();

            System.out.println(decision);

            for (Compra compraNueva : comprasNuevasPre) {

                if (compraNueva.getFechaCompra() != null) {
                    comprasNuevas.add(compraNueva);
                }

            }

            modelo.put("comprasNuevas", comprasNuevas);

            if (comprasNuevas.isEmpty()) {
                modelo.put("sinNuevas", "sinNuevas");
            }

        } else if (decision.equalsIgnoreCase("RECHAZAR")) {
            compra.setEstadoCompra(EstadoCompra.RECHAZADA);
            compra.setObservacionesCompra(observaciones);
            compraRepositorio.save(compra);

            //RESTITUIMOS EL STOCK DE PRODUCTOS (COMO EN EL SERVICIO ANULAR COMPRA PERO SIN ELIMINAR LA COMPRA DE LA BASE DE DATOS):
            Set<ItemCompra> listaItems = compra.getItemCompra();

            Producto productoReponer = new Producto();

            for (ItemCompra item : listaItems) {

                productoReponer = item.getProducto();

                productoReponer.setCantidad(productoReponer.getCantidad() + item.getCantidad());

                productoRepositorio.save(productoReponer);

            }

            List<Compra> comprasNuevasPre = compraRepositorio.buscarComprasNuevas();
            List<Compra> comprasNuevas = new ArrayList();

            for (Compra compraNueva : comprasNuevasPre) {

                if (compraNueva.getFechaCompra() != null) {
                    comprasNuevas.add(compraNueva);
                }

            }

            modelo.put("comprasNuevas", comprasNuevas);

            if (comprasNuevas.isEmpty()) {
                modelo.put("sinNuevas", "sinNuevas");
            }

        }
        return "administradorweb.html";
    }

    @GetMapping("/usuarioweb")
    public String usuarioWeb(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
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
    public String verCompras(ModelMap modelo, @RequestParam String id) {

        List<Compra> comprasUsuario = compraRepositorio.buscarComprasTotalesPorUsuario(id);

        modelo.put("compras", comprasUsuario);

        return "usuarioweb.html";
    }

}
