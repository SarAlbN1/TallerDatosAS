package cliente.soap.users;

import jakarta.xml.bind.annotation.*;

/**
 * Clase generada para el tipo User del esquema XSD
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User", propOrder = {
    "id",
    "nombre",
    "email",
    "telefono",
    "direccion",
    "ciudad",
    "pais"
})
public class User {

    @XmlElement(required = true)
    protected Long id;
    
    @XmlElement(required = true)
    protected String nombre;
    
    @XmlElement(required = true)
    protected String email;
    
    @XmlElement(required = true)
    protected String telefono;
    
    @XmlElement(required = true)
    protected String direccion;
    
    @XmlElement(required = true)
    protected String ciudad;
    
    @XmlElement(required = true)
    protected String pais;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String value) {
        this.telefono = value;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String value) {
        this.direccion = value;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String value) {
        this.ciudad = value;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String value) {
        this.pais = value;
    }
}
