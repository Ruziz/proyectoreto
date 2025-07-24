package EduData.controller;

import EduData.entity.Matricula;
import EduData.service.MatriculaServicio;
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
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "JWT")
@Tag(name = "Matrículas", description = "Gestión de inscripciones de estudiantes en cursos - Registro académico")
public class MatriculaControlador {

    @Autowired
    private MatriculaServicio matriculaServicio;

    @GetMapping
    @Operation(
        summary = "Obtener todas las matrículas",
        description = "Devuelve la lista completa de todas las inscripciones de estudiantes en cursos registradas en el sistema."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de matrículas obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Matricula.class)
        )
    )
    public List<Matricula> listar() {
        return matriculaServicio.obtenerTodas();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener matrícula por ID",
        description = "Busca y devuelve una matrícula específica usando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Matrícula encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Matricula.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Matrícula no encontrada"
        )
    })
    public ResponseEntity<Matricula> obtenerPorId(
        @Parameter(description = "ID único de la matrícula", required = true, example = "1")
        @PathVariable Long id
    ) {
        return matriculaServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva matrícula",
        description = "Registra la inscripción de un estudiante en un curso específico. Requiere ID de estudiante, ID de curso y año académico."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Matrícula creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Matricula.class)
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - el estudiante ya está matriculado en este curso"
        )
    })
    public Matricula crear(
        @Parameter(description = "Datos de la nueva matrícula", required = true)
        @RequestBody Matricula matricula
    ) {
        return matriculaServicio.crear(matricula);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar matrícula",
        description = "Modifica los datos de una matrícula existente. Puede cambiar el año académico o el estado de la matrícula."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Matrícula actualizada exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Matricula.class)
        )
    )
    public Matricula actualizar(
        @Parameter(description = "ID de la matrícula a actualizar", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Nuevos datos de la matrícula", required = true)
        @RequestBody Matricula matricula
    ) {
        return matriculaServicio.actualizar(id, matricula);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar matrícula",
        description = "Desmatricula a un estudiante de un curso. Esta acción afectará las asistencias y calificaciones relacionadas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Matrícula eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Matrícula no encontrada"
        )
    })
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID de la matrícula a eliminar", required = true)
        @PathVariable Long id
    ) {
        matriculaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
