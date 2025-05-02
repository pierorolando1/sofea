package com.example.biblioteca;


public class Libro {
    private String codigo; // Renamed from idLibro
    private String isbn;
    private String titulo;
    private String autor;
    private int anioPublicacion;
    private int ejemplaresDisponibles;

    // Constructor
    public Libro(String codigo, String isbn, String titulo, String autor, int anioPublicacion, int ejemplaresDisponibles) {
        this.codigo = codigo; // Updated parameter name
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    // Getters and Setters
    public String getCodigo() { // Renamed from getIdLibro
        return codigo;
    }
    public void setCodigo(String codigo) { // Renamed from setIdLibro
        this.codigo = codigo;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    // Add a simple display method if needed (optional, used in BibliotecaUNT initialization)
    public void mostrarInformacion() {
        System.out.println("Libro [Codigo=" + codigo + ", ISBN=" + isbn + ", Titulo=" + titulo + ", Autor=" + autor
                + ", Año=" + anioPublicacion + ", Disponibles=" + ejemplaresDisponibles + "]");
    }
}