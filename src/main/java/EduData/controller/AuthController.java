package EduData.controller;

import EduData.dto.LoginRequest;
import EduData.dto.LoginResponse;
import EduData.dto.RegisterRequest;
import EduData.service.AuthService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. Requiere nombre de usuario único y email válido."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario registrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Error en los datos de registro - usuario ya existe o datos inválidos"
        )
    })
    public ResponseEntity<LoginResponse> register(
        @Parameter(description = "Datos de registro del nuevo usuario", required = true)
        @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT para acceder a endpoints protegidos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Credenciales inválidas"
        )
    })
    public ResponseEntity<LoginResponse> authenticate(
        @Parameter(description = "Credenciales de acceso", required = true)
        @RequestBody LoginRequest request
    ) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/guest")
    @Operation(
        summary = "Acceso como invitado",
        description = "Genera un token temporal para acceso limitado al sistema sin registro."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Token de invitado generado",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginResponse.class)
        )
    )
    public ResponseEntity<LoginResponse> loginAsGuest() {
        return ResponseEntity.ok(authService.loginAsGuest());
    }

    @GetMapping("/validate")
    @Operation(
        summary = "Validar token JWT",
        description = "Verifica si el token JWT actual es válido y no ha expirado."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Token válido",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Token inválido o expirado"
        )
    })
    public ResponseEntity<String> validateToken() {
        return ResponseEntity.ok("Token válido");
    }
}
