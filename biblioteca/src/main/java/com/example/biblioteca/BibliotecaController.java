package com.example.biblioteca;

import com.example.biblioteca.dto.LibroDTO;
import com.example.biblioteca.dto.PrestamoRequestDTO;
import com.example.biblioteca.dto.UsuarioAuthDTO;
import com.example.biblioteca.dto.LoginRequestDTO;
import com.example.biblioteca.dto.AdminLoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api") // Base path for all endpoints in this controller
@Tag(name = "Biblioteca API", description = "API para la gestión de libros, usuarios y préstamos en la biblioteca.")
public class BibliotecaController {

    @Autowired
    private BibliotecaUNT bibliotecaService;

    // WARNING: Storing passwords in plaintext in a HashMap is highly insecure.
    // This is for demonstration purposes only. Use proper password hashing in production.
    private final Map<String, String> authUsuarios = new HashMap<>();
    // Hardcoded admin password for demonstration purposes

    private static final String ADMIN_PASSWORD = "vegarojasmuerte"; // Hardcoded admin password

    // --- Libro Endpoints ---

    @PostMapping("/libros")
    @Operation(summary = "Registrar un nuevo libro", description = "Crea un nuevo libro en el sistema con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para el libro"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Libro> registrarLibro(@RequestBody LibroDTO libroDTO) {
        try {
            // Create Libro object from DTO
            Libro nuevoLibro = new Libro(
                    UUID.randomUUID().toString(), // Generate internal code
                    libroDTO.getIsbn(),
                    libroDTO.getTitulo(),
                    libroDTO.getAutor(),
                    libroDTO.getAnioPublicacion(),
                    libroDTO.getEjemplaresDisponibles()
            );
            bibliotecaService.registrarLibro(nuevoLibro);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar libro", e);
        }
    }

    @GetMapping("/libros")
    @Operation(summary = "Listar todos los libros", description = "Devuelve una colección de todos los libros registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida")
    public Collection<Libro> listarLibros() {
        return bibliotecaService.getLibros();
    }

    @GetMapping("/libros/{isbn}")
    @Operation(summary = "Obtener un libro por ISBN", description = "Busca y devuelve un libro específico usando su ISBN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public ResponseEntity<Libro> getLibroPorIsbn(
            @Parameter(description = "ISBN del libro a buscar", required = true) @PathVariable String isbn) {
        Libro libro = bibliotecaService.getLibroByIsbn(isbn);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Usuario Endpoints ---

    @PostMapping("/usuarios")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema. Requiere contraseña para autenticación futura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente (devuelve datos sin contraseña)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para el usuario (e.g., contraseña vacía, tipo inválido)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody UsuarioAuthDTO usuarioAuthDTO) {
        try {
            // Validate password presence (basic check)
            if (usuarioAuthDTO.getPassword() == null || usuarioAuthDTO.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            // Validate and convert tipo
            TipoUsuario tipoUsuario;
            try {
                // Convert string tipo from DTO to enum, case-insensitive
                tipoUsuario = TipoUsuario.valueOf(usuarioAuthDTO.getTipo().toUpperCase());
            } catch (NullPointerException | IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid or missing user type (tipo). Must be 'EXTERNO' or 'ESTUDIANTE'.");
            }

            Usuario nuevoUsuario = new Usuario(
                    usuarioAuthDTO.getId(),
                    usuarioAuthDTO.getNombre(),
                    usuarioAuthDTO.getEmail(), // Correct order based on Usuario constructor
                    usuarioAuthDTO.getTelefono(),
                    usuarioAuthDTO.getDireccion(),
                    tipoUsuario // Use converted enum
            );
            bibliotecaService.registrarUsuario(nuevoUsuario);
            // Store user ID and password in the map
            authUsuarios.put(nuevoUsuario.getId(), usuarioAuthDTO.getPassword());
            // Return user details without password
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar usuario", e);
        }
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una colección de todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida")
    public Collection<Usuario> listarUsuarios() {
        return bibliotecaService.getUsuarios();
    }

    @GetMapping("/usuarios/{idUsuario}")
    @Operation(summary = "Obtener un usuario por ID", description = "Busca y devuelve un usuario específico usando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> getUsuarioPorId(
            @Parameter(description = "ID del usuario a buscar", required = true) @PathVariable String idUsuario) {
        Usuario usuario = bibliotecaService.getUsuarioById(idUsuario);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Prestamo Endpoints ---

    @PostMapping("/prestamos")
    @Operation(summary = "Realizar un préstamo de libro", description = "Registra un nuevo préstamo asociando un usuario y un libro (por ISBN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo realizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Prestamo.class))),
            @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado"),
            @ApiResponse(responseCode = "409", description = "No hay ejemplares disponibles del libro"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Prestamo> prestarLibro(@RequestBody PrestamoRequestDTO prestamoRequest) {
        try {
            Prestamo nuevoPrestamo = bibliotecaService.prestarLibro(
                    prestamoRequest.getIdUsuario(),
                    prestamoRequest.getIsbn() // Use the renamed getter
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPrestamo);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            // User or Book not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            // No copies available
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al realizar préstamo", e);
        }
    }

    @PutMapping("/prestamos/{idPrestamo}/devolver")
    @Operation(summary = "Devolver un libro prestado", description = "Marca un préstamo como devuelto usando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID de préstamo inválido"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> devolverLibro(
            @Parameter(description = "ID del préstamo a devolver (UUID)", required = true) @PathVariable String idPrestamo) {
        try {
            // Validate ID format before calling service (optional, service might do it too)
             bibliotecaService.validarIdPrestamo(idPrestamo);
             bibliotecaService.devolverLibro(idPrestamo);
            return ResponseEntity.ok().build(); // OK or No Content (204) are suitable
        } catch (IllegalArgumentException e) {
             // Invalid UUID format
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al devolver libro", e);
        }
    }

    @GetMapping("/prestamos")
    @Operation(summary = "Listar todos los préstamos", description = "Devuelve una colección de todos los préstamos registrados (activos e históricos).")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida")
    public Collection<Prestamo> listarPrestamos() {
        return bibliotecaService.getPrestamos();
    }

     @GetMapping("/prestamos/{idPrestamo}")
     @Operation(summary = "Obtener un préstamo por ID", description = "Busca y devuelve un préstamo específico usando su ID.")
     @ApiResponses(value = {
             @ApiResponse(responseCode = "200", description = "Préstamo encontrado",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Prestamo.class))),
             @ApiResponse(responseCode = "400", description = "ID de préstamo inválido"),
             @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
             @ApiResponse(responseCode = "500", description = "Error interno del servidor")
     })
    public ResponseEntity<Prestamo> getPrestamoPorId(
            @Parameter(description = "ID del préstamo a buscar (UUID)", required = true) @PathVariable String idPrestamo) {
         try {
             // Validate ID format before calling service
             bibliotecaService.validarIdPrestamo(idPrestamo);
             Prestamo prestamo = bibliotecaService.getPrestamoById(idPrestamo);
             if (prestamo != null) {
                 return ResponseEntity.ok(prestamo);
             } else {
                 // Should ideally be caught by NoSuchElementException if service throws it
                 return ResponseEntity.notFound().build();
             }
         } catch (IllegalArgumentException e) {
             // Invalid UUID format
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
         } catch (Exception e) {
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar préstamo", e);
         }
    }

    // --- Authentication Endpoints ---

    @PostMapping("/auth/usuario")
    @Operation(summary = "Autenticar un usuario", description = "Verifica las credenciales (ID y contraseña) de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Faltan ID de usuario o contraseña"),
            @ApiResponse(responseCode = "401", description = "Autenticación fallida (credenciales inválidas o inconsistencia)")
    })
    public ResponseEntity<String> autenticarUsuario(@RequestBody LoginRequestDTO loginRequest) {
        authUsuarios.put("1022700623", "contrasena");

        String userId = loginRequest.getIdUsuario();
        String providedPassword = loginRequest.getPassword();

        if (userId == null || providedPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID and password are required.");
        }

        String storedPassword = authUsuarios.get(userId);

        if (storedPassword != null && storedPassword.equals(providedPassword)) {
            // Check if user actually exists in the main user list as well
            if (bibliotecaService.getUsuarioById(userId) != null) {
                 return ResponseEntity.ok("User authenticated successfully.");
            } else {
                // Should not happen if registration logic is correct, but good to check
                authUsuarios.remove(userId); // Clean up inconsistent state
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: User inconsistency.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Invalid credentials.");
        }
    }

    @PostMapping("/auth/admin")
    @Operation(summary = "Autenticar administrador", description = "Verifica la contraseña del administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador autenticado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Autenticación de administrador fallida")
    })
    public ResponseEntity<String> autenticarAdmin(@RequestBody AdminLoginRequestDTO adminLoginRequest) {
         String providedPassword = adminLoginRequest.getPassword();

         if (providedPassword != null && providedPassword.equals(ADMIN_PASSWORD)) {
             return ResponseEntity.ok("Admin authenticated successfully.");
         } else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin authentication failed.");
         }
    }

    // Consider adding @ExceptionHandler methods for more centralized error handling if needed.
}
