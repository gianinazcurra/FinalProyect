package com.vineyarg.demo.servicios;

import com.vineyarg.demo.entidades.Compra;
import com.vineyarg.demo.entidades.ItemCompra;
import com.vineyarg.demo.entidades.Producto;
import com.vineyarg.demo.entidades.Usuario;
import com.vineyarg.demo.enumeraciones.EstadoCompra;
import static com.vineyarg.demo.enumeraciones.EstadoCompra.NOENVIADA;
import com.vineyarg.demo.errores.Excepcion;
import com.vineyarg.demo.repositorios.CompraRepositorio;
import com.vineyarg.demo.repositorios.ItemCompraRepositorio;
import com.vineyarg.demo.repositorios.ProductoRepositorio;
import com.vineyarg.demo.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    @Autowired
    private ItemCompraServicio itemCompraServicio;
    @Autowired
    private ItemCompraRepositorio itemCompraRepositorio;

    @Transactional
    public void preCompraCarrito(Producto producto, String id, Integer cantidad) {

        Compra compraEnCurso = compraRepositorio.buscarComprasSinEnviarPorUsuario(id);

        if (compraEnCurso != null) {

            Producto producto1 = productoRepositorio.getById(producto.getId());

            ItemCompra itemCompra = new ItemCompra();
            itemCompra.setProducto(producto1);
            itemCompra.setCantidad(cantidad);
            itemCompra.setTotalProducto(producto1.getPrecio() * cantidad);
            itemCompraRepositorio.save(itemCompra);

            Set<ItemCompra> nuevoProductoParaCarrito = compraEnCurso.getItemCompra();

            nuevoProductoParaCarrito.add(itemCompra);

            compraEnCurso.setItemCompra(nuevoProductoParaCarrito);

            producto1.setCantidad(producto.getCantidad() - cantidad);
            productoRepositorio.save(producto1);

            compraRepositorio.save(compraEnCurso);
            System.out.println("asdasdasdas");
        } else if (compraEnCurso == null) {

            Producto producto1 = productoRepositorio.getById(producto.getId());

            ItemCompra itemCompra = new ItemCompra();
            itemCompra.setProducto(producto1);
            itemCompra.setCantidad(cantidad);
            itemCompra.setTotalProducto(producto1.getPrecio() * cantidad);
            itemCompraRepositorio.save(itemCompra);

            Compra compra = new Compra();

            Set<ItemCompra> nuevoProductoParaCarrito = new HashSet();
            nuevoProductoParaCarrito.add(itemCompra);

            compra.setItemCompra(nuevoProductoParaCarrito);

            Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

            if (respuesta.isPresent()) {
                compra.setUsuario(respuesta.get());
            }

            compra.setEstadoCompra(EstadoCompra.NOENVIADA);

            producto1.setCantidad(producto.getCantidad() - cantidad);
            productoRepositorio.save(producto1);

            compraRepositorio.save(compra);

        }

    }

    @Transactional
    public void anularCompra(String idCompra) throws Excepcion {

        Optional<Compra> optional = compraRepositorio.findById(idCompra);

        if (optional.isPresent()) {

            Compra compraAnular = optional.get();

            //ESTO ES PARA DEVOLVER EL STOCK AL PRODUCTO QUE NO SE COMPRÓ
            Set<ItemCompra> nuevoProductoParaCarrito = compraAnular.getItemCompra();

            for (ItemCompra itemCompra : nuevoProductoParaCarrito) {

                Producto productoA = productoRepositorio.getById(itemCompra.getProducto().getId());
                productoA.setCantidad(productoA.getCantidad() + itemCompra.getCantidad());
                productoRepositorio.save(productoA);
            }
            compraRepositorio.delete(compraAnular);
        }
//           
    }

    @Transactional
    public void quitarProducto(String idProductoEliminar, String idCompraEnCurso) throws Excepcion {

        Optional<Compra> respuesta = compraRepositorio.findById(idCompraEnCurso);

        if (respuesta.isPresent()) {

            Compra compraEnCurso = respuesta.get();

            Set<ItemCompra> ItemsCompraEnCurso = compraEnCurso.getItemCompra();

            Optional<ItemCompra> optional = itemCompraRepositorio.findById(idProductoEliminar);

            if (optional.isPresent()) {

                ItemCompra eliminar = optional.get();

                ItemsCompraEnCurso.remove(eliminar);

                compraEnCurso.setItemCompra(ItemsCompraEnCurso);

                compraRepositorio.save(compraEnCurso);
            }


        }
    }

    @Transactional
    public void enviarPedido(String idCompra, String direccionEnvio, String formaDePago, Double totalCompra) throws Excepcion {

        Optional<Compra> optional = compraRepositorio.findById(idCompra);

        if (optional.isPresent()) {

            Compra compraFinal = new Compra();
            compraFinal = optional.get();

            compraFinal.setDireccionEnvio(direccionEnvio);
            compraFinal.setFechaCompra(new Date());
            compraFinal.setEstadoCompra(EstadoCompra.PENDIENTE);

            compraFinal.setMontoFinal(Math.round(totalCompra * 100.0) / 100.0);

            compraFinal.setFormaDePago(formaDePago);

            compraRepositorio.save(compraFinal);
        }
    }

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
