package com.example.biblioteca.dto;

public class UsuarioAuthDTO {
    private String id; // Renamed from idUsuario
    private String password;
    private String nombre;
    private String direccion;
    private String email;
    private String telefono;
    private String tipo; // Added field (as String for flexibility in request)

    // Getters and Setters
    public String getId() { // Renamed from getIdUsuario
        return id;
    }

    public void setId(String id) { // Renamed from setIdUsuario
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getTipo() { // Added getter
        return tipo;
    }

    public void setTipo(String tipo) { // Added setter
        this.tipo = tipo;
    }
}