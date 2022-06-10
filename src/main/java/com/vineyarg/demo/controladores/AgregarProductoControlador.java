
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.servicios.ProductoServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/agregar-producto")
public class AgregarProductoControlador {
    

    @Autowired
    ProductoServicio productoServicio;
    
    @GetMapping("/agregar-producto")
    public String agregarProducto(){
        return "agregar-producto";
    
}
    
    @PostMapping("/agregar-producto")
    public String agregarProducto(ModelMap modelo, @RequestParam  String nombre, @RequestParam Integer cantidad, @RequestParam Double precio,@RequestParam String descripcion,
            @RequestParam String varietal,@RequestParam Productor productor,@RequestParam String SKU,@RequestParam Double valoraciones,List<Imagenes> imagenes) throws Exception {
        try {
            productoServicio.agregarProducto(null/*null porque todavia no tengo las imagenes*/, nombre, cantidad, precio, descripcion, varietal, productor, SKU, valoraciones);
            System.out.println("Nombre " + nombre);
            System.out.println("Cantidad " + cantidad);
            System.out.println("Precio " + precio);
            System.out.println("Descripcion " + descripcion);
            System.out.println("Varietal " + varietal);
            System.out.println("Productor " + productor);//no esta en el html
            System.out.println("SKU " + SKU);
            System.out.println("Valoraciones " +valoraciones); //no esta en el html y creo que es correcto que no esté en agregar producto
            System.out.println("Imagenes " + imagenes);//no esta en el html de agregar-producto
            modelo.put("exito", "Producto ingresado con éxito!!");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "No se ha podido guardar el producto");
        }
        return "agregar-producto";
    }
          
}
