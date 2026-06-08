package PaqueteGestor.Contratos;

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
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FormularioContratos extends JDialog {

    private JTextField txtTipoContrato;
    private JTextField txtfechaInicio;
    private JTextField txtfechaFin;
    private JTextField txtcondiciones;
    private JTextField txtSalario;
    private JTextField txtEmailEmpleado;


    private VentanaContratos ventanaPadre;
    private VentanaContratos.Contrato contrato;

    public FormularioContratos(VentanaContratos padre, VentanaContratos.Contrato contrato) {
        super(padre, true);

        this.ventanaPadre = padre;
        this.contrato = contrato;

        setTitle(contrato == null ? "Nuevo Contrato" : "Editar Contrato");
        setSize(550, 350);
        setLocationRelativeTo(padre);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email del empleado:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtEmailEmpleado = new JTextField();
        add(txtEmailEmpleado, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tipo Contrato:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtTipoContrato = new JTextField();
        add(txtTipoContrato, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        txtfechaInicio = new JTextField();
        add(txtfechaInicio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Fecha Fin (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        txtfechaFin = new JTextField();
        add(txtfechaFin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Condiciones:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        txtcondiciones = new JTextField();
        add(txtcondiciones, gbc);
        gbc.weightx = 0; // reset

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Salario:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        txtSalario = new JTextField();
        add(txtSalario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        javax.swing.JPanel panelBotones = new javax.swing.JPanel();
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, gbc);

        if (contrato != null) {
            try (Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                 PreparedStatement ps = conexion.prepareStatement("SELECT email FROM Personas WHERE idPersona = ?")) {
                ps.setInt(1, contrato.idPersona);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtEmailEmpleado.setText(rs.getString("email"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            txtTipoContrato.setText(contrato.tipoContrato);
            txtfechaInicio.setText(contrato.fechaInicio != null ? contrato.fechaInicio.toString() : "");
            txtfechaFin.setText(contrato.fechaFin != null ? contrato.fechaFin.toString() : "");
            txtcondiciones.setText(contrato.condiciones);
            txtSalario.setText(Double.toString(contrato.salario));
        }

        btnGuardar.addActionListener(e -> guardarContrato());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarContrato() {
    	String email = txtEmailEmpleado.getText().trim();
    	if (email.isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Debes introducir el email del empleado.");
    	    return;
    	}
    	int idPersona = obtenerIdPersonaDesdeEmail(email); 
    	if (idPersona == -1) {
    	    JOptionPane.showMessageDialog(this, "No se encontró ningún empleado con ese email.");
    	    return;
    	}

        String tipoContrato = txtTipoContrato.getText();
        String fechaInicio = txtfechaInicio.getText();
        String fechaFin = txtfechaFin.getText();
        String condiciones = txtcondiciones.getText();
        String salario = txtSalario.getText();

        if (tipoContrato.isEmpty() || salario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tipo de contrato y salario son obligatorios");
            return;
        }

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula")) {

            if (contrato == null) {
                String sql = "INSERT INTO Contratos(idPersona, tipoContrato, fechaInicio, fechaFin, condiciones, salario) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setInt(1, idPersona);
                    ps.setString(2, tipoContrato);
                    ps.setDate(3, fechaInicio.isEmpty() ? null : Date.valueOf(fechaInicio));
                    ps.setDate(4, fechaFin.isEmpty() ? null : Date.valueOf(fechaFin));
                    ps.setString(5, condiciones);
                    ps.setDouble(6, Double.parseDouble(salario));
                    ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE Contratos SET idPersona=?, tipoContrato=?, fechaInicio=?, fechaFin=?, condiciones=?, salario=? WHERE idContrato=?";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setInt(1, idPersona);
                    ps.setString(2, tipoContrato);
                    ps.setDate(3, fechaInicio.isEmpty() ? null : Date.valueOf(fechaInicio));
                    ps.setDate(4, fechaFin.isEmpty() ? null : Date.valueOf(fechaFin));
                    ps.setString(5, condiciones);
                    ps.setDouble(6, Double.parseDouble(salario));
                    ps.setInt(7, contrato.idContrato);
                    ps.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Contrato guardado correctamente");
            dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar contrato: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El salario debe ser un número válido");
        }
    }
   
    //Hago este metodo para que la persona pueda buscar algo más facil como es el email en vez de tener que buscar el id del empleado que no lo va a saber
    
    private int obtenerIdPersonaDesdeEmail(String email) {
        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
             PreparedStatement ps = conexion.prepareStatement("SELECT idPersona FROM Personas WHERE email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idPersona");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }

}

