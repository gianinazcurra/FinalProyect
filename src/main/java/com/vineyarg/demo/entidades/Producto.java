
package com.vineyarg.demo.entidades;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    private Double valoraciones;

    public Producto() {
    }

    public Producto(String id, String nombre, Integer cantidad, Double precio, String descripcion, String varietal, Productor productor, String sku, Double valoraciones) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.varietal = varietal;
        this.productor = productor;
        this.sku = sku;
        this.valoraciones = valoraciones;
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
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vineyarg.demo.entidades.Producto[ id=" + getId() + " ]";
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
     * @return the cantidad
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the precio
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the varietal
     */
    public String getVarietal() {
        return varietal;
    }

    /**
     * @param varietal the varietal to set
     */
    public void setVarietal(String varietal) {
        this.varietal = varietal;
    }

    /**
     * @return the productor
     */
    public Productor getProductor() {
        return productor;
    }

    /**
     * @param productor the productor to set
     */
    public void setProductor(Productor productor) {
        this.productor = productor;
    }

    /**
     * @return the sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * @param sku the sku to set
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * @return the valoraciones
     */
    public Double getValoraciones() {
        return valoraciones;
    }

    /**
     * @param valoraciones the valoraciones to set
     */
    public void setValoraciones(Double valoraciones) {
        this.valoraciones = valoraciones;
    }
    
}
