package PaqueteGestor.Clientes;

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

import PaqueteGestor.Empleados.FormularioEmpleados;

public class VentanaClientes extends JFrame {

	private DefaultTableModel modelo;
	private JTable tabla;
	
	public VentanaClientes() {
		
		setTitle("Gestión de Clientes");
		setSize(700,500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		String[] columnas = {"idClientes", "nombreCliente", "email", "telefono"};
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
        
        PanelConFondo fondoTabla = new PanelConFondo("images/FondoGestionarClientes.jpg"); 

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
        		JOptionPane.showMessageDialog(this, "Selecciona un cliente para editarlo");
        		return;
        	}
        	
        	int idCliente = (int) modelo.getValueAt(fila, 0);
        	String nombreCliente = (String) modelo.getValueAt(fila, 1);
        	String email = (String) modelo.getValueAt(fila, 2);
        	String telefono = (String) modelo.getValueAt(fila, 3);
        	
        	Cliente cl = new Cliente(idCliente, nombreCliente, email, telefono);
        	abrirFormulario(cl);
        });
        
        btnEliminar.addActionListener(e -> {
        	int fila = tabla.getSelectedRow();
        	if(fila == -1) {
        		JOptionPane.showMessageDialog(this, "Selecciona un cliente para eliminarlo");
        		return;
        	}
        	
        	int idCliente = (int) modelo.getValueAt(fila, 0);
        	
        	int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar al cliente seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        	if(confirm == JOptionPane.YES_OPTION) {
        		eliminarCliente(idCliente);
        		cargarDatos();
        	}
        });
        
        btnBuscar.addActionListener(e -> {
            String emailBuscado = JOptionPane.showInputDialog(this, "Introduce el email del cliente:");
            
            if (emailBuscado != null && !emailBuscado.trim().isEmpty()) {
                boolean encontrado = false;
                
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    String emailTabla = (String) modelo.getValueAt(i, 2); 
                    if (emailTabla.equalsIgnoreCase(emailBuscado.trim())) {
                        tabla.setRowSelectionInterval(i, i); 
                        tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true)); 
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún empleado con ese correo.");
                }
            }
        });
	}

	private void cargarDatos() {
		
		modelo.setRowCount(0);
		
		try (
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
		         Statement stmt = conexion.createStatement();
		         ResultSet rs = stmt.executeQuery("SELECT * FROM Clientes")
				) {
			
			while(rs.next()) {
				Object[] fila = {
						rs.getInt("idCliente"),
						rs.getString("nombreCliente"),
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
	
	private void eliminarCliente(int idCliente) {
		
		try(
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	            PreparedStatement ps = conexion.prepareStatement("DELETE FROM Clientes WHERE idCliente = ?")			
			) {
			
			ps.setInt(1, idCliente);
			ps.executeUpdate();
			JOptionPane.showMessageDialog(this, "Cliente eliminado");
			
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al eliminar al cliente: " + e.getMessage());
		}
		
	}
	
	
		static class Cliente {
			int idCliente;
			String nombreCliente;
			String email;
			String telefono;
			
			// Constructor
			public Cliente(int idCliente, String nombreCliente, String email, String telefono) {
				this.idCliente = idCliente;
				this.nombreCliente = nombreCliente;
				this.email = email;
				this.telefono = telefono;
			}
		}
		
		private void abrirFormulario(Cliente cliente) {
			
			FormularioClientes formulario = new FormularioClientes(this, cliente);
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

