package com.example.biblioteca.model;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Libro extends Material {
    private String isbn;
    private int ejemplaresDisponibles;
    private String editorial;
    private int numeroPaginas;
    private String genero;

    // Regex patterns
    // Complex ISBN regex (covers ISBN-10 and ISBN-13)
    private static final Pattern ISBN_PATTERN = 
            Pattern.compile(
            "(?:ISBN: ?)?(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]");
    
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ0-9\\s'\\-,.]{2,100}$");


    public Libro(String titulo, String autor, int anioPublicacion, String idioma, List<String> palabrasClave,
                 String isbn, int ejemplaresDisponibles, String editorial, int numeroPaginas, String genero) {
        super(titulo, autor, anioPublicacion, idioma, palabrasClave);
        this.isbn = isbn;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
        this.editorial = editorial;
        this.numeroPaginas = numeroPaginas;
        this.genero = genero;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }
    public String getEditorial() { return editorial; }
    public int getNumeroPaginas() { return numeroPaginas; }
    public String getGenero() { return genero; }

    // Setters
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setEjemplaresDisponibles(int ejemplaresDisponibles) { this.ejemplaresDisponibles = ejemplaresDisponibles; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }
    public void setGenero(String genero) { this.genero = genero; }

    @Override
    public Map<String, String> validar() {
        Map<String, String> errors = super.validar();
        if (isbn == null || !ISBN_PATTERN.matcher(isbn).matches()) {
            errors.put("isbn", "ISBN inválido.");
        }
        if (ejemplaresDisponibles < 0) {
            errors.put("ejemplaresDisponibles", "Ejemplares disponibles no puede ser negativo.");
        }
        if (editorial == null || !TEXT_PATTERN.matcher(editorial).matches()) {
            errors.put("editorial", "Editorial inválida. Debe tener entre 2 y 100 caracteres.");
        }
        if (numeroPaginas <= 0) {
            errors.put("numeroPaginas", "Número de páginas debe ser positivo.");
        }
        if (genero == null || !TEXT_PATTERN.matcher(genero).matches()) {
            errors.put("genero", "Género inválido. Debe tener entre 2 y 100 caracteres.");
        }
        return errors;
    }
}
