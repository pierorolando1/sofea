package com.example.biblioteca;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/* ===========================================================
 *  Modelo de dominio y validaciones por Expresiones Regulares
 * =========================================================== */

@Service // Make it a Spring service (singleton bean)
public class BibliotecaUNT {

    /* ==== Patrones REGEX (pre-compilados) - Kept patterns used ONLY by BibliotecaUNT ==== */
    // Libro validation patterns (used before creating Libro object)
    private static final Pattern CODIGO_LIBRO_RX = Pattern // UUID - Used for validation if needed here, though Libro constructor takes it
            .compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern ISBN_RX = Pattern.compile(
            "^(?:ISBN(?:-1[03])?:?\\s*)?(?=[0-9X\\-\\s]{10,17}$)(?:97[89][\\-\\s]?)?(?:\\d[\\-\\s]?){9}[\\dX]$");
    private static final Pattern TITULO_RX = Pattern.compile("^[\\p{L}\\p{N}\\s\\p{P}]{1,150}$");
    private static final Pattern AUTOR_RX = Pattern.compile("^[\\p{L}][\\p{L}\\s\\.'’-]{1,99}$");
    // Usuario validation patterns (used before creating Usuario object)
    private static final Pattern DNI_RX = Pattern.compile("^\\d{8}$"); // Kept for initial ID check
    private static final Pattern UNT_ID_RX = Pattern.compile("^\\d{10}$"); // Kept for initial ID check
    private static final Pattern NOMBRE_RX = Pattern.compile("^[\\p{L}][\\p{L}\\s\\.'’-]{1,79}$");
    private static final Pattern DIRECCION_RX = Pattern.compile("^[\\p{L}\\p{N}\\s\\p{P}.,#-]{5,200}$");
    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TEL_RX = Pattern.compile("^\\+?\\d{7,15}$");
    // Prestamo validation patterns (used before creating/finding Prestamo object)
    private static final Pattern UUID_PRESTAMO_RX = Pattern // Kept for validating input ID
            .compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");


    /* ==== Colecciones ==== */
    // Use ISBN as key for libros for simplicity, though codigo exists
    private final Map<String, Libro> libros = new HashMap<>();
    // Use user ID (DNI or UNT ID) as key for usuarios
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, Prestamo> prestamos = new HashMap<>();

    /* ======== API de dominio ======== */

    public void registrarLibro(Libro libro) {
        validarLibro(libro);
        // Using ISBN as the primary key in the map for lookup convenience
        if (libros.containsKey(libro.getIsbn())) {
             throw new IllegalArgumentException("Libro con este ISBN ya existe: " + libro.getIsbn());
        }
        libros.put(libro.getIsbn(), libro);
    }

