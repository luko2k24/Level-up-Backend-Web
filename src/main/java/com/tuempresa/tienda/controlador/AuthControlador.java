package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.dto.LoginPeticion;
import com.tuempresa.tienda.dto.LoginRespuesta;
import com.tuempresa.tienda.dto.RegistroPeticion;
import com.tuempresa.tienda.util.JwtUtil;
import com.tuempresa.tienda.modelo.Usuario;
import com.tuempresa.tienda.modelo.Rol;
import com.tuempresa.tienda.modelo.ERol;
import com.tuempresa.tienda.repositorio.UsuarioRepositorio;
import com.tuempresa.tienda.repositorio.RolRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControlador {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final PasswordEncoder passwordEncoder;

    public AuthControlador(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UsuarioRepositorio usuarioRepositorio,
                           RolRepositorio rolRepositorio,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRespuesta> autenticarUsuario(@RequestBody LoginPeticion loginPeticion) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginPeticion.getNombreUsuario(), loginPeticion.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generarToken(authentication);

        return ResponseEntity.ok(new LoginRespuesta(jwt));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroPeticion registroPeticion) {

        // Validación usando el método correcto del repositorio
        if (usuarioRepositorio.existsByNombreUsuario(registroPeticion.getNombreUsuario())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso");
        }

        if(usuarioRepositorio.existsByEmail(registroPeticion.getEmail())) {
            return new ResponseEntity<Map<String, String>>(
                    Collections.singletonMap("mensaje", "El correo electrónico ya está en uso."),
                    HttpStatus.BAD_REQUEST
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(registroPeticion.getNombreUsuario());
        usuario.setEmail(registroPeticion.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroPeticion.getPassword()));
        usuario.setNombreCompleto(registroPeticion.getNombreCompleto());
        usuario.setEdad(registroPeticion.getEdad());
        usuario.setRegion(registroPeticion.getRegion());
        usuario.setComuna(registroPeticion.getComuna());

        Rol roles = rolRepositorio.findByNombre(ERol.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error: Rol CLIENTE no encontrado."));

        usuario.setRoles(Collections.singleton(roles));
        usuarioRepositorio.save(usuario);

        return new ResponseEntity<Map<String, String>>(
                Collections.singletonMap("mensaje", "Usuario registrado exitosamente!"),
                HttpStatus.CREATED
        );
    }
}
