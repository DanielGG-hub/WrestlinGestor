package PaqueteGestor;

import javax.swing.*;

import PaqueteGestor.Clientes.VentanaClientes;
import PaqueteGestor.Contratos.VentanaContratos;
import PaqueteGestor.Empleados.VentanaEmpleados;
import PaqueteGestor.Pagos.VentanaPagos;
import PaqueteGestor.Proyecto_Persona.VentanaProyectoPersona;
import PaqueteGestor.Proyectos.VentanaProyectos;

import java.awt.*;
import java.awt.event.*;

public class VentanaMain extends JFrame {

	private String rutaFondo = "images/WrestlinGestor.jpg"; // Creo una variable con la imagen que usare de wallpaper

    public VentanaMain() {
        setTitle("WrestlinGestor");

        setSize(1920, 1080);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Esto sirve para que la aplicación se cierre completamente y no siga en segundo plano


        JPanel panelFondo = new JPanel() {
            // Creo la variable imagenFondo para establecer el fondo con la imagen especificada anteriormente
            Image imagenFondo = new ImageIcon(rutaFondo).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Llamo al metodo para asegurarme de que el panel se pinta
                // Coloca la imagen con el tamaño del panel
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Desactivo el layout para colocar los botones a mano
        panelFondo.setLayout(null);

        // Creo las variables que contienen las imagenes de cada boton
        ImageIcon icono = new ImageIcon("images/GestionarProyectos.png"); 
        ImageIcon iconoEmpleados = new ImageIcon("images/GestionarEmpleados.png"); 
        ImageIcon iconoContratos = new ImageIcon("images/GestionarContratos.png"); 
        ImageIcon iconoClientes = new ImageIcon("images/GestionarClientes.png"); 
        ImageIcon iconoPagos = new ImageIcon("images/GestionarPagos.png"); 
        ImageIcon iconoEmpleadosProyecto = new ImageIcon("images/GestionarEmpleadosProyecto.png"); 
        ImageIcon iconoCrearBaseDeDatos = new ImageIcon("images/BotonCrearBase.png"); 

        // Creo el botón que permite crear la base de datos
        JButton botonCrearBaseDeDatos = new JButton(iconoCrearBaseDeDatos); // Texto + imagen
        
        // Posiciono y defino el tamaño del botón
        botonCrearBaseDeDatos.setBounds(75, 50, 350, 150);

        // Hago el actionListener para manejar lo que hace el botón al pulsarlo
        botonCrearBaseDeDatos.addActionListener(e -> {
        	// Creo un cuadro de confirmacion para asegurar de que quieres crear o resetear la BBDD
            int confirmar = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres crear/resetear la base de datos?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                CrearTablasGestor gestor = new CrearTablasGestor();
                try {
                    gestor.crearBaseDatos();
                    JOptionPane.showMessageDialog(this, 
                        "Base de datos creada/resetada correctamente.", 
                        "Misión cumplida", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al crear la base de datos:\n" + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Añado el botón al panel
        panelFondo.add(botonCrearBaseDeDatos);

        // Se establece el panel de fondo como el contenido principal de la ventana
        setContentPane(panelFondo);
        
        JButton botonProyectos = new JButton(icono); // Texto + imagen
               
        // Se posiciona el botón en el panel (x=1000, y=500) y se define su tamaño (250x100)
        botonProyectos.setBounds(575, 250, 350, 150);

        // Se añade un ActionListener para manejar el clic del botón
        botonProyectos.addActionListener(e -> {
            // Al pulsar el botón, se crea una nueva ventana secundaria y se muestra
            VentanaProyectos ventanaSec = new VentanaProyectos();
            ventanaSec.setVisible(true);
        });

        // Se añade el botón al panel de fondo
        panelFondo.add(botonProyectos);
        
        JButton botonEmpleados = new JButton(iconoEmpleados); 
       
        botonEmpleados.setBounds(575, 500, 350, 150);

        botonEmpleados.addActionListener(e -> {
            VentanaEmpleados ventanaEmp = new VentanaEmpleados();
            ventanaEmp.setVisible(true);
        });

        panelFondo.add(botonEmpleados);
        
        JButton botonContratos = new JButton(iconoContratos); 
        
        botonContratos.setBounds(575, 750, 350, 150);

        botonContratos.addActionListener(e -> {
            VentanaContratos ventanaContrato = new VentanaContratos();
            ventanaContrato.setVisible(true);
        });

        panelFondo.add(botonContratos);
        
        JButton botonClientes = new JButton(iconoClientes); 
        
        botonClientes.setBounds(1000, 250, 350, 150);

        botonClientes.addActionListener(e -> {
            VentanaClientes ventanaCliente = new VentanaClientes();
            ventanaCliente.setVisible(true);
        });

        panelFondo.add(botonClientes);

        JButton botonPagos = new JButton(iconoPagos); 
        
        botonPagos.setBounds(1000, 500, 350, 150);

        botonPagos.addActionListener(e -> {
            VentanaPagos ventanaPago = new VentanaPagos();
            ventanaPago.setVisible(true);
        });

        panelFondo.add(botonPagos);
        
        JButton botonProyectoPersona = new JButton(iconoEmpleadosProyecto); 
        
        botonProyectoPersona.setBounds(1000, 750, 350, 150);

        botonProyectoPersona.addActionListener(e -> {
            VentanaProyectoPersona ventanaProyectoP = new VentanaProyectoPersona();
            ventanaProyectoP.setVisible(true);
        });

        panelFondo.add(botonProyectoPersona);
    }
   

    // Metodo principal para arrancar la app
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Hago visible la ventana principal
            new VentanaMain().setVisible(true);
        });
    }
}