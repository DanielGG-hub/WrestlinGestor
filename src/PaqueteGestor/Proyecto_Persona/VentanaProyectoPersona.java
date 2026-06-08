package PaqueteGestor.Proyecto_Persona;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class VentanaProyectoPersona extends JFrame{
	
	private DefaultTableModel modelo;
    private JTable tabla;
    
    public VentanaProyectoPersona() {
    	
    	setTitle("Gestión de los empleados asignados a cada proyecto");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        String[] columnas = { "idProyecto", "idPersona", "rol"};
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

        PanelConFondo fondoTabla = new PanelConFondo("images/FondoEmpleadoProyecto.jpg"); 
        
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
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);

        add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> abrirFormulario(null));

        btnEditar.addActionListener(e ->  {
        	int fila = tabla.getSelectedRow();
        	if(fila == -1) {
        		JOptionPane.showMessageDialog(this, "Selecciona un proyecto para editar a sus empleados"); 
                return;        
                }
        	
        	int idPersona = (int) modelo.getValueAt(fila, 0);
        	int idProyecto = (int) modelo.getValueAt(fila, 1);
        	String rol = (String) modelo.getValueAt(fila, 2);
        	
        	Proyecto p = new Proyecto(idPersona, idProyecto, rol);
            abrirFormulario(p);
        });
        
        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow(); 
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un pago para eliminarlo");
                return;
            }
            int idProyecto = (int) modelo.getValueAt(fila, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas eliminar el proyecto seleccionado?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
            	eliminarProyecto(idProyecto); 
                cargarDatos(); 
            }
        });
        
        btnBuscar.addActionListener(e -> {
            String nombreProyecto = JOptionPane.showInputDialog(this, "Introduce el nombre del proyecto:");

            if (nombreProyecto != null && !nombreProyecto.trim().isEmpty()) {
                modelo.setRowCount(0); 

                try (Connection conexion = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                     Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT pp.idProyecto, pp.idPersona, pp.rol FROM Proyecto_Persona pp "
                             + "JOIN Proyectos p ON pp.idProyecto = p.idProyecto "
                             + "WHERE p.nombreProyecto LIKE '%" + nombreProyecto + "%'")) {

                    boolean encontrado = false;

                    while (rs.next()) {
                        Object[] fila = { rs.getInt("idProyecto"), rs.getInt("idPersona"), rs.getString("rol") };
                        modelo.addRow(fila);
                        encontrado = true;
                    }

                    if (!encontrado) {
                        JOptionPane.showMessageDialog(this, "No se encontró ningún proyecto con ese nombre.");
                        cargarDatos(); 
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
                }
            }
        });

        
    }
    
    public void cargarDatos() {

        modelo.setRowCount(0);

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Proyecto_Persona")) {

            while (rs.next()) {
                Object[] fila = { rs.getInt("idProyecto"), rs.getInt("idPersona"), rs.getString("rol")};
                modelo.addRow(fila); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void eliminarProyecto(int idProyecto) {
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC",
                "root", "aula");
                java.sql.PreparedStatement ps = conexion.prepareStatement("DELETE FROM Proyecto_Persona WHERE idProyecto = ?")) {
            ps.setInt(1, idProyecto); 
            ps.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Proyecto eliminado");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el proyecto: " + e.getMessage());
        }
    }

    private void abrirFormulario(Proyecto proyecto) {
        FormularioProyectoPersona formulario = new FormularioProyectoPersona(this, proyecto);
        formulario.setVisible(true); 

        cargarDatos();
    }
    
    static class Proyecto {
        int idPersona;
        int idProyecto;
        String rol;

        // Constructor
        public Proyecto(int idPersona, int idProyecto, String rol) {
            this.idPersona = idPersona;
            this.idProyecto = idProyecto;
            this.rol = rol;
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
