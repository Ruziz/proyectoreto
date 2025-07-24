package EduData.controller;

import EduData.entity.Usuario;
import EduData.repository.UsuarioRepository;
import EduData.service.DataInitializationService;
import EduData.service.SampleDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Configuración Inicial", description = "Endpoints para inicialización y configuración del sistema")
public class SetupController {

    private final UsuarioRepository usuarioRepository;
    private final DataInitializationService dataInitializationService;
    private final SampleDataService sampleDataService;

    @PostMapping("/init-users")
    @Operation(
        summary = "Inicializar usuarios del sistema",
        description = """
            Crea los usuarios básicos del sistema incluyendo administradores y usuarios de prueba.
            
            ### Usuarios creados:
            - Admin principal con privilegios completos
            - Usuarios docentes de ejemplo
            - Usuario invitado para pruebas
            
            ### Seguridad:
            Las contraseñas no se muestran en la respuesta por motivos de seguridad.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuarios inicializados correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                        "message": "Usuarios inicializados correctamente",
                        "totalUsuarios": 3,
                        "usuarios": [
                            {
                                "id": 1,
                                "username": "admin",
                                "email": "admin@edudata.com",
                                "role": "ADMIN"
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al inicializar usuarios"
        )
    })
    public ResponseEntity<Map<String, Object>> initializeUsers() {
        try {
            // Ejecutar la inicialización manualmente
            dataInitializationService.run();
            
            List<Usuario> usuarios = usuarioRepository.findAll();
            usuarios.forEach(user -> user.setPassword(null)); // No mostrar contraseñas
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuarios inicializados correctamente");
            response.put("totalUsuarios", usuarios.size());
            response.put("usuarios", usuarios);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al inicializar usuarios: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/init-sample-data")
    @Operation(
        summary = "Inicializar datos de muestra",
        description = """
            Crea un conjunto completo de datos de prueba para el sistema educativo.
            
            ### Datos creados:
            - Estudiantes de ejemplo con información completa
            - Docentes con especialidades variadas
            - Cursos de diferentes materias
            - Matrículas que relacionan estudiantes con cursos
            - Registros de asistencia y calificaciones
            
            ### Uso recomendado:
            - Ideal para desarrollo y testing
            - Permite probar funcionalidades sin datos reales
            - Seguro para ejecutar múltiples veces
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Datos de muestra inicializados correctamente"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al inicializar datos de muestra"
        )
    })
    public ResponseEntity<Map<String, Object>> initializeSampleData() {
        try {
            sampleDataService.inicializarDatosPrueba();
            sampleDataService.mostrarEstadisticas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Datos de prueba inicializados correctamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al inicializar datos de prueba: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/check-users")
    @Operation(
        summary = "Verificar usuarios del sistema",
        description = "Obtiene la lista de todos los usuarios registrados en el sistema para verificación y diagnóstico."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de usuarios obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = """
                {
                    "totalUsuarios": 3,
                    "usernames": ["admin", "docente1", "invitado"],
                    "usuarios": [
                        {
                            "id": 1,
                            "username": "admin",
                            "email": "admin@edudata.com",
                            "role": "ADMIN"
                        }
                    ]
                }
                """
            )
        )
    )
    public ResponseEntity<Map<String, Object>> checkUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(user -> user.setPassword(null)); // No mostrar contraseñas
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsuarios", usuarios.size());
        response.put("usuarios", usuarios);
        response.put("usernames", usuarios.stream().map(Usuario::getUsername).toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-connection")
    @Operation(
        summary = "Probar conexión a base de datos",
        description = "Verifica que la conexión a la base de datos esté funcionando correctamente realizando una consulta simple."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conexión exitosa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                        "message": "Conexión a base de datos OK",
                        "totalUsuarios": 3
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error de conexión a base de datos"
        )
    })
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            long count = usuarioRepository.count();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Conexión a base de datos OK");
            response.put("totalUsuarios", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error de conexión: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
