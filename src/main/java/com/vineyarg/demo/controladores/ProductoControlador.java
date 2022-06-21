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
import java.util.ArrayList;
import java.util.List;
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

            throw new Excepcion ("Usuario no reconocido");
        }

        return "agregar-producto.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/agregarProducto")
    public String agregarProducto(ModelMap modelo, @RequestParam String idProductor, @RequestParam String nombre, @RequestParam Integer cantidad, @RequestParam Double precio, @RequestParam String descripcion,
            @RequestParam String varietal,@RequestParam String SKU, @RequestParam(required = false) List<MultipartFile> imagenes) throws Exception {
        
        try {
            
            Optional<Productor> respuesta = productorRepositorio.findById(idProductor);
            
            if(respuesta.isPresent()) {
            
            Productor productorDelProducto = respuesta.get();
                
            modelo.put("perfil", productorDelProducto);
            
            productoServicio.agregarProducto(null, nombre, cantidad, precio, descripcion, varietal, productorDelProducto, SKU);
            
           
            }
            
           
            
        } catch (Exception e) {
            
            e.getMessage();
            modelo.put("error", "No se ha podido guardar el producto");
        }
        modelo.put("registrado", "Producto agregado correctamente y disponible para la venta");
        
        return "agregar-producto.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @GetMapping("/editar-producto")
    public String editarproducto(ModelMap modelo, HttpSession session, @RequestParam String idProductorSesion) {
        
       Usuario login = (Usuario) session.getAttribute("UsuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(idProductorSesion)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idProductorSesion);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();
            modelo.put("perfil-productor", usuario);
                    
            List<Producto> productos = productoRepositorio.buscarTodosPorCorreoProductor(usuario.getCorreo());
            modelo.put("productos", productos);        
        } 
        
        
        
        
        List<String> opciones = new ArrayList();
        opciones.add("editar");
        opciones.add("eliminar");
        modelo.put("opciones", opciones);
        
        return "editar-producto";

    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/editarProducto")
    public String editarProducto(ModelMap modelo, String idProductoElegido, @RequestParam String nombre, @RequestParam Integer cantidad, @RequestParam Double precio, @RequestParam String descripcion,
           List<Imagenes> imagenes, String eleccion) throws Exception {
        
        if(eleccion.equalsIgnoreCase("editar")) {
            try {
            productoServicio.modificarProducto(idProductoElegido, nombre, cantidad, precio, descripcion);
            
            System.out.println("Nombre " + nombre);
            System.out.println("Cantidad " + cantidad);
            System.out.println("Precio " + precio);
            System.out.println("Descripcion " + descripcion);
//            System.out.println("Varietal " + varietal);
//            System.out.println("Productor " + productor);//no esta en el html
//            System.out.println("SKU " + SKU);
//            System.out.println("Valoraciones " + valoraciones); //no esta en el html y creo que es correcto que no esté en agregar producto
            System.out.println("Imagenes " + imagenes);//no esta en el html de agregar-producto
            
            modelo.put("exito", "Producto modificado con éxito!!");
            
        } catch (Exception e) {
            
            e.getMessage();
            modelo.put("error", "No se han podido guardar las modificaciones");
        }
        return "editar-producto";
    }
        if(eleccion.equalsIgnoreCase("eliminar")) {
            try {
            productoServicio.bajaProducto(idProductoElegido);
            
            modelo.put("exito", "Producto eliminado con éxito!!");
            
        } catch (Exception e) {
            
            e.getMessage();
            modelo.put("error", "No se han podido eliminar el productos");
        }
        return "editar-producto";
    }
        
        return null;
      }  
    
        }
        
