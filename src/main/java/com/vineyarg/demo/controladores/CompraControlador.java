package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.CompraServicio;
import com.vineyarg.demo.servicios.ProductoServicio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.Date;
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
public class CompraControlador {

    @Autowired
    private CompraServicio compraServicio;
    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CompraRepositorio compraRepositorio;

//    @GetMapping("/crearCompra")
//    public String iniciarCompra() {
//        return "carrito.html";
//    }
    
    @PostMapping("/agregaCarrito")
    public String agregaCarrito(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String idProducto, @RequestParam Integer cantidad) {

        Usuario login = (Usuario) session.getAttribute("UsuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {

            modelo.put("error", "Para comprar primero debes iniciar sesión");
            return "login.html";

        }

        Optional<Producto> verificaProducto = productoRepositorio.findById(idProducto);
        Producto producto = new Producto();

        if (verificaProducto.isPresent()) {

            producto = verificaProducto.get();

            if (producto.getCantidad() <= 0) {

                modelo.put("error", "Lo sentimos, el producto se ha agotado, por favor elije otro");
                return "tienda.html";
            }
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();

            Compra compraEnCurso = compraRepositorio.buscarComprasSinEnviarPorUsuario(usuario.getId());

            if (compraEnCurso != null) {

                modelo.put("compraEnCurso", "Se agregó el producto al carrito");
                modelo.put("compra", compraEnCurso);
                compraServicio.preCompraCarrito(producto, id, cantidad);

            }

            modelo.put("compraEnCurso", "Se agregó el producto al carrito");
            modelo.put("compra", compraEnCurso);
            compraServicio.preCompraCarrito(producto, id, cantidad);

        } else {
            modelo.put("error", "Para comprar primero debes iniciar sesión");
            return "login.html";
        }
        return "tienda.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(ModelMap modelo, @RequestParam String idUsuario, @RequestParam String idCompra) {

        try {

            Optional<Compra> verificaCompra = compraRepositorio.findById(idCompra);

            if (verificaCompra.isPresent()) {
                Compra compraAntesDePago = new Compra();
                compraAntesDePago = verificaCompra.get();

                modelo.put("Carrito", compraAntesDePago);

                List<Double> subtotales = compraAntesDePago.getSubtotales();

                Double sbt = 0.00;
                Double subtotal = 0.00;

                for (Double subtotale : subtotales) {

                    subtotal = subtotale + sbt;
                    sbt = subtotale;

                }

                modelo.put("subtotal", subtotal);

                Double envio = 850.00;
                Double totalCompra = (subtotal + envio);

                modelo.put("totalCompra", totalCompra);

            }

        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al realizar la compra.");
        }
        return "carrito.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/checkout")
    public String checkout(ModelMap modelo, @RequestParam String idCompra, HttpSession session) {
        
        try {
            
        
           Optional<Compra> respuesta = compraRepositorio.findById(idCompra);
           
           if (respuesta.isPresent()) {
              
               Compra compra = respuesta.get();
               
               Usuario login = (Usuario) session.getAttribute("UsuarioSession");
               if (login == null || !login.getId().equalsIgnoreCase(compra.getUsuario().getId())) {

            modelo.put("error", "Para comprar primero debes iniciar sesión");
            return "login.html";

        }
               modelo.put("compra", compra);
               modelo.put("usuario", compra.getUsuario());
               
               List<Double> subtotales = compra.getSubtotales();

                Double sbt = 0.00;
                Double subtotal = 0.00;

                for (Double subtotale : subtotales) {

                    subtotal = subtotale + sbt;
                    sbt = subtotale;

                }

                modelo.put("subtotal", subtotal);

                Double envio = 850.00;
                Double totalCompra = (subtotal + envio);

                modelo.put("totalCompra", totalCompra);
           
      }   

        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al realizar la compra.");
        }
        return "checkout.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/enviarPedido")
    public String enviarPedidoCompra(ModelMap modelo, @RequestParam String idCompra, @RequestParam String direccion, @RequestParam String detalles, @RequestParam String provincia,
            @RequestParam String pais, @RequestParam String CP, @RequestParam String modoPago, @RequestParam String numTarjeta, @RequestParam String titTarjeta, 
            @RequestParam String vencimiento, @RequestParam String CVV, @RequestParam String DNI) {

           String direccionEnvio = "Direccion de envío " + direccion + " detalles para el envío " + detalles + " provincia " + provincia + " país" + pais + " CP: " + CP;
           String formaDePago = "Modo " + modoPago + " número tarjeta " + numTarjeta + " titular Tarjeta " + titTarjeta + " vencimiento " + vencimiento + " CVV " + CVV + " DNI titular de la tarjeta " + DNI;
        
           try {
                compraServicio.enviarPedido(idCompra, direccionEnvio, formaDePago);
            
               String exito = "Felicitaciones! Tu compra fue enviada con éxito. Nos encargaremos de procesar el pago y podrás ver la confirmación en tu perfil de usuario cuando tu compra esté finalizasda";
                
               modelo.put("éxito", exito);

        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al realizar la compra.");
        }
        return "perfil-usuario.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/anularCompra")
    public String anularCompra(ModelMap modelo, @RequestParam String idCompra) {
        try {
            compraServicio.anularCompra(idCompra);

            modelo.put("Exito", "La compra se canceló con éxito.");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al cancelar la compra");
        }
        return "tienda.html";
    }

//    @PostMapping("/crearCompra")
//    public String iniciarCompra(ModelMap modelo, @RequestParam Integer cantidad, @RequestParam Usuario usuario, @RequestParam List<Producto> listaProductos, @RequestParam Date fechaCompra, @RequestParam Double montoFinal, @RequestParam String direccionEnvio) {
//        try {
//            compraServicio.crearCompra(cantidad, usuario, listaProductos, fechaCompra, montoFinal, direccionEnvio);
//            modelo.put("Exito", "La compra se realizó con éxito.");
//        } catch (Exception e) {
//            e.getMessage();
//            modelo.put("Error", "Error al realizar la compra.");
//        }
//        return "carrito.html";
//    }
//
//    @GetMapping("/agregarProducto")
//    public String agregarProducto() {
//        return "carrito.html";
//    }
//
//    @PostMapping("/agregarProducto")
//    public String agregarProducto(ModelMap modelo, @RequestParam List<Producto> listaProductos, @RequestParam String id) {
//        try {
//            compraServicio.agregarProducto(listaProductos, id);
//            modelo.put("Exito", "El producto se agregó correctamente.");
//        } catch (Exception e) {
//            e.getMessage();
//            modelo.put("Error", "Error al agregar el producto.");
//        }
//        return "carrito.html";
//    }
    @GetMapping("/quitarProducto")
    public String eliminarProducto() {
        return "carrito.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/eliminar-producto")
    public String eliminarProducto(ModelMap modelo, @RequestParam String idProductoEliminar, @RequestParam Integer cantidades, @RequestParam String idCompraEnCurso) {

        try {
            compraServicio.quitarProducto(idProductoEliminar, cantidades, idCompraEnCurso);

            Optional<Compra> verificaCompra = compraRepositorio.findById(idCompraEnCurso);

            if (verificaCompra.isPresent()) {
                Compra compraAntesDePago = new Compra();
                compraAntesDePago = verificaCompra.get();

                modelo.put("Carrito", compraAntesDePago);

                List<Double> subtotales = compraAntesDePago.getSubtotales();

                Double sbt = 0.00;
                Double subtotal = 0.00;

                for (Double subtotale : subtotales) {

                    subtotal = subtotale + sbt;
                    sbt = subtotale;

                }

                modelo.put("subtotal", subtotal);

                Double envio = 850.00;
                Double totalCompra = (subtotal + envio);

                modelo.put("totalCompra", totalCompra);

            }
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Error", "Error al quitar el producto.");
        }
        return "carrito.html";
    }

//    @GetMapping("/eliminarCompra")
//    public String borrarCompra() {
//        return "carrito.html";
//    }

//    @PostMapping("/eliminarCompra")
//    public String borrarCompra(ModelMap modelo, @RequestParam String id) {
//        try {
//            compraServicio.eliminarCompra(id);
//            modelo.put("Exito", "La compra se ha eliminado correctamente.");
//        } catch (Exception e) {
//            e.getMessage();
//            modelo.put("Error", "Error al eliminar la compra.");
//        }
//        return "carrito.html";
//    }

//    @GetMapping("/compraCarrito")
//    public String carrito(){
//        return "carrito.html";
//    }
}
