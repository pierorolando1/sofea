package com.example.biblioteca.model;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Tesis extends Material {
    private String grado;
    private String areaInvestigacion;
    private String universidad;
    private String asesor;

    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'\\-,.]{2,100}$");
    private static final Pattern GRADO_PATTERN = Pattern.compile("^(Licenciatura|Maestría|Doctorado)$");

    public Tesis(String titulo, String autor, int anioPublicacion, String idioma, List<String> palabrasClave,
                 String grado, String areaInvestigacion, String universidad, String asesor) {
        super(titulo, autor, anioPublicacion, idioma, palabrasClave);
        this.grado = grado;
        this.areaInvestigacion = areaInvestigacion;
        this.universidad = universidad;
        this.asesor = asesor;
    }

    // Getters
    public String getGrado() { return grado; }
    public String getAreaInvestigacion() { return areaInvestigacion; }
    public String getUniversidad() { return universidad; }
    public String getAsesor() { return asesor; }

    // Setters
    public void setGrado(String grado) { this.grado = grado; }
    public void setAreaInvestigacion(String areaInvestigacion) { this.areaInvestigacion = areaInvestigacion; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }
    public void setAsesor(String asesor) { this.asesor = asesor; }

    @Override
    public Map<String, String> validar() {
        Map<String, String> errors = super.validar();
        if (grado == null || !GRADO_PATTERN.matcher(grado).matches()) {
            errors.put("grado", "Grado inválido. Debe ser 'Licenciatura', 'Maestría' o 'Doctorado'.");
        }
        if (areaInvestigacion == null || !TEXT_PATTERN.matcher(areaInvestigacion).matches()) {
            errors.put("areaInvestigacion", "Área de investigación inválida. Debe tener entre 2 y 100 caracteres.");
        }
        if (universidad == null || !TEXT_PATTERN.matcher(universidad).matches()) {
            errors.put("universidad", "Universidad inválida. Debe tener entre 2 y 100 caracteres.");
        }
        if (asesor == null || !TEXT_PATTERN.matcher(asesor).matches()) {
            errors.put("asesor", "Asesor inválido. Debe tener entre 2 y 100 caracteres.");
        }
        return errors;
    }
}
