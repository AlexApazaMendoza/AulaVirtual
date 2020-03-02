/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aulavirtual;

import Conexion.Conectar;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alex
 */
public class Profesor extends javax.swing.JFrame {

    /**
     * Creates new form Profesor
     */
    public Profesor() {
        initComponents();
    }
    /*creo una variable donde se va a almacenar el dni del profesor*/
    private String dniProfesor;

    public String getDniProfesor() {
        return dniProfesor;
    }
    public void setDniProfesor(String dniProfesor) {
        this.dniProfesor = dniProfesor;
    }
    
    //creo variable para almacenar el codigo del curso
    private String codigoCurso;
    
    public String getCodigoCurso() {
        return codigoCurso;
    }
    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }
    
    public void Mostrar(){//muestra los datos personales del profesor
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select  persona.nombre, persona.dni, persona.fechaNacimiento, escuela.nombreEscuela\n" +
            "from profesor\n" +
            "inner join persona on profesor.dni=persona.dni\n" +
            "inner join escuela on profesor.codEscuela=escuela.codEscuela where persona.dni ='"+getDniProfesor()+"';";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while (rs.next()){ 
                jTextFieldNombreProfesor.setText(rs.getString(1));
                jTextFieldDniProfesor.setText(rs.getString(2));
                jTextFieldEdadProfesor.setText(String.valueOf(calcular(dateToCalendar(rs.getDate(3)))));
                jTextFieldEscuelaProfesor.setText(rs.getString(4));
            }
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    public void CargaCursos(){//carga los cursos  que el profesor dicta durante el ciclo
        DefaultTableModel modelo=(DefaultTableModel) jTableCursos.getModel();
        modelo.setRowCount(0);//limpiar el modelo
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select curso.nombre_curso, ciclo.nombreciclo\n" +
            "from curso\n" +
            "inner join ciclo on curso.numCiclo=ciclo.numCiclo\n" +
            "where curso.dni=\""+getDniProfesor()+"\";";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                Vector v=new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));
                modelo.addRow(v);
            }  
            jTableCursos.setModel(modelo);
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
        
    }
    
    //para que me cargue la foto de la base de datos
    public ImageIcon getFoto(String id){//leer un flujo de imagen de una base de datos
        String sql="select foto from persona where dni=\""+id+"\";";
        ImageIcon li=null;
        InputStream is =null;
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            if(rs.next()){
                is = rs.getBinaryStream(1);
                BufferedImage bi=ImageIO.read(is);
                li=new ImageIcon(bi.getScaledInstance(jLabelFoto.getWidth(),jLabelFoto.getHeight(), Image.SCALE_DEFAULT));
            }
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Error:" + e.getMessage());
        }catch (IOException ex){
            System.out.println("Error:" + ex);
        }
        return li;
    }
    
    //selecciono el curso y se capturan los datos del curso(codigoCurso)
    public void seleccionarCurso(String nombreCurso){
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            String sql="Select *from Curso where Curso.nombre_curso ='"+nombreCurso+"';";
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while (rs.next()){ 
                setCodigoCurso(rs.getString(1));
                jTextFieldNomCurso.setText(rs.getString(2));
            }
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    public void AgregarSilabuAMatriculas(){//se agrega el silabu seleccionado a  las matriculas
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select *from curso_estudiantesemestre\n" +
            "where codSemestre=\"2019A\" and codCurso=\""+getCodigoCurso()+"\"";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while (rs.next()){ 
                subirSilabu(fis, longitudBytes,rs.getString(4));
            }
            JOptionPane.showMessageDialog(null, "Se agregaron a todas las matriculas");
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    /*codigo para subir silabu*/
    private FileInputStream fis =null;/*ahi se va almacenar el flujo de datos del archivo*/
    private int longitudBytes = 0;//longitud en bytes del archivo      
    public void selecionarArchivo(){//metodo para poder seleccionar el archivo
        JFileChooser se = new JFileChooser();
        se.setFileSelectionMode(JFileChooser.FILES_ONLY);//para selecionar solo archivos, no carpetas
        int estado = se.showOpenDialog(null);
        if(estado==JFileChooser.APPROVE_OPTION){//si el usuario dio aceptar
            try{
                fis = new FileInputStream(se.getSelectedFile());//archivo seleccionado
                this.longitudBytes = (int)se.getSelectedFile().length();//COMVIERTO A UN ENTERO SIMPLE
                //el nombre se queda el el textfield
                jTextFieldNombreSilabu.setText(String.valueOf(se.getSelectedFile().getName()));
                JOptionPane.showMessageDialog(null, "Se seleccionó correctamente el archivo");
            } catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }
    public void subirSilabu(FileInputStream archivo, int bytes,String codigosSemestre){
        try{
            Conectar cnx = new Conectar();
            Connection fotosql = cnx.getConnection();
            String sqlsilabu="UPDATE silabu  SET nombreSilabu = ?, archivoPDF = ? " +  "WHERE codCursoEstudianteSemestre =\"" + codigosSemestre+"\";";
            PreparedStatement stsilabu = fotosql.prepareStatement(sqlsilabu);
            stsilabu.setString(1,jTextFieldNombreSilabu.getText());
            stsilabu.setBlob(2, fis, bytes);
            stsilabu.executeUpdate();
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el statement
            stsilabu.close();
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    
    //private FileInputStream fis;//ahi se va almacenar el flujo de datos del archivo
    //private int longitudBytes;//longitud en bytes del archivo
    public void seleccionarFoto(){//metodo para poder seleccionar foto
        JFileChooser se = new JFileChooser();
        se.setFileSelectionMode(JFileChooser.FILES_ONLY);//para selecionar foto
        int estado = se.showOpenDialog(null);
        if(estado==JFileChooser.APPROVE_OPTION){//
            try{
                fis = new FileInputStream(se.getSelectedFile());//foto seleccionada
                this.longitudBytes = (int)se.getSelectedFile().length();//longitud
                Image icono = ImageIO.read(se.getSelectedFile()).getScaledInstance(jLabelFoto.getWidth(),jLabelFoto.getHeight(), Image.SCALE_DEFAULT);
                jLabelFoto.setIcon(new ImageIcon(icono));
                jLabelFoto.updateUI();   
            } catch (FileNotFoundException ex){ ex.printStackTrace();} 
            catch (IOException ex) { ex.printStackTrace();}
            }
    }
    public void subirFoto(FileInputStream foto, int longitud){//Codigo de subir foto
        try{
            Conectar cnx = new Conectar();
            Connection fotosql = cnx.getConnection();
            String sqlfoto="UPDATE persona  SET foto = ?" +  "WHERE dni = " + getDniProfesor();
            PreparedStatement stfoto = fotosql.prepareStatement(sqlfoto);
            stfoto.setBlob(1, fis, longitud);
            stfoto.executeUpdate();
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            stfoto.close();
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    public void cerrarSesión(){
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }
    public static int calcular(Calendar fechaNac) {
        Calendar fechaActual = Calendar.getInstance();
        // Cálculo de las diferencias.
        int years = fechaActual.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        int months = fechaActual.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
        int days = fechaActual.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);
 
        // Hay que comprobar si el día de su cumpleaños es posterior
        // a la fecha actual, para restar 1 a la diferencia de años,
        // pues aún no ha sido su cumpleaños.
 
        if(months < 0 // Aún no es el mes de su cumpleaños
           || (months==0 && days < 0)) { // o es el mes pero no ha llegado el día.
            years--;
        }
        return years;
    }
    public Calendar dateToCalendar(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	return calendar;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCabecera = new javax.swing.JPanel();
        jPanelInfoProf = new javax.swing.JPanel();
        jLabelFoto = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldNombreProfesor = new javax.swing.JTextField();
        jTextFieldDniProfesor = new javax.swing.JTextField();
        jTextFieldEdadProfesor = new javax.swing.JTextField();
        jTextFieldEscuelaProfesor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanelActividad = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanelCurso = new javax.swing.JPanel();
        jTextFieldNomCurso = new javax.swing.JLabel();
        jButtonAlumnos = new javax.swing.JButton();
        jButtonSeleccionarSilabu = new javax.swing.JButton();
        jButtonArchivos = new javax.swing.JButton();
        jTextFieldNombreSilabu = new javax.swing.JTextField();
        jButtonAgregarSilabu = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCursos = new javax.swing.JTable();
        jButtonCerrerSesion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanelCabecera.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanelCabeceraLayout = new javax.swing.GroupLayout(jPanelCabecera);
        jPanelCabecera.setLayout(jPanelCabeceraLayout);
        jPanelCabeceraLayout.setHorizontalGroup(
            jPanelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelCabeceraLayout.setVerticalGroup(
            jPanelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        jPanelInfoProf.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(102, 0, 102), new java.awt.Color(204, 0, 51)));

        jLabelFoto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabelFoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelFotoMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 0, 102));
        jLabel2.setText("Profesor :");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 0, 102));
        jLabel3.setText("DNI:");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 0, 102));
        jLabel4.setText("Edad :");

        jTextFieldNombreProfesor.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldNombreProfesor.setForeground(new java.awt.Color(153, 0, 102));
        jTextFieldNombreProfesor.setEnabled(false);
        jTextFieldNombreProfesor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNombreProfesorActionPerformed(evt);
            }
        });

        jTextFieldDniProfesor.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldDniProfesor.setForeground(new java.awt.Color(153, 0, 102));
        jTextFieldDniProfesor.setEnabled(false);

        jTextFieldEdadProfesor.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldEdadProfesor.setForeground(new java.awt.Color(153, 0, 102));
        jTextFieldEdadProfesor.setEnabled(false);

        jTextFieldEscuelaProfesor.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldEscuelaProfesor.setForeground(new java.awt.Color(153, 0, 102));
        jTextFieldEscuelaProfesor.setEnabled(false);

        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 102));
        jLabel6.setText("Escuela :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextFieldEdadProfesor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addComponent(jTextFieldDniProfesor, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldEscuelaProfesor)
                    .addComponent(jTextFieldNombreProfesor, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldNombreProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldDniProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldEdadProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldEscuelaProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelInfoProfLayout = new javax.swing.GroupLayout(jPanelInfoProf);
        jPanelInfoProf.setLayout(jPanelInfoProfLayout);
        jPanelInfoProfLayout.setHorizontalGroup(
            jPanelInfoProfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoProfLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelInfoProfLayout.setVerticalGroup(
            jPanelInfoProfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoProfLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInfoProfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelActividad.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(231, 166, 16), new java.awt.Color(255, 204, 0)));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextFieldNomCurso.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTextFieldNomCurso.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonAlumnos.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonAlumnos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/alumnos.png"))); // NOI18N
        jButtonAlumnos.setText("Alumnos");
        jButtonAlumnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlumnosActionPerformed(evt);
            }
        });

        jButtonSeleccionarSilabu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonSeleccionarSilabu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/seleccionarArchivo.png"))); // NOI18N
        jButtonSeleccionarSilabu.setText("Seleccionar Silabu");
        jButtonSeleccionarSilabu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSeleccionarSilabuActionPerformed(evt);
            }
        });

        jButtonArchivos.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonArchivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/archivos.png"))); // NOI18N
        jButtonArchivos.setText("Archivos");
        jButtonArchivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonArchivosActionPerformed(evt);
            }
        });

        jTextFieldNombreSilabu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTextFieldNombreSilabu.setEnabled(false);

        jButtonAgregarSilabu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonAgregarSilabu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/subirSilabu.png"))); // NOI18N
        jButtonAgregarSilabu.setText("Subir Silabu");
        jButtonAgregarSilabu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarSilabuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCursoLayout = new javax.swing.GroupLayout(jPanelCurso);
        jPanelCurso.setLayout(jPanelCursoLayout);
        jPanelCursoLayout.setHorizontalGroup(
            jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCursoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCursoLayout.createSequentialGroup()
                        .addComponent(jButtonAlumnos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonArchivos))
                    .addComponent(jTextFieldNomCurso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelCursoLayout.createSequentialGroup()
                        .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldNombreSilabu)
                            .addComponent(jButtonSeleccionarSilabu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(jButtonAgregarSilabu)))
                .addContainerGap())
        );
        jPanelCursoLayout.setVerticalGroup(
            jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCursoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldNomCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAlumnos)
                    .addComponent(jButtonArchivos))
                .addGap(18, 18, 18)
                .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldNombreSilabu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAgregarSilabu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSeleccionarSilabu)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.setLayer(jPanelCurso, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCurso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTableCursos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cursos", "Ciclo"
            }
        ));
        jTableCursos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCursosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCursos);

        jButtonCerrerSesion.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonCerrerSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/salida.png"))); // NOI18N
        jButtonCerrerSesion.setText("Cerrar Sesión");
        jButtonCerrerSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrerSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelActividadLayout = new javax.swing.GroupLayout(jPanelActividad);
        jPanelActividad.setLayout(jPanelActividadLayout);
        jPanelActividadLayout.setHorizontalGroup(
            jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelActividadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCerrerSesion))
                .addGap(18, 18, 18)
                .addComponent(jLayeredPane1)
                .addContainerGap())
        );
        jPanelActividadLayout.setVerticalGroup(
            jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelActividadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelActividadLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonCerrerSesion))
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCabecera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelInfoProf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelActividad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelInfoProf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanelActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAlumnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAlumnosActionPerformed
        // TODO add your handling code here:
        ProfesorAlumnos pAl=new ProfesorAlumnos();
        pAl.setCodCurso(getCodigoCurso());
        pAl.setVisible(true);
    }//GEN-LAST:event_jButtonAlumnosActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        Mostrar();
        CargaCursos();
        //cargar foto
        ImageIcon foto= getFoto(getDniProfesor());
        if(foto!=null){
            jLabelFoto.setIcon(foto);
        }else{
            jLabelFoto.setIcon(null);
        }
        jLabelFoto.updateUI();
    }//GEN-LAST:event_formWindowOpened

    private void jTextFieldNombreProfesorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombreProfesorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombreProfesorActionPerformed

    private void jTableCursosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCursosMouseClicked
        // TODO add your handling code here:
        int seleccion=jTableCursos.rowAtPoint(evt.getPoint());
        seleccionarCurso(String.valueOf(jTableCursos.getValueAt(seleccion,0)));//se obtiene el codigo del curso y se muestra el nombre
    }//GEN-LAST:event_jTableCursosMouseClicked

    private void jButtonSeleccionarSilabuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSeleccionarSilabuActionPerformed
        // TODO add your handling code here:
        selecionarArchivo();
    }//GEN-LAST:event_jButtonSeleccionarSilabuActionPerformed

    private void jButtonAgregarSilabuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarSilabuActionPerformed
        // TODO add your handling code here:
        //se agregar a todas las matriculas del curso
        AgregarSilabuAMatriculas();
    }//GEN-LAST:event_jButtonAgregarSilabuActionPerformed

    private void jButtonArchivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonArchivosActionPerformed
        // TODO add your handling code here:
        ProfesorArchivos pA=new ProfesorArchivos();
        pA.setCodCurso(getCodigoCurso());
        pA.setVisible(true);
    }//GEN-LAST:event_jButtonArchivosActionPerformed

    private void jButtonCerrerSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrerSesionActionPerformed
        // TODO add your handling code here:
        cerrarSesión();
    }//GEN-LAST:event_jButtonCerrerSesionActionPerformed

    private void jLabelFotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFotoMouseClicked
        // TODO add your handling code here:
        seleccionarFoto();
        subirFoto(fis,longitudBytes);
    }//GEN-LAST:event_jLabelFotoMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Profesor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Profesor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Profesor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Profesor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Profesor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAgregarSilabu;
    private javax.swing.JButton jButtonAlumnos;
    private javax.swing.JButton jButtonArchivos;
    private javax.swing.JButton jButtonCerrerSesion;
    private javax.swing.JButton jButtonSeleccionarSilabu;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelFoto;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelActividad;
    private javax.swing.JPanel jPanelCabecera;
    private javax.swing.JPanel jPanelCurso;
    private javax.swing.JPanel jPanelInfoProf;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCursos;
    private javax.swing.JTextField jTextFieldDniProfesor;
    private javax.swing.JTextField jTextFieldEdadProfesor;
    private javax.swing.JTextField jTextFieldEscuelaProfesor;
    private javax.swing.JLabel jTextFieldNomCurso;
    private javax.swing.JTextField jTextFieldNombreProfesor;
    private javax.swing.JTextField jTextFieldNombreSilabu;
    // End of variables declaration//GEN-END:variables
}
