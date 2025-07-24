package EduData.controller;

import EduData.entity.Asistencia;
import EduData.service.AsistenciaServicio;
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
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "JWT")
@Tag(name = "Asistencias", description = "Control de asistencia de estudiantes - Registro diario de presencia en clases")
public class AsistenciaControlador {

    @Autowired
    private AsistenciaServicio asistenciaServicio;

    @GetMapping
    @Operation(
        summary = "Obtener todas las asistencias",
        description = "Devuelve el registro completo de asistencias de todos los estudiantes en todas las clases."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de asistencias obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Asistencia.class)
        )
    )
    public List<Asistencia> listar() {
        return asistenciaServicio.obtenerTodas();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener asistencia por ID",
        description = "Busca y devuelve un registro de asistencia específico usando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Asistencia encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Asistencia.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Registro de asistencia no encontrado"
        )
    })
    public ResponseEntity<Asistencia> obtenerPorId(
        @Parameter(description = "ID único del registro de asistencia", required = true, example = "1")
        @PathVariable Long id
    ) {
        return asistenciaServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva asistencia",
        description = "Marca la asistencia de un estudiante en una fecha específica. Requiere ID de estudiante, fecha y estado (presente/ausente)."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Asistencia registrada exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Asistencia.class)
        )
    )
    public Asistencia crear(
        @Parameter(description = "Datos del nuevo registro de asistencia", required = true)
        @RequestBody Asistencia asistencia
    ) {
        return asistenciaServicio.crear(asistencia);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar registro de asistencia",
        description = "Modifica un registro de asistencia existente. Puede cambiar el estado (presente/ausente) o agregar observaciones."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Asistencia actualizada exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Asistencia.class)
        )
    )
    public Asistencia actualizar(
        @Parameter(description = "ID del registro de asistencia a actualizar", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Nuevos datos del registro de asistencia", required = true)
        @RequestBody Asistencia asistencia
    ) {
        return asistenciaServicio.actualizar(id, asistencia);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar registro de asistencia",
        description = "Elimina permanentemente un registro de asistencia del sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Registro de asistencia eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Registro de asistencia no encontrado"
        )
    })
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID del registro de asistencia a eliminar", required = true)
        @PathVariable Long id
    ) {
        asistenciaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
