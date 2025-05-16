package com.example.biblioteca;

import com.example.biblioteca.model.Material;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.model.Prestamo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BibliotecaUNT {
    private final List<Material> materiales = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Prestamo> prestamos = new ArrayList<>();

    private String nombre = "Biblioteca Central UNT";
    private String direccion = "Av. Juan Pablo II s/n, Trujillo";

    // Material methods
    public Material registrarMaterial(Material material) {
        // Basic check for duplicates by title and author, could be more sophisticated
        boolean exists = materiales.stream().anyMatch(m -> m.getTitulo().equalsIgnoreCase(material.getTitulo()) && m.getAutor().equalsIgnoreCase(material.getAutor()));
        if (exists) {
            // In a real app, throw a custom exception like MaterialAlreadyExistsException
            // For now, let's allow it or return null/throw IllegalArgumentException
            // throw new IllegalArgumentException("Material with same title and author already exists.");
        }
        materiales.add(material);
        return material;
    }

    public List<Material> getMateriales() {
        return new ArrayList<>(materiales);
    }

    public Optional<Material> getMaterialById(String id) {
        return materiales.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public Material updateMaterial(String id, Material materialDetails) {
        Material material = getMaterialById(id)
                .orElseThrow(() -> new NoSuchElementException("Material no encontrado con ID: " + id));

        material.setTitulo(materialDetails.getTitulo());
        material.setAutor(materialDetails.getAutor());
        material.setAnioPublicacion(materialDetails.getAnioPublicacion());
        material.setIdioma(materialDetails.getIdioma());
        material.setPalabrasClave(materialDetails.getPalabrasClave());
        // Update specific fields for Libro or Tesis if necessary, by casting or specific DTOs
        // This requires materialDetails to be of the correct subtype or handling it carefully.
        return material;
    }
    
    public boolean deleteMaterial(String id) {
        return materiales.removeIf(m -> m.getId().equals(id));
    }

    // Usuario methods
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()))) {
            // throw new IllegalArgumentException("Usuario con el mismo email ya existe.");
        }
        usuarios.add(usuario);
        return usuario;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public Optional<Usuario> getUsuarioById(String id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }
    
    public Usuario updateUsuario(String id, Usuario usuarioDetails) {
        Usuario usuario = getUsuarioById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));
        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setTelefono(usuarioDetails.getTelefono());
        usuario.setDireccion(usuarioDetails.getDireccion());
        usuario.setEstado(usuarioDetails.getEstado());
        // Update specific fields for Alumno, Docente, Externo
        return usuario;
    }

    public boolean deleteUsuario(String id) {
        // Consider implications: what if user has active loans?
        return usuarios.removeIf(u -> u.getId().equals(id));
    }


    // Prestamo methods
    public Prestamo registrarPrestamo(Prestamo prestamo) {
        // Validate material and user exist
        getMaterialById(prestamo.getMaterialId())
                .orElseThrow(() -> new NoSuchElementException("Material no encontrado para préstamo: " + prestamo.getMaterialId()));
        getUsuarioById(prestamo.getUsuarioId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado para préstamo: " + prestamo.getUsuarioId()));
        
        // Check if user has too many active loans, or if material is available (if Libro, check ejemplares)
        // For simplicity, just add.
        prestamos.add(prestamo);
        return prestamo;
    }

    public List<Prestamo> getPrestamos() {
        return new ArrayList<>(prestamos);
    }

    public Optional<Prestamo> getPrestamoById(String id) {
        return prestamos.stream().filter(p -> p.getIdPrestamo().equals(id)).findFirst();
    }

    public Prestamo updatePrestamo(String id, Prestamo prestamoDetails) {
        Prestamo prestamo = getPrestamoById(id)
            .orElseThrow(() -> new NoSuchElementException("Préstamo no encontrado con ID: " + id));
        
        // Validate material and user IDs if they are being changed (usually not for an update)
        if (prestamoDetails.getMaterialId() != null && !prestamoDetails.getMaterialId().equals(prestamo.getMaterialId())) {
             getMaterialById(prestamoDetails.getMaterialId())
                .orElseThrow(() -> new NoSuchElementException("Nuevo Material no encontrado para préstamo: " + prestamoDetails.getMaterialId()));
            prestamo.setMaterialId(prestamoDetails.getMaterialId());
        }
        if (prestamoDetails.getUsuarioId() != null && !prestamoDetails.getUsuarioId().equals(prestamo.getUsuarioId())) {
            getUsuarioById(prestamoDetails.getUsuarioId())
                .orElseThrow(() -> new NoSuchElementException("Nuevo Usuario no encontrado para préstamo: " + prestamoDetails.getUsuarioId()));
            prestamo.setUsuarioId(prestamoDetails.getUsuarioId());
        }

        prestamo.setFechaPrestamo(prestamoDetails.getFechaPrestamo());
        prestamo.setFechaDevolucionEsperada(prestamoDetails.getFechaDevolucionEsperada());
        prestamo.setFechaDevolucionReal(prestamoDetails.getFechaDevolucionReal());
        prestamo.setEstado(prestamoDetails.getEstado());
        return prestamo;
    }
    
    public boolean deletePrestamo(String id) {
        return prestamos.removeIf(p -> p.getIdPrestamo().equals(id));
    }

    // Biblioteca Info
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
