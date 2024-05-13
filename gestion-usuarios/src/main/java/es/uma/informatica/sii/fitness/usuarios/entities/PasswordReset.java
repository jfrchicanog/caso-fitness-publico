package es.uma.informatica.sii.fitness.usuarios.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PasswordReset {
    @Id
    private String token;
    private Timestamp tokenCreation;
    @ManyToOne
    private Usuario usuario;
}
