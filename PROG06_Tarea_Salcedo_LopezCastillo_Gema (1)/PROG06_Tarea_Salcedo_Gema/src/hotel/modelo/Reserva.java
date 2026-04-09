package hotel.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * @author gema natalia salcedo
 *
 * Clase que representa una reserva del hotel.
 *
 * 
 */
public class Reserva implements Comparable<Reserva> {

    private int codigoReserva;
    private Cliente cliente;           // referencia completa al cliente
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private TipoHabitacion tipoHabitacion;
    private boolean camaSupletoria;
    private String numeroHabitacion;
    private double costeTotal;

    private static int contadorReserva = 0;

    private static final double PRECIO_DOBLE    = 50.0;
    private static final double PRECIO_SUITE    = 100.0;
    private static final double RECARGO_CAMA    = 20.0;
    private static final double DESCUENTO_LARGA = 0.90;

    private static int obtenerCodigoReserva() {
        return ++contadorReserva;
    }

    /**
     * Comparator por fecha de entrada ascendente.
     * Usado para ordenar el listado de reservas de un cliente (opcion 4).
     */
    public static final Comparator<Reserva> POR_FECHA_ENTRADA =
            Comparator.comparing(Reserva::getFechaEntrada);

    /**
     * Comparator por importe total descendente (mayor a menor).
     * Disponible para posibles ordenaciones por precio.
     */
    public static final Comparator<Reserva> POR_IMPORTE_DESC =
            Comparator.comparingDouble(Reserva::getCosteTotal).reversed();

    /**
     * Crea una nueva reserva asociada a un objeto Cliente real.
     *
     * @param cliente          El cliente que realiza la reserva.
     * @param fechaEntrada     Fecha de inicio de la estancia.
     * @param fechaSalida      Fecha de fin de la estancia.
     * @param tipoHabitacion   Tipo de habitacion elegido.
     * @param camaSupletoria   Si se solicita cama supletoria.
     * @param numeroHabitacion Codigo de habitacion
     * @throws IllegalArgumentException Si el numero de habitacion no es valido.
     */
    public Reserva(Cliente cliente, LocalDate fechaEntrada, LocalDate fechaSalida,
                   TipoHabitacion tipoHabitacion, boolean camaSupletoria,
                   String numeroHabitacion) {
        if (!validarNumeroHabitacion(numeroHabitacion)) {
            throw new IllegalArgumentException(
                "Numero de habitacion no valido: '" + numeroHabitacion +
                "'. Formato: [A-C][1-7][0-9][0-9]  (ej: A122, B307, C415).");
        }
        this.codigoReserva = obtenerCodigoReserva();
        this.cliente  = cliente;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.tipoHabitacion = tipoHabitacion;
        this.camaSupletoria = camaSupletoria;
        this.numeroHabitacion = numeroHabitacion.toUpperCase();
        this.costeTotal = calcularCosteTotal();
    }

    /**
     * Orden de reservas: coste total ascendente.
     * Permite usar Collections.max() y Collections.min() en estadisticas
     * sin necesidad de pasar un Comparator externo.
     */
    @Override
    public int compareTo(Reserva otra) {
        return Double.compare(this.costeTotal, otra.costeTotal);
    }

    /**
     * Valida el codigo de habitacion con expresion regular.
     *
     * Patron: ^[ABC][1-7][0-9][0-9]$
     *   [ABC]  -> torre: solo A, B o C.
     *   [1-7]  -> planta: del 1 al 7.
     *   [0-9]  -> tercer caracter: cualquier digito.
     *   [0-9]  -> cuarto caracter: cualquier digito.
     *
     * Validos:   A122, B709, C333
     * Invalidos: D123 (torre), A812 (planta), C31a (letra en digito)
     *
     * @param numero El codigo a validar.
     * @return true si es correcto, false en caso contrario.
     */
    public static boolean validarNumeroHabitacion(String numero) {
        if (numero == null) return false;
        return numero.toUpperCase().matches("^[ABC][1-7][0-9][0-9]$");
    }

    // CALCULO DEL COSTE DE LA ESTANCIA

    private double calcularCosteTotal() {
        long dias = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        double total = (tipoHabitacion == TipoHabitacion.SUITE)
                       ? PRECIO_SUITE * dias
                       : PRECIO_DOBLE * dias;
        if (camaSupletoria) total += RECARGO_CAMA * dias;
        if (dias > 7)       total *= DESCUENTO_LARGA;
        return total;
    }

    /**
     * Devuelve una linea con los datos de la reserva en columnas de ancho fijo.
     */
    public String mostrarDetalles() {
        String cama = camaSupletoria ? "Si" : "No";
        return String.format("%-6d %-5s %-12s %-12s %-8s %-5s %8.2f EUR",
                codigoReserva, numeroHabitacion, fechaEntrada, fechaSalida,
                tipoHabitacion, cama, costeTotal);
    }

    /**
     * Cabecera de columnas para el listado de reservas.
     */
    public static String cabecera() {
        return String.format("%-6s %-5s %-12s %-12s %-8s %-5s %12s",
                "CodRes", "Hab", "Entrada", "Salida", "Tipo", "Cama", "Importe");
    }

    // GETTERS Y SETTERS

    public int getCodigoReserva(){ 
        return codigoReserva; 
    }
    public Cliente getCliente(){
        return cliente; 
    }
    public int getCodigoCliente(){ 
        return cliente.getCodigo();
    }
    public String getDniCliente(){ 
        return cliente.getDni(); 
    }
    public LocalDate getFechaEntrada(){ 
        return fechaEntrada; 
    }
    public LocalDate getFechaSalida(){ 
        return fechaSalida;
    }
    public TipoHabitacion getTipoHabitacion(){ 
        return tipoHabitacion; 
    }
    public boolean isCamaSupletoria(){ 
        return camaSupletoria; 
    }
    public String getNumeroHabitacion(){ 
        return numeroHabitacion; 
    }
    public double getCosteTotal(){
        return costeTotal; 
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        if (fechaEntrada.isBefore(fechaSalida))
            this.fechaEntrada = fechaEntrada;
        else
            throw new IllegalArgumentException("La fecha de entrada debe ser anterior a la de salida.");
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        if (fechaSalida.isAfter(fechaEntrada))
            this.fechaSalida = fechaSalida;
        else
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
    }

    public void setNumeroHabitacion(String numeroHabitacion) {
        if (!validarNumeroHabitacion(numeroHabitacion))
            throw new IllegalArgumentException("Numero de habitacion no valido: " + numeroHabitacion);
        this.numeroHabitacion = numeroHabitacion.toUpperCase();
    }
}
