package com.tuempresa.tienda;

import com.tuempresa.tienda.modelo.ERol;
import com.tuempresa.tienda.modelo.Rol;
import com.tuempresa.tienda.repositorio.RolRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final RolRepositorio rolRepositorio;

    public DatosIniciales(RolRepositorio rolRepositorio) {
        this.rolRepositorio = rolRepositorio;
    }

    @Override
    public void run(String... args) throws Exception {
        // Lista de roles que deben existir: ADMIN, VENDEDOR, CLIENTE
        List<ERol> rolesNecesarios = Arrays.asList(
                ERol.ROLE_ADMIN,
                ERol.ROLE_VENDEDOR,
                ERol.ROLE_CLIENTE
        );

        for (ERol nombreRol : rolesNecesarios) {
            // Si el rol no existe, lo crea y lo guarda
            if (rolRepositorio.findByNombre(nombreRol).isEmpty()) {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre(nombreRol);
                rolRepositorio.save(nuevoRol);
                System.out.println("Rol creado: " + nombreRol.name());
            }
        }
    }
}