
package com.vineyarg.demo.entidades;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;


@Entity
public class Producto implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;
    private Integer cantidad;
    private Double precio;
    private String descripcion;
    private String varietal;
    @ManyToOne
    private Productor productor;
    private String sku;
    private int cantidadVecesValorado;
    private int cantidadValoraciones;
    private Double promedioValoraciones;
    @ManyToOne
    private Compra compra;
    
    private boolean alta;
    
    @ElementCollection(targetClass=Imagenes.class)
    @OneToMany
    private Set<Imagenes> imagenes;
    
    public Producto() {
    }

    public Producto(String id, String nombre, Integer cantidad, Double precio, String descripcion, String varietal, Productor productor, String sku, int cantidadVecesValorado, int cantidadValoraciones, Double promedioValoraciones, Compra compra, boolean alta, Set<Imagenes> imagenes) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.varietal = varietal;
        this.productor = productor;
        this.sku = sku;
        this.cantidadVecesValorado = cantidadVecesValorado;
        this.cantidadValoraciones = cantidadValoraciones;
        this.promedioValoraciones = promedioValoraciones;
        this.compra = compra;
        this.alta = alta;
        this.imagenes = imagenes;
    }



  

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

   
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVarietal() {
        return varietal;
    }

    public void setVarietal(String varietal) {
        this.varietal = varietal;
    }

    public Productor getProductor() {
        return productor;
    }

    public void setProductor(Productor productor) {
        this.productor = productor;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getCantidadVecesValorado() {
        return cantidadVecesValorado;
    }

    public void setCantidadVecesValorado(int cantidadVecesValorado) {
        this.cantidadVecesValorado = cantidadVecesValorado;
    }

    public int getCantidadValoraciones() {
        return cantidadValoraciones;
    }

    public void setCantidadValoraciones(int cantidadValoraciones) {
        this.cantidadValoraciones = cantidadValoraciones;
    }

    public Double getPromedioValoraciones() {
        return promedioValoraciones;
    }

    public void setPromedioValoraciones(Double promedioValoraciones) {
        this.promedioValoraciones = promedioValoraciones;
    }

 

    public boolean isAlta() {
        return alta;
    }

    public void setAlta(boolean alta) {
        this.alta = alta;
    }

    public Set<Imagenes> getImagenes() {
        return imagenes;
    }

    public void setImagenes(Set<Imagenes> imagenes) {
        this.imagenes = imagenes;
    }


    
    
    
}
