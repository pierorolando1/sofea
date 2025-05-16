package com.example.biblioteca.model;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.time.Year;

public abstract class Material {
    protected String id;
    protected String titulo;
    protected String autor;
    protected int anioPublicacion;
    protected String idioma;
    protected List<String> palabrasClave;

    // Regex patterns
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ0-9\\s'\\-,.]{2,100}$");

    // El autor debe ser "Apellido, Nombre"
    private static final Pattern AUTOR_PATTERN = Pattern.compile("^[A-Z][a-z]+, [A-Z][a-z]+( [A-Z][a-z]+)*$");

    // ingles, español, francés, alemán, italiano, portugués, chino, japonés, árabe solo uno de esos
    private static final Pattern IDIOMA_PATTERN = Pattern.compile("^(Inglés|Español|Francés|Alemán|Italiano|Portugués|Chino|Japonés|Árabe)$");


    public Material(String titulo, String autor, int anioPublicacion, String idioma, List<String> palabrasClave) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.idioma = idioma;
        this.palabrasClave = palabrasClave;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnioPublicacion() { return anioPublicacion; }
    public String getIdioma() { return idioma; }
    public List<String> getPalabrasClave() { return palabrasClave; }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public void setPalabrasClave(List<String> palabrasClave) { this.palabrasClave = palabrasClave; }

    public Map<String, String> validar() {
        Map<String, String> errors = new HashMap<>();
        if (titulo == null || !TEXT_PATTERN.matcher(titulo).matches()) {
            errors.put("titulo", "Título inválido. Debe tener entre 2 y 100 caracteres alfanuméricos, espacios, apóstrofes, guiones, comas o puntos.");
        }
        if (autor == null || !AUTOR_PATTERN.matcher(autor).matches()) {
            errors.put("autor", "Autor inválido. Debe ser en formato 'Apellido, Nombre'.");
        }
        if (anioPublicacion < 1000 || anioPublicacion > Year.now().getValue()) {
            errors.put("anioPublicacion", "Año de publicación inválido. Debe ser entre 1000 y el año actual.");
        }
        if (idioma == null || !IDIOMA_PATTERN.matcher(idioma).matches()) {
            errors.put("idioma", "Idioma inválido. Debe ser uno de los siguientes: Inglés, Español, Francés, Alemán, Italiano, Portugués, Chino, Japonés o Árabe.");
        }
        if (palabrasClave == null || palabrasClave.isEmpty()) {
            errors.put("palabrasClave", "Debe proporcionar al menos una palabra clave.");
        } else {
            for (String palabra : palabrasClave) {
                if (palabra == null || !TEXT_PATTERN.matcher(palabra).matches()) {
                    errors.put("palabrasClave", "Una o más palabras clave son inválidas. Deben tener entre 2 y 100 caracteres.");
                    break;
                }
            }
        }
        return errors;
    }
}
