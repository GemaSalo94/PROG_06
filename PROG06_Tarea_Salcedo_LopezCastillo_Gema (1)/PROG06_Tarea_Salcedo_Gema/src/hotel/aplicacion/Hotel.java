package hotel.aplicacion;

import hotel.modelo.Cliente;
import hotel.modelo.GestionHotel;
import hotel.modelo.Reserva;
import hotel.modelo.TipoHabitacion;
import hotel.utilidades.Utilidades;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * @author gema natalia salcedo
 *
 * Clase principal de la aplicacion de gestion del hotel.
 * Contiene el metodo main y el menu de interaccion con el usuario.
 *
 */
public class Hotel {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GestionHotel gestion = new GestionHotel();
        
        int opcion = 0;

        do {
            System.out.println("**** MENU PRINCIPAL ****");
            System.out.println("1) Crear cliente");
            System.out.println("2) Eliminar cliente");
            System.out.println("3) Listado de clientes");
            System.out.println("4) Listado de reservas de cliente");
            System.out.println("5) Crear reserva");
            System.out.println("6) Estadisticas del hotel");
            System.out.println("7) Cargar datos de prueba");
            System.out.println("8) Salir");
            System.out.println("***********************************");
            System.out.print("Selecciona una opcion: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }
            System.out.println();

            switch (opcion) {
                case 1: // CREAR CLIENTE
                    System.out.println("--- CREAR CLIENTE ---");
                    System.out.print("Nombre    : ");
                    String nombre = scanner.nextLine();

                    // Validar DNI con un bucle hasta que sea correcto
                    String dni = "";
                    boolean dniValido = false;
                    while (!dniValido) {
                        System.out.print("DNI       : ");
                        dni = scanner.nextLine().trim().toUpperCase();
                        try {
                            Utilidades.validarDNI(dni);
                            dniValido = true;
                        } catch (IllegalArgumentException e) {
                            System.out.println("ERROR: " + e.getMessage() + " Intentalo de nuevo.");
                        }
                    }

                    System.out.print("Telefono  : ");
                    String telefono = scanner.nextLine().trim();

                    // Validar emai
                    String email = "";
                    boolean emailValido = false;
                    while (!emailValido) {
                        System.out.print("Email (opcional, Pulsa Enter para omitir): ");
                        email = scanner.nextLine().trim();
                        if (Utilidades.validarEmail(email)) {
                            emailValido = true;
                        } else {
                            System.out.println("Email con formato incorrecto. Intentalo de nuevo.");
                        }
                    }

                    Cliente nuevoCliente = new Cliente(nombre, dni, telefono, email);
                    // agregarCliente devuelve false si el DNI ya existe (Set.add())
                    if (gestion.agregarCliente(nuevoCliente)) {
                        System.out.println("Cliente creado correctamente. Codigo asignado: " +
                                nuevoCliente.getCodigo());
                    } else {
                        System.out.println("ERROR: Ya existe un cliente registrado con el DNI: " +
                                dni);
                    }
                    break;
                    
                case 2: // ELIMINAR CLIENTE
                    System.out.println("--- ELIMINAR CLIENTE ---");
                    System.out.println("Clientes registrados:");
                    System.out.println(gestion.listaResumidaClientes());

                    System.out.print("Introduce el DNI del cliente que desea eliminar: ");
                    String dniEliminar = scanner.nextLine().trim().toUpperCase();

                    Cliente clienteAEliminar = gestion.buscarClientePorDni(dniEliminar);
                    if (clienteAEliminar == null) {
                        System.out.println("ERROR: No existe ningun cliente con DNI: " + dniEliminar);
                        break;
                    }

                    // Comprobamos si tiene reservas y preguntar
                    boolean eliminarReservasCliente = false;
                    if (gestion.clienteTieneReservas(dniEliminar)) {
                        System.out.println("AVISO: El cliente tiene reservas activas.");
                        System.out.print("¿Deseas eliminar tambien todas sus reservas? (s/n): ");
                        String respuesta = scanner.nextLine().trim();
                        if (respuesta.equalsIgnoreCase("s")) {
                            eliminarReservasCliente = true;
                        } else {
                            System.out.println("Operacion cancelada. El cliente NO ha sido eliminado.");
                            break;
                        }
                    }

                    //La eliminacion con Iterator se produce dentro de GestionHotel
                    System.out.println(gestion.eliminarCliente(dniEliminar, eliminarReservasCliente));
                    break;

                case 3: //Listado de clientes ordenados
                    System.out.println("--- LISTADO DE CLIENTES (orden alfabetico por nombre) ---");
                    //GestionHotel usa Collections.sort() con compareTo() de Cliente
                    System.out.println(gestion.listarClientesOrdenados());
                    break;

                case 4: // LISTADO DE RESERVAS DE UN CLIENTE (por DNI)
                    System.out.println("--- LISTADO DE RESERVAS DE UN CLIENTE ---");
                    System.out.println("Clientes registrados:");
                    System.out.println(gestion.listaResumidaClientes());

                    System.out.print("Introduce el DNI del cliente: ");
                    String dniReservas = scanner.nextLine().trim().toUpperCase();

                    Cliente clienteReservas = gestion.buscarClientePorDni(dniReservas);
                    if (clienteReservas == null) {
                        System.out.println("ERROR: No existe ningun cliente con DNI " + dniReservas + ".");
                    } else {
                        System.out.println("Reservas de " + clienteReservas.getNombre() +
                                " (ordenadas por fecha de entrada):");
                        //GestionHotel ordena con Reserva.POR_FECHA_ENTRADA (Comparator)
                        System.out.println(gestion.listarReservasCliente(dniReservas));
                    }
                    break;
                case 5: // CREAR RESERVA
                    System.out.println("--- CREAR RESERVA ---");
                    System.out.println("Clientes registrados:");
                    System.out.println(gestion.listaResumidaClientes());

                    System.out.print("DNI del cliente para la reserva: ");
                    String dniReserva = scanner.nextLine().trim().toUpperCase();

                    Cliente clienteReserva = gestion.buscarClientePorDni(dniReserva);
                    if (clienteReserva == null) {
                        System.out.println("ERROR: No existe ningun cliente con DNI: " + dniReserva);
                        break;
                    }
                    System.out.println("Cliente encontrado: " + clienteReserva.getNombre());

                    //Pedir fechas
                    LocalDate entrada = null;
                    LocalDate salida  = null;
                    boolean fechasValidas = false;
                    while (!fechasValidas) {
                        try {
                            entrada = Utilidades.leerFecha("Fecha de entrada:");
                            salida  = Utilidades.leerFecha("Fecha de salida:");
                            Utilidades.validarFecha(entrada, salida);
                            fechasValidas = true;
                        } catch (Exception e) {
                            System.out.println("Error " + e.getMessage() + " Intentalo de nuevo.");
                        }
                    }

                    // Pedir tipo de habitacion
                    TipoHabitacion tipo = null;
                    while (tipo == null) {
                        System.out.print("Tipo de habitacion (DOBLE / SUITE): ");
                        try {
                            tipo = TipoHabitacion.valueOf(scanner.nextLine().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Escribe DOBLE o SUITE.");
                        }
                    }

                    // Pedir cama supletoria
                    System.out.print("¿Cama supletoria? (s/n): ");
                    boolean cama = scanner.nextLine().trim().equalsIgnoreCase("s");

                    // Pedir y validar numero de habitacion
                    String numHab = "";
                    boolean habitacionValida = false;
                    while (!habitacionValida) {
                        System.out.print("Numero de habitacion (ej: A122, B307): ");
                        numHab = scanner.nextLine().trim().toUpperCase();
                        if (Reserva.validarNumeroHabitacion(numHab)) {
                            habitacionValida = true;
                        } else {
                            System.out.println("Formato incorrecto: [A-C][1-7][0-9][0-9]. Intentalo de nuevo.");
                        }
                    }

                    try {
                        Reserva nuevaReserva = new Reserva(clienteReserva, entrada, salida,
                                tipo, cama, numHab);
                        gestion.agregarReserva(nuevaReserva);
                        System.out.println("Reserva creada correctamente. Codigo: " +
                                nuevaReserva.getCodigoReserva());
                        System.out.printf("Importe total: %.2f EUR%n", nuevaReserva.getCosteTotal());
                    } catch (IllegalArgumentException e) {
                        System.out.println("ERROR al crear la reserva: " + e.getMessage());
                    }
                    break;
                case 6: //ESTADISTICAS DEL HOTEL
                    System.out.println("--- ESTADISTICAS DEL HOTEL ---");
                    //GestionHotel usa Collections.max() y Collections.min()
                    System.out.println(gestion.obtenerEstadisticas());
                    break;

                case 7: //CARGAR DATOS DE PRUEBA
                    System.out.println("--- CARGANDO DATOS DE PRUEBA ---");
                    gestion.cargarDatosPrueba();
                    System.out.println("Datos de prueba cargados: 3 clientes y 6 reservas.");
                    System.out.println("(Los clientes con DNI duplicado se han omitido automaticamente)");
                    break;
                case 8: //SALIR
                    System.out.println("Cerrando la aplicacion...");
                    break;

                default:
                    System.out.println("Opcion no valida. Elige un numero del 1 al 8.");
            }

        } while (opcion != 8);

        scanner.close();
    }
}
