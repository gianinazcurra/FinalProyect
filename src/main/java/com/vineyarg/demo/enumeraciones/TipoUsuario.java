/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.enumeraciones;

/**
 *
 * @author joaqu
 */
public enum TipoUsuario {
    USUARIOCOMUN, ADMINISTRADOR, PRODUCTOR;

    public static TipoUsuario getUSUARIOCOMUN() {
        return USUARIOCOMUN;
    }

    public static TipoUsuario getADMINISTRADOR() {
        return ADMINISTRADOR;
    }

    public static TipoUsuario getPRODUCTOR() {
        return PRODUCTOR;
    }
    
    
}
