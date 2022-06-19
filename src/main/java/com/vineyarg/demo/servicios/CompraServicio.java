package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.EstadoCompra;
import static com.vineyarg.demo.enumeraciones.EstadoCompra.NOENVIADA;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
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
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void preCompraCarrito(Producto producto, String id, Integer cantidad) {

        Compra compraEnCurso = compraRepositorio.buscarComprasSinEnviarPorUsuario(id);

        if (compraEnCurso != null) {

            List<Producto> listaProductos = compraEnCurso.getListaProductos();
            List<Integer> listaCantidades = compraEnCurso.getCantidades();
            List<Double> listaSubtotales = compraEnCurso.getSubtotales();

            listaProductos.add(producto);
            listaCantidades.add(cantidad);
            listaSubtotales.add((producto.getPrecio() * cantidad.doubleValue()));

            compraEnCurso.setListaProductos(listaProductos);
            compraEnCurso.setCantidades(listaCantidades);
            compraEnCurso.setSubtotales(listaSubtotales);
            
            producto.setCantidad(producto.getCantidad() - cantidad);
            productoRepositorio.save(producto);

            compraRepositorio.save(compraEnCurso);

        } else {
            List<Producto> listaProductos = new ArrayList();
            List<Integer> listaCantidades = new ArrayList();
            List<Double> listaSubtotales = new ArrayList();

            listaProductos.add(producto);
            listaCantidades.add(cantidad);
            listaSubtotales.add((producto.getPrecio() * cantidad.doubleValue()));

            Compra compra = new Compra();

            compra.setListaProductos(listaProductos);
            compra.setCantidades(listaCantidades);
            compra.setSubtotales(listaSubtotales);

            Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

            if (respuesta.isPresent()) {
                compra.setUsuario(respuesta.get());
            }

            compraEnCurso.setEstadoCompra(EstadoCompra.NOENVIADA);
            
            producto.setCantidad(producto.getCantidad() - cantidad);
            productoRepositorio.save(producto);
            compraRepositorio.save(compra);

        }

    }

    @Transactional
    public void anularCompra(String idCompra) throws Excepcion {

        Optional<Compra> optional = compraRepositorio.findById(idCompra);

        if (optional.isPresent()) {

            Compra compraAnular = optional.get();

            //ESTO ES PARA DEVOLVER EL STOCK AL PRODUCTO QUE NO SE COMPRÓ
            List<Producto> listaProductos = compraAnular.getListaProductos();
            List<Integer> listaCantidades = compraAnular.getCantidades();

            for (int i = 0; i < listaProductos.size(); i++) {
                for (int j = 0; j < listaCantidades.size(); j++) {
                    
                    if(i == j) {
                        
                        Producto producto = productoRepositorio.buscarPorId(listaProductos.get(i).getId());
                        
                        producto.setCantidad(producto.getCantidad()+listaCantidades.get(j));
                        
                        productoRepositorio.save(producto);
                              
                        
                        
                }
                }
            }
            compraRepositorio.delete(compraAnular);
        }
    }
    
    

    @Transactional
    public void quitarProducto(String idProductoEliminar, Integer cantidades, String idCompraEnCurso) throws Excepcion {

        Optional<Compra> optional = compraRepositorio.findById(idCompraEnCurso);

        if (optional.isPresent()) {

            Compra compraEnCurso = new Compra();
            compraEnCurso = optional.get();

            Optional<Producto> respuesta = productoRepositorio.findById(idProductoEliminar);

            if (respuesta.isPresent()) {

                Producto productoEliminar = new Producto();
                productoEliminar = respuesta.get();

                List<Producto> listaProductos = compraEnCurso.getListaProductos();

                int posicionProducto = listaProductos.indexOf(productoEliminar);
                listaProductos.remove(productoEliminar);

                List<Integer> listaCantidades = compraEnCurso.getCantidades();

                listaCantidades.remove(cantidades);

                List<Double> listaSubtotales = compraEnCurso.getSubtotales();

                listaSubtotales.remove(posicionProducto);

                compraEnCurso.setListaProductos(listaProductos);
                compraEnCurso.setCantidades(listaCantidades);
                compraEnCurso.setSubtotales(listaSubtotales);

                productoEliminar.setCantidad(productoEliminar.getCantidad() + cantidades);

                productoRepositorio.save(productoEliminar);

                compraRepositorio.save(compraEnCurso);

            }
        }
    }

    @Transactional
    public void enviarPedido(String idCompra, String direccionEnvio, String formaDePago) throws Excepcion {

        Optional<Compra> optional = compraRepositorio.findById(idCompra);

        if (optional.isPresent()) {

            Compra compraFinal = new Compra();
            compraFinal = optional.get();

            compraFinal.setDireccionEnvio(direccionEnvio);
            compraFinal.setFechaCompra(new Date());
            compraFinal.setEstadoCompra(EstadoCompra.PENDIENTE);
            
            List<Double> subtotales = compraFinal.getSubtotales();

            Double sbt = 0.00;
            Double total = 0.00;

            for (Double subtotale : subtotales) {

                total = subtotale + sbt;
                sbt = subtotale;

            }

            compraFinal.setMontoFinal(total);

            compraFinal.setFormaDePago(formaDePago);

            compraRepositorio.save(compraFinal);
        }
    }

//   @Transactional
//    public void agregarProducto(List<Producto> listaProductos, String id) throws Excepcion{
//        
//        Optional<Producto> optional = productoRepositorio.findById(id);
//        if (optional.isPresent()) {
//
//            listaProductos.add(optional.get());
//        }
//    }
    public void validar(Integer cantidad, Usuario usuario, List<Producto> listaProductos, Date fechaCompra, Double montoFinal, String direccionEnvio) throws Excepcion {

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
