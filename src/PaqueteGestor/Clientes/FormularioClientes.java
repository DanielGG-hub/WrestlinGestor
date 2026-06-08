package PaqueteGestor.Clientes;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import PaqueteGestor.Empleados.VentanaEmpleados;

public class FormularioClientes extends JDialog{

	private JTextField txtNombre;
	private JTextField txtEmail;
	private JTextField txtTelefono;
	
	private VentanaClientes ventanaPadre;
	private VentanaClientes.Cliente cliente;
	
	public FormularioClientes(VentanaClientes padre, VentanaClientes.Cliente cliente) {
	    super(padre, true);
	    
	    this.ventanaPadre = padre;
	    this.cliente = cliente; 

	    setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
	    setSize(400,300);
	    setLocationRelativeTo(padre);
	    setLayout(new GridLayout(5,2,10,10));
	    
	    add(new JLabel("Nombre Cliente:"));
	    txtNombre = new JTextField();
	    add(txtNombre);
	    
	    add(new JLabel("Email Cliente:"));
	    txtEmail = new JTextField();
	    add(txtEmail);
	    
	    add(new JLabel("Teléfono Cliente:"));
	    txtTelefono = new JTextField();
	    add(txtTelefono);
	    
	    JButton btnGuardar = new JButton("Guardar");
	    JButton btnCancelar = new JButton("Cancelar");

	    add(btnGuardar);
	    add(btnCancelar);  
	        
	    if (cliente != null) {
	        txtNombre.setText(cliente.nombreCliente);
	        txtEmail.setText(cliente.email);
	        txtTelefono.setText(cliente.telefono);
	    }
	    
	    btnGuardar.addActionListener(e -> guardarCliente());
	    btnCancelar.addActionListener(e -> dispose());
	}


	private void guardarCliente() {
		String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();		
        String telefono = txtTelefono.getText().trim();		
    
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
            return;
        } else if (email.isEmpty()) {
        	JOptionPane.showMessageDialog(this, "El email es obligatorio");
            return;
        } else if (telefono.isEmpty()) {
        	JOptionPane.showMessageDialog(this, "El teléfono es obligatorio");
            return;
        }
        
        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula")) {

            if (cliente == null) {
                String sql = "INSERT INTO Clientes(nombreCliente, email, telefono) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, email);
                    ps.setString(3, telefono);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Cliente insertado");
            } else {
                String sql = "UPDATE Clientes SET nombre=?, email=?, telefono=? WHERE idCliente=?";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                	 ps.setString(1, nombre);
	                    ps.setString(2, email);
	                    ps.setString(3, telefono);
	                    ps.setInt(4, cliente.idCliente);
	                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Cliente actualizado");
            }
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }


	}
	
