

package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.servicios.ProductoServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping
public class TiendaControlador {
   
    
    @Autowired
    ProductoServicio productoServicio;
    
    @Autowired
    ProductoRepositorio productoRepositorio;
    
    @GetMapping("/tienda")
    public String tienda(ModelMap modelo) {
        
       
        List<Producto> productos = productoRepositorio.findAll();

        modelo.put("productos", productos);
        
        
        
        return "tienda";

    }

      
            
        
        
  
    
    @GetMapping("/mostrarProducto")
    public String mostrarproducto(ModelMap modelo,@RequestParam String idProducto) {
        Producto producto = productoRepositorio.buscarPorId(idProducto);
        modelo.addAttribute("producto", producto);
        
       List<Producto> productosSimilares = productoRepositorio.buscarTodosPorVarietal(productoRepositorio.findById(idProducto).get().getVarietal());
              
        modelo.put("productosSimilares", productosSimilares);
        
        return "producto";
    }
}
