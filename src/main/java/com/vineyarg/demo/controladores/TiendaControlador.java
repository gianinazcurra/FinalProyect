package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.ProductoServicio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class TiendaControlador {

    @Autowired
    ProductoServicio productoServicio;
    @Autowired
    private CompraRepositorio compraRepositorio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/tienda")
    public String tienda(ModelMap modelo, HttpSession session) {

    
        
        List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();
        
        
        //con el forEach descartamos productos dados de baja o que correspondan a productores dados de baja
        for (Producto producto : productosT) {
            if (producto.isAlta() && producto.getProductor().isAlta()) {
                productos.add(producto);
                
               
            }
        }
        modelo.put("productos", productos);

        return "tienda.html";

    }

    @GetMapping("/mostrarProducto")
    public String mostrarproducto(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String idProducto) {

        if (session != null) {
            Usuario login = (Usuario) session.getAttribute("usuarioSession");
            if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
                return "redirect:/index.html";
            }

        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();

            modelo.put("perfil", usuario);
        }

        Producto productoElegido = productoRepositorio.buscarPorId(idProducto);

        modelo.put("productoElegido", productoElegido);

        List<Producto> productosSimilares = productoRepositorio.buscarTodosPorVarietal(productoElegido.getVarietal());
        
        productosSimilares.remove(productoElegido);

        modelo.put("productosSimilares", productosSimilares);

        return "producto.html";
    }
    
     @GetMapping("/tiendaRegiones")
    public String tiendaRegiones(ModelMap modelo, HttpSession session,@RequestParam String region) {

    
        
        List<Producto> productosT = productoRepositorio.buscarPorRegion(region);
        List<Producto> productos = new ArrayList();
        
        
        //con el forEach descartamos productos dados de baja o que correspondan a productores dados de baja
        for (Producto producto : productosT) {
            if (producto.isAlta() && producto.getProductor().isAlta()) {
                productos.add(producto);
                
               
            }
        }
        modelo.put("productos", productos);

        return "tienda.html";

    }
}
