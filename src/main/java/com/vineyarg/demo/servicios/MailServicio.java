/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaqu
 */
@Service
public class MailServicio {
   
  
  @Autowired
    private JavaMailSender mailServicio;
    
    @Async //el programa se sigue ejecutando mientras ejecuta esta acción
    public void enviar(String correo, String asunto, String contenido){
        SimpleMailMessage mensaje= new SimpleMailMessage ();//es un objeto de mailSender
        mensaje.setTo(correo);
        mensaje.setFrom("noreplay@vineyarg.com.ar");
        mensaje.setSubject(asunto);
        mensaje.setText(contenido);
    //se le puede agregar fecha o demas cosas
        
        mailServicio.send(mensaje);
    }
      }  

