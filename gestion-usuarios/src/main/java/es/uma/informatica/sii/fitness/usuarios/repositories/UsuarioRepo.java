package es.uma.informatica.sii.fitness.usuarios.repositories;

import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByEmail(String email);
}
