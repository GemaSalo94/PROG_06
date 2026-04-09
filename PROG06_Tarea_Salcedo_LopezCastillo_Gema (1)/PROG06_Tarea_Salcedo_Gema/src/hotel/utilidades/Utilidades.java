package hotel.utilidades;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * @author gema natalia salcedo
 * Clase con metodos estaticos de utilidad general reutilizables en cualquier proyecto.
 */
public class Utilidades {

    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";
    // VALIDACION DNI

    private static char calcularLetraDNI(int dni) {
        return LETRAS_DNI.charAt(dni % 23);
    }

    /**
     * Valida que un DNI sea correcto en formato y letra.
     * @param dni La cadena con el DNI completo.
     * @throws IllegalArgumentException Si el DNI no es valido.
     */
    public static void validarDNI(String dni) {
        if (dni == null || dni.isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacio.");
        }
        if (dni.length() != 9) {
            throw new IllegalArgumentException("El DNI debe tener 9 caracteres.");
        }
        try {
            int numero = Integer.parseInt(dni.substring(0, 8));
            char letraLeida = Character.toUpperCase(dni.charAt(8));
            char letraCalculada = calcularLetraDNI(numero);
            if (letraLeida != letraCalculada) {
                throw new IllegalArgumentException(
                    "La letra del DNI no es correcta. Letra esperada: " + letraCalculada + ".");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los primeros 8 caracteres del DNI deben ser numericos.");
        }
    }
    
    // VALIDACION EMAIL (expresion regular)

    /**
     * Valida que un email tenga un formato correcto.
     * El email es opcional: si esta en blanco se acepta directamente.
     *
     * Patron: ^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,6}$
     *   ^            inicio de la cadena.
     *   [\w.-]+      usuario (letras, numeros, puntos, guiones).
     *   @            separador obligatorio usuario/dominio.
     *   [\w.-]+      dominio.
     *   \.           punto separador dominio/extension.
     *   [a-zA-Z]{2,6} extension de 2 a 6 letras.
     *   $            fin de la cadena.
     *
     * @param email El correo a validar.
     * @return true si el email es valido o esta vacio, false si tiene formato incorrecto.
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email opcional: se acepta vacio
        }
        String patron = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(patron);
    }

    // LECTURA DE FECHAS

    /**
     * Solicita una fecha por consola.
     * @param mensajeFecha Mensaje que se muestra al pedir la fecha.
     * @return Un objeto LocalDate con la fecha introducida.
     */
    public static LocalDate leerFecha(String mensajeFecha) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(mensajeFecha);
        System.out.print("  Dia  : ");
        int dia = scanner.nextInt();
        System.out.print("  Mes  : ");
        int mes = scanner.nextInt();
        System.out.print("  Año : ");
        int año = scanner.nextInt();
        return LocalDate.of(año, mes, dia);
    }

    /**
     * Valida las fechas de la reserva siguiendo las siguientes normas:
     * la entrada no puede ser pasada y la salida debe ser posterior a la entrada.
     *
     * @param fechaEntrada Fecha de inicio.
     * @param fechaSalida  Fecha de fin.
     * @throws IllegalArgumentException Si las fechas no son validas.
     */
    public static void validarFecha(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "La fecha de entrada no puede ser anterior a la fecha actual.");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException(
                "La fecha de salida debe ser posterior a la fecha de entrada.");
        }
    }
}
