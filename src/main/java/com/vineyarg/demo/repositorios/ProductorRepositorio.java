
package com.vineyarg.demo.repositorios;


import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductorRepositorio extends JpaRepository<Productor, String>{
    
     @Query("SELECT b FROM Productor b WHERE b.id=:id" )
    public Productor buscarPorId(@Param("id")String id);  
    
    @Query("SELECT a FROM Productor a WHERE a.nombre=:nombre" )
    public Productor buscarPorNombre(@Param("nombre")String nombre);
    
     @Query("SELECT p from Productor p WHERE p.region Like % :region%")
      public Productor buscarPorRegion(@Param("varietal") String region);
      
      @Query("SELECT p from Productor p where p.correo = :correo")
    public Productor BuscarProductorPorCorreo(@Param("correo") String correo);
    
}

