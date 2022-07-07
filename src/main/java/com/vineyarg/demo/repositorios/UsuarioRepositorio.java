/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.repositorios;

import com.vineyarg.demo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author joaqu
 */
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT a from Usuario a where a.correo = :correo AND a.clave = :clave")
    public Usuario BuscarUsuarioPorCorreoYClave(@Param("correo") String correo, @Param("clave") String clave);

    @Query("SELECT a from Usuario a where a.correo = :correo")
    public Usuario BuscarUsuarioPorCorreo(@Param("correo") String correo);

}
