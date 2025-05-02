package com.example.biblioteca;

// Define the enum for user types
enum TipoUsuario {
    EXTERNO, ESTUDIANTE
}

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private TipoUsuario tipo; // Added field

    // Constructor updated
    public Usuario(String id, String nombre, String email, String telefono, String direccion, TipoUsuario tipo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.tipo = tipo; // Added parameter
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public TipoUsuario getTipo() { // Added getter
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) { // Added setter
        this.tipo = tipo;
    }

    // Override toString for better logging (optional)
    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", telefono=" + telefono
                + ", direccion=" + direccion + ", tipo=" + tipo + "]";
    }
}
