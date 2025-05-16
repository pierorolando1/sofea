package com.example.biblioteca.model;

import java.util.Map;
import java.util.regex.Pattern;

public class Externo extends Usuario {
    private String dni;
    private String institucionProcedencia;

    // Example DNI for Peru (8 digits)
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}$");
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ0-9\\s'\\-,.]{2,100}$");


    public Externo(String nombre, String email, String telefono, String direccion,
                   String dni, String institucionProcedencia) {
        super(nombre, email, telefono, direccion);
        this.dni = dni;
        this.institucionProcedencia = institucionProcedencia;
    }

    // Getters
    public String getDni() { return dni; }
    public String getInstitucionProcedencia() { return institucionProcedencia; }

    // Setters
    public void setDni(String dni) { this.dni = dni; }
    public void setInstitucionProcedencia(String institucionProcedencia) { this.institucionProcedencia = institucionProcedencia; }

    @Override
    public Map<String, String> validar() {
        Map<String, String> errors = super.validar();
        if (dni == null || !DNI_PATTERN.matcher(dni).matches()) {
            errors.put("dni", "DNI inválido. Debe tener 8 dígitos.");
        }
        if (institucionProcedencia == null || !TEXT_PATTERN.matcher(institucionProcedencia).matches()) {
            errors.put("institucionProcedencia", "Institución de procedencia inválida. Debe tener entre 2 y 100 caracteres.");
        }
        return errors;
    }
}
