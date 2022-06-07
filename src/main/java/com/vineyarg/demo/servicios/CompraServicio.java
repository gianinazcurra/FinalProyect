
package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CompraServicio {
    @Autowired
    private CompraRepositorio compraRepositorio;
    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private ProductoRepositorio productoRepositorio;
    
    @Transactional
    public void crearCompra(Integer cantidad, Usuario usuario, List<Producto> listaProductos, Date fechaCompra, Double montoFinal, String direccionEnvio) throws Excepcion{
        validar(cantidad, usuario, listaProductos, fechaCompra, montoFinal, direccionEnvio);
        
        Compra compra = new Compra();
        compra.setCantidad(cantidad);
        compra.setDireccionEnvio(direccionEnvio);
        compra.setFechaCompra(fechaCompra);
        compra.setListaProductos(listaProductos);
        compra.setMontoFinal(montoFinal);
        compra.setUsuario(usuario);

        compraRepositorio.save(compra);
    }
    
    @Transactional
    public void quitarProducto(List<Producto> listaProductos, String id) throws Excepcion{
        
        Optional<Producto> optional = productoRepositorio.findById(id);
        if (optional.isPresent()) {

            listaProductos.remove(optional.get());
        }
    }
   @Transactional
    public void agregarProducto(List<Producto> listaProductos, String id) throws Excepcion{
        
        Optional<Producto> optional = productoRepositorio.findById(id);
        if (optional.isPresent()) {

            listaProductos.add(optional.get());
        }
    }
    @Transactional
    public void eliminarCompra(String id)throws Excepcion{
        Optional<Compra> optional = compraRepositorio.findById(id);

        if (optional.isPresent()) {
            compraRepositorio.delete(optional.get());
        }
    }
    
    public void validar (Integer cantidad, Usuario usuario, List<Producto> listaProductos, Date fechaCompra, Double montoFinal, String direccionEnvio) throws Excepcion{
       
        if (cantidad == null || cantidad.toString().trim().isEmpty()) {
            throw new Excepcion("La cantidad no puede ser nula");
        }
        if (usuario == null || usuario.toString().trim().isEmpty()) {
            throw new Excepcion("El usuario no puede ser nulo");
        }
        if (listaProductos == null || listaProductos.toString().trim().isEmpty()) {
            throw new Excepcion("Este valor no puede ser nulo");
        }
        if (fechaCompra == null || fechaCompra.toString().trim().isEmpty()) {
            throw new Excepcion("Este valor no puede ser nulo");
        }
        if (montoFinal == null || montoFinal.toString().trim().isEmpty()) {
            throw new Excepcion("Este valor no puede ser nulo");
        }
        if (direccionEnvio == null || direccionEnvio.trim().isEmpty()) {
            throw new Excepcion("Este valor no puede ser nulo");
        }
    }

}
