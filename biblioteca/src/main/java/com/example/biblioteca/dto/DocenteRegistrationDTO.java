package com.example.biblioteca.dto;

import com.example.biblioteca.model.Docente;
import com.example.biblioteca.model.Usuario;


public class DocenteRegistrationDTO extends UsuarioRegistrationDTO {
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String codigoDocente;
    private String area;
    private String tipoDeContrato;
    private String gradoAcademico;

    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCodigoDocente() { return codigoDocente; }
    public void setCodigoDocente(String codigoDocente) { this.codigoDocente = codigoDocente; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getTipoDeContrato() { return tipoDeContrato; }
    public void setTipoDeContrato(String tipoDeContrato) { this.tipoDeContrato = tipoDeContrato; }
    public String getGradoAcademico() { return gradoAcademico; }
    public void setGradoAcademico(String gradoAcademico) { this.gradoAcademico = gradoAcademico; }


    @Override
    public Usuario toUsuario() {
        Docente docente = new Docente(nombre, email, telefono, direccion, codigoDocente, area, tipoDeContrato, gradoAcademico);
        return docente;
    }
}
