package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.ImagenesRepositorio;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Transactional
    public Productor guardar(String nombre, String razonSocial, String domicilio, String correo,
            String clave1, String clave2, String descripcion, String region, MultipartFile archivo) throws Exception {

        //VALIDACIONES   
        validar(nombre, razonSocial, domicilio, correo, clave1, clave2, descripcion, region);
        Productor productor = new Productor();

        //SETEO DE ATRIBUTOS    
        productor.setNombre(nombre);
        productor.setRazonSocial(razonSocial);
        productor.setDomicilio(domicilio);
        productor.setCorreo(correo);

        String encriptada = new BCryptPasswordEncoder().encode(clave1);
        productor.setClave(encriptada);

        productor.setDescripcion(descripcion);
        productor.setAlta(true);

        Imagenes imagen = new Imagenes();
        imagenesServicio.guardarNueva(archivo);

        productor.setImagen(imagen);

        //PERSISTENCIA DEL OBJETO
        return productorRepositorio.save(productor);

    }

    //MODIFICAR DATOS
    @Transactional
    public void modificar(String idProductor, String nombre, String razonSocial, String domicilio, String correo,
            String clave1, String clave2, String descripcion, String region, MultipartFile archivo) throws Exception {

       

        Optional<Productor> respuesta = productorRepositorio.findById(idProductor);

        if (respuesta.isPresent()) {
            
            Productor productor = respuesta.get();
            
            if(productor.getCorreo().equalsIgnoreCase(correo)) {
                
                String correoEstaOk = "estaok@estaok.com";
                
                validar(nombre, razonSocial, domicilio, correoEstaOk, clave1, clave2, descripcion, region);
                
            } else {
                validar(nombre, razonSocial, domicilio, correo, clave1, clave2, descripcion, region);
            }
            

            
            productor.setNombre(nombre);
            productor.setRazonSocial(razonSocial);
            productor.setDomicilio(domicilio);
            productor.setCorreo(correo);

            String encriptada = new BCryptPasswordEncoder().encode(clave1);
            productor.setClave(encriptada);

            productor.setRegion(region);

            Imagenes imagen = new Imagenes();
            imagenesServicio.guardarNueva(archivo);

            productor.setImagen(imagen);
            
           

            productorRepositorio.save(productor);

        } else {
            throw new Excepcion("Usuario o clave no hallada");
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
    public void darDeBaja(@RequestParam String correo, @RequestParam String clave) throws Exception {
        
        Productor verificacionProductor = productorRepositorio.BuscarProductorPorCorreoYClave(correo, clave);

        if (verificacionProductor == null) {
            throw new Excepcion("Usuario no encontrado");

        }

        Optional<Productor> respuesta = productorRepositorio.findById(productorRepositorio.BuscarProductorPorCorreoYClave(correo, clave).getId());

        if (respuesta.isPresent()){

            Productor productor = respuesta.get();

            productor.setAlta(false);
            
            productorRepositorio.save(productor);
            
        } else {

            throw new Excepcion("Usuario o clave no hallada");
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
            String clave1, String clave2, String descripcion, String region) throws Exception {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Excepcion("Debe indicar el nombre del productor");
        }

        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new Excepcion("Debe indicar la razón social del productor");
        }

        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new Excepcion("Debe indicar el domicilio del productor");
        }

        if (clave1 == null || clave1.trim().isEmpty()) {
            throw new Excepcion("Debe indicar la clave de su cuenta");
        }

        if (!correo.contains("@") || !correo.contains(".") || correo.trim() == null || correo.trim().isEmpty()) {
            throw new Excepcion("E-mail inválido");
        }

        List<Productor> correoNoRepetido = productorRepositorio.findAll();

        for (Productor productor : correoNoRepetido) {

            if (productor.getCorreo().equalsIgnoreCase(correo) && productor.isAlta()) {

                throw new Excepcion("Ya existe un usuario regisrado con este correo");
            }
        }
        //Validación clave contiene requisitos

        

       if (clave1.trim() == null || clave1.trim().isEmpty()) {
            throw new Excepcion("La contraseña no puede ser nula");
        }

        char ch;
        
        int verificacionClaveNumero = 0;
        int verificacionClaveMayuscula = 0;
        
        for (int i = 0; i < clave1.length(); i++) {

//            ch = (char) i;
            if (Character.isUpperCase(clave1.charAt(i))) {
                verificacionClaveMayuscula++;
               
            };
        }
        for (int i = 0; i < clave1.length(); i++) {

//            ch = (char) i;
            if (Character.isDigit(clave1.charAt(i))) {
                verificacionClaveNumero++;

            };
        }
//        System.out.println(verificacionClaveMayuscula + " " + verificacionClaveNumero);
        if (verificacionClaveMayuscula < 1 || verificacionClaveNumero < 1 || clave1.trim().length() < 6) {
            throw new Excepcion("La contraseña no cumple con los requisitos especificados (debe contener una mayúscula, un número y por lo menos 6 caractéres");
        }

        if (!clave2.trim().equalsIgnoreCase(clave1.trim())) {
            throw new Excepcion("Las contraseñas ingresadas no son iguales");
        }
        if (!clave2.trim().equalsIgnoreCase(clave1.trim())) {
            throw new Excepcion("Las contraseñas ingresadas no son iguales");
        }

        if (region == null || region.trim().isEmpty()) {
            throw new Excepcion("Debe indicar la región del productor");
        }
    }
}
