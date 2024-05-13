package es.uma.informatica.sii.fitness.usuarios.exceptions;

import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;

public class UsuarioInexistente extends RuntimeException {
    public UsuarioInexistente(Usuario usuario) {
        this(usuario.getId());
    }

    public UsuarioInexistente(Long id) {
        super("El usuario con ID " +id + " no existe");
    }

    public UsuarioInexistente(String email) {
        super("El usuario con email " +email + " no existe");
    }

    public UsuarioInexistente() {
        super();
    };
}
