
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.servicios.CompraServicio;
import com.vineyarg.demo.servicios.ProductoServicio;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/carrito")
public class CompraControlador {
    @Autowired
    private CompraServicio compraServicio;
    @Autowired
    private ProductoServicio productoServicio;
    
    @GetMapping
    public String crearCompra (){
        return "carrito.html";
    }
    
    @PostMapping("/carrito")
    public String crearCompra(ModelMap modelo, @RequestParam Integer cantidad,  @RequestParam Usuario usuario, @RequestParam List<Producto> listaProductos, @RequestParam Date fechaCompra, @RequestParam Double montoFinal, @RequestParam String direccionEnvio){
        try {
            compraServicio.crearCompra(cantidad, usuario, listaProductos, fechaCompra, montoFinal, direccionEnvio);
            modelo.put("Exito", "La compra se realizó con éxito.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al realizar la compra.");
        }
        return "carrito.html";
    }
    @GetMapping("/carrito")
    public String agregarProducto(){
        return "carrito.html";
    }
    @PostMapping("/carrito")
    public String agregarProducto(ModelMap modelo, @RequestParam List<Producto> listaProductos, @RequestParam String id){
        try {
            compraServicio.agregarProducto(listaProductos, id);
            modelo.put("Exito", "El producto se agregó correctamente.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al agregar el producto.");
        }
        return "carrito.html";
    }
    
    @GetMapping("/carrito")
    public String quitarProducto(){
        return "carrito.html";
    }
    @PostMapping("/carrito")
    public String quitarProducto(ModelMap modelo, @RequestParam List<Producto> listaProductos, @RequestParam String id){
        try {
            compraServicio.quitarProducto(listaProductos, id);
            modelo.put("Exito", "El producto se eliminó correctamente.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al quitar el producto.");
        }
        return "carrito.html";
    }
    
    @GetMapping("/carrito")
    public String eliminarCompra(){
        return "carrito.html";
    }
    
    @PostMapping("/carrito")
    public String eliminarCompra(ModelMap modelo, @RequestParam String id){
        try {
            compraServicio.eliminarCompra(id);
            modelo.put("Exito", "La compra se ha eliminado correctamente.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al eliminar la compra.");
        }
        return "carrito.html";
    }


}