    public void registrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
         if (usuarios.containsKey(usuario.getId())) {
             throw new IllegalArgumentException("Usuario con este ID ya existe: " + usuario.getId());
        }
        usuarios.put(usuario.getId(), usuario);
    }

    public Prestamo prestarLibro(String idUsuario, String isbn) {
        Usuario u = usuarios.get(idUsuario);
        // Look up libro by ISBN
        Libro l = libros.get(isbn);
        Objects.requireNonNull(u, "Usuario no encontrado con ID: " + idUsuario);
        Objects.requireNonNull(l, "Libro no encontrado con ISBN: " + isbn);

        if (l.getEjemplaresDisponibles() == 0)
            throw new IllegalStateException("No hay ejemplares disponibles para el libro: " + l.getTitulo());

        l.setEjemplaresDisponibles(l.getEjemplaresDisponibles() - 1);

        Prestamo p = new Prestamo(
                UUID.randomUUID().toString(), // Generate Prestamo ID
                LocalDate.now(),
                LocalDate.now().plusWeeks(2), // fechaDevolucion (renamed)
                u,
                l);
        prestamos.put(p.getIdPrestamo(), p);
        return p;
    }

    public void devolverLibro(String idPrestamo) {
        Prestamo p = prestamos.get(idPrestamo);
        if (p == null)
            throw new NoSuchElementException("Préstamo no encontrado con ID: " + idPrestamo);

        if (p.getFechaRetornoReal() != null) {
             System.out.println("El préstamo " + idPrestamo + " ya fue devuelto.");
             return; // ya devuelto
        }

        p.setFechaRetornoReal(LocalDate.now());
        // Increment available copies of the returned book
        Libro libroDevuelto = p.getLibro();
        libroDevuelto.setEjemplaresDisponibles(
                libroDevuelto.getEjemplaresDisponibles() + 1);
        System.out.println("Libro devuelto: " + libroDevuelto.getTitulo());
    }

    /* ======== Validaciones centralizadas ======== */

    private void validarLibro(Libro l) {
        Objects.requireNonNull(l, "El libro no puede ser nulo.");
        Objects.requireNonNull(l.getCodigo(), "Código de libro no puede ser nulo."); // Basic null check
        if (l.getIsbn() == null || !ISBN_RX.matcher(l.getIsbn()).matches())
            throw new IllegalArgumentException("ISBN inválido: " + l.getIsbn());
        if (l.getTitulo() == null || !TITULO_RX.matcher(l.getTitulo()).matches())
            throw new IllegalArgumentException("Título inválido: " + l.getTitulo());
        if (l.getAutor() == null || !AUTOR_RX.matcher(l.getAutor()).matches())
            throw new IllegalArgumentException("Autor inválido: " + l.getAutor());
        if (l.getAnioPublicacion() < 1400 || l.getAnioPublicacion() > LocalDate.now().getYear() + 1) // Adjusted range
            throw new IllegalArgumentException("Año de publicación fuera de rango (1400-" + (LocalDate.now().getYear() + 1) + "): " + l.getAnioPublicacion());
        if (l.getEjemplaresDisponibles() < 0 || l.getEjemplaresDisponibles() > 9_999) // Allow 0 initially
            throw new IllegalArgumentException("Cantidad de ejemplares inválida (0-9999): " + l.getEjemplaresDisponibles());
    }

    private void validarUsuario(Usuario u) {
        Objects.requireNonNull(u, "El usuario no puede ser nulo.");
        String id = u.getId();
        if (id == null || !(DNI_RX.matcher(id).matches() || UNT_ID_RX.matcher(id).matches()))
            throw new IllegalArgumentException("ID de usuario inválido (debe ser DNI de 8 dígitos o Código UNT de 10 dígitos): " + id);
        Objects.requireNonNull(u.getTipo(), "Tipo de usuario no puede ser nulo."); // Validate tipo
        if (u.getNombre() == null || !NOMBRE_RX.matcher(u.getNombre()).matches())
            throw new IllegalArgumentException("Nombre inválido: " + u.getNombre());
        if (u.getDireccion() == null || !DIRECCION_RX.matcher(u.getDireccion()).matches())
             throw new IllegalArgumentException("Dirección inválida: " + u.getDireccion());
        if (u.getEmail() == null || !EMAIL_RX.matcher(u.getEmail()).matches())
            throw new IllegalArgumentException("E-mail inválido: " + u.getEmail());
        if (u.getTelefono() == null || !TEL_RX.matcher(u.getTelefono()).matches())
            throw new IllegalArgumentException("Teléfono inválido: " + u.getTelefono());
    }

    public void validarIdPrestamo(String idPrestamo) {
         if (idPrestamo == null || !UUID_PRESTAMO_RX.matcher(idPrestamo).matches())
             throw new IllegalArgumentException("ID de préstamo inválido (debe ser UUID): " + idPrestamo);
    }

    /* ======== Getters for API access ======== */

    public Collection<Libro> getLibros() {
        return libros.values();
    }

    public Libro getLibroByIsbn(String isbn) {
        return libros.get(isbn);
    }

    public Collection<Usuario> getUsuarios() {
        return usuarios.values();
    }

    public Usuario getUsuarioById(String idUsuario) {
        return usuarios.get(idUsuario);
    }

    public Collection<Prestamo> getPrestamos() {
        return prestamos.values();
    }

    public Prestamo getPrestamoById(String idPrestamo) {
        return prestamos.get(idPrestamo);
    }

    /* ======== Sample Data Initialization ======== */

    @PostConstruct // Run this method after the bean is created
    public void inicializarDatos() {
        try {
            Libro l1 = new Libro(UUID.randomUUID().toString(), "978-8437604947", "Cien Años de Soledad", "Gabriel García Márquez", 1967, 5);
            this.registrarLibro(l1);
            Libro l2 = new Libro(UUID.randomUUID().toString(), "978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald", 1925, 3);
            this.registrarLibro(l2);

            Usuario u1 = new Usuario("1022700623", "Juan Perez", "jperez@unitru.edu.pe", "+51987654321", "Av. España 123", TipoUsuario.ESTUDIANTE); // Estudiante UNT
            this.registrarUsuario(u1);
            Usuario u2 = new Usuario("70123456", "Maria Lopez", "mlopez@gmail.com", "+51912345678", "Jr. Pizarro 456", TipoUsuario.EXTERNO); // Externo DNI
            this.registrarUsuario(u2);

            System.out.println("--- Biblioteca Inicializada con Datos de Ejemplo ---");
            this.libros.values().forEach(Libro::mostrarInformacion);
            this.usuarios.values().forEach(System.out::println);
            System.out.println("----------------------------------------------------");

        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
             System.err.println("Error inicializando datos de ejemplo: " + e.getMessage());
        }
    }
}
