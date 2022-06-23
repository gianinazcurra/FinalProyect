
package com.vineyarg.demo.repositorios;

import com.vineyarg.demo.entidades.Compra;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CompraRepositorio extends JpaRepository<Compra, String> {
    @Query("SELECT c FROM Compra c WHERE c.id = :id")
    public Compra buscarPorId(@Param("id") String id);
    
//    @Query("SELECT c FROM Compra c WHERE c.producto = :producto")
//    public List<Compra> buscarPorProducto(@Param("producto")String producto);
    
    @Query("SELECT c FROM Compra c WHERE c.fechaCompra = :fechaCompra")
    public Compra buscarPorFechaCompra(@Param("fechaCompra") String fechaCompra);
    
    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :idUsuario and c.fechaCompra is null")
    public Compra buscarComprasSinEnviarPorUsuario(@Param("idUsuario") String idUsuario);
    
    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :idUsuario")
    public List<Compra> buscarComprasTotalesPorUsuario(@Param("idUsuario") String idUsuario);
    
    @Query("SELECT c FROM Compra c WHERE c.estadoCompra != ACEPTADA and c.estadoCompra != RECHAZADA")
    public List<Compra> buscarComprasNuevas();
    
    @Query("SELECT c FROM Compra c WHERE c.estadoCompra = ACEPTADA or c.estadoCompra = RECHAZADA")
    public List<Compra> buscarComprasHistoricas();
    
    

}
