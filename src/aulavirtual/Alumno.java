/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aulavirtual;

import Conexion.Conectar;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Blob;
import java.util.Calendar;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;


/**
 *
 * @author Alex
 */
public class Alumno extends javax.swing.JFrame {

    /**
     * Creates new form Alumno
     */
    public Alumno() {
        initComponents();     
    }
    
    /*creo una variable donde se va a almacenar el dni del estudiante*/
    private String dniEstudiante;

    public String getDniEstudiante() {
        return dniEstudiante;
    }
    public void setDniEstudiante(String dniEstudiante) {
        this.dniEstudiante = dniEstudiante;
    }
    
    private String codigoCurso;
    private String codCursoEstudianteSemestre;//la matricula
    private String codAsistencia;//asistencia de la semana
    private String nombreSeleccionado;//nombre del archivo seleccionado
    private String descripcionSeleccionado;//descripcion del arhcivo seleccionado

    public String getCodCursoEstudianteSemestre() {
        return codCursoEstudianteSemestre;
    }
    public void setCodCursoEstudianteSemestre(String codCursoEstudianteSemestre) {
        this.codCursoEstudianteSemestre = codCursoEstudianteSemestre;
    }
    public String getCodAsistencia() {
        return codAsistencia;
    }
    public void setCodAsistencia(String codAsistencia) {
        this.codAsistencia = codAsistencia;
    }
    public String getNombreSeleccionado() {
        return nombreSeleccionado;
    }
    public void setNombreSeleccionado(String nombreSeleccionado) {
        this.nombreSeleccionado = nombreSeleccionado;
    }
    public String getDescripcionSeleccionado() {
        return descripcionSeleccionado;
    }
    public void setDescripcionSeleccionado(String descripcionSeleccionado) {
        this.descripcionSeleccionado = descripcionSeleccionado;
    }
    public String getCodigoCurso() {
        return codigoCurso;
    }
    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }
    
    public void Mostrar(){//mostrar datos personales del alumno
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="Select  persona.nombre, estudiante.codEstudiante , persona.fechaNacimiento , escuela.nombreEscuela\n" +
            "from estudiante\n" +
            "inner join persona on estudiante.dni=persona.dni\n" +
            "inner join escuela on estudiante.codEscuela=escuela.codEscuela where persona.dni ='"+getDniEstudiante()+"';";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while (rs.next()){ 
                jTextFieldAlumno.setText(rs.getString(1));
                jTextFieldCodigo.setText(rs.getString(2));
                jTextFieldEdad.setText(String.valueOf(calcular(dateToCalendar(rs.getDate(3)))));
                jTextFieldCarrera.setText(rs.getString(4));
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
    
    public void CargaCursos(){//cargar cursos del alumno
        DefaultTableModel modelo=(DefaultTableModel) jTableCursos.getModel();
        modelo.setRowCount(0);//limpiar el modelo
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select curso.nombre_curso, ciclo.nombreciclo\n" +
            "from curso_estudiantesemestre\n" +
            "inner join curso on  curso_estudiantesemestre.codCurso=curso.codCurso\n" +
            "inner join ciclo on curso.numCiclo=ciclo.numCiclo\n" +
            "where curso_estudiantesemestre.codSemestre='2019A' and curso_estudiantesemestre.dni='"+getDniEstudiante()+"'";
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
    //luego de obtiene la matricula(codCursoEstudianteSemestre) cuando selecciono un curso
    public void obtenerMatricula(String codCurso){
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select curso_estudiantesemestre.codCursoEstudianteSemestre\n" +
            "from curso_estudiantesemestre\n" +
            "where curso_estudiantesemestre.codSemestre=\"2019A\" and curso_estudiantesemestre.dni=\""+getDniEstudiante()+"\"\n" +
            "and curso_estudiantesemestre.codCurso=\""+codCurso+"\"";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                setCodCursoEstudianteSemestre(rs.getString(1));
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
    
    public void obtenerCodigoAsistencia(String codigoSemana){
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            String sql="select *from Asistencia\n" +
            "where codCursoEstudianteSemestre=\""+getCodCursoEstudianteSemestre()+"\" and codSemana=\""+codigoSemana+"\"; ";
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            if(rs.next()){
                setCodAsistencia(rs.getString(1));
            }else{
                JOptionPane.showInputDialog("No se encontro la asistencia");
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
    
    public void seleccionarSemana(){
        switch((String)jComboBoxSemana.getSelectedItem()){
            case "Semana 1":
                obtenerCodigoAsistencia("01");
                break;
            case "Semana 2":
                obtenerCodigoAsistencia("02");
                break;
            case "Semana 3":
                obtenerCodigoAsistencia("03");
                break;
            case "Semana 4":
                obtenerCodigoAsistencia("04");
                break;
            case "Semana 5":
                obtenerCodigoAsistencia("05");
                break;
            case "Semana 6":
                obtenerCodigoAsistencia("06");
                break;
            case "Semana 7":
                obtenerCodigoAsistencia("07");
                break;
            case "Semana 8":
                obtenerCodigoAsistencia("08");
                break;
            case "Semana 9":
                obtenerCodigoAsistencia("09");
                break;
            case "Semana 10":
                obtenerCodigoAsistencia("10");
                break;
            case "Semana 11":
                obtenerCodigoAsistencia("11");
                break;
            case "Semana 12":
                obtenerCodigoAsistencia("12");
                break;
            case "Semana 13":
                obtenerCodigoAsistencia("13");
                break;
            case "Semana 14":
                obtenerCodigoAsistencia("14");
                break;
            case "Semana 15":
                obtenerCodigoAsistencia("15");
                break;
            case "Semana 16":
                obtenerCodigoAsistencia("16");
        } 
    }    
    
    public void refrescar(){// refresca la tabla de los archivos de la semana seleccionada
        DefaultTableModel modelo=(DefaultTableModel) jTableArchivos.getModel();
        modelo.setRowCount(0);//limpiar el modelo
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select Archivo.nombreArchivo, Archivo.descripcion from Archivo\n" +
            "where Archivo.codAsistencia='"+getCodAsistencia()+"'";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                Vector v=new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));
                modelo.addRow(v);
            }  
            jTableArchivos.setModel(modelo);
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el resultset y statement
            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    /*codigo para descargar archivo*/
    public void descargarArchivo(){
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            String sql="select *from Archivo\n" +
            "where nombreArchivo=\""+getNombreSeleccionado()+"\" and descripcion=\""+getDescripcionSeleccionado()+"\";";
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                Blob blob = rs.getBlob(5);
                InputStream is = blob.getBinaryStream();
                almacenarDiscoDuro(is,getNombreSeleccionado());
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
    
    public void descargarSilabu(){
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            String sql="select *from Silabu where codCursoEstudianteSemestre=\""+getCodCursoEstudianteSemestre()+"\";";
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                String nombresilabu =rs.getString(2);
                Blob blob = rs.getBlob(3);
                InputStream is = blob.getBinaryStream();
                almacenarDiscoDuro(is,nombresilabu);
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
    
    public void almacenarDiscoDuro(InputStream x,String nombre){
        JFileChooser de = new JFileChooser();
        de.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//para selecionar solo archivos, no carpetas
        int estado = de.showOpenDialog(null);
        if(estado==JFileChooser.APPROVE_OPTION){//si el usuario dio aceptar
            File directorio=de.getSelectedFile();
            String ruta=directorio.getAbsolutePath();
            File fichero = new File(ruta+"/"+nombre);
           
            //BufferedInputStream in = new BufferedInputStream(x);
            OutputStream out =null;
            try{
                out = new FileOutputStream(fichero);
                byte[] bytes=new byte[8095];
                int len=0;
                while( (len=x.read(bytes))>0 ){
                    out.write(bytes, 0, len);
                }
                //out.flush();
                out.close();
                x.close();
                JOptionPane.showMessageDialog(null, "Se decargó su archivo");
            }catch(FileNotFoundException e){
                System.out.println("Error"+e.getMessage());
            }catch(IOException e){
                System.out.println("Error"+e.getMessage());
            }
        }
        
    }
    /**/
    private FileInputStream fis;//ahi se va almacenar el flujo de datos del archivo
    private int longitudBytes;//longitud en bytes del archivo
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
            String sqlfoto="UPDATE persona  SET foto = ?" +  "WHERE dni = " + getDniEstudiante();
            PreparedStatement stfoto = fotosql.prepareStatement(sqlfoto);
            stfoto.setBlob(1, fis, longitud);
            stfoto.executeUpdate();
            cnx.desconectar();
            //Cierro el statement
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
        jLabel8 = new javax.swing.JLabel();
        jPanelInfoProf = new javax.swing.JPanel();
        jLabelFoto = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldAlumno = new javax.swing.JTextField();
        jTextFieldCodigo = new javax.swing.JTextField();
        jTextFieldEdad = new javax.swing.JTextField();
        jTextFieldCarrera = new javax.swing.JTextField();
        jPanelActividad = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanelCurso = new javax.swing.JPanel();
        jButtonDescargarSilabu = new javax.swing.JButton();
        jComboBoxSemana = new javax.swing.JComboBox<>();
        jButtonSeleccionar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableArchivos = new javax.swing.JTable();
        jTextFieldNomCurso = new javax.swing.JTextField();
        jButtonDescargarArchivo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCursos = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(840, 650));
        setResizable(false);
        setSize(new java.awt.Dimension(840, 650));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanelCabecera.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Algerian", 1, 36)); // NOI18N
        jLabel8.setForeground(java.awt.Color.blue);
        jLabel8.setText("UNIVERSIDAD NACIONAL DEL CALLAO");

        javax.swing.GroupLayout jPanelCabeceraLayout = new javax.swing.GroupLayout(jPanelCabecera);
        jPanelCabecera.setLayout(jPanelCabeceraLayout);
        jPanelCabeceraLayout.setHorizontalGroup(
            jPanelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCabeceraLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCabeceraLayout.setVerticalGroup(
            jPanelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanelInfoProf.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 0, 102), new java.awt.Color(51, 51, 255)));

        jLabelFoto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabelFoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelFotoMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setText("Alumno :");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 153));
        jLabel3.setText("Codigo :");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 153));
        jLabel4.setText("Fecha de nacimiento :");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("Carrera :");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 153));
        jLabel5.setText("2019-A");

        jTextFieldAlumno.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldAlumno.setForeground(new java.awt.Color(0, 0, 153));
        jTextFieldAlumno.setEnabled(false);

        jTextFieldCodigo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldCodigo.setForeground(new java.awt.Color(0, 0, 153));
        jTextFieldCodigo.setEnabled(false);

        jTextFieldEdad.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldEdad.setForeground(new java.awt.Color(0, 0, 153));
        jTextFieldEdad.setEnabled(false);

        jTextFieldCarrera.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextFieldCarrera.setForeground(new java.awt.Color(0, 0, 153));
        jTextFieldCarrera.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5))
                    .addComponent(jTextFieldCarrera)
                    .addComponent(jTextFieldEdad)
                    .addComponent(jTextFieldCodigo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldEdad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelInfoProfLayout = new javax.swing.GroupLayout(jPanelInfoProf);
        jPanelInfoProf.setLayout(jPanelInfoProfLayout);
        jPanelInfoProfLayout.setHorizontalGroup(
            jPanelInfoProfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoProfLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jPanelActividad.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 102, 0), new java.awt.Color(0, 204, 102)));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonDescargarSilabu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonDescargarSilabu.setForeground(new java.awt.Color(51, 102, 0));
        jButtonDescargarSilabu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/descargarSilabu.png"))); // NOI18N
        jButtonDescargarSilabu.setText("Descargar Silabu");
        jButtonDescargarSilabu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDescargarSilabuActionPerformed(evt);
            }
        });

        jComboBoxSemana.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jComboBoxSemana.setForeground(new java.awt.Color(51, 102, 0));
        jComboBoxSemana.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semana 1", "Semana 2", "Semana 3", "Semana 4", "Semana 5", "Semana 6", "Semana 7", "Semana 8", "Semana 9", "Semana 10", "Semana 11", "Semana 12", "Semana 13", "Semana 14", "Semana 15", "Semana 16" }));

        jButtonSeleccionar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonSeleccionar.setForeground(new java.awt.Color(51, 102, 0));
        jButtonSeleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Seleccionar.png"))); // NOI18N
        jButtonSeleccionar.setText("Seleccionar");
        jButtonSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSeleccionarActionPerformed(evt);
            }
        });

        jTableArchivos.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTableArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Descripcion"
            }
        ));
        jTableArchivos.setEnabled(false);
        jTableArchivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableArchivosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableArchivos);

        jTextFieldNomCurso.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTextFieldNomCurso.setEnabled(false);

        jButtonDescargarArchivo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonDescargarArchivo.setForeground(new java.awt.Color(51, 102, 0));
        jButtonDescargarArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/descargarArchivo.png"))); // NOI18N
        jButtonDescargarArchivo.setText("Descargar Archivo");
        jButtonDescargarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDescargarArchivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCursoLayout = new javax.swing.GroupLayout(jPanelCurso);
        jPanelCurso.setLayout(jPanelCursoLayout);
        jPanelCursoLayout.setHorizontalGroup(
            jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCursoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelCursoLayout.createSequentialGroup()
                        .addComponent(jComboBoxSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonSeleccionar))
                    .addGroup(jPanelCursoLayout.createSequentialGroup()
                        .addComponent(jButtonDescargarArchivo)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonDescargarSilabu))
                    .addComponent(jTextFieldNomCurso)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCursoLayout.setVerticalGroup(
            jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCursoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldNomCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxSemana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSeleccionar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDescargarArchivo)
                    .addComponent(jButtonDescargarSilabu))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.setLayer(jPanelCurso, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTableCursos.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTableCursos.setForeground(new java.awt.Color(51, 102, 0));
        jTableCursos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cursos", "Ciclo"
            }
        ));
        jTableCursos.setEnabled(false);
        jTableCursos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCursosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCursos);

        jButton3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/salida.png"))); // NOI18N
        jButton3.setText("Cerrar Sesión");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelActividadLayout = new javax.swing.GroupLayout(jPanelActividad);
        jPanelActividad.setLayout(jPanelActividadLayout);
        jPanelActividadLayout.setHorizontalGroup(
            jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelActividadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelActividadLayout.setVerticalGroup(
            jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelActividadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelActividadLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelCabecera, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelInfoProf, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelActividad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelInfoProf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        Mostrar();
        CargaCursos();
        //cargar foto
        ImageIcon foto= getFoto(getDniEstudiante());
        if(foto!=null){
            jLabelFoto.setIcon(foto);
        }else{
            jLabelFoto.setIcon(null);
        }
        jLabelFoto.updateUI();
        
    }//GEN-LAST:event_formWindowOpened

    private void jTableCursosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCursosMouseClicked
      // TODO add your handling code here:
      int seleccion=jTableCursos.rowAtPoint(evt.getPoint());
      seleccionarCurso(String.valueOf(jTableCursos.getValueAt(seleccion,0)));//se obtiene el codigo del curso y se muestra el nombre
      obtenerMatricula(getCodigoCurso());
    }//GEN-LAST:event_jTableCursosMouseClicked

    private void jButtonSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSeleccionarActionPerformed
        // TODO add your handling code here:
        seleccionarSemana();
        refrescar();//refresca la tabla de archivos
    }//GEN-LAST:event_jButtonSeleccionarActionPerformed

    private void jButtonDescargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescargarArchivoActionPerformed
        // TODO add your handling code here:
        descargarArchivo();
    }//GEN-LAST:event_jButtonDescargarArchivoActionPerformed

    private void jTableArchivosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableArchivosMouseClicked
        // TODO add your handling code here:
        int seleccion = jTableArchivos.rowAtPoint(evt.getPoint()); //muestra el numero de la fila selecionada
        setNombreSeleccionado(String.valueOf(jTableArchivos.getValueAt(seleccion, 0)));
        setDescripcionSeleccionado(String.valueOf(jTableArchivos.getValueAt(seleccion, 1)));
        
    }//GEN-LAST:event_jTableArchivosMouseClicked

    private void jButtonDescargarSilabuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescargarSilabuActionPerformed
        // TODO add your handling code here:
        descargarSilabu();
    }//GEN-LAST:event_jButtonDescargarSilabuActionPerformed
    
    
    private void jLabelFotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFotoMouseClicked
        // TODO add your handling code here:
        seleccionarFoto();
        subirFoto(fis,longitudBytes);
    }//GEN-LAST:event_jLabelFotoMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        cerrarSesión();
    }//GEN-LAST:event_jButton3ActionPerformed
    
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
            java.util.logging.Logger.getLogger(Alumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Alumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Alumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Alumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Alumno().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonDescargarArchivo;
    private javax.swing.JButton jButtonDescargarSilabu;
    private javax.swing.JButton jButtonSeleccionar;
    private javax.swing.JComboBox<String> jComboBoxSemana;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelFoto;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelActividad;
    private javax.swing.JPanel jPanelCabecera;
    private javax.swing.JPanel jPanelCurso;
    private javax.swing.JPanel jPanelInfoProf;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableArchivos;
    private javax.swing.JTable jTableCursos;
    private javax.swing.JTextField jTextFieldAlumno;
    private javax.swing.JTextField jTextFieldCarrera;
    private javax.swing.JTextField jTextFieldCodigo;
    private javax.swing.JTextField jTextFieldEdad;
    private javax.swing.JTextField jTextFieldNomCurso;
    // End of variables declaration//GEN-END:variables
}
