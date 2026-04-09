package hotel.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gema natalia salcedo
 *
 * Clase central que gestiona las colecciones de clientes y reservas del hotel.
 *
 * JUSTIFICACION DE LA ELECCION DE COLECCIONES:
 *
 * CLIENTES: LinkedHashSet<Cliente>
 *   - Se elige Set porque el enunciado exige que no existan dos clientes
 *     con el mismo DNI. Set garantiza unicidad automaticamente usando
 *     equals() y hashCode(), que en Cliente estan basados en el DNI.
 *   - Se elige LinkedHashSet (en lugar de HashSet) para preservar el orden
 *     de insercion, lo que hace los listados mas predecibles y faciles de
 *     depurar durante el desarrollo.
 *
 * RESERVAS: ArrayList<Reserva>
 *   - Un mismo cliente puede tener varias reservas: se necesitan duplicados.
 *   - Se necesita acceso por posicion para recorridos y ordenaciones con
 *     Collections.sort(), que requiere List.
 *   - ArrayList es la implementacion de List mas eficiente para acceso
 *     aleatorio y para iterar. 
 */
public class GestionHotel {

    // Coleccion de clientes: LinkedHashSet para unicidad por DNI y orden de insercion
    private Set<Cliente>  clientes;

    // Coleccion de reservas: ArrayList para permitir multiples reservas por cliente
    private List<Reserva> reservas;

    /**
     * Crea una nueva instancia de GestionHotel con las colecciones vacias.
     */
    public GestionHotel() {
        clientes = new LinkedHashSet<>();
        reservas = new ArrayList<>();
    }

    /**
     * Intenta agregar un cliente al Set.
     * Si ya existe un cliente con el mismo DNI, Set lo rechaza automaticamente
     * (gracias a equals/hashCode por DNI) y el metodo devuelve false.
     *
     * @param cliente El cliente a agregar.
     * @return true si se agrego correctamente, false si ya existia ese DNI.
     */
    public boolean agregarCliente(Cliente cliente) {
        return clientes.add(cliente); // add() devuelve false si ya estaba (equals/hashCode)
    }

