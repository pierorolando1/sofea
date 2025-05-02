package com.example.biblioteca.dto;

public class LoginRequestDTO {
    private String idUsuario;
    private String password;

    // Getters and Setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
