
package com.vineyarg.demo.repositorios;


    
import org.springframework.data.jpa.repository.JpaRepository;
import com.vineyarg.demo.entidades.Productor;
import org.springframework.stereotype.Repository;
import com.vineyarg.demo.entidades.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, String> {
    @Query ("SELECT p FROM Producto p WHERE p.nombre= :nombre")
      public Producto buscarPorNombre (@Param ("nombre") String nombre);

      @Query("SELECT p from Producto p WHERE p.productor.id= :idProductor")
      public Producto buscarPorProductor(@Param("idProductor") String idProductor);
      
      @Query("SELECT p from Producto p WHERE p.productor.correo= :correo")
      public List<Producto> buscarTodosPorCorreoProductor(@Param("correo") String correo);

      @Query ("SELECT * FROM Producto p WHERE p.precio ORDER BY p.precio ASC= :precio")
      public Producto buscarPorPrecio (@Param ("precio") Double precio);
      
      @Query("SELECT p from Producto p WHERE p.varietal = :varietal")
      public Producto buscarPorVarietal(@Param("varietal") String varietal);
      
       @Query("SELECT p from Producto p WHERE p.varietal = :varietal")
      public List <Producto> buscarTodosPorVarietal(@Param("varietal") String varietal);
      
      @Query ("SELECT p FROM Producto p WHERE p.id= :id")
      public Producto buscarPorId (@Param ("id") String id);
    
}
