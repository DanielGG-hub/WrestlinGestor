package PaqueteGestor.Empleados;

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

import PaqueteGestor.Proyectos.FormularioProyectos;

public class VentanaEmpleados extends JFrame{

	private DefaultTableModel modelo;
	private JTable tabla;
	
	public VentanaEmpleados() {
		
		setTitle("Gestión de Empleados");
		setSize(700,500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		String[] columnas = {"idPersona", "nombre", "apellido", "email", "telefono"};
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
        
        cargarDatos();
        
        PanelConFondo fondoTabla = new PanelConFondo("images/FondoGestionarEmpleados.png"); 

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
        
        btnEditar.addActionListener(e -> {
        	
        	int fila = tabla.getSelectedRow();
        	if(fila == -1) {
        		JOptionPane.showMessageDialog(this, "Selecciona un empleado para editarlo");
        		return;
        	}
        	
        	int idPersona = (int) modelo.getValueAt(fila, 0);
        	String nombre = (String) modelo.getValueAt(fila, 1);
        	String apellido = (String) modelo.getValueAt(fila, 2);
        	String email = (String) modelo.getValueAt(fila, 3);
        	String telefono = (String) modelo.getValueAt(fila, 4);
        	
        	Empleado em = new Empleado(idPersona, nombre, apellido, email, telefono);
        	abrirFormulario(em);
        });
        
        btnEliminar.addActionListener(e -> {
        	int fila = tabla.getSelectedRow();
        	if(fila == -1) {
        		JOptionPane.showMessageDialog(this, "Selecciona un empleado para eliminarlo");
        		return;
        	}
        	
        	int idPersona = (int) modelo.getValueAt(fila, 0);
        	
        	int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar al empleado seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        	if(confirm == JOptionPane.YES_OPTION) {
        		eliminarEmpleado(idPersona);
        		cargarDatos();
        	}
        });
        
        btnBuscar.addActionListener(e -> {
            String emailBuscado = JOptionPane.showInputDialog(this, "Introduce el correo del empleado:");

            if (emailBuscado != null && !emailBuscado.trim().isEmpty()) {
                modelo.setRowCount(0); 

                try (
                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                    PreparedStatement ps = conexion.prepareStatement("SELECT * FROM Personas WHERE email LIKE ?")
                ) {
                    ps.setString(1, "%" + emailBuscado.trim() + "%");
                    ResultSet rs = ps.executeQuery();

                    boolean encontrado = false;
                    while (rs.next()) {
                        Object[] fila = {
                            rs.getInt("idPersona"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("telefono")
                        };
                        modelo.addRow(fila);
                        encontrado = true;
                    }

                    if (!encontrado) {
                        JOptionPane.showMessageDialog(this, "No se encontró ningún empleado con ese correo.");
                        cargarDatos();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
                }
            }
        });
	}

	private void cargarDatos() {
		
		modelo.setRowCount(0);
		
		try (
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
		         Statement stmt = conexion.createStatement();
		         ResultSet rs = stmt.executeQuery("SELECT * FROM Personas")
				) {
			
			while(rs.next()) {
				Object[] fila = {
						rs.getInt("idPersona"),
						rs.getString("nombre"),
						rs.getString("apellido"),
						rs.getString("email"),
						rs.getString("telefono")
				};
				
				modelo.addRow(fila);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
		}
		
	}
	
	private void eliminarEmpleado(int idPersona) {
		
		try(
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	            PreparedStatement ps = conexion.prepareStatement("DELETE FROM Personas WHERE idPersona = ?")			
			) {
			
			ps.setInt(1, idPersona);
			ps.executeUpdate();
			JOptionPane.showMessageDialog(this, "Empleado eliminado");
			
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al eliminar al empleado: " + e.getMessage());
		}
		
	}
	
	
		static class Empleado {
			int idPersona;
			String nombre;
			String apellido;
			String email;
			String telefono;
			
			// Constructor
			public Empleado(int idPersona, String nombre, String apellido, String email, String telefono) {
				this.idPersona = idPersona;
				this.nombre = nombre;
				this.apellido = apellido;
				this.email = email;
				this.telefono = telefono;
			}
		}
		
		private void abrirFormulario(Empleado empleado) {
			
			FormularioEmpleados formulario = new FormularioEmpleados(this, empleado);
			formulario.setVisible(true); 
			
			cargarDatos();
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
