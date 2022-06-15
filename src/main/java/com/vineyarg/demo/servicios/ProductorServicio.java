package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.ImagenesRepositorio;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductorServicio {
    
    @Autowired
    private ProductorRepositorio productorRepositorio;
    
    @Autowired
    private ImagenesServicio imagenesServicio;
    
    @Autowired
    private ImagenesRepositorio imagenesRepositorio;

    //GUARDAR UN PRODUCTOR:creación
    @Transactional(propagation = Propagation.NESTED)
    public Productor guardar(String nombre, String razonSocial, String domicilio, String correo,
            String clave, String descripcion, String region, MultipartFile archivo, Boolean alta) throws Exception {

        //VALIDACIONES   
        validar(nombre, razonSocial, domicilio, correo, clave, descripcion, region);
        Productor productor = new Productor();

        //SETEO DE ATRIBUTOS    
        productor.setNombre(nombre);
        productor.setRazonSocial(razonSocial);
        productor.setDomicilio(domicilio);
        productor.setCorreo(correo);
        productor.setClave(clave);
        productor.setDescripcion(descripcion);
        productor.setAlta(true);
        
        Imagenes imagen = new Imagenes();
        imagenesServicio.guardarNueva(archivo);
        
        productor.setImagen(imagen);

        //PERSISTENCIA DEL OBJETO
        return productorRepositorio.save(productor);
        
    }

    //MODIFICAR DATOS
    @Transactional(propagation = Propagation.NESTED)
    public void modificar(String id, String nombre, String razonSocial, String domicilio, String correo,
            String clave, String descripcion, String region, MultipartFile archivo, boolean alta) throws Exception {
        validar(nombre, razonSocial, domicilio, correo, clave, descripcion, region);
        Optional<Productor> respuesta = productorRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Productor productor = respuesta.get();
            productor.setNombre(nombre);
            productor.setRazonSocial(razonSocial);
            productor.setDomicilio(domicilio);
            productor.setCorreo(correo);
            productor.setClave(clave);
            productor.setRegion(region);
            
            Imagenes imagen = new Imagenes();
            imagenesServicio.guardarNueva(archivo);
            
            productor.setImagen(imagen);
            Imagenes foto = new Imagenes();
            
            productorRepositorio.save(productor);
        } else {
            throw new Excepcion("No se pueden modificar los datos");
        }
        
    }

    //ELIMINAR UN PRODUCTOR
    @Transactional(propagation = Propagation.NESTED)
    public void borrarPorId(String id) {
        Optional<Productor> optional = productorRepositorio.findById(id);
        
        if (optional.isPresent()) {
            productorRepositorio.delete(optional.get());
        }
    }

    //DAR DE BAJA
    public void darDeBaja(String id) throws Exception {
        Optional<Productor> optional = productorRepositorio.findById(id);
        if (optional.isPresent()) {
            
            Productor productor = optional.get();
            productor.setAlta(false);
        } else {
            throw new Excepcion("No se pueden modificar los datos");
        }
    }

    //CONSULTA
    @Transactional(readOnly = true)
    public void buscarPorId(String id) {
        Optional<Productor> optional = productorRepositorio.findById(id);
        
        if (optional.isPresent()) {
            productorRepositorio.findById(id);
        }
    }

    //CONSULTA POR NOMBRE
    @Transactional(readOnly = true)
    public Productor buscarPorNombre(String nombre) throws Exception {
        validar(nombre, nombre, nombre, nombre, nombre, nombre, nombre);
        Productor productor = productorRepositorio.buscarPorNombre(nombre);
        return productor;
    }

    //TRAER LISTA POR NOMBRE
    @Transactional(readOnly = true)
    public List<Productor> listaProductores() {
        List<Productor> listaProductores = productorRepositorio.findAll();
        return listaProductores();
    }

    //VALIDAR
    public void validar(String nombre, String razonSocial, String domicilio, String correo,
            String clave, String descripcion, String region) throws Exception {
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Excepcion("Debe indicar el nombre del productor");
        }
        
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new Excepcion("Debe indicar la razón social del productor");
        }
        
        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new Excepcion("Debe indicar el domicilio del productor");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new Excepcion("Debe indicar el correo del productor");
        }
        if (clave == null || clave.trim().isEmpty()) {
            throw new Excepcion("Debe indicar la clave del productor");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new Excepcion("Debe indicar la región del productor");
        }
    }
}
