package EduData.controller;

import EduData.entity.Estudiante;
import EduData.service.EstudianteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
@Tag(name = "Sistema", description = "Endpoints de diagnóstico y administración del sistema")
public class HealthController {

    @Autowired(required = false)
    private EstudianteServicio estudianteServicio;

    @GetMapping
    @Operation(
        summary = "Verificar estado del sistema",
        description = "Endpoint de health check para verificar que el backend está funcionando correctamente. No requiere autenticación."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Sistema funcionando correctamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = """
                {
                    "status": "OK",
                    "message": "Backend is running correctly",
                    "timestamp": 1703123456789,
                    "server": "Spring Boot"
                }
                """
            )
        )
    )
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend is running correctly");
        response.put("timestamp", System.currentTimeMillis());
        response.put("server", "Spring Boot");
        return response;
    }

    @GetMapping("/init-data")
    @Operation(
        summary = "Inicializar datos de prueba",
        description = """
            Crea datos de muestra en el sistema para propósitos de testing y desarrollo.
            
            ### Datos creados:
            - 3 estudiantes de ejemplo con información básica
            - Identificaciones numéricas válidas
            - Correos electrónicos únicos
            
            ### Nota:
            Este endpoint es seguro de ejecutar múltiples veces ya que ignora errores de duplicados.
            """
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Resultado de la inicialización de datos",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = """
                {
                    "status": "SUCCESS",
                    "message": "Datos de prueba creados correctamente"
                }
                """
            )
        )
    )
    public Map<String, Object> initializeData() {
        Map<String, Object> response = new HashMap<>();
        try {
            if (estudianteServicio != null) {
                // Crear algunos estudiantes de prueba
                createSampleStudent("12345678", "Juan", "Pérez", "juan.perez@email.com");
                createSampleStudent("87654321", "María", "González", "maria.gonzalez@email.com");
                createSampleStudent("11111111", "Carlos", "Rodríguez", "carlos.rodriguez@email.com");
                
                response.put("status", "SUCCESS");
                response.put("message", "Datos de prueba creados correctamente");
            } else {
                response.put("status", "ERROR");
                response.put("message", "Servicio de estudiantes no disponible");
            }
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al crear datos: " + e.getMessage());
        }
        return response;
    }

    /**
     * Método privado para crear estudiantes de ejemplo
     * Ignora errores si el estudiante ya existe
     */
    private void createSampleStudent(String identificacion, String nombre, String apellido, String correo) {
        try {
            Estudiante estudiante = new Estudiante();
            estudiante.setIdentificacion(identificacion);
            estudiante.setNombre(nombre);
            estudiante.setApellido(apellido);
            estudiante.setCorreo(correo);
            estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
            estudiante.setGenero("Otro");
            estudiante.setTelefono("300-000-0000");
            estudiante.setDireccion("Dirección de ejemplo");
            
            estudianteServicio.createStudent(estudiante);
        } catch (Exception e) {
            // Ignorar si ya existe
            System.out.println("Error al crear estudiante (probablemente ya existe): " + e.getMessage());
        }
    }
}
