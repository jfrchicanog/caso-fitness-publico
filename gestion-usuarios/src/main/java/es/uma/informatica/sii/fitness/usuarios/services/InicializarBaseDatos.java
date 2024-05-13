package es.uma.informatica.sii.fitness.usuarios.services;

import es.uma.informatica.sii.fitness.usuarios.entities.Usuario;
import es.uma.informatica.sii.fitness.usuarios.services.UsuarioService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class InicializarBaseDatos {

    private final Logger log = Logger.getLogger(InicializarBaseDatos.class.getName());
    private final UsuarioService service;
    public InicializarBaseDatos(UsuarioService service) {
        this.service = service;
    }

    @PostConstruct
    public void inicializarSiNecesario() {
        if (service.findAll().isEmpty()) {
            log.info("No hay usuarios en la base de datos. Creando usuario admin");
            var usuario = Usuario.builder()
                    .nombre("Admin")
                    .apellido1("Admin")
                    .apellido2("Admin")
                    .email("admin@uma.es")
                    .password("admin")
                    .administrador(true)
                    .build();
            service.save(usuario);
            usuario = Usuario.builder()
                    .nombre("Antonio")
                    .apellido1("García")
                    .apellido2("Ramos")
                    .email("antonio@uma.es")
                    .password("5678")
                    .administrador(false)
                    .build();
            service.save(usuario);
        }
    }

}
