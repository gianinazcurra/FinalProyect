/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.errores;

/**
 *
 * @author joaqu
 */
public class Excepcion extends Exception {

    /**
     * Creates a new instance of <code>Excepcion</code> without detail message.
     */
    public Excepcion() {
    }

    /**
     * Constructs an instance of <code>Excepcion</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public Excepcion(String msg) {
        super(msg);
    }
}
