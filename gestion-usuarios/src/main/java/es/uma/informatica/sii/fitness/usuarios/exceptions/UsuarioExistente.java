package es.uma.informatica.sii.fitness.usuarios.exceptions;

import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;

public class UsuarioExistente extends RuntimeException {
    public UsuarioExistente(Usuario usuario) {
        super("El usuario con email " + usuario.getEmail() + " ya existe");
    }

    public UsuarioExistente() {
        super();
    };
}
