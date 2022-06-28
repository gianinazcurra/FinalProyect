package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.Regiones;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.ProductoServicio;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class ProductoControlador {

    @Autowired
    ProductoServicio productoServicio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    ProductorRepositorio productorRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @GetMapping("/agregar-producto")
    public String agregarproducto(ModelMap modelo, HttpSession session, @RequestParam String idUsuario) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isPresent()) {

            Usuario usuario = new Usuario();
            usuario = respuesta.get();

            Productor productor = new Productor();
            productor = productorRepositorio.BuscarProductorPorCorreo(usuario.getCorreo());

//            modelo.put("regiones", Regiones.values());
            modelo.put("perfil", productor);

            modelo.put("perfilUsuario", usuario);

        } else {

            throw new Excepcion("Usuario no reconocido");
        }

        return "agregar-producto.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/agregarProducto")
    public String agregarProducto(ModelMap modelo, @RequestParam String idProductor, @RequestParam String nombre, @RequestParam Integer cantidad, @RequestParam Double precio, @RequestParam String descripcion,
            @RequestParam String varietal, @RequestParam String SKU, @RequestParam(required = false) Set<MultipartFile> imagenes) throws Exception {

        try {

            Optional<Productor> respuesta = productorRepositorio.findById(idProductor);

            if (respuesta.isPresent()) {

                Productor productorDelProducto = respuesta.get();

                modelo.put("perfil", productorDelProducto);

                productoServicio.agregarProducto(imagenes, nombre, cantidad, precio, descripcion, varietal, productorDelProducto, SKU);

            }

        } catch (Exception e) {

            e.getMessage();
            modelo.put("error", "No se ha podido guardar el producto");
        }
        modelo.put("registrado", "Producto agregado correctamente al catálogo y disponible para la venta");

        return "agregar-producto.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @GetMapping("/editar-producto")
    public String editarproducto(ModelMap modelo, HttpSession session, @RequestParam String idProductor) {

        List<Producto> productos = productoRepositorio.buscarTodosPorProductor(idProductor);

        modelo.put("productos", productos);

        Producto productoElegido;
        modelo.put("productoElegido", null);

        List<String> opciones = new ArrayList();
        opciones.add("editar");
        opciones.add("eliminar");
        modelo.put("opciones", opciones);

        return "editar-producto.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/editarProducto1")
    public String editarproducto(ModelMap modelo, HttpSession session, @RequestParam String idProducto, @RequestParam String idUsuario, @RequestParam String eleccion) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
            return "redirect:/index.html";
        }

        if (eleccion.equalsIgnoreCase("editar")) {

            Optional<Producto> respuesta = productoRepositorio.findById(idProducto);

            if (respuesta.isPresent()) {
                Producto productoElegido = new Producto();
                productoElegido = respuesta.get();

                modelo.put("productoElegido", productoElegido);

            }
        }
        if (eleccion.equalsIgnoreCase("eliminar")) {

            try {
                productoServicio.bajaProducto(idProducto);

                modelo.put("exito", "El producto se quitó del catálogo");
                
                List<Producto> productos = productoRepositorio.buscarTodosPorProductor(productoRepositorio.getById(idProducto).getProductor().getId());

            modelo.put("productos", productos);

            Producto productoElegido;
            modelo.put("productoElegido", null);

            List<String> opciones = new ArrayList();
            opciones.add("editar");
            opciones.add("eliminar");
            modelo.put("opciones", opciones);


            } catch (Exception e) {

                e.getMessage();
                modelo.put("error", "No se han podido eliminar el producto");
                
                List<Producto> productos = productoRepositorio.buscarTodosPorProductor(productoRepositorio.getById(idProducto).getProductor().getId());

            modelo.put("productos", productos);

            Producto productoElegido;
            modelo.put("productoElegido", null);

            List<String> opciones = new ArrayList();
            opciones.add("editar");
            opciones.add("eliminar");
            modelo.put("opciones", opciones);

            }

        }
        return "editar-producto.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/editarProducto")
    public String editarProducto(ModelMap modelo, HttpSession session, @RequestParam String idProductoElegido, String idUsuario, @RequestParam String nombre, @RequestParam Integer cantidad,
            @RequestParam Double precio, @RequestParam String descripcion, @RequestParam String varietal, @RequestParam(required = false) Set<MultipartFile> imagenes) throws Exception {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
            return "redirect:/index.html";
        }

        try {
            productoServicio.modificarProducto(imagenes, idProductoElegido, nombre, cantidad, precio, descripcion, varietal);

            modelo.put("exito", "Producto modificado con éxito");

            List<Producto> productos = productoRepositorio.buscarTodosPorProductor(productoRepositorio.getById(idProductoElegido).getProductor().getId());

            modelo.put("productos", productos);

            Producto productoElegido;
            modelo.put("productoElegido", null);

            List<String> opciones = new ArrayList();
            opciones.add("editar");
            opciones.add("eliminar");
            modelo.put("opciones", opciones);

        } catch (Exception e) {

            e.getMessage();
            modelo.put("error",e.getMessage());
            
           

            List<Producto> productos = productoRepositorio.buscarTodosPorProductor(productoRepositorio.getById(idProductoElegido).getProductor().getId());

            modelo.put("productos", productos);

            Producto productoElegido;
            modelo.put("productoElegido", null);

            List<String> opciones = new ArrayList();
            opciones.add("editar");
            opciones.add("eliminar");
            modelo.put("opciones", opciones);
        }
        return "editar-producto.html";
    }
    
   
    @GetMapping("/valorar")
    public String editarproducto(ModelMap modelo, String valoracion, String idProducto, HttpSession session) throws Excepcion {

        int valoracionInt = parseInt(valoracion);
        
        productoServicio.valorarProducto(idProducto, valoracionInt);
        
        List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();
        
        
        
        for (Producto producto : productosT) {
            if (producto.isAlta() && producto.getProductor().isAlta()) {
                productos.add(producto);
                
               
            }
        }
        modelo.put("productos", productos);

        return "tienda.html";
    }
    
}
