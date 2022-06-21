/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.controladores;

import com.vineyarg.demo.entidades.Usuario;
import javax.servlet.http.HttpSession;
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
    
    @GetMapping("/")
    public String index(@RequestParam(required = false) String logout, ModelMap modelo) {
        
      
       
        if (logout != null) {
            modelo.put("logout", "Has salido de tu cuenta");
            

        }
        
       
        
       
        return "index.html";
    
    
}
   
}