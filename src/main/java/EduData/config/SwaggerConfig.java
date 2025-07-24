package EduData.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para la documentación automática de la API.
 * 
 * Esta configuración permite:
 * - Generar documentación automática de todos los endpoints
 * - Probar la API directamente desde la interfaz web
 * - Definir esquemas de autenticación JWT
 * - Personalizar la información de la API
 * 
 * Acceso a la documentación:
 * - Swagger UI: http://localhost:8081/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8081/v3/api-docs
 * 
 * @author EduData Team
 * @version 1.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configuración principal de OpenAPI
     * Define la información general de la API, servidores y esquemas de seguridad
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                    new Server()
                        .url("http://localhost:8081")
                        .description("Servidor de desarrollo local"),
                    new Server()
                        .url("https://api.edudata.com")
                        .description("Servidor de producción (futuro)")
                ))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                    .addSecuritySchemes("JWT", 
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Token JWT para autenticación. Formato: Bearer {token}")
                    )
                );
    }

    /**
     * Información detallada de la API
     * Incluye título, descripción, versión, contacto y licencia
     */
    private Info apiInfo() {
        return new Info()
                .title("EduData API")
                .description("""
                    ## Sistema de Gestión Educativa EduData
                    
                    Esta API proporciona endpoints para la gestión completa de un sistema educativo, incluyendo:
                    
                    ### Funcionalidades principales:
                    - **Gestión de Estudiantes**: Registro, consulta, actualización y eliminación
                    - **Gestión de Docentes**: Administración del personal docente
                    - **Gestión de Cursos**: Creación y administración de cursos
                    - **Matrículas**: Inscripción de estudiantes en cursos
                    - **Asistencias**: Control de asistencia de estudiantes
                    - **Calificaciones**: Registro y consulta de notas
                    - **Búsqueda Inteligente**: Consultas en lenguaje natural usando IA
                    - **Autenticación**: Sistema de login con JWT
                    
                    ### Autenticación:
                    La mayoría de endpoints requieren autenticación JWT. 
                    1. Obtén un token usando el endpoint `/api/auth/login`
                    2. Incluye el token en el header: `Authorization: Bearer {token}`
                    
                    ### Búsqueda Inteligente:
                    El sistema incluye capacidades de procesamiento de lenguaje natural para realizar 
                    consultas como "¿cuántos estudiantes hay en matemáticas?" o "muestra los cursos de María"
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo EduData")
                    .email("admin@edudata.com")
                    .url("https://github.com/usuario/edudata"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"));
    }
}
