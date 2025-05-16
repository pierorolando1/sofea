package com.example.biblioteca.model;

import java.util.Map;
import java.util.regex.Pattern;
import java.time.Year;

public class Alumno extends Usuario {
    private String codigoMatricula;
    private String escuela;
    private int anioIngreso;
    private int cicloActual;

    // tiene que ser tipo 1022700623, tener esa longitud y empezar con 1, y que los ultimos 2 digitos esten entre el 10 y el 25
    private static final Pattern CODIGO_MATRICULA_PATTERN = Pattern.compile("^1\\d{7}(1[0-9]|2[0-5])$");
    // La escuela puede ser Informatica, Estadistica, Matematicas, Fisica
    private static final Pattern TEXT_PATTERN = Pattern.compile("^(Informatica|Estadistica|Matematicas|Fisica)$");


    public Alumno(String nombre, String email, String telefono, String direccion,
                  String codigoMatricula, String escuela, int anioIngreso, int cicloActual) {
        super(nombre, email, telefono, direccion);
        this.codigoMatricula = codigoMatricula;
        this.escuela = escuela;
        this.anioIngreso = anioIngreso;
        this.cicloActual = cicloActual;
    }

    // Getters
    public String getCodigoMatricula() { return codigoMatricula; }
    public String getEscuela() { return escuela; }
    public int getAnioIngreso() { return anioIngreso; }
    public int getCicloActual() { return cicloActual; }

    // Setters
    public void setCodigoMatricula(String codigoMatricula) { this.codigoMatricula = codigoMatricula; }
    public void setEscuela(String escuela) { this.escuela = escuela; }
    public void setAnioIngreso(int anioIngreso) { this.anioIngreso = anioIngreso; }
    public void setCicloActual(int cicloActual) { this.cicloActual = cicloActual; }

    @Override
    public Map<String, String> validar() {
        Map<String, String> errors = super.validar();
        if (codigoMatricula == null || !CODIGO_MATRICULA_PATTERN.matcher(codigoMatricula).matches()) {
            errors.put("codigoMatricula", "Código de matrícula inválido. Debe ser alfanumérico de 5-20 caracteres.");
        }
        if (escuela == null || !TEXT_PATTERN.matcher(escuela).matches()) {
            errors.put("escuela", "Escuela inválida. Debe ser 'Informatica', 'Estadistica', 'Matematicas' o 'Fisica'.");
        }
        if (anioIngreso < 2005 || anioIngreso > Year.now().getValue()) {
            errors.put("anioIngreso", "Año de ingreso inválido. Debe ser entre 2005 y el año actual.");
        }
        if (cicloActual < 1 || cicloActual > 14) { // Assuming 12-14 cycles max
            errors.put("cicloActual", "Ciclo actual inválido. Debe ser entre 1 y 14.");
        }
        return errors;
    }
}
