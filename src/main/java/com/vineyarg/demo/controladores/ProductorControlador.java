package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.ProductorServicio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.Optional;
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

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/registro-bodega")
    public String guardarProductor() {
        return "registro-bodega"; //me devuelve la vista
    }

    @PostMapping("/registro-bodega")
    public String guardarProductor(ModelMap modelo, @RequestParam String nombre, @RequestParam String razonSocial, @RequestParam String domicilio, @RequestParam String correo,
            @RequestParam String clave, @RequestParam String descripcion, @RequestParam String region, @RequestParam MultipartFile archivo, @RequestParam Boolean alta) throws Exception {
        try {

            productorServicio.guardar(nombre, razonSocial, domicilio, correo, clave, descripcion, region,
                    archivo, alta);

            System.out.println("Productor " + nombre);
            modelo.put("Atención", "Productor ingresado de manera satisfactoria");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Ha ocurrido un error", "No se ha podido ingresar el Productor");
            modelo.put("nombre", nombre);
            modelo.put("razonSocial", razonSocial);
            modelo.put("domicilio", domicilio);
            modelo.put("correo", correo);
            modelo.put("clave", clave);
            modelo.put("descripcion", descripcion);
            modelo.put("region", region);
        }
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
            modelo.put("perfil", usuario);
        } else {

            throw new Excepcion("Usuario no reconocido");
        }

        return "editar-productor";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/editar-productor")
    public String editarProductor(ModelMap modelo, @RequestParam String nombre, @RequestParam String razonSocial, @RequestParam String domicilio, @RequestParam String correo,
            @RequestParam String clave, @RequestParam String descripcion, @RequestParam String region, @RequestParam MultipartFile archivo, Boolean alta) throws Excepcion {

        try {

            productorServicio.modificar(region, nombre, razonSocial, domicilio, correo, clave, descripcion, region, archivo, true);

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("razonSocial", razonSocial);
            modelo.put("domicilio", domicilio);
            modelo.put("correo", correo);
            modelo.put("clave", clave);
            modelo.put("descripcion", descripcion);
            modelo.put("region", region);

            return "editar-productor";

        }
        modelo.put("Atención", "Productor ingresado de manera satisfactoria");
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
    public String darDeBaja(ModelMap modelo, @RequestParam String id) throws Excepcion {

        productorServicio.borrarPorId(id);

        modelo.put("Atención", "El Productor ha sido dado de baja con éxito");
        return "index.html";
    }

}
