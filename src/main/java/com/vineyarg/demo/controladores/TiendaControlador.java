

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
    ProductoRepositorio productoRepositorio;
     @Autowired
     ProductoServicio productoServicio;
    
    
    
    @GetMapping("/mostrarProducto")
    public String mostrarproducto(ModelMap modelo,@RequestParam String idProducto) {
        Producto producto = productoRepositorio.buscarPorId(idProducto);
        modelo.addAttribute("producto", producto);
        return "producto";
    }
}
