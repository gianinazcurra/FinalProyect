/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.entidades;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author joaqu
 */
@Entity
public class Productor implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;
    private String razonSocial;
    private String domicilio;
    private String correo;
    private String clave;
    private String descripcion;
    private String region;
//    private Imagenes imagen;//Finalmente le setearemos imagen como Usuario

    private boolean alta;

    public Productor() {
    }

    public Productor(String id, String nombre, String razonSocial, String domicilio,
            String correo, String clave, String descripcion, String region,/*Imagenes imagen,*/ boolean alta) {
        this.id = id;
        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.domicilio = domicilio;
        this.correo = correo;
        this.clave = clave;
        this.descripcion = descripcion;
        this.region = region;
//        this.imagen=imagen;
        this.alta = alta;
    }

 

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productor)) {
            return false;
        }
        Productor other = (Productor) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vineyarg.demo.entidades.Productor[ id=" + getId() + " ]";
    }

    /**
     * @return the serialVersionUID
     */
    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the razonSocial
     */
    public String getRazonSocial() {
        return razonSocial;
    }

    /**
     * @param razonSocial the razonSocial to set
     */
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    /**
     * @return the domicilio
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * @param domicilio the domicilio to set
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * @return the mail
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param mail the mail to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return the password
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param password the password to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the resenia
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param resenia the resenia to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the alta
     */
    public boolean isAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(boolean alta) {
        this.alta = alta;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the imagen
     */
//    public Imagenes getImagen() {
//        return imagen;
//    }
//
//    /**
//     * @param imagen the imagen to set
//     */
//    public void setImagen(Imagenes imagen) {
//        this.imagen = imagen;
//    }

}
