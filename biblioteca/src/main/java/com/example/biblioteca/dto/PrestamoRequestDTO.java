package com.example.biblioteca.dto;

public class PrestamoRequestDTO {

    private String idUsuario;
    private String isbn; // Renamed from idLibro

    // Getters and Setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIsbn() { // Renamed from getIdLibro
        return isbn;
    }

    public void setIsbn(String isbn) { // Renamed from setIdLibro
        this.isbn = isbn;
    }
}