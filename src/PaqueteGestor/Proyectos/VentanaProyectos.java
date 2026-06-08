package PaqueteGestor.Proyectos;

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

public class VentanaProyectos extends JFrame { 

	private DefaultTableModel modelo;
	private JTable tabla;
	
	public VentanaProyectos() {
		
		//Configuro la ventana de Proyectos
		setTitle("Gestión de Proyectos");
		setSize(700,500);
		setLocationRelativeTo(null); //Centro la ventana en la pantalla
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Esto sirve para cerrar solo esta ventana no la aplicación entera
        setLayout(new BorderLayout()); // Layout para organizar
		
        // Defino las columnas de la tabla
        String[] columnas = {"ID Proyecto", "Nombre Proyecto", "Fecha Entrega", "Fecha Reunión"};
        modelo = new DefaultTableModel(null, columnas);
        tabla = new JTable(modelo);
        
     // Cambiar color del texto de las celdas
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.WHITE); // texto blanco
                setOpaque(false);
                return c;
            }
        });
        
        //Modifico el texto de las columnas y los encabezados
        
        tabla.setFont(new Font("Arial", Font.PLAIN, 16));
        tabla.setRowHeight(25);
        tabla.setOpaque(false);
        tabla.setShowGrid(false);

        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        tabla.getTableHeader().setForeground(Color.WHITE); 
        tabla.getTableHeader().setOpaque(false);

        
        cargarDatos(); // Método para cargar datos desde la BBDD
        
     // Establezco una imagen de fondo para la tabla
        PanelConFondo fondoTabla = new PanelConFondo("images/FondoGestionarProyectos.png"); 

        // Hago la tabla transparente para ver la imagen
        tabla.setOpaque(false);
        tabla.setBackground(new Color(0, 0, 0, 0));
        tabla.setShowGrid(false);
        tabla.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 150));   
        scrollPane.setOpaque(false);     

        // Añadir la tabla al panel 
        fondoTabla.add(scrollPane, BorderLayout.CENTER);

        // Añadir el panel a la ventana
        add(fondoTabla, BorderLayout.CENTER);
        
        //Panel que contiene los botones 
        JPanel panelBotones = new JPanel();
        
        //Creo los botones
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        
        //Añado los botones al panel en la parte inferior
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        
        //Añadir el panel de botones a la parte inferior de la ventana
        add(panelBotones, BorderLayout.SOUTH);
        
        
        // Nuevo usa el metodo abrirformulario para abrir otra ventana que permite rellenar los datos
        btnNuevo.addActionListener(e -> abrirFormulario(null));	
        
        btnEditar.addActionListener(e -> {
        	int fila = tabla.getSelectedRow(); //Guardo la fila seleccionada por el usuario
        	if(fila == -1) {
        		JOptionPane.showMessageDialog(this, "Selecciona un proyecto para editarlo"); //Si no ha seleccionado nada muestro este mensaje
        		return;
        	}
        	
        	// Extraigo los campos de la fila seleccionada
            int idProyecto = (int) modelo.getValueAt(fila, 0);
            String nombre = (String) modelo.getValueAt(fila, 1);
            java.sql.Date fechaEntrega = (java.sql.Date) modelo.getValueAt(fila, 2);
            java.sql.Date fechaReunion = (java.sql.Date) modelo.getValueAt(fila, 3);

            // Crea un objeto Proyecto y abro el formulario con los datos para editar
            Proyecto p = new Proyecto(idProyecto, nombre, fechaEntrega, fechaReunion);
            abrirFormulario(p);
        });
        
        btnEliminar.addActionListener(e -> {
        	int fila = tabla.getSelectedRow(); //Guardo la fila seleccionada por el usuario
        	if(fila == -1) {
        		JOptionPane.showMessageDialog(this, "Selecciona un proyecto para eliminarlo");
        		return;
        	}
        	
        	// Guardo el id del proyecto que se quiere eliminar
        	int idProyecto = (int) modelo.getValueAt(fila, 0);
        	
        	// Pido confirmación al usuario
        	int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar el proyecto seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        	if(confirm == JOptionPane.YES_OPTION) {
        		eliminarProyecto(idProyecto); 
        		cargarDatos(); //Recargo la tabla sin los eliminados
        	}
        });
        
        btnBuscar.addActionListener(e -> {
            String nombreBuscado = JOptionPane.showInputDialog(this, "Introduce el nombre del proyecto:"); // Guardo el nombre que busca el usuario en una variable

            if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) { // Me aseguro de que el nombre no este vacio ni solo contenga espacios
                modelo.setRowCount(0); // Limpio la tabla para mostrar solo los resultados de la busqueda

                try (
                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
                    PreparedStatement ps = conexion.prepareStatement("SELECT * FROM Proyectos WHERE nombreProyecto LIKE ?") // Selecciono todos los proyectos que tengan el mismo nombre
                ) {
                    ps.setString(1, "%" + nombreBuscado.trim() + "%"); 
                    ResultSet rs = ps.executeQuery(); // Establezco la variable dentro de la consulta y la ejecuto

                    boolean encontrado = false; // Variable para saber si se encontro algun resultado
                   
                    while (rs.next()) { //Recorro los resultados obtenidos de la base de datos
                    	// Creo una fila con los datos del proyecto y añado la fila a la tabla
                        Object[] fila = {
                            rs.getInt("idProyecto"),
                            rs.getString("nombreProyecto"),
                            rs.getDate("fechaEntrega"),
                            rs.getDate("fechaReunion")
                        };
                        modelo.addRow(fila);
                        encontrado = true; // Marco que si se encontro al menos un resultado
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

	private void cargarDatos() {
	
		modelo.setRowCount(0); //Limpia la tabla actual
		
		try(
			 Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	         Statement stmt = conexion.createStatement();
	         ResultSet rs = stmt.executeQuery("SELECT * FROM Proyectos") // Me conecto a la base de datos y hago la consulta para obtener todos los datos de la tabla proyectos
			) {
			
			//Itero todos los resultados y los añado al modelo de la tabla	
			while(rs.next()) {
				Object[] fila = {
					rs.getInt("idProyecto"),
					rs.getString("nombreProyecto"),
					rs.getDate("fechaEntrega"),
					rs.getDate("fechaReunion")
				};
				modelo.addRow(fila);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
		}
		
	}
	
	// Método para eliminar un proyecto
	private void eliminarProyecto(int idProyecto) {
		try (
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/WorkSuite?serverTimezone=UTC", "root", "aula");
	            PreparedStatement ps = conexion.prepareStatement("DELETE FROM Proyectos WHERE idProyecto = ?")				
		) {	
			ps.setInt(1, idProyecto); //Asigno el id del proyecto a eliminar a la consulta
			ps.executeUpdate(); //Ejecuto la eliminación 
			JOptionPane.showMessageDialog(this, "Proyecto eliminado");
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al eliminar proyecto: " + e.getMessage());
		}
	}
	

	//Método para abrir el formulario tanto para editar como para crear
	private void abrirFormulario(Proyecto proyecto) {
		
		FormularioProyectos formulario = new FormularioProyectos(this, proyecto);
		formulario.setVisible(true); //Muestra el formulario en pantalla
		
		//Al cerrar el formulario recargo los datos de la tabla
		cargarDatos();
	}
	
	//Clase interna que representa un proyecto para mejorar el manejo de los datos
	static class Proyecto {
		int idProyecto;
		String nombreProyecto;
		java.sql.Date fechaEntrega;
		java.sql.Date fechaReunion;
		
		public Proyecto(int idProyecto, String nombreProyecto, java.sql.Date fechaEntrega, java.sql.Date fechaReunion) {
			this.idProyecto = idProyecto;
			this.nombreProyecto = nombreProyecto;
			this.fechaEntrega = fechaEntrega;
			this.fechaReunion = fechaReunion;
		}
	}

	// Creo la clase panel para establecer la imagen de fondo
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


