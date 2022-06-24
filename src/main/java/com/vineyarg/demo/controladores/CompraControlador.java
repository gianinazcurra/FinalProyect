package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.ItemCompra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ItemCompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.CompraServicio;
import com.vineyarg.demo.servicios.ItemCompraServicio;
import com.vineyarg.demo.servicios.ProductoServicio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private ItemCompraServicio itemCompraServicio;
    @Autowired
    private ItemCompraRepositorio ItemCompraRepositorio;

//    @GetMapping("/crearCompra")
//    public String iniciarCompra() {
//        return "carrito.html";
//    }
    @PostMapping("/agregaCarrito")
    public String agregaCarrito(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String idProducto, @RequestParam Integer cantidad) {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {

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

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();
            System.out.println(usuario.getNombre());

            Compra compraEnCurso = compraRepositorio.buscarComprasSinEnviarPorUsuario(usuario.getId());
           

            if (compraEnCurso != null) {
                System.out.println("aca1");
                compraServicio.preCompraCarrito(producto, idUsuario, cantidad);
                System.out.println("aca2");
                System.out.println(compraEnCurso.getId());
                modelo.put("compraEnCurso", "Se agregó el producto al carrito");
                modelo.put("compra", compraEnCurso);
//                System.out.println(compraEnCurso.getListaProductos());

                List<Producto> productosT = productoRepositorio.findAll();
                List<Producto> productos = new ArrayList();
                for (Producto productoS : productosT) {
                    if (productoS.isAlta()) {
                        productos.add(productoS);
                    }
                }
                modelo.put("productos", productos);
                return "tienda.html";

            } else if (compraEnCurso == null) {

                compraServicio.preCompraCarrito(producto, idUsuario, cantidad);

                compraEnCurso = compraRepositorio.buscarComprasSinEnviarPorUsuario(usuario.getId());
                modelo.put("compra", compraEnCurso);
                modelo.put("compraEnCurso", "Se agregó el producto al carrito");
                
                List<Producto> productosT = productoRepositorio.findAll();
                List<Producto> productos = new ArrayList();
                for (Producto productoS : productosT) {
                    if (productoS.isAlta()) {
                        productos.add(productoS);
                    }
                }
                modelo.put("productos", productos);
                return "tienda.html";
            }

        } else {
            modelo.put("error", "Para comprar primero debes iniciar sesión");
            return "login.html";
        }
        return "tienda.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/finalizarCompra")
    public String finalizarCompra(ModelMap modelo, @RequestParam String idUsuario, @RequestParam String idCompra, @RequestParam(required = false) String decision) {

        
        
        if (decision.equalsIgnoreCase("anular")) {

           
         try {
            compraServicio.anularCompra(idCompra);

            modelo.put("exito", "La compra se canceló con éxito.");
            
            List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();
        for (Producto producto : productosT) {
            if (producto.isAlta()) {
                productos.add(producto);
            }
        }
        modelo.put("productos", productos);
        return "tienda.html";
        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al cancelar la compra");
            
            List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();
        for (Producto producto : productosT) {
            if (producto.isAlta()) {
                productos.add(producto);
            }
        }
        modelo.put("productos", productos);
        }
        return "tienda.html";
        }
                
          if (decision.equalsIgnoreCase("continuar"))   {
        try {

            Compra compraAntesDePago = compraRepositorio.buscarComprasSinEnviarPorUsuario(idUsuario);

            System.out.println(compraAntesDePago.getId());
            if (compraAntesDePago != null) {

                modelo.put("carrito", compraAntesDePago);

                Set<ItemCompra> productosCompra = compraAntesDePago.getItemCompra();

                Double total = 0.00;
                Double totalSumaProductos = 0.00;

                for (ItemCompra itemCompra : productosCompra) {

                    total = itemCompra.getTotalProducto();

                    totalSumaProductos = totalSumaProductos + total;

                }
                modelo.put("itemsCompra", productosCompra);

                modelo.put("subtotal", Math.round(totalSumaProductos * 100.0) / 100.0);

                Double envio = 850.00;
                Double totalCompraConEnvio = (totalSumaProductos + envio);

                modelo.put("totalCompra", Math.round(totalCompraConEnvio * 100.0) / 100.0);

            } 
        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al realizar la compra.");
        }
        return "carrito.html";
    }  if (decision.equalsIgnoreCase("notDecision"))  {
              
               try {

            Compra compraAntesDePago = compraRepositorio.buscarComprasSinEnviarPorUsuario(idUsuario);

            System.out.println(compraAntesDePago.getId());
            if (compraAntesDePago != null) {

                modelo.put("carrito", compraAntesDePago);

                Set<ItemCompra> productosCompra = compraAntesDePago.getItemCompra();

                Double total = 0.00;
                Double totalSumaProductos = 0.00;

                for (ItemCompra itemCompra : productosCompra) {

                    total = itemCompra.getTotalProducto();

                    totalSumaProductos = totalSumaProductos + total;

                }
                modelo.put("itemsCompra", productosCompra);

                modelo.put("subtotal", Math.round(totalSumaProductos * 100.0) / 100.0);

                Double envio = 850.00;
                Double totalCompraConEnvio = (totalSumaProductos + envio);

                modelo.put("totalCompra", Math.round(totalCompraConEnvio * 100.0) / 100.0);

            } 
        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al realizar la compra.");
        }
        return "carrito.html";
          }
              return "index.html";
           }   

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/checkout")
    public String checkout(ModelMap modelo, @RequestParam String idCompra, HttpSession session) {

        
        try {

            Optional<Compra> respuesta = compraRepositorio.findById(idCompra);

            if (respuesta.isPresent()) {

                Compra compraDef = respuesta.get();
                
                Usuario login = (Usuario) session.getAttribute("usuarioSession");
                if (login == null || !login.getId().equalsIgnoreCase(compraDef.getUsuario().getId())) {

                    modelo.put("error", "Para comprar primero debes iniciar sesión");
                    return "login.html";

                }       
                
                modelo.put("carrito", compraDef);
                modelo.put("usuario", compraDef.getUsuario());

                Set<ItemCompra> productosCompra = compraDef.getItemCompra();

                Double total = 0.00;
                Double totalSumaProductos = 0.00;

                for (ItemCompra itemCompra : productosCompra) {

                    total = itemCompra.getTotalProducto();

                    totalSumaProductos = totalSumaProductos + total;

                }
                modelo.put("itemsCompra", productosCompra);

                modelo.put("subtotal", Math.round(totalSumaProductos * 100.0) / 100.0);

                Double envio = 850.00;
                Double totalCompraConEnvio = (totalSumaProductos + envio);

                modelo.put("totalCompra", Math.round(totalCompraConEnvio * 100.0) / 100.0);


//                modelo.put("subtotal", subtotal);


                modelo.put("totalCompra", totalCompraConEnvio);

            }

        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al realizar la compra.");
        }
        return "checkout.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @PostMapping("/enviarPedido")
    public String enviarPedidoCompra(ModelMap modelo, @RequestParam String idCompra, @RequestParam String direccion, @RequestParam String detalles, @RequestParam String provincia,
            @RequestParam String pais, @RequestParam String CP, @RequestParam String modoPago, @RequestParam String numTarjeta, @RequestParam String titTarjeta,
            @RequestParam String vencimiento, @RequestParam String CVV, @RequestParam String DNI, @RequestParam Double totalCompra) {

        String direccionEnvio = "Direccion de envío " + direccion + " detalles para el envío " + detalles + " provincia " + provincia + " país" + pais + " CP: " + CP;
        String formaDePago = "Modo " + modoPago + " número tarjeta " + numTarjeta + " titular Tarjeta " + titTarjeta + " vencimiento " + vencimiento + " CVV " + CVV + " DNI titular de la tarjeta " + DNI;

        try {
            compraServicio.enviarPedido(idCompra, direccionEnvio, formaDePago, totalCompra);

            String exito = "Felicitaciones! Tu compra fue enviada con éxito. Nos encargaremos de procesar el pago y podrás ver la confirmación en tu perfil de usuario cuando tu compra esté finalizada";

            modelo.put("exito", exito);

        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al realizar la compra");
        }
        return "exito-compra.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/anularCompra")
    public String anularCompra(ModelMap modelo, @RequestParam String idCompra) {
        try {
            compraServicio.anularCompra(idCompra);

            modelo.put("exito", "La compra se canceló con éxito.");
            
            List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();
        for (Producto producto : productosT) {
            if (producto.isAlta()) {
                productos.add(producto);
            }
        }
        modelo.put("productos", productos);
        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al cancelar la compra");
            
            List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();
        for (Producto producto : productosT) {
            if (producto.isAlta()) {
                productos.add(producto);
            }
        }
        modelo.put("productos", productos);
        }
        return "tienda.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_USUARIO_COMUN')")
    @GetMapping("/eliminarProducto")
    public String eliminarProducto(ModelMap modelo, @RequestParam String idProductoEliminar, @RequestParam String idCompraEnCurso) {

        try {
            compraServicio.quitarProducto(idProductoEliminar, idCompraEnCurso);

            Compra compraAntesDePago = compraRepositorio.getById(idCompraEnCurso);

            if (compraAntesDePago != null) {

                modelo.put("carrito", compraAntesDePago);

                Set<ItemCompra> productosCompra = compraAntesDePago.getItemCompra();

                Double total = 0.00;
                Double totalSumaProductos = 0.00;

                for (ItemCompra itemCompra : productosCompra) {

                    total = itemCompra.getTotalProducto();

                    totalSumaProductos = totalSumaProductos + total;

                }
                modelo.put("itemsCompra", productosCompra);

                modelo.put("subtotal", Math.round(totalSumaProductos * 100.0) / 100.0);

                Double envio = 850.00;
                Double totalCompraConEnvio = (totalSumaProductos + envio);

                modelo.put("totalCompra", Math.round(totalCompraConEnvio * 100.0) / 100.0);

            }

        } catch (Exception e) {
            e.getMessage();
            modelo.put("error", "Error al realizar la compra.");
        }
        return "carrito.html";
    }
}