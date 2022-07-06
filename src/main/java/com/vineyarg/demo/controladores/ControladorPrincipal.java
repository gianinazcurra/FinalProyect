/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author joaqu
 */
@Controller
@RequestMapping
public class ControladorPrincipal {

    @Autowired
    private CompraRepositorio compraRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String logout, ModelMap modelo, HttpSession session) {

        Usuario login = (Usuario) session.getAttribute("usuarioSession");

        List<Producto> productosT = productoRepositorio.findAll();
        List<Producto> productos = new ArrayList();

        for (Producto producto : productosT) {
            if (producto.isAlta() && producto.getProductor().isAlta()) {

                if (productos.size() < 3) {
                    productos.add(producto);
                }

            }
        }
        modelo.put("productos", productos);

        if (login != null) {
            Compra compraEnCursoInicioSesion = compraRepositorio.buscarComprasSinEnviarPorUsuario(login.getId());

            if (compraEnCursoInicioSesion != null) {

                modelo.put("compraEnCursoInicioSesion", compraEnCursoInicioSesion);
                modelo.put("compraPendiente", "Tenés un compra sin finalizar. ¿Querés continuarla?");

                List<String> decision = new ArrayList();
                decision.add("continuar");
                decision.add("anular");
                modelo.put("decisiones", decision);
            }
        }
        if (logout != null) {
            modelo.put("logout", "Has salido de tu cuenta");

        }

        return "index.html";

    }

    @GetMapping("/index.html")
    public String index1(@RequestParam(required = false) String logout, ModelMap modelo, HttpSession session) {

        return "redirect:/";
    }

    @GetMapping("/index")
    public String index2(@RequestParam(required = false) String logout, ModelMap modelo, HttpSession session) {

        return "redirect:/";
    }
}
