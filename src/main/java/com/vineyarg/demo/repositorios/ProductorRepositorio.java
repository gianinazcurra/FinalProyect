
package com.vineyarg.demo.repositorios;


import com.vineyarg.demo.entidades.Productor;
import com.vineyarg.demo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductorRepositorio extends JpaRepository<Productor, String>{
    
     @Query("SELECT p FROM Productor p WHERE p.id=:id" )
    public Productor buscarPorId(@Param("id")String id);  
    
    @Query("SELECT p FROM Productor p WHERE p.nombre=:nombre" )
    public Productor buscarPorNombre(@Param("nombre")String nombre);
    
     @Query("SELECT p from Productor p WHERE p.region = :region")
      public Productor buscarPorRegion(@Param("region") String region);
      
      @Query("SELECT p from Productor p where p.correo = :correo")
    public Productor BuscarProductorPorCorreo(@Param("correo") String correo);
    
    @Query("SELECT p from Productor p where p.correo = :correo AND p.clave = :clave")
    public Productor BuscarProductorPorCorreoYClave(@Param("correo") String correo, @Param("clave") String clave);
    
}

