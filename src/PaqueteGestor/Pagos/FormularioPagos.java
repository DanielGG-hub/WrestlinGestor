package PaqueteGestor.Pagos;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import PaqueteGestor.Contratos.VentanaContratos;

public class FormularioPagos extends JDialog{

		private JTextField txtEmailCliente;
		private JTextField txtNombreProyecto;
	    private JTextField txtFechapago;
	    private JTextField txtMonto;
	    private JComboBox<String> comboEstadoPago;

	    private VentanaPagos ventanaPadre;
	    private VentanaPagos.Pago pago;

	    public FormularioPagos(VentanaPagos padre, VentanaPagos.Pago pago) {
	        super(padre, true);

	        this.ventanaPadre = padre;
	        this.pago = pago;

	        setTitle(pago == null ? "Nuevo Pago" : "Editar Pago");
	        setSize(550, 350);
	        setLocationRelativeTo(padre);

	        setLayout(new GridBagLayout());
	        //Creo este objeto para organizar los componentes
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(6, 6, 6, 6);
	        // Hago que los campos deban rellenar todo el espacio horizontal
	        gbc.fill = GridBagConstraints.HORIZONTAL;

	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        add(new JLabel("Cliente:"), gbc);

	        gbc.gridx = 1;
	        gbc.gridy = 0;
	        gbc.weightx = 1.0; 
	        txtEmailCliente = new JTextField();
	        add(txtEmailCliente, gbc);
	        
	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        add(new JLabel("Proyecto:"), gbc);

	        gbc.gridx = 1;
	        gbc.gridy = 1;
	        txtNombreProyecto = new JTextField();
	        add(txtNombreProyecto, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = 2;
	        add(new JLabel("Fecha del Pago(YYYY-MM-DD:"), gbc);

	        gbc.gridx = 1;
	        gbc.gridy = 2;
	        txtFechapago = new JTextField();
	        add(txtFechapago, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = 3;
	        add(new JLabel("Monto a pagar:"), gbc);

	        gbc.gridx = 1;
	        gbc.gridy = 3;
	        txtMonto = new JTextField();
	        add(txtMonto, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = 4;
	        add(new JLabel("Estado del pago:"), gbc);

	        comboEstadoPago = new JComboBox<>(new String[] {"sin realizar", "completado"});
	        gbc.gridx = 1;
	        gbc.gridy = 4;
	        add(comboEstadoPago, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = 5;
	        gbc.gridwidth = 2;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.fill = GridBagConstraints.NONE;

	        JButton btnGuardar = new JButton("Guardar");
	        JButton btnCancelar = new JButton("Cancelar");

	   
	        javax.swing.JPanel panelBotones = new javax.swing.JPanel();
	        panelBotones.add(btnGuardar);
	        panelBotones.add(btnCancelar);
	        add(panelBotones, gbc);

	        if (pago != null) {
	        	txtEmailCliente.setText(obtenerEmailClientePorId(pago.idCliente));
	        	txtNombreProyecto.setText(obtenerNombreProyectoPorId(pago.idProyecto));
	            txtFechapago.setText(pago.fechaPago != null ? pago.fechaPago.toString() : "");
	            txtMonto.setText(Double.toString(pago.monto));
	            comboEstadoPago.setSelectedItem(pago.estadoPago);
	        }

	        btnGuardar.addActionListener(e -> guardarPago());
	        btnCancelar.addActionListener(e -> dispose());

	        }
	  
	    private void guardarPago() {
	    	String emailCliente = txtEmailCliente.getText().trim();
	    	String nombreProyecto = txtNombreProyecto.getText().trim();

	    	if (emailCliente.isEmpty() || nombreProyecto.isEmpty()) {
	    	    JOptionPane.showMessageDialog(this, "Debes ingresar el email del cliente y el nombre del proyecto.");
	    	    return;
	    	}

	    	int idCliente = obtenerIdClientePorEmail(emailCliente);
	    	if (idCliente == -1) {
	    	    JOptionPane.showMessageDialog(this, "No se encontró un cliente con ese email.");
	    	    return;
	    	}

	    	int idProyecto = obtenerIdProyectoPorNombre(nombreProyecto);
	    	if (idProyecto == -1) {
	    	    JOptionPane.showMessageDialog(this, "No se encontró un proyecto con ese nombre.");
	    	    return;
	    	}

	        String fechaPago = txtFechapago.getText();
	        String monto = txtMonto.getText();
	        String estadoPago = (String) comboEstadoPago.getSelectedItem();

	        if (fechaPago.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Debe haber una fecha del pago");
	            return;
	        } else if(monto.isEmpty()) {
	        	 JOptionPane.showMessageDialog(this, "Debe haber un monto a pagar");
		         return;
	        } else if(estadoPago.isEmpty()) {
	        	JOptionPane.showMessageDialog(this, "Debes indicar en que estado se encuentra el pago (realizado, en espera etc");
		         return;
	        }

	        try (Connection conexion = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula")) {

	            if (pago == null) {
	                String sql = "INSERT INTO Pagos(idCliente, idProyecto, fechaPago, monto, estadoPago) VALUES (?, ?, ?, ?, ?)";
	                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
	                    ps.setInt(1, idCliente);
	                    ps.setInt(2, idProyecto);
	                    ps.setDate(3, fechaPago.isEmpty() ? null : Date.valueOf(fechaPago));
	                    ps.setDouble(4, Double.parseDouble(monto));
	                    ps.setString(5, estadoPago);
	                    ps.executeUpdate();
	                }
	            } else {
	                String sql = "UPDATE Pagos SET idCliente=?, idProyecto=?, fechaPago=?, monto=?, estadoPago=? WHERE idPago=?";
	                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
	                    ps.setInt(1, idCliente);
	                    ps.setInt(2, idProyecto);
	                    ps.setDate(3, fechaPago.isEmpty() ? null : Date.valueOf(fechaPago));
	                    ps.setDouble(4, Double.parseDouble(monto));
	                    ps.setString(5, estadoPago);
	                    ps.setInt(6, pago.idPago);
	                    ps.executeUpdate();
	                }
	            }

	            JOptionPane.showMessageDialog(this, "Pago guardado correctamente");
	            dispose();

	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Error al guardar el pago: " + e.getMessage());
	        } catch (NumberFormatException e) {
	            JOptionPane.showMessageDialog(this, "El monto debe ser un número válido");
	        }
	    }
	  
	    private int obtenerIdClientePorEmail(String email) {
	        try (Connection conexion = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	             PreparedStatement ps = conexion.prepareStatement("SELECT idCliente FROM Clientes WHERE email = ?")) {
	            ps.setString(1, email);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return rs.getInt("idCliente");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Error al buscar el cliente: " + e.getMessage());
	        }
	        return -1;
	    }

	    private int obtenerIdProyectoPorNombre(String nombreProyecto) {
	        try (Connection conexion = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	             PreparedStatement ps = conexion.prepareStatement("SELECT idProyecto FROM Proyectos WHERE nombreProyecto = ?")) {
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

	    private String obtenerEmailClientePorId(int idCliente) {
	        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	             PreparedStatement ps = conexion.prepareStatement("SELECT email FROM Clientes WHERE idCliente = ?")) {
	            ps.setInt(1, idCliente);
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
	        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	             PreparedStatement ps = conexion.prepareStatement("SELECT nombreProyecto FROM Proyectos WHERE idProyecto = ?")) {
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

