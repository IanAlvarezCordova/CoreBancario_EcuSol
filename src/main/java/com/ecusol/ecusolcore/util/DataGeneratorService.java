// servicio/DataGeneratorService.java
package com.ecusol.ecusolcore.util;

import com.ecusol.ecusolcore.core.modelo.ClientePersona;
import com.ecusol.ecusolcore.core.modelo.Cuenta;
import com.ecusol.ecusolcore.core.repositorio.ClientePersonaRepository;
import com.ecusol.ecusolcore.core.repositorio.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class DataGeneratorService {

    @Autowired private ClientePersonaRepository clienteRepo;
    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    // 60 NOMBRES REALES ECUATORIANOS (sin tildes)
    private final String[] nombres = {
            "Juan","Jose","Luis","Carlos","Pedro","Diego","Andres","Fernando","Miguel","Pablo",
            "Alejandro","Daniel","David","Eduardo","Francisco","Gabriel","Guillermo","Hector","Ivan","Javier",
            "Jorge","Leonardo","Manuel","Marco","Mario","Martin","Mateo","Nicolas","Oscar","Rafael",
            "Ricardo","Roberto","Rodrigo","Santiago","Sebastian","Victor","Maria","Ana","Laura","Carmen",
            "Sofia","Isabella","Valeria","Gabriela","Camila","Patricia","Adriana","Carolina","Daniela","Veronica",
            "Natalia","Jessica","Andrea","Juliana","Catalina","Alejandra","Vanessa","Paola","Ximena","Monica"
    };

    // 60 APELLIDOS REALES ECUATORIANOS (sin tildes)
    private final String[] apellidos = {
            "Garcia","Martinez","Lopez","Gonzalez","Rodriguez","Perez","Sanchez","Ramirez","Torres","Flores",
            "Rivera","Gomez","Diaz","Vasquez","Castro","Morales","Ortiz","Romero","Jimenez","Reyes",
            "Cruz","Moreno","Herrera","Medina","Aguilar","Vargas","Castillo","Mendoza","Guerrero","Rojas",
            "Chavez","Nunez","Silva","Cabrera","Ortega","Dominguez","Mora","Ramos","Vega","Pena",
            "Rios","Soto","Contreras","Guzman","Navarro","Velasquez","Molina","Delgado","Sandoval","Cortes",
            "Leon","Fuentes","Campos","Espinoza","Lara","Carrillo","Salazar","Figueroa","Alvarado","Rivas"
    };

    private final String[] dominios = {
            "gmail.com","hotmail.com","yahoo.com","outlook.com","ecuador.net","ute.edu.ec", "espe.edu.ec"
    };

    public String generarClientesConCuentas(int cantidad) {
        int generados = 0;

        while (generados < cantidad) {
            String nombre1 = nombres[random.nextInt(nombres.length)];
            String segundoNombre = random.nextBoolean() ? nombres[random.nextInt(nombres.length)] : "";
            String apellido1 = apellidos[random.nextInt(apellidos.length)];
            String apellido2 = apellidos[random.nextInt(apellidos.length)];

            String nombresCompletos = segundoNombre.isEmpty() ? nombre1 : nombre1 + " " + segundoNombre;
            String apellidosCompletos = apellido1 + " " + apellido2;

            String cedula = generarCedulaUnica();
            String usuario = generarUsuarioUnico(nombre1.toLowerCase(), apellido1.toLowerCase());
            String email = generarEmailUnico(nombre1.toLowerCase(), apellido1.toLowerCase(), apellido2.toLowerCase());

            ClientePersona cliente = ClientePersona.builder()
                    .cedula(cedula)
                    .nombres(nombresCompletos)
                    .apellidos(apellidosCompletos)
                    .email(email)
                    .usuario(usuario)
                    .passwordHash(passwordEncoder.encode("123456"))
                    .telefono("09" + String.format("%08d", random.nextInt(20000000)))
                    .direccion(generarDireccionRealista())
                    .entidadId(1L)
                    .estado("ACTIVO")
                    .build();

            clienteRepo.save(cliente);
            generados++;

            // Distribución realista de cuentas
            int numCuentas = generarNumeroCuentas();

            for (int j = 0; j < numCuentas; j++) {
                Cuenta cuenta = new Cuenta();
                cuenta.setEntidadId(1L);                                      // ← OBLIGATORIO
                cuenta.setTipoCuentaId(random.nextBoolean() ? 1L : 2L);       // 1 = Ahorro, 2 = Corriente
                cuenta.setClientePersonaId(cliente.getClienteId());          // ← persona
                cuenta.setEmpresaId(null);                                   // ← no es empresa
                cuenta.setNumeroCuenta(generarNumeroCuentaUnico());
                cuenta.setSaldo(BigDecimal.valueOf(100 + random.nextDouble() * 14900));
                cuenta.setFechaApertura(LocalDate.now().minusDays(random.nextInt(1000)));
                cuenta.setEstado("ACTIVA");
                cuentaRepo.save(cuenta);
            }
        }

        return "¡Generados " + generados + " clientes con cuentas correctamente!";
    }

    // Métodos auxiliares únicos (igual que antes, pero completos)
    private String generarUsuarioUnico(String baseNombre, String baseApellido) {
        String base = (baseNombre + baseApellido.substring(0, Math.min(4, baseApellido.length())));
        String usuario;
        int sufijo = 0;
        do {
            usuario = base + (sufijo == 0 ? "" : sufijo);
            sufijo++;
        } while (clienteRepo.existsByUsuario(usuario));
        return usuario;
    }

    private String generarEmailUnico(String n, String a1, String a2) {
        String email;
        int sufijo = 0;
        do {
            String num = sufijo == 0 ? "" : String.valueOf(sufijo + random.nextInt(9000));
            email = n + "." + a1 + num + "@" + dominios[random.nextInt(dominios.length)];
            sufijo++;
        } while (clienteRepo.existsByEmail(email));
        return email;
    }

    private String generarNumeroCuentaUnico() {
        String num;
        do {
            num = "10" + String.format("%010d", random.nextLong(1_000_000_000L));
        } while (cuentaRepo.findByNumeroCuenta(num).isPresent());
        return num;
    }

    private String generarCedulaUnica() {
        String cedula;
        do {
            cedula = generarCedulaValida();
        } while (clienteRepo.findByCedula(cedula).isPresent()); // necesitas agregar este método al repo
        return cedula;
    }

    private String generarCedulaValida() {
        int[] c = new int[10];
        c[0] = random.nextInt(24) + 1;
        for (int i = 1; i < 9; i++) c[i] = random.nextInt(10);
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int dig = c[i] * ((i % 2 == 0) ? 2 : 1);
            suma += dig > 9 ? dig - 9 : dig;
        }
        int dv = (10 - (suma % 10)) % 10;
        c[9] = dv;
        StringBuilder sb = new StringBuilder();
        for (int d : c) sb.append(d);
        return sb.toString();
    }

    private int generarNumeroCuentas() {
        int r = random.nextInt(100);
        if (r < 60) return 1;
        if (r < 90) return 2;
        if (r < 98) return 3;
        return 4;
    }

    private String generarDireccionRealista() {
        String[] calles = {"Av. Amazonas", "Av. Eloy Alfaro", "Av. 6 de Diciembre", "Av. Mariscal Sucre", "Av. 10 de Agosto"};
        String[] ciudades = {"Quito", "Guayaquil", "Cuenca", "Ambato", "Manta"};
        return calles[random.nextInt(calles.length)] + " y " + calles[random.nextInt(calles.length)] + ", " + ciudades[random.nextInt(ciudades.length)];
    }
}