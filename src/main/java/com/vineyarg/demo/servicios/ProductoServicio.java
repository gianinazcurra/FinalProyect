package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Imagenes;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.repositorios.ImagenesRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.ProductorRepositorio;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
//import com.vineyarg.demo.repositorios.ProductorRepositorio;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ProductoServicio {


    
    @Autowired
    private ImagenesRepositorio imagenesRepositorio;

     @Autowired
    private ImagenesServicio imagenesServicio;

     
    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
     private ProductorRepositorio productorRepositorio;

    @Transactional
    public void agregarProducto(Set<MultipartFile> imagenes, /*si no funciona probar así: MutiplepartFile[] imagenes*/String nombre, Integer cantidad, Double precio, String descripcion,
            String varietal, Productor productor, String SKU) throws Excepcion {
        /*Antes de persistir el objeto tenemos que validar que los atributos lleguen*/
        validar(nombre, cantidad, precio, descripcion,
                varietal, productor, SKU);

        //Creamos un nuevo producto y le seteamos los datos
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCantidad(cantidad);  
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
        producto.setVarietal(varietal);
        producto.setProductor(productor);
        producto.setSku(SKU);
        producto.setAlta(true);
        producto.setCantidadValoraciones(0);
        producto.setCantidadVecesValorado(0);
        producto.setPromedioValoraciones(0.00);
//        producto.setValoraciones(valoraciones);
        //producto.setAlta(true);

        Set<Imagenes> imagenesCargadas = new HashSet();
        Set<MultipartFile> imagenesInput = imagenes;
        
        for (MultipartFile multipartFile : imagenesInput) {
            
            Imagenes imagen = new Imagenes();
            imagen = imagenesServicio.guardarNueva(multipartFile);

            imagenesCargadas.add(imagen);

        }

        producto.setImagenes(imagenesCargadas);

        productoRepositorio.save(producto);//el repositorio guarda el objeto creado en la base de datos, lo transforma en una tabla

    }

    public void modificarProducto(Set<MultipartFile> imagenes, String idProductoElegido, String nombre, Integer cantidad, Double precio, String descripcion, String varietal) throws Excepcion {

        Optional<Producto> respuesta = productoRepositorio.findById(idProductoElegido);
        if (respuesta.isPresent()) {

            Producto producto = respuesta.get();

            if(producto.getNombre().equalsIgnoreCase(nombre)) {
                
                String nombreEstaOk = "nombreOk";
                validar(nombreEstaOk, cantidad, precio, descripcion,
                    producto.getVarietal(), producto.getProductor(), producto.getSku());
                
            } else {validar(nombre, cantidad, precio, descripcion,
                    producto.getVarietal(), producto.getProductor(), producto.getSku());
            }
            

            producto.setNombre(nombre);
            producto.setCantidad(cantidad);
            producto.setPrecio(precio);
            producto.setDescripcion(descripcion);
        producto.setVarietal(varietal);

        if(!imagenes.isEmpty()) {
            
             Set<Imagenes> imagenesCargadas = new HashSet();
        
             Set<MultipartFile> imagenesInput = imagenes;
        
        for (MultipartFile multipartFile : imagenesInput) {
            
            Imagenes imagen = new Imagenes();
            imagen = imagenesServicio.guardarNueva(multipartFile);

            imagenesCargadas.add(imagen);

        }
        
            producto.setImagenes(imagenesCargadas);
        }
            productoRepositorio.save(producto);

        }
    }

    @Transactional
    public void bajaProducto(String id) throws Excepcion {

        Optional<Producto> respuesta = productoRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Producto producto = respuesta.get();

            producto.setAlta(false);
            producto.setCantidad(0); //le seteamos la cantidad de stock en cero porque retiró el producto

            productoRepositorio.save(producto);
        }
    }

    @Transactional
    public void valorarProducto(String id, int valoracion) throws Excepcion {

        Optional<Producto> respuesta = productoRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Producto producto = respuesta.get();

            producto.setCantidadVecesValorado(producto.getCantidadVecesValorado() + 1);
            producto.setCantidadValoraciones(producto.getCantidadValoraciones() + valoracion);
            Double doble1 = Double.valueOf(producto.getCantidadVecesValorado());
            Double doble2 = Double.valueOf(producto.getCantidadValoraciones());
            Double doble3 = Double.valueOf(doble2 / doble1);
            doble3 = (Double) (Math.round(doble3 * 100.0) / 100.0);
            producto.setPromedioValoraciones(doble3);
            productoRepositorio.save(producto);
        }
    }

    public List<Producto> listarProductos() {
        List<Producto> productos = productoRepositorio.findAll();

        return productos;
    }

    public Producto buscarPorId(String id) throws Excepcion {

        if (id == null) {
            throw new Excepcion("Debe indicar Id");
        }
        Producto producto = productoRepositorio.buscarPorId(id);
        return producto;
    }

    public Producto buscarPorNombre(String nombre) throws Excepcion {

        if (nombre == null) {
            throw new Excepcion("Debe indicar nombre");
        }
        Producto producto = productoRepositorio.buscarPorNombre(nombre);
        return producto;
    }

    public Producto buscarPorProductor(Productor productor) throws Excepcion {

        if (productor == null) {
            throw new Excepcion("Debe indicar la bodega o productor que desea buscar");
        }
        Producto producto = productoRepositorio.buscarPorProductor(productor.getId());
        return producto;
    }

    public Producto buscarPorPrecio(Double precio) throws Excepcion {

        if (precio < 0) {
            throw new Excepcion("El precio debe ser mayor a 0");
        }
        Producto producto = productoRepositorio.buscarPorPrecio(precio);
        return producto;
    }

    public Producto buscarPorVarietal(String varietal) throws Excepcion {

        if (varietal == null) {
            throw new Excepcion("Debe indicar el varietal que desea");
        }
        Producto producto = productoRepositorio.buscarPorVarietal(varietal);
        return producto;
    }

    public void validar(String nombre, Integer cantidad, Double precio, String descripcion,
            String varietal, Productor productor, String SKU) throws Excepcion {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Excepcion("El nombre no puede estar vacío");
        }
        
        List<Producto> productos = productoRepositorio.findAll();
        
        for (Producto producto : productos) {
            
            if(producto.getNombre().equalsIgnoreCase(nombre))
            throw new Excepcion("Ya hay un producto registrado con ese nombre");
        }
        
        if (cantidad < 0) {
            throw new Excepcion("No puedes agregar un stock negativo");
        }
        if (precio < 0) {
            throw new Excepcion("El precio debe ser mayor a 0");
        }
        if (descripcion == null) {
            throw new Excepcion("La descripcion no puede estar vacía");
        }
        if (varietal == null) {
            throw new Excepcion("Debes indicar el varietal de tu vino");
        }
        if (productor == null) {
            throw new Excepcion("Debes ingresar la bodega");
        }
        if (SKU == null) {
            throw new Excepcion("Debes ingresar SKU del producto");
        }
//        if (valoraciones == null || valoraciones.isNaN()) {//PREGUNTAR
//            throw new Excepcion("Debe valorar el producto");
//        }

    }
}
