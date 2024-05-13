package es.uma.informatica.sii.fitness.usuarios.controllers;

import es.uma.informatica.sii.fitness.usuarios.dtos.UsuarioDTO;
import es.uma.informatica.sii.fitness.usuarios.dtos.UsuarioNuevoDTO;
import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import es.uma.informatica.sii.fitness.usuarios.exceptions.UsuarioExistente;
import es.uma.informatica.sii.fitness.usuarios.exceptions.UsuarioInexistente;
import es.uma.informatica.sii.fitness.usuarios.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/usuario")
@CrossOrigin()
@Tag(name="Gestión de usuarios", description="Operaciones para la gestión de usuarios")
public class GestionUsuarios {

    private final UsuarioService usuarioService;

    public GestionUsuarios(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(description = "Obtiene la lista de usuarios del sistema",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Devuelve la lista de usuarios"),
                    @ApiResponse(responseCode = "403",
                            description = "Acceso no autorizado",
                            content = {@Content(schema = @Schema(implementation = Void.class))})
            })
    public List<UsuarioDTO> obtenerUsuarios(@RequestParam(value = "email", required = false) String email) {
        Stream<Usuario> stream;
        if (email != null) {
            stream = usuarioService.findByEmail(email).stream();
        } else {
            stream = usuarioService.findAll().stream();
        }
        return stream.map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static Function<Long, URI> usuarioUriBuilder(UriComponents uriBuilder) {
        return id -> UriComponentsBuilder.newInstance().uriComponents(uriBuilder).path("/usuario")
                .path(String.format("/%d", id))
                .build()
                .toUri();
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(description = "Crea un nuevo usuario en el sistema",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "El usuario se crea correctamente",
                            headers = {@Header(name = "Location",
                                    description = "URI del nuevo recurso",
                                    schema = @Schema(type = "string", subTypes = {URI.class}))}),
                    @ApiResponse(responseCode = "409",
                            description = "Hay un usuario con el mismo correo electrónico en el sistema",
                            content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(responseCode = "403",
                            description = "Acceso no autorizado",
                            content = {@Content(schema = @Schema(implementation = Void.class))})
            })
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioNuevoDTO usuarioDTO, UriComponentsBuilder uriBuilder) {
        var nuevoUsuario= UsuarioDTO.fromEntity(usuarioService.save(usuarioDTO.toEntity()));
        URI uri = uriBuilder.path("/usuario")
            .path(String.format("/%d", nuevoUsuario.getId()))
            .build()
            .toUri();

        return ResponseEntity.created(uri).body(nuevoUsuario);
    }

    @GetMapping("/{idUsuario}")
    @Operation(description = "Obtiene un usuario concreto",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "El usuario existe"),
                    @ApiResponse(responseCode = "404",
                            description = "El usuario no existe",
                            content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(responseCode = "403",
                            description = "Acceso no autorizado",
                            content = {@Content(schema = @Schema(implementation = Void.class))})
            })
    public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.of(usuarioService.findById(idUsuario)
                .map(usuario->UsuarioDTO.fromEntity(usuario)));
    }

    @PutMapping("/{idUsuario}")
    @Operation(description = "Actualiza un usuario",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "El usuario se ha actualizado"),
                    @ApiResponse(responseCode = "404",
                            description = "El usuario no existe",
                            content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(responseCode = "403",
                            description = "Acceso no autorizado",
                            content = {@Content(schema = @Schema(implementation = Void.class))})
            })
    public UsuarioDTO actualizarUsuario(@PathVariable Long idUsuario, @RequestBody UsuarioDTO usuarioDTO) {
        usuarioDTO.setId(idUsuario);
        return UsuarioDTO.fromEntity(usuarioService.save(usuarioDTO.toEntity()));
    }

    @DeleteMapping("/{idUsuario}")
    @Operation(description = "Elimina el usuario.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "El usuario se ha elminado"),
                    @ApiResponse(responseCode = "404",
                            description = "El usuario no existe",
                            content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(responseCode = "403",
                            description = "Acceso no autorizado",
                            content = {@Content(schema = @Schema(implementation = Void.class))})
            })
    public void eliminarUsuario(@PathVariable Long idUsuario) {
        usuarioService.deleteById(idUsuario);
    }

    @ExceptionHandler(UsuarioInexistente.class)
    public ResponseEntity<?> noEntcontrado() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UsuarioExistente.class)
    public ResponseEntity<?> yaExiste() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


}
