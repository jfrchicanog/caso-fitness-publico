package es.uma.informatica.sii.fitness.usuarios.dtos;

import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO extends UsuarioNuevoDTO {
	private Long id;

	@Builder
	public UsuarioDTO(Long id, String nombre, String apellido1, String apellido2, String email, String password, Boolean administrador) {
		super(nombre, apellido1, apellido2, email, password, administrador);
		this.id = id;
	}

	public static UsuarioDTO fromEntity(Usuario usuario) {
		return UsuarioDTO.builder()
				.id(usuario.getId())
				.nombre(usuario.getNombre())
				.apellido1(usuario.getApellido1())
				.apellido2(usuario.getApellido2())
				.email(usuario.getEmail())
				.administrador(usuario.getAdministrador())
				.build();
	}

	public Usuario toEntity() {
		return Usuario.builder()
				.id(getId())
				.nombre(getNombre())
				.apellido1(getApellido1())
				.apellido2(getApellido2())
				.email(getEmail())
				.password(getPassword())
				.administrador(getAdministrador())
				.build();
	}
}
