package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.Regiones;
import com.vineyarg.demo.enumeraciones.TipoUsuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.ProductorServicio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/registro-bodega")
public class ProductorControlador {

    @Autowired
    ProductorServicio productorServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private ProductorRepositorio productorRepositorio;

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/registro-bodega")
    public String guardarProductor(ModelMap modelo) {

        modelo.put("regiones", Regiones.values());
        return "registro-bodega"; //me devuelve la vista
    }

    @PostMapping("/registro-bodega")
    public String guardarProductor(ModelMap modelo, @RequestParam String nombre, @RequestParam String razonSocial, @RequestParam String domicilio, @RequestParam String correo,
            @RequestParam String clave1, @RequestParam String clave2, @RequestParam String descripcion, @RequestParam String region, @RequestParam MultipartFile archivo) throws Exception {
        try {

            productorServicio.guardar(nombre, razonSocial, domicilio, correo, clave1, clave2, descripcion, region, archivo);

            usuarioServicio.registrarUsuario(null, nombre, null, null, correo, clave1, clave2, null, TipoUsuario.PRODUCTOR);

            System.out.println("Productor " + nombre);

        } catch (Exception e) {

            modelo.put("error", e.getMessage());

            modelo.put("Ha ocurrido un error", "No se ha podido ingresar el Productor");
            modelo.put("nombre", nombre);
            modelo.put("razonSocial", razonSocial);
            modelo.put("domicilio", domicilio);
            modelo.put("correo", correo);
            modelo.put("clave", clave1);
            modelo.put("clave", clave2);
            modelo.put("descripcion", descripcion);
            modelo.put("region", region);

            return "registro-bodega";
        }

        modelo.put("Atención", "Productor ingresado de manera satisfactoria");
        return "registro-bodega";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")//está bien este role?
    @GetMapping("/editar-productor")
    public String editarproductor(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("UsuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            
            Usuario usuario = new Usuario();
            usuario = respuesta.get();
            
            Productor productor = productorRepositorio.BuscarProductorPorCorreo(usuario.getCorreo());
            modelo.put("regiones", Regiones.values());
            modelo.put("productor", productor);
        } else {

            throw new Excepcion("Usuario no reconocido");
        }

        return "editar-productor";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/editar-productor")
    public String editarProductor(ModelMap modelo, @RequestParam String nombre, @RequestParam String razonSocial, @RequestParam String domicilio, @RequestParam String correo,
            @RequestParam String clave1, @RequestParam String clave2, @RequestParam String descripcion, @RequestParam String region, @RequestParam MultipartFile archivo) throws Excepcion {

        try {

            productorServicio.modificar(nombre, razonSocial, domicilio, correo, clave1, clave2, descripcion, region, archivo);

            usuarioServicio.modificarUsuario(null, nombre, null, null, correo, clave1, clave2, null, TipoUsuario.PRODUCTOR);

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("razonSocial", razonSocial);
            modelo.put("domicilio", domicilio);
            modelo.put("correo", correo);
            modelo.put("clave", clave1);
            modelo.put("clave", clave2);
            modelo.put("descripcion", descripcion);
            modelo.put("region", region);

            return "editar-productor";

        }
        modelo.put("Atención", "Productor modificado de manera satisfactoria");
        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @GetMapping("/eliminar-productor")
    public String darDeBaja(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

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

        return "eliminar-productor";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/eliminar-productor")
    public String darDeBaja(ModelMap modelo, @RequestParam String correo, @RequestParam String clave) throws Excepcion {

        try {

            usuarioServicio.eliminarUsuario(correo, clave);

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("mail", correo);
            modelo.put("clave1", clave);

            return "eliminar-productor.html";
        }

        modelo.put("borrado", "Productor eliminado con éxito");
        return "index.html";
    }

}
