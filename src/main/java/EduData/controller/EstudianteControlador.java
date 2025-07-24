package EduData.controller;

import EduData.entity.Estudiante;
import EduData.service.EstudianteServicio;
import EduData.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "JWT")
@Tag(name = "Estudiantes", description = "Gestión completa de estudiantes - CRUD, búsquedas y reportes PDF")
public class EstudianteControlador {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteControlador.class);
    private final EstudianteServicio servicio;
    private final PdfService pdfService;

    public EstudianteControlador(EstudianteServicio servicio, PdfService pdfService) {
        this.servicio = servicio;
        this.pdfService = pdfService;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los estudiantes",
        description = "Devuelve una lista completa de todos los estudiantes registrados en el sistema. " +
                     "Incluye manejo de errores de base de datos devolviendo lista vacía en caso de fallo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de estudiantes obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Estudiante.class)
            )
        )
    })
    public ResponseEntity<List<Estudiante>> getAll() {
        try {
            logger.info("Intentando obtener todos los estudiantes...");
            List<Estudiante> estudiantes = servicio.getAllStudents();
            logger.info("Se encontraron {} estudiantes", estudiantes.size());
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            logger.error("Error al obtener estudiantes: ", e);
            // Devolver una lista vacía en caso de error de base de datos
            logger.warn("Devolviendo lista vacía debido a error de base de datos");
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener estudiante por ID",
        description = "Busca y devuelve un estudiante específico usando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estudiante encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Estudiante.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Estudiante no encontrado"
        )
    })
    public ResponseEntity<Estudiante> getById(
        @Parameter(description = "ID único del estudiante", required = true, example = "1")
        @PathVariable Long id
    ) {
        try {
            logger.info("Buscando estudiante con ID: {}", id);
            return servicio.getStudentById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al buscar estudiante con ID {}: ", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo estudiante",
        description = "Registra un nuevo estudiante en el sistema. La identificación debe ser única y numérica (máximo 10 dígitos)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estudiante creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Estudiante.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno - posible identificación duplicada"
        )
    })
    public ResponseEntity<Estudiante> create(
        @Parameter(description = "Datos del nuevo estudiante", required = true)
        @RequestBody Estudiante estudiante
    ) {
        try {
            logger.info("Creando nuevo estudiante: {}", estudiante.getNombre());
            Estudiante nuevo = servicio.createStudent(estudiante);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            logger.error("Error al crear estudiante: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar estudiante",
        description = "Modifica los datos de un estudiante existente. Todos los campos pueden ser actualizados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estudiante actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Estudiante.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al actualizar - estudiante no encontrado o datos inválidos"
        )
    })
    public ResponseEntity<Estudiante> update(
        @Parameter(description = "ID del estudiante a actualizar", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Nuevos datos del estudiante", required = true)
        @RequestBody Estudiante estudiante
    ) {
        try {
            logger.info("Actualizando estudiante con ID: {}", id);
            Estudiante actualizado = servicio.updateStudent(id, estudiante);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            logger.error("Error al actualizar estudiante con ID {}: ", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar estudiante",
        description = "Elimina permanentemente un estudiante del sistema. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Estudiante eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al eliminar - estudiante no encontrado o tiene dependencias"
        )
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID del estudiante a eliminar", required = true)
        @PathVariable Long id
    ) {
        try {
            logger.info("Eliminando estudiante con ID: {}", id);
            servicio.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al eliminar estudiante con ID {}: ", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pdf")
    @Operation(
        summary = "Generar reporte PDF de todos los estudiantes",
        description = "Crea un documento PDF con la lista completa de estudiantes registrados."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "PDF generado exitosamente",
        content = @Content(mediaType = "application/pdf")
    )
    public ResponseEntity<byte[]> generatePdf() {
        try {
            logger.info("Generando PDF de todos los estudiantes");
            byte[] pdfBytes = pdfService.generateEstudiantesPdf();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "estudiantes.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            logger.error("Error al generar PDF de estudiantes: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/pdf")
    @Operation(
        summary = "Generar reporte PDF de un estudiante específico",
        description = "Crea un documento PDF con los datos detallados de un estudiante individual."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "PDF del estudiante generado exitosamente",
            content = @Content(mediaType = "application/pdf")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al generar PDF - estudiante no encontrado"
        )
    })
    public ResponseEntity<byte[]> generateStudentPdf(
        @Parameter(description = "ID del estudiante para generar el PDF", required = true)
        @PathVariable Long id
    ) {
        try {
            logger.info("Generando PDF del estudiante con ID: {}", id);
            byte[] pdfBytes = pdfService.generateEstudiantePdf(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "estudiante_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            logger.error("Error al generar PDF del estudiante con ID {}: ", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
