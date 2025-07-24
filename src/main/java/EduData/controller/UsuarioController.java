package EduData.controller;

import EduData.entity.Usuario;
import EduData.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Usuarios", description = "Administración de usuarios del sistema - Solo acceso para administradores")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener todos los usuarios",
        description = """
            Devuelve la lista completa de usuarios registrados en el sistema.
            
            ### Restricciones:
            - Solo accesible para usuarios con rol ADMIN
            - Las contraseñas se omiten por seguridad
            
            ### Información incluida:
            - ID, username, email, rol
            - Fecha de creación y última modificación
            - Estado del usuario (activo/inactivo)
            """
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de usuarios obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Usuario.class)
        )
    )
    @ApiResponse(
        responseCode = "403", 
        description = "Acceso denegado - requiere rol ADMIN"
    )
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Limpiar contraseñas por seguridad
        usuarios.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(
        summary = "Obtener usuario por ID",
        description = """
            Obtiene la información de un usuario específico.
            
            ### Restricciones de acceso:
            - Administradores: pueden ver cualquier usuario
            - Usuarios normales: solo pueden ver su propia información
            
            ### Seguridad:
            - La contraseña nunca se devuelve en la respuesta
            - Validación automática de permisos basada en JWT
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - no tiene permisos para ver este usuario"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado"
        )
    })
    public ResponseEntity<Usuario> getUserById(
        @Parameter(description = "ID único del usuario", required = true, example = "1")
        @PathVariable Long id
    ) {
        return usuarioRepository.findById(id)
                .map(user -> {
                    user.setPassword(null); // No devolver contraseña
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar usuario",
        description = """
            Elimina permanentemente un usuario del sistema.
            
            ### Restricciones:
            - Solo accesible para usuarios con rol ADMIN
            - La acción no se puede deshacer
            
            ### Precauciones:
            - Verificar que el usuario no tenga datos asociados críticos
            - Considerar desactivar en lugar de eliminar para preservar historial
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Usuario eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - requiere rol ADMIN"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado"
        )
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID del usuario a eliminar", required = true)
        @PathVariable Long id
    ) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
