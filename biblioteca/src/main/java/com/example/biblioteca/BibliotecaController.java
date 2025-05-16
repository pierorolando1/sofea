package com.example.biblioteca;

import com.example.biblioteca.dto.AdminLoginRequestDTO;
import com.example.biblioteca.dto.LoginRequestDTO;
import com.example.biblioteca.dto.UsuarioRegistrationDTO;
import com.example.biblioteca.model.Material;
import com.example.biblioteca.model.Prestamo;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.model.Libro;
import com.example.biblioteca.model.Tesis;
import com.example.biblioteca.model.Alumno;
import com.example.biblioteca.model.Docente;
import com.example.biblioteca.model.Externo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api") // Base path for all endpoints in this controller
@Tag(name = "Biblioteca API", description = "API para la gestión de libros, usuarios y préstamos en la biblioteca.")
public class BibliotecaController {

    @Autowired
    private BibliotecaUNT bibliotecaService;

    private final Map<String, String> authUsuarios = new HashMap<>();

    private static final String ADMIN_PASSWORD = "vegarojasmuerte"; // Hardcoded admin password

    @PostMapping("/auth/usuario")
    @Operation(summary = "Autenticar un usuario", description = "Verifica las credenciales (ID y contraseña) de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Faltan ID de usuario o contraseña"),
            @ApiResponse(responseCode = "401", description = "Autenticación fallida (credenciales inválidas o inconsistencia)")
    })
    public ResponseEntity<String> autenticarUsuario(@RequestBody LoginRequestDTO loginRequest) {
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

    // --- Material Endpoints ---

    @PostMapping("/materiales/libro")
    @Operation(summary = "Registrar un nuevo libro", description = "Crea un nuevo libro en la biblioteca.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado exitosamente", content = @Content(schema = @Schema(implementation = Libro.class))),
            @ApiResponse(responseCode = "400", description = "Datos de libro inválidos", content = @Content(schema = @Schema(type = "object"))),
    })
    public ResponseEntity<?> registrarLibro(@RequestBody Libro libro) {
        Map<String, String> errors = libro.validar();
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        // Assuming 'type' is set correctly within the Libro object or handled by constructor/setter
        Material nuevoLibro = bibliotecaService.registrarMaterial(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @PostMapping("/materiales/tesis")
    @Operation(summary = "Registrar una nueva tesis", description = "Crea una nueva tesis en la biblioteca.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tesis creada exitosamente", content = @Content(schema = @Schema(implementation = Tesis.class))),
            @ApiResponse(responseCode = "400", description = "Datos de tesis inválidos", content = @Content(schema = @Schema(type = "object"))),
    })
    public ResponseEntity<?> registrarTesis(@RequestBody Tesis tesis) {
        Map<String, String> errors = tesis.validar();
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Material nuevaTesis = bibliotecaService.registrarMaterial(tesis);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTesis);
    }

    @GetMapping("/materiales")
    @Operation(summary = "Obtener todos los materiales", description = "Retorna una lista de todos los materiales.")
    @ApiResponse(responseCode = "200", description = "Lista de materiales", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Material.class))))
    public ResponseEntity<List<Material>> getAllMateriales() {
        return ResponseEntity.ok(bibliotecaService.getMateriales());
    }

    @GetMapping("/materiales/{id}")
    @Operation(summary = "Obtener un material por ID", description = "Retorna un material específico basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material encontrado", content = @Content(schema = @Schema(implementation = Material.class))),
            @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    public ResponseEntity<Material> getMaterialById(@PathVariable String id) {
        return bibliotecaService.getMaterialById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material no encontrado con ID: " + id));
    }

    @PutMapping("/materiales/{id}")
    @Operation(summary = "Actualizar un material existente", description = "Actualiza los detalles de un material existente. El tipo de material (Libro/Tesis) no puede cambiarse; asegúrese que el JSON corresponda al tipo original.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material actualizado", content = @Content(schema = @Schema(implementation = Material.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    public ResponseEntity<?> updateMaterial(@PathVariable String id, @RequestBody Material materialDetails) {
        Map<String, String> errors = materialDetails.validar();
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Material updatedMaterial = bibliotecaService.updateMaterial(id, materialDetails);
            return ResponseEntity.ok(updatedMaterial);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/materiales/{id}")
    @Operation(summary = "Eliminar un material", description = "Elimina un material por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Material eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        if (bibliotecaService.deleteMaterial(id)) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material no encontrado con ID: " + id);
        }
    }

    // --- Usuario Endpoints ---

    @PostMapping("/usuarios/alumno")
    @Operation(summary = "Registrar un nuevo alumno", description = "Crea un nuevo usuario de tipo alumno.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alumno creado exitosamente", content = @Content(schema = @Schema(implementation = Alumno.class))),
            @ApiResponse(responseCode = "400", description = "Datos de alumno inválidos", content = @Content(schema = @Schema(type = "object"))),
    })
    public ResponseEntity<?> registrarAlumno(@RequestBody UsuarioRegistrationDTO registrationDTO) {
        // Asegurarse que el tipo sea correcto
        
        Alumno alumno = (Alumno) registrationDTO.toUsuario();
        Map<String, String> errors = alumno.validar();
        if (registrationDTO.getPassword() == null || registrationDTO.getPassword().length() < 6) {
             errors.put("password", "La contraseña es requerida y debe tener al menos 6 caracteres.");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        
        Usuario nuevoUsuario = bibliotecaService.registrarUsuario(alumno);
        // Store password (insecurely, as per existing pattern)
        authUsuarios.put(nuevoUsuario.getId(), registrationDTO.getPassword());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
    
    @PostMapping("/usuarios/docente")
    @Operation(summary = "Registrar un nuevo docente", description = "Crea un nuevo usuario de tipo docente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Docente creado exitosamente", content = @Content(schema = @Schema(implementation = Docente.class))),
            @ApiResponse(responseCode = "400", description = "Datos de docente inválidos", content = @Content(schema = @Schema(type = "object"))),
    })
    public ResponseEntity<?> registrarDocente(@RequestBody UsuarioRegistrationDTO registrationDTO) {
        // Asegurarse que el tipo sea correcto
        
        Docente docente = (Docente) registrationDTO.toUsuario();
        Map<String, String> errors = docente.validar();
        if (registrationDTO.getPassword() == null || registrationDTO.getPassword().length() < 6) {
             errors.put("password", "La contraseña es requerida y debe tener al menos 6 caracteres.");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        
        Usuario nuevoUsuario = bibliotecaService.registrarUsuario(docente);
        // Store password (insecurely, as per existing pattern)
        authUsuarios.put(nuevoUsuario.getId(), registrationDTO.getPassword());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
    
    @PostMapping("/usuarios/externo")
    @Operation(summary = "Registrar un nuevo usuario externo", description = "Crea un nuevo usuario de tipo externo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario externo creado exitosamente", content = @Content(schema = @Schema(implementation = Externo.class))),
            @ApiResponse(responseCode = "400", description = "Datos de usuario externo inválidos", content = @Content(schema = @Schema(type = "object"))),
    })
    public ResponseEntity<?> registrarExterno(@RequestBody UsuarioRegistrationDTO registrationDTO) {
        
        Externo externo = (Externo) registrationDTO.toUsuario();
        Map<String, String> errors = externo.validar();
        if (registrationDTO.getPassword() == null || registrationDTO.getPassword().length() < 6) {
             errors.put("password", "La contraseña es requerida y debe tener al menos 6 caracteres.");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        
        Usuario nuevoUsuario = bibliotecaService.registrarUsuario(externo);
        // Store password (insecurely, as per existing pattern)
        authUsuarios.put(nuevoUsuario.getId(), registrationDTO.getPassword());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(bibliotecaService.getUsuarios());
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener un usuario por ID", description = "Retorna un usuario específico basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable String id) {
        return bibliotecaService.getUsuarioById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
    }
    
    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los detalles de un usuario existente. El tipo de usuario no puede cambiarse.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> updateUsuario(@PathVariable String id, @RequestBody Usuario usuarioDetails) {
        // Note: usuarioDetails will be deserialized into Alumno, Docente, or Externo based on 'type' field.
        // Ensure the 'type' field in the request matches the actual type of the user being updated.
        Map<String, String> errors = usuarioDetails.validar();
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Usuario updatedUsuario = bibliotecaService.updateUsuario(id, usuarioDetails);
            return ResponseEntity.ok(updatedUsuario);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUsuario(@PathVariable String id) {
        if (bibliotecaService.deleteUsuario(id)) {
            authUsuarios.remove(id); // Also remove from auth map
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }
    }

    // --- Prestamo Endpoints ---

    @PostMapping("/prestamos")
    @Operation(summary = "Registrar un nuevo préstamo", description = "Crea un nuevo préstamo de un material a un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente", content = @Content(schema = @Schema(implementation = Prestamo.class))),
            @ApiResponse(responseCode = "400", description = "Datos de préstamo inválidos", content = @Content(schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Material o Usuario no encontrado")
    })
    public ResponseEntity<?> registrarPrestamo(@RequestBody Prestamo prestamo) {
        Map<String, String> errors = prestamo.validar(true); // true for new prestamo
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Prestamo nuevoPrestamo = bibliotecaService.registrarPrestamo(prestamo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPrestamo);
        } catch (NoSuchElementException e) {
            // This exception comes from BibliotecaUNT if material/user not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/prestamos")
    @Operation(summary = "Obtener todos los préstamos", description = "Retorna una lista de todos los préstamos.")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Prestamo.class))))
    public ResponseEntity<List<Prestamo>> getAllPrestamos() {
        return ResponseEntity.ok(bibliotecaService.getPrestamos());
    }

    @GetMapping("/prestamos/{id}")
    @Operation(summary = "Obtener un préstamo por ID", description = "Retorna un préstamo específico basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo encontrado", content = @Content(schema = @Schema(implementation = Prestamo.class))),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    public ResponseEntity<Prestamo> getPrestamoById(@PathVariable String id) {
        return bibliotecaService.getPrestamoById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con ID: " + id));
    }
    
    @PutMapping("/prestamos/{id}")
    @Operation(summary = "Actualizar un préstamo existente", description = "Actualiza los detalles de un préstamo, por ejemplo, para registrar una devolución.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo actualizado", content = @Content(schema = @Schema(implementation = Prestamo.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Préstamo, Material o Usuario no encontrado")
    })
    public ResponseEntity<?> updatePrestamo(@PathVariable String id, @RequestBody Prestamo prestamoDetails) {
        Map<String, String> errors = prestamoDetails.validar(false); // false for update
         if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Prestamo updatedPrestamo = bibliotecaService.updatePrestamo(id, prestamoDetails);
            return ResponseEntity.ok(updatedPrestamo);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/prestamos/{id}")
    @Operation(summary = "Eliminar un préstamo", description = "Elimina un préstamo por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Préstamo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    public ResponseEntity<Void> deletePrestamo(@PathVariable String id) {
        if (bibliotecaService.deletePrestamo(id)) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con ID: " + id);
        }
    }
}
