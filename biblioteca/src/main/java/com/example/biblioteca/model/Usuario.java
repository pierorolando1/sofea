package com.example.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;


public abstract class Usuario {
    protected String id;
    protected String nombre;
    protected String email;
    protected String telefono;
    protected String direccion;
    @JsonFormat(pattern="yyyy-MM-dd")
    protected LocalDate fechaRegistro;
    protected String estado; // e.g., "Activo", "Inactivo"

    // Regex patterns
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'\\-]{2,100}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^9\\d{8}$"); // Permite números locales e internacionales
    private static final Pattern DIRECCION_PATTERN = Pattern.compile("^[a-zA-Z0-9À-ÿ\\s'\\-,.#]{5,150}$");
    private static final Pattern ESTADO_PATTERN = Pattern.compile("^(Activo|Inactivo)$");


    public Usuario(String nombre, String email, String telefono, String direccion) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = LocalDate.now();
        this.estado = "Activo"; // Default state
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public String getEstado() { return estado; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setEstado(String estado) { this.estado = estado; }


    public Map<String, String> validar() {
        Map<String, String> errors = new HashMap<>();
        if (nombre == null || !NOMBRE_PATTERN.matcher(nombre).matches()) {
            errors.put("nombre", "Nombre inválido. Debe tener entre 2 y 100 caracteres alfabéticos, espacios, apóstrofes o guiones.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.put("email", "Email inválido.");
        }
        if (telefono == null || !TELEFONO_PATTERN.matcher(telefono).matches()) {
            errors.put("telefono", "Teléfono inválido. Formato peruano solo (ej: 91234578).");
        }
        if (direccion == null || !DIRECCION_PATTERN.matcher(direccion).matches()) {
            errors.put("direccion", "Dirección inválida. Debe tener entre 5 y 150 caracteres.");
        }
        if (estado == null || !ESTADO_PATTERN.matcher(estado).matches()) {
            errors.put("estado", "Estado inválido. Debe ser 'Activo' o 'Inactivo'.");
        }
        return errors;
    }
}
