package com.example.biblioteca.dto;

import com.example.biblioteca.model.Usuario;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Using actual model classes for user data part, and adding password
// The 'type' property will distinguish between Alumno, Docente, Externo during deserialization
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AlumnoRegistrationDTO.class, name = "alumno"),
    @JsonSubTypes.Type(value = DocenteRegistrationDTO.class, name = "docente"),
    @JsonSubTypes.Type(value = ExternoRegistrationDTO.class, name = "externo")
})
public abstract class UsuarioRegistrationDTO {
    private String password;
    // Common user fields are in subclasses that extend this or directly in specific DTOs

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract Usuario toUsuario();
}
