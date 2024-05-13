package es.uma.informatica.sii.fitness.usuarios.dtos;

import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "builderNuevo")
public class UsuarioNuevoDTO {
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String email;
	private String password;
	private Boolean administrador;

	public Usuario toEntity() {
		return Usuario.builder()
				.nombre(this.nombre)
				.apellido1(this.apellido1)
				.apellido2(this.apellido2)
				.email(this.email)
				.password(this.password)
				.administrador(this.administrador)
				.build();
	}
}
