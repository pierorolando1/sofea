package com.example.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class Prestamo {
    private String idPrestamo;
    private String materialId; // Store ID instead of full object to avoid circular dependencies in simple model
    private String usuarioId;  // Store ID
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate fechaPrestamo;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate fechaDevolucionEsperada;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate fechaDevolucionReal;
    private String estado; // e.g., "Activo", "Devuelto", "Vencido"

    private static final Pattern ESTADO_PRESTAMO_PATTERN = Pattern.compile("^(Activo|Devuelto|Vencido)$");

    // Constructor for creating a new loan
    public Prestamo(String materialId, String usuarioId, LocalDate fechaPrestamo, LocalDate fechaDevolucionEsperada) {
        this.idPrestamo = UUID.randomUUID().toString().substring(0, 8);
        this.materialId = materialId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.estado = "Activo"; // Default state for a new loan
    }

    // Default constructor for JSON deserialization if needed, or use specific DTOs
    public Prestamo() {
         this.idPrestamo = UUID.randomUUID().toString().substring(0, 8);
    }


    // Getters
    public String getIdPrestamo() { return idPrestamo; }
    public String getMaterialId() { return materialId; }
    public String getUsuarioId() { return usuarioId; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public String getEstado() { return estado; }

    // Setters
    public void setMaterialId(String materialId) { this.materialId = materialId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }
    public void setEstado(String estado) { this.estado = estado; }

    public Map<String, String> validar(boolean isNew) {
        Map<String, String> errors = new HashMap<>();
        if (materialId == null || materialId.trim().isEmpty()) {
            errors.put("materialId", "ID de material es requerido.");
        }
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            errors.put("usuarioId", "ID de usuario es requerido.");
        }
        if (fechaPrestamo == null) {
            errors.put("fechaPrestamo", "Fecha de préstamo es requerida.");
        } else if (isNew && fechaPrestamo.isAfter(LocalDate.now())) {
            errors.put("fechaPrestamo", "Fecha de préstamo no puede ser en el futuro.");
        }
        if (fechaDevolucionEsperada == null) {
            errors.put("fechaDevolucionEsperada", "Fecha de devolución esperada es requerida.");
        } else if (fechaPrestamo != null && fechaDevolucionEsperada.isBefore(fechaPrestamo)) {
            errors.put("fechaDevolucionEsperada", "Fecha de devolución esperada debe ser posterior a la fecha de préstamo.");
        }
        if (estado == null || !ESTADO_PRESTAMO_PATTERN.matcher(estado).matches()) {
            errors.put("estado", "Estado de préstamo inválido. Debe ser 'Activo', 'Devuelto' o 'Vencido'.");
        }
        if (fechaDevolucionReal != null && fechaPrestamo != null && fechaDevolucionReal.isBefore(fechaPrestamo)) {
             errors.put("fechaDevolucionReal", "Fecha de devolución real no puede ser anterior a la fecha de préstamo.");
        }
        return errors;
    }
}
