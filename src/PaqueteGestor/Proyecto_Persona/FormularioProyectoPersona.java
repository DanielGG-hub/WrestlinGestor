package PaqueteGestor.Proyecto_Persona;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FormularioProyectoPersona extends JDialog {

	private JTextField txtNombreProyecto;
	private JTextField txtEmailEmpleado;
	private JTextField txtRol;

	private VentanaProyectoPersona ventanaPadre;
	private VentanaProyectoPersona.Proyecto proyecto;

	public FormularioProyectoPersona(VentanaProyectoPersona padre, VentanaProyectoPersona.Proyecto proyecto) {
		super(padre, true);

		this.ventanaPadre = padre;
		this.proyecto = proyecto;

		setTitle(proyecto == null ? "Nuevo Proyecto" : "Editar Proyecto");
		setSize(550, 350);
		setLocationRelativeTo(padre);
		setLayout(new GridLayout(5, 2, 10, 10));

		add(new JLabel("Nombre Proyecto:"));
		txtNombreProyecto = new JTextField();
		add(txtNombreProyecto);

		add(new JLabel("Correo empleado:"));
		txtEmailEmpleado = new JTextField();
		add(txtEmailEmpleado);

		add(new JLabel("Rol del empleado:"));
		txtRol = new JTextField();
		add(txtRol);

		JButton btnGuardar = new JButton("Guardar");
		JButton btnCancelar = new JButton("Cancelar");

		javax.swing.JPanel panelBotones = new javax.swing.JPanel();
		panelBotones.add(btnGuardar);
		panelBotones.add(btnCancelar);
		add(panelBotones);

		if (proyecto != null) {
			txtNombreProyecto.setText(obtenerNombreProyectoPorId(proyecto.idProyecto));
			txtEmailEmpleado.setText(obtenerEmailClientePorId(proyecto.idPersona));
			txtRol.setText(proyecto.rol);
		}

		btnGuardar.addActionListener(e -> guardarProyectoPersona());
		btnCancelar.addActionListener(e -> dispose());

	}

	private void guardarProyectoPersona() {
		String nombreProyecto = txtNombreProyecto.getText().trim();
		String emailEmpleado = txtEmailEmpleado.getText().trim();

		if (emailEmpleado.isEmpty() || nombreProyecto.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Debes ingresar el email del empleado y el nombre del proyecto.");
			return;
		}

		int idPersona = obtenerIdClientePorEmail(emailEmpleado);
		if (idPersona == -1) {
			JOptionPane.showMessageDialog(this, "No se encontró un empleado con ese email.");
			return;
		}

		int idProyecto = obtenerIdProyectoPorNombre(nombreProyecto);
		if (idProyecto == -1) {
			JOptionPane.showMessageDialog(this, "No se encontró un proyecto con ese nombre.");
			return;
		}

		String rol = txtRol.getText();

		if (rol.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Debe haber una rol del empleado");
			return;
		}

		try (Connection conexion = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula")) {

			if (proyecto == null) {
				String sql = "INSERT INTO Proyecto_Persona(idProyecto, idPersona, rol) VALUES (?, ?, ?)";
				try (PreparedStatement ps = conexion.prepareStatement(sql)) {
					ps.setInt(1, idProyecto);
					ps.setInt(2, idPersona);
					ps.setString(3, rol);
					ps.executeUpdate();
				}
			} else {
				String sql = "UPDATE ProyectoPersona SET idProyecto=?, idPersona=?, rol=?";
				try (PreparedStatement ps = conexion.prepareStatement(sql)) {
					ps.setInt(1, idProyecto);
					ps.setInt(2, idPersona);
					ps.setString(3, rol);
					ps.executeUpdate();
				}
			}

			JOptionPane.showMessageDialog(this, "Empleados asignados al proyecto guardados correctamente");
			dispose();

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al guardar el proyecto: " + e.getMessage());
		}
	}

	private int obtenerIdClientePorEmail(String email) {
		try (Connection conexion = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
				PreparedStatement ps = conexion.prepareStatement("SELECT idPersona FROM Personas WHERE email = ?")) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("idPersona");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al buscar el empleado: " + e.getMessage());
		}
		return -1;
	}

	private int obtenerIdProyectoPorNombre(String nombreProyecto) {
		try (Connection conexion = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
				PreparedStatement ps = conexion
						.prepareStatement("SELECT idProyecto FROM Proyectos WHERE nombreProyecto = ?")) {
			ps.setString(1, nombreProyecto);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("idProyecto");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al buscar el proyecto: " + e.getMessage());
		}
		return -1;
	}

	private String obtenerEmailClientePorId(int idPersona) {
		try (Connection conexion = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
				PreparedStatement ps = conexion.prepareStatement("SELECT email FROM Personas WHERE idPersona = ?")) {
			ps.setInt(1, idPersona);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String obtenerNombreProyectoPorId(int idProyecto) {
		try (Connection conexion = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
				PreparedStatement ps = conexion
						.prepareStatement("SELECT nombreProyecto FROM Proyectos WHERE idProyecto = ?")) {
			ps.setInt(1, idProyecto);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("nombreProyecto");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
