package EduData.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import EduData.service.NlqServiceMejorado;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nlq")
@SecurityRequirement(name = "JWT")
@Tag(name = "Búsqueda Inteligente (NLQ)", 
     description = "Procesamiento de consultas en lenguaje natural usando IA para generar consultas SQL dinámicas")
public class NlqControlador {
    private final NlqServiceMejorado service;

    public NlqControlador(NlqServiceMejorado service){
        this.service = service;
    }

    @PostMapping(
            path= "/pregunta",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Consulta en lenguaje natural",
        description = """
            Procesa preguntas en español natural y las convierte en consultas SQL usando IA.
            
            ### Ejemplos de consultas soportadas:
            - "¿Cuántos estudiantes hay en total?"
            - "Muestra todos los cursos de matemáticas"
            - "¿Qué estudiantes están matriculados en programación?"
            - "Lista los docentes que enseñan ciencias"
            - "¿Cuál es la asistencia promedio del curso de inglés?"
            
            ### Capacidades:
            - Consultas de conteo y agregación
            - Búsquedas por nombre, materia o características
            - Relaciones entre estudiantes, cursos y docentes
            - Filtros por fecha, estado, género, etc.
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Pregunta en lenguaje natural en español",
            required = true,
            content = @Content(
                mediaType = "text/plain",
                examples = {
                    @ExampleObject(
                        name = "Conteo de estudiantes",
                        value = "¿Cuántos estudiantes hay en total?"
                    ),
                    @ExampleObject(
                        name = "Búsqueda de cursos",
                        value = "Muestra todos los cursos de matemáticas"
                    ),
                    @ExampleObject(
                        name = "Estudiantes por curso",
                        value = "¿Qué estudiantes están en el curso de programación?"
                    ),
                    @ExampleObject(
                        name = "Consulta relacional",
                        value = "Muestra en qué curso está cada estudiante"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consulta procesada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "array",
                    example = """
                    [
                        {
                            "estudiante_nombre": "Juan",
                            "estudiante_apellido": "Pérez",
                            "curso_nombre": "Matemáticas Básicas",
                            "matricula_anio": 2024
                        },
                        {
                            "resultado": 25
                        }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Error al procesar la consulta - pregunta no válida o ambigua"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno - problema con la base de datos o servicio de IA"
        )
    })
    public List<Map<String, Object>> consulta (
        @Parameter(hidden = true) // El @RequestBody ya está documentado arriba
        @RequestBody String pregunta
    ){
        return service.answer(pregunta);
    }
    
    @GetMapping("/test")
    @Operation(
        summary = "Verificar estado del servicio NLQ",
        description = "Endpoint de prueba para verificar que el servicio de búsqueda inteligente está funcionando correctamente."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Servicio funcionando correctamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = """
                {
                    "status": "OK",
                    "message": "El servicio NLQ mejorado está funcionando",
                    "timestamp": 1703123456789,
                    "version": "2.0"
                }
                """
            )
        )
    )
    public Map<String, Object> test() {
        return Map.of(
                "status", "OK",
                "message", "El servicio NLQ mejorado está funcionando",
                "timestamp", System.currentTimeMillis(),
                "version", "2.0"
        );
    }
}