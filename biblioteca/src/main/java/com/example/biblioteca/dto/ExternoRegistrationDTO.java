package com.example.biblioteca.dto;

import com.example.biblioteca.model.Externo;
import com.example.biblioteca.model.Usuario;

import java.time.LocalDate;

public class ExternoRegistrationDTO extends UsuarioRegistrationDTO {
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String dni;
    private String institucionProcedencia;

    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getInstitucionProcedencia() { return institucionProcedencia; }
    public void setInstitucionProcedencia(String institucionProcedencia) { this.institucionProcedencia = institucionProcedencia; }

    @Override
    public Usuario toUsuario() {
        Externo externo = new Externo(nombre, email, telefono, direccion, dni, institucionProcedencia);
        return externo;
    }
}
