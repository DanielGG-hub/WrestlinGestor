package PaqueteGestor.Contratos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class VentanaContratos extends JFrame {

    private DefaultTableModel modelo;
    private JTable tabla;

    public VentanaContratos() {

        setTitle("Gestión de Contratos");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnas = { "idContrato", "idPersona", "tipoContrato", "fechaInicio", "fechaFin", "condiciones",
                "salario" };
        modelo = new DefaultTableModel(null, columnas);
        tabla = new JTable(modelo);

        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.WHITE);
                setOpaque(false); 
                return c;
            }
        });
        
        tabla.setFont(new Font("Arial", Font.PLAIN, 16));
        tabla.setRowHeight(25);
        tabla.setOpaque(false);
        tabla.setShowGrid(false);
        tabla.setForeground(Color.WHITE); 

        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        tabla.getTableHeader().setForeground(Color.WHITE); 
        tabla.getTableHeader().setOpaque(false);
        
        tabla.setFont(new Font("Arial", Font.PLAIN, 16)); 
        tabla.setRowHeight(25); 
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18)); 

        cargarDatos();

        PanelConFondo fondoTabla = new PanelConFondo("images/FondoGestionarContratos.jpeg"); 

        tabla.setOpaque(false);
        tabla.setBackground(new Color(0, 0, 0, 0));
        tabla.setShowGrid(false);
        tabla.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        fondoTabla.add(scrollPane, BorderLayout.CENTER);

        add(fondoTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnOrdenarFechaFin = new JButton("Ordenar por fecha final");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnOrdenarFechaFin);

        add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> abrirFormulario(null));

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow(); 
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un contrato para editarlo"); 
                return;
            }

            int idContrato = (int) modelo.getValueAt(fila, 0);
            int idPersona = (int) modelo.getValueAt(fila, 1);
            String tipoContrato = (String) modelo.getValueAt(fila, 2);
            java.sql.Date fechaInicio = (java.sql.Date) modelo.getValueAt(fila, 3);
            java.sql.Date fechaFin = (java.sql.Date) modelo.getValueAt(fila, 4);
            String condiciones = (String) modelo.getValueAt(fila, 5);
            Double salario = (Double) modelo.getValueAt(fila, 6);

            Contrato p = new Contrato(idContrato, idPersona, tipoContrato, fechaInicio, fechaFin, condiciones, salario);
            abrirFormulario(p);
        });             

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow(); 
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un contrato para eliminarlo");
                return;
            }

            int idContrato = (int) modelo.getValueAt(fila, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas eliminar el contrato seleccionado?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarContrato(idContrato); 
                cargarDatos(); 
            }
        });
        
        btnBuscar.addActionListener(e -> {
            String emailBuscado = JOptionPane.showInputDialog(this, "Introduce el email del empleado:");

            if (emailBuscado != null && !emailBuscado.trim().isEmpty()) {
                modelo.setRowCount(0); 

                try (
                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                    PreparedStatement psPersona = conexion.prepareStatement("SELECT idPersona FROM Personas WHERE email LIKE ?")
                ) {
                    psPersona.setString(1, "%" + emailBuscado.trim() + "%");
                    ResultSet rsPersona = psPersona.executeQuery();

                    if (rsPersona.next()) {
                        int idPersona = rsPersona.getInt("idPersona");

                        PreparedStatement psContratos = conexion.prepareStatement(
                            "SELECT * FROM Contratos WHERE idPersona = ?"
                        );
                        psContratos.setInt(1, idPersona);
                        ResultSet rsContratos = psContratos.executeQuery();

                        boolean hayContratos = false;
                        while (rsContratos.next()) {
                            Object[] fila = {
                                rsContratos.getInt("idContrato"),
                                rsContratos.getInt("idPersona"),
                                rsContratos.getString("tipoContrato"),
                                rsContratos.getDate("fechaInicio"),
                                rsContratos.getDate("fechaFin"),
                                rsContratos.getString("condiciones"),
                                rsContratos.getBigDecimal("salario")
                            };
                            modelo.addRow(fila);
                            hayContratos = true;
                        }

                        if (!hayContratos) {
                            JOptionPane.showMessageDialog(this, "Este empleado no tiene contratos.");
                            cargarDatos(); 
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró ningún empleado con ese email.");
                        cargarDatos();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
                }
            }
        });

        btnOrdenarFechaFin.addActionListener(e -> cargarDatosOrdenadosPorFechaFin());

    }

    public void cargarDatos() {

        modelo.setRowCount(0);

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Contratos ORDER BY idPersona ASC")) {

            while (rs.next()) {
                Object[] fila = { rs.getInt("idContrato"), rs.getInt("idPersona"), rs.getString("tipoContrato"),
                        rs.getDate("fechaInicio"), rs.getDate("fechaFin"), rs.getString("condiciones"),
                        rs.getDouble("salario") };
                modelo.addRow(fila);  
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void eliminarContrato(int idContrato) {
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC",
                "root", "aula");
                java.sql.PreparedStatement ps = conexion.prepareStatement("DELETE FROM Contratos WHERE idContrato = ?")) {
            ps.setInt(1, idContrato); 
            ps.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Contrato eliminado");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el contrato: " + e.getMessage());
        }
    }

    private void abrirFormulario(Contrato contrato) {
        FormularioContratos formulario = new FormularioContratos(this, contrato);
        formulario.setVisible(true); 

        cargarDatos();
    }

    static class Contrato {
        int idContrato;
        int idPersona;
        String tipoContrato;
        java.sql.Date fechaInicio;
        java.sql.Date fechaFin;
        String condiciones;
        double salario;

        // Constructor
        public Contrato(int idContrato, int idPersona, String tipoContrato, java.sql.Date fechaInicio,
                java.sql.Date fechaFin, String condiciones, double salario) {
            this.idContrato = idContrato;
            this.idPersona = idPersona;
            this.tipoContrato = tipoContrato;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
            this.condiciones = condiciones;
            this.salario = salario;
        }
    }
    
    private void cargarDatosOrdenadosPorFechaFin() {
        modelo.setRowCount(0);

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Contratos ORDER BY fechaFin ASC")) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("idContrato"),
                        rs.getInt("idPersona"),
                        rs.getString("tipoContrato"),
                        rs.getDate("fechaInicio"),
                        rs.getDate("fechaFin"),
                        rs.getString("condiciones"),
                        rs.getDouble("salario")
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + e.getMessage());
        }
    }


    class PanelConFondo extends JPanel {
        private Image imagen;

        public PanelConFondo(String rutaImagen) {
            this.imagen = new ImageIcon(rutaImagen).getImage();
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }

}

