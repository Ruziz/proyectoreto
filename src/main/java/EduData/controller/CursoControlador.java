package EduData.controller;

import EduData.entity.Curso;
import EduData.service.CursoServicio;
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
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "JWT")
@Tag(name = "Cursos", description = "Gestión de cursos académicos - Materias, asignaturas y programas educativos")
public class CursoControlador {

    @Autowired
    private CursoServicio cursoServicio;

    @GetMapping
    @Operation(
        summary = "Obtener todos los cursos",
        description = "Devuelve la lista completa de todos los cursos/materias disponibles en el sistema educativo."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de cursos obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Curso.class)
        )
    )
    public List<Curso> listar() {
        return cursoServicio.obtenerTodos();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener curso por ID",
        description = "Busca y devuelve un curso específico usando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Curso encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Curso.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Curso no encontrado"
        )
    })
    public ResponseEntity<Curso> obtenerPorId(
        @Parameter(description = "ID único del curso", required = true, example = "1")
        @PathVariable Long id
    ) {
        return cursoServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo curso",
        description = "Registra un nuevo curso/materia en el sistema. Requiere nombre, código único y descripción."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Curso creado exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Curso.class)
        )
    )
    public Curso crear(
        @Parameter(description = "Datos del nuevo curso", required = true)
        @RequestBody Curso curso
    ) {
        return cursoServicio.crear(curso);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar curso",
        description = "Modifica los datos de un curso existente. Puede actualizar nombre, descripción, código y docente asignado."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Curso actualizado exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Curso.class)
        )
    )
    public Curso actualizar(
        @Parameter(description = "ID del curso a actualizar", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Nuevos datos del curso", required = true)
        @RequestBody Curso curso
    ) {
        return cursoServicio.actualizar(id, curso);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar curso",
        description = "Elimina permanentemente un curso del sistema. Esta acción no se puede deshacer y puede afectar matrículas existentes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Curso eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "No se puede eliminar - curso tiene estudiantes matriculados"
        )
    })
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID del curso a eliminar", required = true)
        @PathVariable Long id
    ) {
        cursoServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