    /**
     * Busca un cliente por su DNI.
     *
     * @param dni El DNI a buscar.
     * @return El objeto Cliente encontrado o null si no existe.
     */
    public Cliente buscarClientePorDni(String dni) {
        for (Cliente cliente : clientes) {
            if (cliente.getDni().equalsIgnoreCase(dni)) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Busca un cliente por su codigo numerico.
     *
     * @param codigo El codigo del cliente.
     * @return El objeto Cliente encontrado, null si no existe.
     */
    public Cliente buscarClientePorCodigo(int codigo) {
        for (Cliente cliente : clientes) {
            if (cliente.getCodigo() == codigo) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Elimina un cliente del sistema.
     *
     * ITERATOR recorre la coleccion de reservas y elimina de forma
     * segura todas las reservas del cliente antes de eliminar al cliente.
     * 
     * Si el cliente tiene reservas, primero pregunta si debe
     * eliminarlas tambien.
     *
     * @param dni DNI del cliente a eliminar.
     * @param eliminarReservas Si es true, elimina tambien todas sus reservas.
     * @return Mensaje con el resultado de la operacion.
     */
    public String eliminarCliente(String dni, boolean eliminarReservas) {
        Cliente cliente = buscarClientePorDni(dni);
        if (cliente == null) {
            return "Error: no existe ningun cliente con DNI: " + dni;
        }

        List<Reserva> reservasCliente = obtenerReservasPorDni(dni);

        if (!reservasCliente.isEmpty() && !eliminarReservas) {
            return "AVISO: El cliente tiene " + reservasCliente.size() +
                   " reservas activas. No se puede eliminar sin eliminar antes sus reservas.";
        }

        // Eliminar reservas del cliente usando Iterator
        if (eliminarReservas) {
            Iterator<Reserva> iteradorReservas = reservas.iterator();
            while (iteradorReservas.hasNext()) {
                Reserva r = iteradorReservas.next();
                if (r.getDniCliente().equalsIgnoreCase(dni)) {
                    iteradorReservas.remove(); 
                }
            }
        }

        // Eliminar el cliente del Set
        clientes.remove(cliente);
        return "Cliente " + cliente.getNombre() + " (DNI: " + dni + ") eliminado correctamente.";
    }

    /**
     * Indica si hay reservas asociadas a un DNI de cliente.
     *
     * @param dni El DNI del cliente.
     * @return true si tiene al menos una reserva.
     */
    public boolean clienteTieneReservas(String dni) {
        for (Reserva r : reservas) {
            if (r.getDniCliente().equalsIgnoreCase(dni)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve una cadena con el codigo y nombre de todos los clientes
     * en el orden en que estan almacenados (LinkedHashSet).
     * @return Cadena con la lista resumida de clientes.
     * 
     */
    public String listaResumidaClientes() {
        if (clientes.isEmpty()) {
            return "(No hay clientes registrados)";
        }
        StringBuilder sb = new StringBuilder();
        for (Cliente cliente : clientes) {
            sb.append(String.format("  [%d] %-20s DNI: %s%n",
                    cliente.getCodigo(), cliente.getNombre(), cliente.getDni()));
        }
        return sb.toString();
    }

    /**
     * Devuelve el listado completo de clientes ordenado alfabeticamente por nombre.
     * Usa Collections.sort() que se apoya en el compareTo() de Cliente.
     *
     * @return Cadena formateada con todos los clientes en orden alfabetico.
     * 
     */
    public String listarClientesOrdenados() {
        if (clientes.isEmpty()) {
            return "  (No hay clientes registrados)\n";
        }
        // Copiamos a una lista para poder ordenar
        List<Cliente> lista = new ArrayList<>(clientes);
        Collections.sort(lista); // usa compareTo() de Cliente: orden alfabetico por nombre
        StringBuilder sb = new StringBuilder();
        sb.append(Cliente.cabecera()).append("\n");
        sb.append("-".repeat(80)).append("\n");
        for (Cliente cliente : lista) {
            sb.append(cliente.mostrarInformacion()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Agrega una reserva a la lista.
     * Un cliente puede tener multiples reservas.
     *
     * @param reserva La reserva que se agrega.
     */
    public void agregarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    /**
     * Devuelve la lista de reservas de un cliente identificado por su DNI,
     * ordenadas por fecha de entrada (ascendente).
     *
     * @param dni DNI del cliente.
     * @return Lista de reservas del cliente ordenadas por fecha de entrada.
     */
    public List<Reserva> obtenerReservasPorDni(String dni) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getDniCliente().equalsIgnoreCase(dni)) {
                resultado.add(r);
            }
        }
        Collections.sort(resultado, Reserva.POR_FECHA_ENTRADA); // ordena por fecha entrada asc
        return resultado;
    }

    /**
     * Devuelve el listado de reservas de un cliente
     * ordenadas por fecha de entrada.
     *
     * @param dni DNI del cliente cuyas reservas se quieren listar.
     * @return Cadena con las reservas, o un mensaje si no tiene ninguna.
     */
    public String listarReservasCliente(String dni) {
        List<Reserva> lista = obtenerReservasPorDni(dni);
        if (lista.isEmpty()) {
            return "El cliente no tiene reservas.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Reserva.cabecera()).append("\n");
        sb.append("-".repeat(70)).append("\n");
        for (Reserva reserva : lista) {
            sb.append(reserva.mostrarDetalles()).append("\n");
        }
        return sb.toString();
    }

    // ESTADISTICAS (Collections.max / Collections.min)

    /**
     * Devuelve las estadisticas globales del hotel:
     *  - Reserva con mayor importe  (Collections.max con Comparable de Reserva)
     *  - Reserva con menor importe  (Collections.min con Comparable de Reserva)
     *  - Tipo de habitacion mas solicitado
     *
     * @return Cadena con las estadisticas o  un mensaje si no hay reservas.
     */
    public String obtenerEstadisticas() {
        if (reservas.isEmpty()) {
            return "No hay reservas registradas para calcular estadisticas.";
        }

        // Collections.max/min usan el compareTo() de Reserva (por coste total)
        Reserva maxImporte = Collections.max(reservas);
        Reserva minImporte = Collections.min(reservas);

        // Tipo de habitacion mas solicitado: contar manualmente
        int contadorDoble = 0;
        int contadorSuite = 0;
        for (Reserva r : reservas) {
            if (r.getTipoHabitacion() == TipoHabitacion.DOBLE) {
                contadorDoble++;
            } else {
                contadorSuite++;
            }
        }
        String tipoMasSolicitado = (contadorDoble >= contadorSuite) ? "DOBLE" : "SUITE";

        StringBuilder sb = new StringBuilder();
        sb.append("Total de reservas registradas : ").append(reservas.size()).append("\n");
        sb.append("\n");
        sb.append("Reserva con MAYOR importe:\n");
        sb.append("  ").append(Reserva.cabecera()).append("\n");
        sb.append("  ").append(maxImporte.mostrarDetalles()).append("\n");
        sb.append("\n");
        sb.append("Reserva con MENOR importe:\n");
        sb.append("  ").append(Reserva.cabecera()).append("\n");
        sb.append("  ").append(minImporte.mostrarDetalles()).append("\n");
        sb.append("\n");
        sb.append(String.format("Tipo de habitacion mas solicitado : %s  (Doble: %d, Suite: %d)%n",
                tipoMasSolicitado, contadorDoble, contadorSuite));
        return sb.toString();
    }

    // DATOS DE PRUEBA

    /**
     * Carga clientes y reservas de prueba con datos fijos.
     * Permite probar rapidamente la aplicacion sin introducir datos a mano.
     * Intenta cargar 3 clientes y 6 reservas; si ya existe algun DNI, lo omite.
     */
    public void cargarDatosPrueba() {
        Cliente c1 = new Cliente("Ana Garcia Lopez",  "87654321K", "666777888", "ana.garcia@email.com");
        Cliente c2 = new Cliente("Gema Salcedo Lopez-Castillo", "12345678Z", "654321987", "");
        Cliente c3 = new Cliente("Maria Soto Vega",   "11223344H", "699112233", "m.soto@correo.es");

        agregarCliente(c1);
        agregarCliente(c2);
        agregarCliente(c3);

        // Recuperamos los objetos reales del Set (pueden ser los recien insertados
        // o los ya existentes si habia duplicado de DNI)
        Cliente reserva1 = buscarClientePorDni("87654321K");
        Cliente reserva2 = buscarClientePorDni("12345678Z");
        Cliente reserva3 = buscarClientePorDni("11223344H");

        if (reserva1 != null) {
            agregarReserva(new Reserva(reserva1, LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 15),
                    TipoHabitacion.DOBLE, false, "A213"));
            agregarReserva(new Reserva(reserva1, LocalDate.of(2026, 6, 1),  LocalDate.of(2026, 6, 12),
                    TipoHabitacion.SUITE, true,  "B501"));
        }
        if (reserva2 != null) {
            agregarReserva(new Reserva(reserva2, LocalDate.of(2026, 4, 5),  LocalDate.of(2026, 4, 8),
                    TipoHabitacion.DOBLE, true,  "C314"));
            agregarReserva(new Reserva(reserva2, LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25),
                    TipoHabitacion.SUITE, false, "A705"));
        }
        if (reserva3 != null) {
            agregarReserva(new Reserva(reserva3, LocalDate.of(2026, 5, 15), LocalDate.of(2026, 5, 18),
                    TipoHabitacion.DOBLE, false, "B222"));
            agregarReserva(new Reserva(reserva3, LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 22),
                    TipoHabitacion.SUITE, true,  "C633"));
        }
    }
}
