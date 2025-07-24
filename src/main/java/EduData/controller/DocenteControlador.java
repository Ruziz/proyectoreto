package EduData.controller;

import EduData.entity.Docente;
import EduData.service.DocenteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docentes")
@CrossOrigin
@SecurityRequirement(name = "JWT")
@Tag(name = "Docentes", description = "Gestión del personal docente - CRUD completo y administración de profesores")
public class DocenteControlador {

    private final DocenteServicio servicio;

    public DocenteControlador(DocenteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los docentes",
        description = "Devuelve la lista completa de todos los docentes registrados en el sistema educativo."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de docentes obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Docente.class)
        )
    )
    public List<Docente> getAll() {
        return servicio.obtenerTodos();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener docente por ID",
        description = "Busca y devuelve un docente específico usando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Docente encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Docente.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Docente no encontrado"
        )
    })
    public ResponseEntity<Docente> getById(
        @Parameter(description = "ID único del docente", required = true, example = "1")
        @PathVariable Long id
    ) {
        return servicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo docente",
        description = "Registra un nuevo docente en el sistema. La identificación debe ser única y numérica (máximo 10 dígitos)."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Docente creado exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Docente.class)
        )
    )
    public Docente create(
        @Parameter(description = "Datos del nuevo docente", required = true)
        @RequestBody Docente docente
    ) {
        return servicio.crear(docente);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar docente",
        description = "Modifica los datos de un docente existente. Todos los campos pueden ser actualizados."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Docente actualizado exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Docente.class)
        )
    )
    public Docente update(
        @Parameter(description = "ID del docente a actualizar", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Nuevos datos del docente", required = true)
        @RequestBody Docente docente
    ) {
        return servicio.actualizar(id, docente);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar docente",
        description = "Elimina permanentemente un docente del sistema. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Docente eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Docente no encontrado"
        )
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID del docente a eliminar", required = true)
        @PathVariable Long id
    ) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}