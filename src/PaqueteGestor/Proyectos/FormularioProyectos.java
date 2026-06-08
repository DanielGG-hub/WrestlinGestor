package PaqueteGestor.Proyectos;

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

public class FormularioProyectos extends JDialog {

	private JTextField txtNombre;
	private JTextField txtFechaEntrega;
	private JTextField txtFechaReunion;
	
	private VentanaProyectos ventanaPadre;
	private VentanaProyectos.Proyecto proyecto;
	
	public FormularioProyectos(VentanaProyectos padre, VentanaProyectos.Proyecto proyecto) {
		super(padre, true);
		
		setTitle(proyecto == null ? "Nuevo Proyecto" : "Editar Proyecto");
		setSize(400,300);
		setLocationRelativeTo(padre);
		setLayout(new GridLayout(5,2,10,10));
		
		// Creo toda la estructura de los campos que tienes que insertar
		
		add(new JLabel("Nombre Proyecto:"));
		txtNombre = new JTextField();
		add(txtNombre);
		
		add(new JLabel("Fecha Entrega (yyyy-mm-dd):"));
        txtFechaEntrega = new JTextField();
        add(txtFechaEntrega);

        add(new JLabel("Fecha Reunión (yyyy-mm-dd):"));
        txtFechaReunion = new JTextField();
        add(txtFechaReunion);
        
        // Creo los botones de guardar y de cancelar
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        add(btnGuardar);
        add(btnCancelar);     
        
        //Si estamos editando un proyecto rellenamos los campos con sus datos
        if (proyecto != null) { 
            txtNombre.setText(proyecto.nombreProyecto);
            txtFechaEntrega.setText(proyecto.fechaEntrega != null ? proyecto.fechaEntrega.toString() : "");
            txtFechaReunion.setText(proyecto.fechaReunion != null ? proyecto.fechaReunion.toString() : "");
        }
        btnGuardar.addActionListener(e -> guardarProyecto());
        btnCancelar.addActionListener(e -> dispose()); // Si le das a cancelar cierra la ventana directamente
    }

    private void guardarProyecto() { // Metodo para crear y actualizar proyectos
    	// Recojo los textos escritos por el usuario en variables
        String nombre = txtNombre.getText().trim();
        String fechaEntrega = txtFechaEntrega.getText().trim();
        String fechaReunion = txtFechaReunion.getText().trim();

        // Hago que sea obligatorio introducir el nombre del proyecto
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
            return;
        }

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula")) {

            if (proyecto == null) {
                // Insertar nuevo proyecto estableciendo las variables en los campos de la consultas
                String sql = "INSERT INTO Proyectos(nombreProyecto, fechaEntrega, fechaReunion) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setDate(2, fechaEntrega.isEmpty() ? null : Date.valueOf(fechaEntrega));
                    ps.setDate(3, fechaReunion.isEmpty() ? null : Date.valueOf(fechaReunion));
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Proyecto creado");
            } else {
                // Actualizar proyecto estableciendo las variables creadas anteriormente en la consulta
                String sql = "UPDATE Proyectos SET nombreProyecto=?, fechaEntrega=?, fechaReunion=? WHERE idProyecto=?";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setDate(2, fechaEntrega.isEmpty() ? null : Date.valueOf(fechaEntrega));
                    ps.setDate(3, fechaReunion.isEmpty() ? null : Date.valueOf(fechaReunion));
                    ps.setInt(4, proyecto.idProyecto);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Proyecto actualizado");
            }
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
}