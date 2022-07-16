package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.MailServicio;
import com.vineyarg.demo.servicios.ProductoServicio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
    MailServicio  mailServicio;
    
    @Autowired
    private CompraRepositorio compraRepositorio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/tienda")
    public String tienda(ModelMap modelo, HttpSession session) throws MessagingException {

        List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();

        //con el forEach descartamos productos dados de baja o que correspondan a productores dados de baja
        for (Producto producto : productosT) {
            if (producto.isAlta() && producto.getProductor().isAlta()) {
                productos.add(producto);

            }
        }

        
      

        modelo.put("productos", productos);

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
          if (login != null) {
            Compra compraEnCursoInicioSesion = compraRepositorio.buscarComprasSinEnviarPorUsuario(login.getId());

            if (compraEnCursoInicioSesion != null) {

                modelo.put("compraEnCursoInicioSesion", compraEnCursoInicioSesion);
                modelo.put("compraPendiente", "Tenés un compra sin finalizar, cuando quieras podés continuar");

                List<String> decision = new ArrayList();
                decision.add("continuar");
                decision.add("anular");
                modelo.put("decisiones", decision);
            }
        }
          
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
        for (Producto productosSimilare : productosSimilares) {
            System.out.println(productosSimilare);
        }

        return "producto.html";
    }

    @GetMapping("/tiendaRegiones")
    public String tiendaRegiones(ModelMap modelo, HttpSession session, @RequestParam String region) {

        List<Producto> productosT = productoRepositorio.buscarPorRegion(region);
        List<Producto> productos = new ArrayList();

        //con el forEach descartamos productos dados de baja o que correspondan a productores dados de baja
        for (Producto producto : productosT) {
            if (producto.isAlta() && producto.getProductor().isAlta()) {
                productos.add(producto);

            }
        }
        modelo.put("productos", productos);

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
          if (login != null) {
            Compra compraEnCursoInicioSesion = compraRepositorio.buscarComprasSinEnviarPorUsuario(login.getId());

            if (compraEnCursoInicioSesion != null) {

                modelo.put("compraEnCursoInicioSesion", compraEnCursoInicioSesion);
                modelo.put("compraPendiente", "Tenés un compra sin finalizar, cuando quieras podés continuar");

                List<String> decision = new ArrayList();
                decision.add("continuar");
                decision.add("anular");
                modelo.put("decisiones", decision);
            }
        }
          
        return "tienda.html";

    }
}
