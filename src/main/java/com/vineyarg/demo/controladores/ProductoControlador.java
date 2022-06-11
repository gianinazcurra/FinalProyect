package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import com.vineyarg.demo.servicios.ProductoServicio;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class ProductoControlador {

    @Autowired
    ProductoServicio productoServicio;
    
    @Autowired
    ProductoRepositorio productoRepositorio;
    
    @Autowired
    ProductorRepositorio productorRepositorio;

    @GetMapping("/agregar-producto")
    public String agregarproducto(ModelMap modelo) {
        
       
        
        return "agregar-producto";

    }

    //FALTA AGREGAR ROL 
    @PostMapping("/agregarProducto")
    public String agregarProducto(ModelMap modelo, @RequestParam String nombre, @RequestParam Integer cantidad, @RequestParam Double precio, @RequestParam String descripcion,
            @RequestParam String varietal, @RequestParam String idProductor, @RequestParam String SKU, List<Imagenes> imagenes) throws Exception {
        
        try {
            productoServicio.agregarProducto(null/*null porque todavia no tengo las imagenes*/, nombre, cantidad, precio, descripcion, varietal, productor, SKU);
            
            System.out.println("Nombre " + nombre);
            System.out.println("Cantidad " + cantidad);
            System.out.println("Precio " + precio);
            System.out.println("Descripcion " + descripcion);
            System.out.println("Varietal " + varietal);
            System.out.println("Productor " + productor);//no esta en el html
            System.out.println("SKU " + SKU);
//            System.out.println("Valoraciones " + valoraciones); //no esta en el html y creo que es correcto que no esté en agregar producto
            System.out.println("Imagenes " + imagenes);//no esta en el html de agregar-producto
            
            modelo.put("exito", "Producto ingresado con éxito!!");
        } catch (Exception e) {
            
            e.getMessage();
            modelo.put("error", "No se ha podido guardar el producto");
        }
        return "agregar-producto";
    }
    
    @GetMapping("/editar-producto")
    public String editarproducto(ModelMap modelo) {
        
        List<Producto> productos = productoRepositorio.findAll();
        modelo.put("productos", productos);
        
        List<String> opciones = new ArrayList();
        opciones.add("editar");
        opciones.add("eliminar");
        modelo.put("opciones", opciones);
        
        return "editar-producto";

    }

    //FALTA AGREGAR ROL 
    @PostMapping("/editarProducto")
    public String editarProducto(ModelMap modelo, String id, @RequestParam String nombre, @RequestParam Integer cantidad, @RequestParam Double precio, @RequestParam String descripcion,
           List<Imagenes> imagenes, String eleccion) throws Exception {
        
        if(eleccion.equalsIgnoreCase("editar")) {
            try {
            productoServicio.modificarProducto(id, nombre, cantidad, precio, descripcion);
            
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
            productoServicio.bajaProducto(id);
            
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
        
