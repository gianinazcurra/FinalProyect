/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vineyarg.demo.entidades;

import com.vineyarg.demo.enumeraciones.EstadoCompra;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author joaqu
 */
@Entity
public class Compra implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

   
    @ElementCollection(targetClass=Integer.class)
    private List<Integer> cantidades;
    @OneToOne
    private Usuario usuario;
    
//    @OneToMany
//    @ElementCollection(targetClass=Producto.class)
//    @OneToMany(mappedBy = "Compra", cascade = CascadeType.ALL, orphanRemoval = true)
//   private Set<Producto> listaProductos;
    
    @ElementCollection(targetClass=ItemCompra.class)
    @OneToMany
    private Set<ItemCompra> itemCompra;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCompra;
    @ElementCollection(targetClass=Double.class)
    private List<Double> subtotales;
    private Double montoFinal;
    private String direccionEnvio;
    private String formaDePago;
    private boolean compraEnviadaParaAceptacion;
    @Enumerated(EnumType.STRING)
    private EstadoCompra estadoCompra;
    private String observacionesCompra;

    public Compra(String id, List<Integer> cantidades, Usuario usuario, Set<ItemCompra> itemCompra, Date fechaCompra, List<Double> subtotales, Double montoFinal, String direccionEnvio, String formaDePago, boolean compraEnviadaParaAceptacion, EstadoCompra estadoCompra, String observacionesCompra) {
        this.id = id;
        this.cantidades = cantidades;
        this.usuario = usuario;
        this.itemCompra = itemCompra;
        this.fechaCompra = fechaCompra;
        this.subtotales = subtotales;
        this.montoFinal = montoFinal;
        this.direccionEnvio = direccionEnvio;
        this.formaDePago = formaDePago;
        this.compraEnviadaParaAceptacion = compraEnviadaParaAceptacion;
        this.estadoCompra = estadoCompra;
        this.observacionesCompra = observacionesCompra;
    }

    
    

    

    

    

   
    

    public Compra() {
    }

    public List<Double> getSubtotales() {
        return subtotales;
    }

    public void setSubtotales(List<Double> subtotales) {
        this.subtotales = subtotales;
    }

    public String getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }

    public EstadoCompra getEstadoCompra() {
        return estadoCompra;
    }

    public void setEstadoCompra(EstadoCompra estadoCompra) {
        this.estadoCompra = estadoCompra;
    }

    

    

    public boolean isCompraEnviadaParaAceptacion() {
        return compraEnviadaParaAceptacion;
    }

    public void setCompraEnviadaParaAceptacion(boolean compraEnviadaParaAceptacion) {
        this.compraEnviadaParaAceptacion = compraEnviadaParaAceptacion;
    }

    public String getObservacionesCompra() {
        return observacionesCompra;
    }

    public void setObservacionesCompra(String observacionesCompra) {
        this.observacionesCompra = observacionesCompra;
    }

    

    

    public List<Integer> getCantidades() {
        return cantidades;
    }

    public void setCantidades(List<Integer> cantidades) {
        this.cantidades = cantidades;
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
        if (!(object instanceof Compra)) {
            return false;
        }
        Compra other = (Compra) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString() {
        return "com.vineyarg.demo.entidades.Compra[ id=" + getId() + " ]";
    }

    /**
     * @return the serialVersionUID
     */
    

    /**
     * @return the cantidad
     */
   
    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Set<ItemCompra> getItemCompra() {
        return itemCompra;
    }

    /**
     * @return the listaProductos
     */
    public void setItemCompra(Set<ItemCompra> itemCompra) {   
        this.itemCompra = itemCompra;
    }

    /**
     * @return the fechaCompra
     */
    public Date getFechaCompra() {
        return fechaCompra;
    }

    /**
     * @param fechaCompra the fechaCompra to set
     */
    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    /**
     * @return the montoFinal
     */
    public Double getMontoFinal() {
        return montoFinal;
    }

    /**
     * @param montoFinal the montoFinal to set
     */
    public void setMontoFinal(Double montoFinal) {
        this.montoFinal = montoFinal;
    }

    /**
     * @return the direccionEnvio
     */
    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    /**
     * @param direccionEnvio the direccionEnvio to set
     */
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }
    
    
}
