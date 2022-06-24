package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.ItemCompra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.EstadoCompra;
import com.vineyarg.demo.enumeraciones.Regiones;
import com.vineyarg.demo.enumeraciones.TipoUsuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import com.vineyarg.demo.servicios.ProductorServicio;
import com.vineyarg.demo.servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ProductorControlador {

    @Autowired
    private ProductorServicio productorServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private CompraRepositorio compraRepositorio;

    @Autowired
    private ProductorRepositorio productorRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/registro-bodega")
    public String guardarProductor(ModelMap modelo) {

        modelo.put("regiones", Regiones.values());
        return "registro-bodega"; //me devuelve la vista
    }

    @PostMapping("/registro-bodega")
    public String registroBodega(ModelMap modelo, @RequestParam String nombre, @RequestParam String razonSocial, @RequestParam String domicilio, @RequestParam String correo,
            @RequestParam String clave1, @RequestParam String clave2, @RequestParam String descripcion, @RequestParam String region, @RequestParam MultipartFile archivo) throws Exception {
        try {

            productorServicio.guardar(nombre, razonSocial, domicilio, correo, clave1, clave2, descripcion, region, archivo);

            usuarioServicio.registrarUsuario(null, nombre, null, null, correo, clave1, clave2, null, TipoUsuario.PRODUCTOR);

        } catch (Exception e) {

            modelo.put("error", e.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("razonSocial", razonSocial);
            modelo.put("domicilio", domicilio);
            modelo.put("correo", correo);
            modelo.put("clave", clave1);
            modelo.put("clave", clave2);
            modelo.put("descripcion", descripcion);
            modelo.put("regiones", Regiones.values());

            return "registro-bodega";
        }

        modelo.put("registrado", "Productor registrado con éxito");
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @GetMapping("/editar-productor")
    public String editarProductor(ModelMap modelo, HttpSession session, @RequestParam String idUsuario) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isPresent()) {

            Usuario usuario = new Usuario();
            usuario = respuesta.get();

            Productor productor = new Productor();
            productor = productorRepositorio.BuscarProductorPorCorreo(usuario.getCorreo());

            modelo.put("regiones", Regiones.values());

            modelo.put("perfil", productor);

            modelo.put("perfilUsuario", usuario);

        } else {

            throw new Excepcion("Usuario no reconocido");
        }

        return "editar-productor.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/editarProductor")
    public String editarProductor(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String idProductor, @RequestParam String nombre,
            @RequestParam String razonSocial, @RequestParam String domicilio, @RequestParam String correo,
            @RequestParam String clave1, @RequestParam String clave2, @RequestParam String descripcion, @RequestParam String region, @RequestParam(required = false) MultipartFile archivo)
            throws Excepcion, Exception {

        try {
            Usuario login = (Usuario) session.getAttribute("usuarioSession");
            if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
                return "redirect:/index.html";
            }

            Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

            Usuario usuario = new Usuario();
            Productor productor = new Productor();
            if (respuesta.isPresent()) {

                usuario = respuesta.get();

            }

            Optional<Productor> respuesta1 = productorRepositorio.findById(idProductor);

            if (respuesta1.isPresent()) {

                productor = respuesta1.get();

            }
            modelo.put("productor", productor);
            modelo.put("perfilUsuario", usuario);

            productorServicio.modificar(idUsuario, idProductor, nombre, razonSocial, domicilio, correo, clave1, clave2, descripcion, region, archivo);

        } catch (Exception ex) {

            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("razonSocial", razonSocial);
            modelo.put("domicilio", domicilio);
            modelo.put("correo", correo);
            modelo.put("clave", clave1);
            modelo.put("clave", clave2);
            modelo.put("descripcion", descripcion);
            modelo.put("regiones", Regiones.values());

            return "editar-productor";

        }

        modelo.put("exito", "Productor modificado de manera satisfactoria");

        return "productorweb.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PRODUCTOR')")
    @PostMapping("/eliminarProductor")
    public String eliminarProductor(ModelMap modelo, HttpSession session, @RequestParam String idProductor, @RequestParam String idUsuario, @RequestParam String correo, @RequestParam String clave) throws Excepcion, Exception {

        try {

            Usuario login = (Usuario) session.getAttribute("usuarioSession");
            if (login == null || !login.getId().equalsIgnoreCase(idUsuario)) {
                return "redirect:/index.html";
            }

            Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

            if (respuesta.isPresent()) {
                Productor productor = new Productor();
                Usuario usuario = new Usuario();
                usuario = respuesta.get();

                Optional<Productor> respuesta1 = productorRepositorio.findById(idProductor);

                if (respuesta1.isPresent()) {

                    productor = respuesta1.get();

                }
                modelo.put("perfil", productor);
                modelo.put("perfilUsuario", usuario);

                productorServicio.eliminarProductor(idUsuario, idProductor, correo, clave);

            }
        } catch (Excepcion ex) {
            modelo.put("error1", ex.getMessage());

            modelo.put("mail", correo);
            modelo.put("clave", clave);

            return "editar-productor.html";
        }

        modelo.put("registrado", "Baja exitosa");
        return "registro.html";
    }

    @GetMapping("/productorweb")
    public String productorWeb(ModelMap modelo, HttpSession session, @RequestParam String id) throws Excepcion {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {
            return "redirect:/index.html";
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = new Usuario();
            usuario = respuesta.get();

            Productor productor = productorRepositorio.BuscarProductorPorCorreo(usuario.getCorreo());

            modelo.put("productor", productor);

        } else {

            throw new Excepcion("Productor no reconocido");
        }

        return "productorweb.html";
    }

    @GetMapping("/verVentas") //FALTA DESARROLLAR PARA QUE EL PRODUCTOR PUEDA VER SUS VENTAS
    public String verVentas(ModelMap modelo, HttpSession session, @RequestParam String idProductor) {

        List<Compra> listaComprasTotales = compraRepositorio.findAll();
        List<Compra> listaComprasCompletadas = new ArrayList();
        
        

        for (Compra listaCompras : listaComprasTotales) {
            
            if(listaCompras.getEstadoCompra().equals(EstadoCompra.ACEPTADA)) {
                
                listaComprasCompletadas.add(listaCompras);
            }
        }
        
        Set<ItemCompra> itemsProductor = new HashSet();
        for (Compra compras : listaComprasTotales) {

            Set<ItemCompra> items = compras.getItemCompra();

            for (ItemCompra item : items) {

                if (item.getProducto().getProductor().getId().equals(idProductor)) {

                    itemsProductor.add(item);
                }

            }
        }
        Productor productor = productorRepositorio.getById(idProductor);
        
          if(itemsProductor.isEmpty()) {
            modelo.put("sinVentas", "sinVentas");
        }
          
        modelo.put("productor", productor);
        modelo.put("itemsProductor", itemsProductor);
        return "productorweb.html";

    }

    @GetMapping("/verProductos")
    public String verProductos(ModelMap modelo, HttpSession session, @RequestParam String idProductor) {

        List<Producto> listaProductosProductor = productoRepositorio.buscarTodosPorProductor(idProductor);
        
        
        List<Producto> productosProductor = new ArrayList();

        
        for (Producto producto : listaProductosProductor) {

            if (producto.isAlta()) {
                productosProductor.add(producto);
            }
        }

        Productor productor = productorRepositorio.getById(idProductor);
        
        if(productosProductor.isEmpty()) {
            modelo.put("sinProductos", "sinProductos");
        }
        
        modelo.put("ProductosProductor", productosProductor);
        modelo.put("productor", productor);

         return "productorweb.html";
    }

}
