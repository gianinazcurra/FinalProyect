package com.vineyarg.demo.controladores;

import com.vineyarg.demo.servicios.ProductorServicio;
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
@RequestMapping("/registro-bodega")
public class ProductorControlador {

    @Autowired
    ProductorServicio productorServicio;

    @GetMapping("/guardar")
    public String guardarProductor() {
        return "registro-bodega"; //me devuelve la vista
    }

    @PostMapping("/guardar")
    public String guardarProductor(ModelMap modelo, @RequestParam String nombre, String razonSocial, String domicilio, String correo,
            String clave, String descripcion, String region, MultipartFile archivo, Boolean alta) throws Exception {
        try {

            productorServicio.guardar(nombre, razonSocial, domicilio, correo, clave, descripcion, region,
            archivo, alta);

            System.out.println("Productor " + nombre);
            modelo.put("Bien hecho", "Productor ingresado de manera correcta");
        } catch (Exception e) {
            e.getMessage();
            modelo.put("Ha ocurrido un error", "No se ha podido guardar el Productor");
        }
        return "registro-bodega";
    }

//    @GetMapping("/mostrar")
//    public String mostrarProductores(ModelMap modelo) {
//        List<Productores> productores = productorServicio.listaProductores();
//        modelo.addAttribute("productor", autores);
//        return "administracionAutores";
//    }

}
