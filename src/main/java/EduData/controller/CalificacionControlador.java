package EduData.controller;

import EduData.entity.Calificacion;
import EduData.service.CalificacionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "JWT")
@Tag(name = "Calificaciones", description = "Gestión de notas y evaluaciones - Registro académico de estudiantes")
public class CalificacionControlador {

    @Autowired
    private CalificacionServicio calificacionServicio;

    @GetMapping
    @Operation(
        summary = "Obtener todas las calificaciones",
        description = "Devuelve el registro completo de todas las calificaciones y notas de estudiantes en el sistema."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de calificaciones obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Calificacion.class)
        )
    )
    public List<Calificacion> listar() {
        return calificacionServicio.obtenerTodas();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener calificación por ID",
        description = "Busca y devuelve una calificación específica usando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Calificación encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Calificacion.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Calificación no encontrada"
        )
    })
    public ResponseEntity<Calificacion> obtenerPorId(
        @Parameter(description = "ID único de la calificación", required = true, example = "1")
        @PathVariable Long id
    ) {
        return calificacionServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva calificación",
        description = "Registra una nueva nota o calificación para un estudiante. Requiere ID de estudiante, curso, nota y tipo de evaluación."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Calificación registrada exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Calificacion.class)
        )
    )
    public Calificacion crear(
        @Parameter(description = "Datos de la nueva calificación", required = true)
        @RequestBody Calificacion calificacion
    ) {
        return calificacionServicio.crear(calificacion);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar calificación",
        description = "Modifica una calificación existente. Puede cambiar la nota, tipo de evaluación o agregar observaciones."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Calificación actualizada exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Calificacion.class)
        )
    )
    public Calificacion actualizar(
        @Parameter(description = "ID de la calificación a actualizar", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Nuevos datos de la calificación", required = true)
        @RequestBody Calificacion calificacion
    ) {
        return calificacionServicio.actualizar(id, calificacion);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar calificación",
        description = "Elimina permanentemente una calificación del sistema. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Calificación eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Calificación no encontrada"
        )
    })
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID de la calificación a eliminar", required = true)
        @PathVariable Long id
    ) {
        calificacionServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
