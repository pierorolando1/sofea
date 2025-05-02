package com.example.biblioteca;

import java.time.LocalDate;
// Assuming Libro and Usuario classes exist in the same package or are imported
// import com.example.biblioteca.Libro;
// import com.example.biblioteca.Usuario;

public class Prestamo {
    private String idPrestamo;
    private Libro libro; // Changed from String idLibro
    private Usuario usuario; // Changed from String idUsuario
    private LocalDate fechaPrestamo; // Changed from String
    private LocalDate fechaDevolucion; // Renamed from fechaDevolucionPrevista
    private LocalDate fechaRetornoReal; // New field

    // Constructor updated
    public Prestamo(String idPrestamo, LocalDate fechaPrestamo, LocalDate fechaDevolucion, Usuario usuario, Libro libro) {
        this.idPrestamo = idPrestamo;
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion; // Updated parameter name
        this.fechaRetornoReal = null; // Initially null
    }

     // get isbn
    public String getIsbn() {
        return libro.getIsbn(); // Assuming Libro has a method getIsbn()
    }

    // Getters and Setters updated
    public String getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Libro getLibro() { // Changed return type and name
        return libro;
    }

    public void setLibro(Libro libro) { // Changed parameter type and name
        this.libro = libro;
    }

    public Usuario getUsuario() { // Changed return type and name
        return usuario;
    }

    public void setUsuario(Usuario usuario) { // Changed parameter type and name
        this.usuario = usuario;
    }

    public LocalDate getFechaPrestamo() { // Changed return type
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) { // Changed parameter type
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() { // Renamed and changed return type
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) { // Renamed and changed parameter type
        this.fechaDevolucion = fechaDevolucion;
    }

    public LocalDate getFechaRetornoReal() { // New getter
        return fechaRetornoReal;
    }

    public void setFechaRetornoReal(LocalDate fechaRetornoReal) { // New setter
        this.fechaRetornoReal = fechaRetornoReal;
    }
}
