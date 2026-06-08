package PaqueteGestor.Pagos;

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

public class VentanaPagos extends JFrame{

	private DefaultTableModel modelo;
    private JTable tabla;

    public VentanaPagos() {

        setTitle("Gestión de Pagos");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnas = { "idPago", "idCliente", "idProyecto", "fechaPago", "monto", "estadoPago"};
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
        tabla.setForeground(Color.black); 

        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        tabla.getTableHeader().setForeground(Color.WHITE); 
        tabla.getTableHeader().setOpaque(false);
        
   
        tabla.setFont(new Font("Arial", Font.PLAIN, 16)); 
        tabla.setRowHeight(25); 
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18)); 
        
      
        tabla.setFont(new Font("Arial", Font.PLAIN, 16)); 
        tabla.setRowHeight(25); 
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18)); 

        cargarDatos();

        PanelConFondo fondoTabla = new PanelConFondo("images/FondoGestionarPagos.jpeg"); // Cambia la ruta

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
        JButton btnVerCompletados = new JButton("Ver solo pagados");
        JButton btnVerSinRealizar = new JButton("Ver solo sin pagar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnVerCompletados);
        panelBotones.add(btnVerSinRealizar);

        add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> abrirFormulario(null));

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow(); 
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un pago para editarlo");
                return;
            }

            int idPago = (int) modelo.getValueAt(fila, 0);
            int idCliente = (int) modelo.getValueAt(fila, 1);
            int idProyecto = (int) modelo.getValueAt(fila, 2);
            java.sql.Date fechaPago = (java.sql.Date) modelo.getValueAt(fila, 3);
            Double monto = (Double) modelo.getValueAt(fila, 4);
            String estadoPago = (String) modelo.getValueAt(fila, 5);

            Pago p = new Pago(idPago, idCliente, idProyecto, fechaPago, monto, estadoPago);
            abrirFormulario(p);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow(); // Obtiene la fila seleccionada
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un pago para eliminarlo");
                return;
            }

            int idPago = (int) modelo.getValueAt(fila, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas eliminar el pago seleccionado?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarPago(idPago); 
                cargarDatos(); 
            }
        });
        
        btnBuscar.addActionListener(e -> {
            String emailBuscado = JOptionPane.showInputDialog(this, "Introduce el email del cliente:");

            if (emailBuscado != null && !emailBuscado.trim().isEmpty()) {
                modelo.setRowCount(0); 

                try (
                    Connection conexion = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                    PreparedStatement psCliente = conexion.prepareStatement(
                        "SELECT idCliente FROM Clientes WHERE email LIKE ?")
                ) {
                    psCliente.setString(1, "%" + emailBuscado.trim() + "%");
                    ResultSet rsCliente = psCliente.executeQuery();

                    if (rsCliente.next()) {
                        int idCliente = rsCliente.getInt("idCliente");

                        PreparedStatement psPagos = conexion.prepareStatement(
                            "SELECT * FROM Pagos WHERE idCliente = ?"
                        );
                        psPagos.setInt(1, idCliente);
                        ResultSet rsPagos = psPagos.executeQuery();

                        boolean hayPagos = false;
                        while (rsPagos.next()) {
                            Object[] fila = {
                                rsPagos.getInt("idPago"),
                                rsPagos.getInt("idCliente"),
                                rsPagos.getInt("idProyecto"),
                                rsPagos.getDate("fechaPago"),
                                rsPagos.getBigDecimal("monto"),
                                rsPagos.getString("estadoPago")
                            };
                            modelo.addRow(fila);
                            hayPagos = true;
                        }

                        if (!hayPagos) {
                            JOptionPane.showMessageDialog(this, "Este cliente no tiene pagos registrados.");
                            cargarDatos(); // Vuelve a cargar todos si no hay resultados
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró ningún cliente con ese email.");
                        cargarDatos();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
                }
            }
        });
        
        btnVerCompletados.addActionListener(e -> cargarDatosFiltrados("completado"));
        btnVerSinRealizar.addActionListener(e -> cargarDatosFiltrados("sin realizar"));

    }

    public void cargarDatos() {

        modelo.setRowCount(0);

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Pagos")) {

            while (rs.next()) {
                Object[] fila = { rs.getInt("idPago"), rs.getInt("idCliente"), rs.getString("idProyecto"),
                        rs.getDate("fechaPago"), rs.getDouble("monto"), rs.getString("estadoPago")};
                modelo.addRow(fila); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void eliminarPago(int idPago) {
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC",
                "root", "aula");
                java.sql.PreparedStatement ps = conexion.prepareStatement("DELETE FROM Pagos WHERE idPago = ?")) {
            ps.setInt(1, idPago); 
            ps.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Pago eliminado");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el pago: " + e.getMessage());
        }
    }

    private void abrirFormulario(Pago pago) {
        FormularioPagos formulario = new FormularioPagos(this, pago);
        formulario.setVisible(true); 

        cargarDatos();
    }

    static class Pago {
        int idPago;
        int idCliente;
        int idProyecto;
        java.sql.Date fechaPago;
        double monto;
        String estadoPago;

        // Constructor
        public Pago(int idPago, int idCliente, int idProyecto, java.sql.Date fechaPago, double monto, String estadoPago) {
            this.idPago = idPago;
            this.idCliente = idCliente;
            this.idProyecto = idProyecto;
            this.fechaPago = fechaPago;
            this.monto = monto;
            this.estadoPago = estadoPago;
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

    public void cargarDatosFiltrados(String estado) {
        modelo.setRowCount(0);

        String sql = "SELECT * FROM Pagos WHERE estadoPago = ?";
        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("idPago"),
                    rs.getInt("idCliente"),
                    rs.getInt("idProyecto"),
                    rs.getDate("fechaPago"),
                    rs.getDouble("monto"),
                    rs.getString("estadoPago")
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

}


