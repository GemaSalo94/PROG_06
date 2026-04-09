package hotel.modelo;

import java.util.Objects;

/**
 * @author gema natalia salcedo
 *
 * Clase que representa a un cliente del hotel.
 *
 * 
 */
public class Cliente implements Comparable<Cliente> {

    private int    codigo;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;

    private static int contadorCliente = 0;

    private static int obtenerNumeroCliente() {
        return ++contadorCliente;
    }

    /**
     * Crea un nuevo cliente. El codigo se asigna automaticamente.
     *
     * @param nombre   Nombre completo del cliente.
     * @param dni      DNI del cliente (clave unica de negocio).
     * @param telefono Telefono de contacto.
     * @param email    Email (opcional, puede estar vacio).
     */
    public Cliente(String nombre, String dni, String telefono, String email) {
        this.codigo   = obtenerNumeroCliente();
        this.nombre   = nombre;
        this.dni      = dni.toUpperCase();
        this.telefono = telefono;
        this.email    = email;
    }

    /**
     * Dos clientes son iguales si tienen el mismo DNI
     * El LinkedHashSet usa este metodo para detectar y rechazar duplicados.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cliente)) return false;
        Cliente otro = (Cliente) obj;
        return this.dni.equalsIgnoreCase(otro.dni);
    }

    /**
     * Coherente con equals: el hash se calcula solo a partir del DNI en mayusculas.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dni.toUpperCase());
    }

    /**
     * Orden natural: alfabetico por nombre, sin distinguir mayusculas.
     * Usado por Collections.sort() al mostrar el listado de clientes (opcion 3 del menu).
     */
    @Override
    public int compareTo(Cliente otro) {
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }

    /**
     * Devuelve una linea con los datos del cliente
     */
    public String mostrarInformacion() {
        String emailMostrar = (email == null || email.trim().isEmpty()) ? "-" : email;
        return String.format("%-5d %-20s %-10s %-12s %-28s",
                codigo, nombre, dni, telefono, emailMostrar);
    }

    /**
     * Cabecera de columnas para el listado de clientes.
     */
    public static String cabecera() {
        return String.format("%-5s %-20s %-10s %-12s %-28s",
                "Cod", "Nombre", "DNI", "Telefono", "Email");
    }


    public int    getCodigo(){ 
        return codigo; 
    }
    public String getNombre(){
        return nombre; 
    }
    public void   setNombre(String nombre){ 
        this.nombre = nombre; 
    }
    public String getDni(){ 
        return dni; 
    }
    public String getTelefono(){
        return telefono; 
    }
    public void   setTelefono(String telefono){ 
        this.telefono = telefono; 
    }
    public String getEmail(){
        return email; 
    }
    public void   setEmail(String email){ 
        this.email = email; 
    }
}
