
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Productor;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/carrito")
public class CompraControlador {
    @Autowired
    private CompraServicio compraServicio;
    @Autowired
    private ProductoServicio productoServicio;
    
    @GetMapping("/crearCompra")
    public String iniciarCompra (){
        return "carrito.html";
    }
    
    @PostMapping("/crearCompra")
    public String iniciarCompra(ModelMap modelo, @RequestParam Integer cantidad,  @RequestParam Usuario usuario, @RequestParam List<Producto> listaProductos, @RequestParam Date fechaCompra, @RequestParam Double montoFinal, @RequestParam String direccionEnvio){
        try {
            compraServicio.crearCompra(cantidad, usuario, listaProductos, fechaCompra, montoFinal, direccionEnvio);
            modelo.put("Exito", "La compra se realizó con éxito.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al realizar la compra.");
        }
        return "carrito.html";
    }
    @GetMapping("/agregarProducto")
    public String agregarProducto(){
        return "carrito.html";
    }
    @PostMapping("/agregarProducto")
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
    
    @GetMapping("/quitarProducto")
    public String eliminarProducto(){
        return "carrito.html";
    }
    @PostMapping("/quitarProducto")
    public String eliminarProducto(ModelMap modelo, @RequestParam List<Producto> listaProductos, @RequestParam String id){
        try {
            compraServicio.quitarProducto(listaProductos, id);
            modelo.put("Exito", "El producto se eliminó correctamente.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al quitar el producto.");
        }
        return "carrito.html";
    }
    
    @GetMapping("/eliminarCompra")
    public String borrarCompra(){
        return "carrito.html";
    }
    
    @PostMapping("/eliminarCompra")
    public String borrarCompra(ModelMap modelo, @RequestParam String id){
        try {
            compraServicio.eliminarCompra(id);
            modelo.put("Exito", "La compra se ha eliminado correctamente.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al eliminar la compra.");
        }
        return "carrito.html";
    }
    
    @GetMapping("/compraCarrito")
    public String carrito(){
        return "carrito.html";
    }
    
    @PostMapping("/compraCarrito")
    public String carrito(ModelMap modelo, @RequestParam String id, @RequestParam List<Producto> listaProductos, @RequestParam Double montoFinal, @RequestParam Integer cantidad, @RequestParam List<MultipartFile> imagenes, @RequestParam String nombre, @RequestParam Double precio, @RequestParam Productor productor){
        try {
            compraServicio.compraCarrito(listaProductos, id, cantidad, montoFinal);
            productoServicio.agregarProducto(imagenes, nombre, cantidad, precio, nombre, id, productor, id);
            
            System.out.println("Nombre " + nombre);
            System.out.println("Precio " + precio);
            System.out.println("Imagenes " + imagenes);
            modelo.put("Exito", "El producto se agregó correctamente al carrito.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "No se pudo agregar el producto.");
        }
        return "carrito.html";
    }
    


}
