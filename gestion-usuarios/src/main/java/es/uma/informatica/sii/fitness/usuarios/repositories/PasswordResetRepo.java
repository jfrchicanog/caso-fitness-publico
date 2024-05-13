package es.uma.informatica.sii.fitness.usuarios.repositories;

import es.uma.informatica.sii.fitness.usuarios.entities.PasswordReset;
import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepo extends JpaRepository<PasswordReset, String>{

    @Query("SELECT p FROM PasswordReset p WHERE p.usuario = :usuario")
    Optional<PasswordReset> findByUsuario(Usuario usuario);

    @Query("SELECT p FROM PasswordReset p WHERE p.usuario.id = :id")
    Optional<PasswordReset> findByUsuario(Long id);

}
