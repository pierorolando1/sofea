package com.example.biblioteca.dto;

import com.example.biblioteca.model.Alumno;
import com.example.biblioteca.model.Usuario;

import java.time.LocalDate;

public class AlumnoRegistrationDTO extends UsuarioRegistrationDTO {
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String codigoMatricula;
    private String escuela;
    private int anioIngreso;
    private int cicloActual;

    // Getters and Setters

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCodigoMatricula() { return codigoMatricula; }
    public void setCodigoMatricula(String codigoMatricula) { this.codigoMatricula = codigoMatricula; }
    public String getEscuela() { return escuela; }
    public void setEscuela(String escuela) { this.escuela = escuela; }
    public int getAnioIngreso() { return anioIngreso; }
    public void setAnioIngreso(int anioIngreso) { this.anioIngreso = anioIngreso; }
    public int getCicloActual() { return cicloActual; }
    public void setCicloActual(int cicloActual) { this.cicloActual = cicloActual; }

    @Override
    public Usuario toUsuario() {
        Alumno alumno = new Alumno(nombre, email, telefono, direccion, codigoMatricula, escuela, anioIngreso, cicloActual);
        // ID is generated in Alumno constructor (via Usuario constructor)
        // FechaRegistro and Estado are set in Usuario constructor
        return alumno;
    }
}
