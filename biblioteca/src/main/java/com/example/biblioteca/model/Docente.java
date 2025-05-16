package com.example.biblioteca.model;

import java.util.Map;
import java.util.regex.Pattern;

public class Docente extends Usuario {
    private String codigoDocente;
    private String area;
    private String tipoDeContrato;
    private String gradoAcademico;

    // Codigo de longitud 4 caracteres numericos, solo numeros, y que empiece con 1
    private static final Pattern CODIGO_DOCENTE_PATTERN = Pattern.compile("^1\\d{3}$");
    private static final Pattern GRADO_ACADEMICO_PATTERN = Pattern.compile("^(Bachiller|Licenciado|Magister|Doctor)$");
    private static final Pattern TIPO_CONTRATO_PATTERN = Pattern.compile("^(Tiempo Completo|Medio Tiempo|Por Horas)$");
    private static final Pattern AREA_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'\\-,.]{2,100}$");

    public Docente(String nombre, String email, String telefono, String direccion,
                   String codigoDocente, String area, String tipoDeContrato, String gradoAcademico) {
        super(nombre, email, telefono, direccion);
        this.codigoDocente = codigoDocente;
        this.area = area;
        this.tipoDeContrato = tipoDeContrato;
        this.gradoAcademico = gradoAcademico;
    }

    // Getters
    public String getCodigoDocente() { return codigoDocente; }
    public String getArea() { return area; }
    public String getTipoDeContrato() { return tipoDeContrato; }
    public String getGradoAcademico() { return gradoAcademico; }

    // Setters
    public void setCodigoDocente(String codigoDocente) { this.codigoDocente = codigoDocente; }
    public void setArea(String area) { this.area = area; }
    public void setTipoDeContrato(String tipoDeContrato) { this.tipoDeContrato = tipoDeContrato; }
    public void setGradoAcademico(String gradoAcademico) { this.gradoAcademico = gradoAcademico; }

    @Override
    public Map<String, String> validar() {
        Map<String, String> errors = super.validar();
        if (codigoDocente == null || !CODIGO_DOCENTE_PATTERN.matcher(codigoDocente).matches()) {
            errors.put("codigoDocente", "Código de docente inválido. Debe ser numérico de 4 caracteres.");
        }
        if (area == null || !AREA_PATTERN.matcher(area).matches()) {
            errors.put("area", "Área inválida. Debe tener entre 2 y 100 caracteres.");
        }
        if (tipoDeContrato == null || !TIPO_CONTRATO_PATTERN.matcher(tipoDeContrato).matches()) {
            errors.put("tipoDeContrato", "Tipo de contrato inválido. Debe ser 'Tiempo Completo', 'Medio Tiempo' o 'Por Horas'.");
        }
        if (gradoAcademico == null || !GRADO_ACADEMICO_PATTERN.matcher(gradoAcademico).matches()) {
            errors.put("gradoAcademico", "Grado académico inválido. Debe ser 'Bachiller', 'Licenciado', 'Magister' o 'Doctor'."); 
        }
        return errors;
    }
}
