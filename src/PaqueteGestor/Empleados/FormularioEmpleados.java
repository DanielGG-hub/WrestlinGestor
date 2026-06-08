package PaqueteGestor.Empleados;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FormularioEmpleados extends JDialog{

		private JTextField txtNombre;
		private JTextField txtApellido;
		private JTextField txtEmail;
		private JTextField txtTelefono;
		
		private VentanaEmpleados ventanaPadre;
		private VentanaEmpleados.Empleado empleado;
		
		public FormularioEmpleados(VentanaEmpleados padre, VentanaEmpleados.Empleado empleado) {
		    super(padre, true);
		    
		    this.ventanaPadre = padre;
		    this.empleado = empleado; 

		    setTitle(empleado == null ? "Nuevo Empleado" : "Editar Empleado");
		    setSize(400,300);
		    setLocationRelativeTo(padre);
		    setLayout(new GridLayout(5,2,10,10));
		    
		    add(new JLabel("Nombre Empleado:"));
		    txtNombre = new JTextField();
		    add(txtNombre);
		    
		    add(new JLabel("Apellido Empleado:"));
		    txtApellido = new JTextField();
		    add(txtApellido);
		    
		    add(new JLabel("Email Empleado:"));
		    txtEmail = new JTextField();
		    add(txtEmail);
		    
		    add(new JLabel("Teléfono Empleado:"));
		    txtTelefono = new JTextField();
		    add(txtTelefono);
		    
		    JButton btnGuardar = new JButton("Guardar");
		    JButton btnCancelar = new JButton("Cancelar");

		    add(btnGuardar);
		    add(btnCancelar);  
		        
		    if (empleado != null) {
		        txtNombre.setText(empleado.nombre);
		        txtApellido.setText(empleado.apellido);
		        txtEmail.setText(empleado.email);
		        txtTelefono.setText(empleado.telefono);
		    }
		    
		    btnGuardar.addActionListener(e -> guardarEmpleado());
		    btnCancelar.addActionListener(e -> dispose());
		}

	
		private void guardarEmpleado() {
			String nombre = txtNombre.getText().trim();
	        String apellido = txtApellido.getText().trim();
	        String email = txtEmail.getText().trim();		
	        String telefono = txtTelefono.getText().trim();		
	    
	        if (nombre.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
	            return;
	        } else if (apellido.isEmpty()) {
	        	JOptionPane.showMessageDialog(this, "El apellido es obligatorio");
	            return;
	        } else if (email.isEmpty()) {
	        	JOptionPane.showMessageDialog(this, "El email es obligatorio");
	            return;
	        }
	        
	        try (Connection conexion = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula")) {

	            if (empleado == null) {
	                String sql = "INSERT INTO Personas(nombre, apellido, email, telefono) VALUES (?, ?, ?, ?)";
	                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
	                    ps.setString(1, nombre);
	                    ps.setString(2, apellido);
	                    ps.setString(3, email);
	                    ps.setString(4, telefono);
	                    ps.executeUpdate();
	                }
	                JOptionPane.showMessageDialog(this, "Empleado insertado");
	            } else {
	                String sql = "UPDATE Personas SET nombre=?, apellido=?, email=?, telefono=? WHERE idPersona=?";
	                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
	                	 ps.setString(1, nombre);
		                    ps.setString(2, apellido);
		                    ps.setString(3, email);
		                    ps.setString(4, telefono);
		                    ps.setInt(5, empleado.idPersona);
		                    ps.executeUpdate();
	                }
	                JOptionPane.showMessageDialog(this, "Empleado actualizado");
	            }
	            dispose();

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
	        }
	    }


		}
		

