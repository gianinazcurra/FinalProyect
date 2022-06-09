
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.servicios.CompraServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agregar-producto")
public class CompraControlador {
    @Autowired
    private CompraServicio compraServicio;
    
    

}
