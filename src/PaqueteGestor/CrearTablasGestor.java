package PaqueteGestor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CrearTablasGestor {

	public void crearBaseDatos() {
		//Creo las variables para conectarme al sql
        String url = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
        String usuario = "root";
        String password = "aula";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Me conecto a mysql
            Connection conexion = DriverManager.getConnection(url, usuario, password);

            Statement stmt = conexion.createStatement();

            // (La aplicación al principio se llamaba WorkSuite y cambie de nombre con el codigo entero hecho ya)
            
            // Elimino la base de datos para que maneje el reseteo si ya estaba creada
            String sqlBorrarBD = "DROP DATABASE IF EXISTS WorkSuite";
            stmt.executeUpdate(sqlBorrarBD);

            // Creo la base de datos
            String sqlCrearBD = "CREATE DATABASE WorkSuite";
            stmt.executeUpdate(sqlCrearBD);

            // Uso la base de datos
            stmt.executeUpdate("USE WorkSuite");

            //Creo las tablas de la BBDD
            String crearProyectos = "CREATE TABLE IF NOT EXISTS Proyectos ("
                    + "idProyecto INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombreProyecto VARCHAR(100) NOT NULL,"
                    + "fechaEntrega DATE,"
                    + "fechaReunion DATE"
                    + ")";
            stmt.executeUpdate(crearProyectos);

            String crearPersonas = "CREATE TABLE IF NOT EXISTS Personas ("
                    + "idPersona INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(50) NOT NULL,"
                    + "apellido VARCHAR(50) NOT NULL,"
                    + "email VARCHAR(100),"
                    + "telefono VARCHAR(20)"
                    + ")";
            stmt.executeUpdate(crearPersonas);

            String crearContratos = "CREATE TABLE IF NOT EXISTS Contratos ("
                    + "idContrato INT AUTO_INCREMENT PRIMARY KEY,"
                    + "idPersona INT,"
                    + "tipoContrato VARCHAR(50),"
                    + "fechaInicio DATE,"
                    + "fechaFin DATE,"
                    + "condiciones TEXT,"
                    + "salario DECIMAL(10, 2),"
                    + "CONSTRAINT fk_persona FOREIGN KEY (idPersona) REFERENCES Personas(idPersona)"
                    + " ON DELETE CASCADE ON UPDATE CASCADE" // Me aseguro de que si se borra una persona tambien se borre su contrato automaticamente
                    + ")";
            stmt.executeUpdate(crearContratos);

            String crearClientes = "CREATE TABLE IF NOT EXISTS Clientes ("
                    + "idCliente INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombreCliente VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(100),"
                    + "telefono VARCHAR(20)"
                    + ")";
            stmt.executeUpdate(crearClientes);

            String crearPagos = "CREATE TABLE IF NOT EXISTS Pagos ("
                    + "idPago INT AUTO_INCREMENT PRIMARY KEY,"
                    + "idCliente INT,"
                    + "idProyecto INT,"
                    + "fechaPago DATE,"
                    + "monto DECIMAL(10,2),"
                    + "estadoPago VARCHAR(20),"
                    + "FOREIGN KEY (idCliente) REFERENCES Clientes(idCliente) ON DELETE CASCADE,"
                    + "FOREIGN KEY (idProyecto) REFERENCES Proyectos(idProyecto) ON DELETE CASCADE"
                    + ")";
            stmt.executeUpdate(crearPagos);

            String crearProyectoPersona = "CREATE TABLE IF NOT EXISTS Proyecto_Persona ("
                    + "idProyecto INT,"
                    + "idPersona INT,"
                    + "rol VARCHAR(50),"
                    + "PRIMARY KEY (idProyecto, idPersona),"
                    + "FOREIGN KEY (idProyecto) REFERENCES Proyectos(idProyecto) ON DELETE CASCADE,"
                    + "FOREIGN KEY (idPersona) REFERENCES Personas(idPersona) ON DELETE CASCADE"
                    + ")";
            stmt.executeUpdate(crearProyectoPersona);

            System.out.println("Base de datos creada con éxito.");

            // Cierro conexiones
            stmt.close();
            conexion.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
