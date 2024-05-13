package es.uma.informatica.sii.fitness.usuarios.services;

import es.uma.informatica.sii.fitness.usuarios.security.SecurityConfguration;
import es.uma.informatica.sii.fitness.usuarios.entities.PasswordReset;
import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import es.uma.informatica.sii.fitness.usuarios.exceptions.CredencialesIncorrectas;
import es.uma.informatica.sii.fitness.usuarios.exceptions.TokenNoValidoException;
import es.uma.informatica.sii.fitness.usuarios.exceptions.UsuarioExistente;
import es.uma.informatica.sii.fitness.usuarios.exceptions.UsuarioInexistente;
import es.uma.informatica.sii.fitness.usuarios.repositories.PasswordResetRepo;
import es.uma.informatica.sii.fitness.usuarios.repositories.UsuarioRepo;
import es.uma.informatica.sii.fitness.usuarios.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Transactional(noRollbackFor = TokenNoValidoException.class)
public class UsuarioService {
    private final UsuarioRepo usuarioRepo;
    private final PasswordResetRepo passwordResetRepo;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final Logger log =Logger.getLogger(UsuarioService.class.getName());

    @Value("${baseURIOfFrontend}")
    private String baseURIOfFrontend = "http://localhost:4200";
    @Value("${passwordresettoken.expiration}")
    private long passwordResetTokenExpiration = 0;

    public UsuarioService(UsuarioRepo usuarioRepo,
                          PasswordEncoder passwordEncoder,
                          PasswordResetRepo passwordResetRepo,
                          JwtUtil jwtUtil) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetRepo = passwordResetRepo;
        this.jwtUtil = jwtUtil;
    }

    public Usuario save(Usuario usuario) {

        Optional<UserDetails> user = SecurityConfguration.getAuthenticatedUser();
        user.ifPresent(u -> log.info("Usuario autenticado: " + u.getUsername()));


        String password;
        if (usuario.getId() == null) {
            // Nuevo usuario
            if (usuarioRepo.findByEmail(usuario.getEmail()).isPresent()) {
                throw new UsuarioExistente(usuario);
            }
            password = usuario.getPassword();
            if (password == null || password.isEmpty()) {
                password = generarPasswordAleatoria();
            }
        } else {
            // Actualizar usuario
            Usuario u = usuarioRepo.findById(usuario.getId())
                    .orElseThrow(() -> new UsuarioInexistente(usuario));
            password = usuario.getPassword();
            if (password == null || password.isEmpty()) {
                password = u.getPassword();
            }
        }
        usuario.setPassword(passwordEncoder.encode(password));
        return usuarioRepo.save(usuario);
    }

    public List<Usuario> findAll() {
        return usuarioRepo.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepo.findById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepo.findByEmail(email);
    }

    public void deleteById(Long id) {
        if (!usuarioRepo.existsById(id)) {
            throw new UsuarioInexistente(id);
        }
        usuarioRepo.deleteById(id);
    }

    public String compruebaLogin(String email, String password) {
        Optional<Usuario> usuario = usuarioRepo.findByEmail(email);
        if (usuario.isEmpty()) {
            throw new UsuarioInexistente(email);
        }
        if (!passwordEncoder.matches(password, usuario.get().getPassword())) {
            throw new CredencialesIncorrectas();
        }

        String jwt = jwtUtil.generateToken(usuario.get());
        return jwt;
    }


    private String generarPasswordAleatoria() {
        return UUID.randomUUID().toString();
    }

    public boolean existsById(Long idUsuario) {
        return usuarioRepo.existsById(idUsuario);
    }

    public void forgottenPassword(String email) {
        usuarioRepo.findByEmail(email).ifPresent(this::preparareinicioContrasenia);
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public void passwordReset(String token, String password) {
        var passwordReset = passwordResetRepo.findById(token)
                .orElseThrow(() -> new TokenNoValidoException());

        if (passwordReset.getTokenCreation().toInstant().plus(passwordResetTokenExpiration, ChronoUnit.SECONDS)
                .isBefore(now().toInstant())) {
            passwordResetRepo.delete(passwordReset);
            throw new TokenNoValidoException();
        }

        var usuario = passwordReset.getUsuario();
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepo.save(usuario);
        passwordResetRepo.delete(passwordReset);
    }

    private void preparareinicioContrasenia(Usuario usuario) {
        // Elimina cualquier token previo
        var passwordReset = passwordResetRepo.findByUsuario(usuario);
        passwordReset.ifPresent(p-> {
            //log.info(p.toString());
            passwordResetRepo.delete(p);
        });

        String token = generarToken();
         var ps = PasswordReset.builder()
                    .tokenCreation(now())
                    .token(token)
                    .usuario(usuario)
                    .build();
        passwordResetRepo.save(ps);


        enviarMensajeParaReiniciarContrasnia(usuario.getEmail(), token);
    }

    private void enviarMensajeParaReiniciarContrasnia(String email, String token) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseURIOfFrontend)
                .queryParam("token", token)
                .build().toUriString();

        StringBuffer mensaje = new StringBuffer();
        mensaje.append("Hola, \n");
        mensaje.append("Hemos recibido una solicitud para reiniciar su contraseña en la aplicación de entrenamientos deportivos. ");
        mensaje.append("Si no ha sido usted, ignore este mensaje. ");
        mensaje.append("Para reiniciar la contraseña, haga clic en el siguiente enlace: ");
        mensaje.append(uri);
        mensaje.append(" Si el enlace no funciona, copie y pegue la dirección en su navegador. \n");
        mensaje.append("Saludos cordiales,\n");
        mensaje.append("Equipo técnico de Entrenamientos Deportivos");
        log.info(mensaje.toString());
    }



    private String generarToken() {
        return UUID.randomUUID().toString();
    }
}
