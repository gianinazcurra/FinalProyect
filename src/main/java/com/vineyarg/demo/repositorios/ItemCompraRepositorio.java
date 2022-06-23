/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.repositorios;

import com.vineyarg.demo.entidades.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author joaqu
 */
public interface ItemCompraRepositorio extends JpaRepository<ItemCompra, String> {
    
}
