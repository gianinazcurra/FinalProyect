/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.ImagenesRepositorio;
import java.io.IOException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author joaqu
 */


@Service
public class ImagenesServicio {

    @Autowired
    private ImagenesRepositorio imagenesRepositorio;
    
    
    

    @Transactional
    public Imagenes guardarNueva(MultipartFile archivo) throws Excepcion {

        if (archivo != null) {
            
            try {
                Imagenes imagen = new Imagenes();
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                
                System.out.println("archivo bytes" + archivo.getBytes());
                return imagenesRepositorio.save(imagen);

            } catch (IOException ex) {
                System.err.print(ex.getMessage());
            }

        } if (archivo == null){
            System.out.println("arhciov nuilo");
        }

        return null;
    }

    @Transactional
    public Imagenes actualizarFoto(String idImagen, MultipartFile archivo) throws Excepcion {

        if (archivo != null) {

            try {
                Imagenes imagen = new Imagenes();

                if (idImagen != null) {
                    Optional<Imagenes> respuesta = imagenesRepositorio.findById(idImagen);

                    if (respuesta.isPresent()) {

                        imagen = respuesta.get();

                    }
                }
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                return imagenesRepositorio.save(imagen);

            } catch (IOException ex) {
                System.err.print(ex.getMessage());
            }

        }

        return null;
    }
    
    @Transactional
    public void eliminarImagen(String idImagen) throws Excepcion {

       
                Imagenes imagen = new Imagenes();

                if (idImagen != null) {
                    Optional<Imagenes> respuesta = imagenesRepositorio.findById(idImagen);

                    if (respuesta.isPresent()) {

                        imagen = respuesta.get();
                        imagenesRepositorio.delete(imagen);
                        
                    }
                }

             
}

}
