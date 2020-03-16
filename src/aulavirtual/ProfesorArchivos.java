/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aulavirtual;

import Conexion.Conectar;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.sql.Blob;

/**
 *
 * @author Alex
 */
public class ProfesorArchivos extends javax.swing.JFrame {

    /**
     * Creates new form ProfesorArchivos
     */
    public ProfesorArchivos() {
        initComponents();
    }
    
    
    /*INICIA MI CODIGO*/
    private String[] codCursoEstudianteSemestre;
    private String[] codAsistencia;
    
    //AGREGO VARIABLE PARA ALMACENAR EL CODIGO DEL CURSO Y OBTENERLO DEL FORM PROFESOR
    private String codCurso;
    //
    
    //variables cuando selecciono algo de la tabla
    private String nombreSeleccionado;
    private String descripcionSeleccionado;

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

    public String getCodCurso() {
        return codCurso;
    }

    public void setCodCurso(String codCurso) {
        this.codCurso = codCurso;
    }
    
    //metodos
    public void obtenercodCursoEstudianteSemestre(){//Listo, revisado
        int numrow=0;
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            String sql="select codCursoEstudianteSemestre from curso_estudiantesemestre "
                    + "where codCurso=\""+getCodCurso()+"\" AND codSemestre=\"2019A\"; ";
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                numrow++;
            }
            codCursoEstudianteSemestre = new String[numrow];
            int i=0;
            rs.beforeFirst();
            while(rs.next()){
                codCursoEstudianteSemestre[i]=rs.getString(1);
                i++;
            }
            //Cerrar el resultset y statement
            rs.close();
            st.close();
            //Cierro la conexion
            cnx.desconectar();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
        
    }
    
    public void obtenerCodigoAsistencia(String codigoSemana){//Listo y revisado
        String[] vector = new String[codCursoEstudianteSemestre.length];
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            for (int i=0;i<codCursoEstudianteSemestre.length;i++){
                String sql="select codAsistencia from asistencia "
                        + "where codCursoEstudianteSemestre=\""+codCursoEstudianteSemestre[i]+"\" and codSemana=\""+codigoSemana+"\"; ";
                PreparedStatement st = registro.prepareStatement(sql);
                ResultSet rs= st.executeQuery();
                if(rs.next()){
                    vector[i]=rs.getString(1);
                }else{
                    JOptionPane.showMessageDialog(null,"No se encontro la asistencia para la matricula : "+codCursoEstudianteSemestre[i]);
                }
                //Cerrar el resultset y statement
                rs.close();
                st.close();
            }
            //quitar los null del arreglo
            int j=0;
            for(int i=0;i<vector.length;i++){
                if(vector[i]!=null){
                    j++;
                }
            }
            codAsistencia = new String[j];
            int k=0;
            for(int i=0;i<vector.length;i++){
                if(vector[i]!=null){
                    codAsistencia[k]=vector[i];
                    k++;
                }
            }
            //Cierro la conexion
            cnx.desconectar();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    public void seleccionarSemena(){
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
    
    public void refrescar(){//Listo, revisado
        DefaultTableModel modelo=(DefaultTableModel) jTableArchivos.getModel();
        modelo.setRowCount(0);//limpiar el modelo
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select Archivo.nombreArchivo, Archivo.descripcion from Archivo\n" +
            "where Archivo.codAsistencia='"+codAsistencia[0]+"'";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                Vector v=new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));
                modelo.addRow(v);
            }  
            jTableArchivos.setModel(modelo);
            //Cerrar el resultset y statement
            rs.close();
            st.close();
            //Cierro la conexion
            cnx.desconectar();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    public void eliminarArchivo(){//Listo, revisado
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            for(int i=0;i<codAsistencia.length;i++){
                String sql="delete from Archivo\n" +
                    "where nombreArchivo=\""+getNombreSeleccionado()+"\" and descripcion=\""+getDescripcionSeleccionado()+"\" "
                        + " and codAsistencia='"+codAsistencia[i]+"'";
                PreparedStatement st = registro.prepareStatement(sql);
                st.executeUpdate();
                //Cerrar el statement
                st.close();
            }
            //Cierro la conexion
            cnx.desconectar();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    /*codigo para subir archivos*/
    private FileInputStream fis;/*ahi se va almacenar el flujo de datos del archivo*/
    private int longitudBytes;//longitud en bytes del archivo      
    public void selecionarArchivo(){//metodo para poder seleccionar el archivo
        JFileChooser se = new JFileChooser();
        se.setFileSelectionMode(JFileChooser.FILES_ONLY);//para selecionar solo archivos, no carpetas
        int estado = se.showOpenDialog(null);
        if(estado==JFileChooser.APPROVE_OPTION){//si el usuario dio aceptar
            try{
                fis = new FileInputStream(se.getSelectedFile());//archivo seleccionado
                this.longitudBytes = (int)se.getSelectedFile().length();//COMVIERTO A UN ENTERO SIMPEL
                
                 /*lo nuevo*/
                jTextFieldNombreArchivo.setText(String.valueOf(se.getSelectedFile().getName()));
                  /*lo nuevo*/
                JOptionPane.showMessageDialog(null, "se seleccionó el archivo");
                /*crear el icono*/
                //Image icono = ImageIO.read( se.getSelectedFile().getScaleInstance() );
            } catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }
    public void subirArchivo(FileInputStream archivo, int bytes){//Listo, revisado
        try{
            Conectar cnx = new Conectar();
            Connection archivosql = cnx.getConnection();
            for(int i=0; i<codAsistencia.length;i++){
                String sqlarchivo="insert into Archivo(codAsistencia,nombreArchivo,descripcion,archivo) values(?,?,?,?);";
                PreparedStatement starchivo = archivosql.prepareStatement(sqlarchivo);
                starchivo.setString(1,codAsistencia[i]);
                starchivo.setString(2,jTextFieldNombreArchivo.getText());
                starchivo.setString(3,jTextArea1.getText());
                starchivo.setBlob(4, fis, bytes);
                starchivo.executeUpdate();
                //Cerrar el statement
                starchivo.close();
            }
            //Cierro la conexion
            cnx.desconectar();
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    /**/
    /*codigo para descargar archivo*/
    public void descargarArchivo(){//Listo,revisado
        try{
            Conectar cnx = new Conectar();
            Connection registro = cnx.getConnection();
            String sql="select *from Archivo\n" +
            "where nombreArchivo=\""+getNombreSeleccionado()+"\" and descripcion=\""+getDescripcionSeleccionado()+"\""
                        + " and codAsistencia='"+codAsistencia[0]+"'";
            PreparedStatement st = registro.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                Blob blob = rs.getBlob(5);
                InputStream is = blob.getBinaryStream();
                almacenarDiscoDuro(is,getNombreSeleccionado());
            }
            //Cerrar el resultset y statement
            st.close();
            rs.close();
            //Cierro la conexion
            cnx.desconectar();
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
    
    /*TERMINA MI CODIGO*/

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCabecera = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableArchivos = new javax.swing.JTable();
        jComboBoxSemana = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButtonEliminarArchivo = new javax.swing.JButton();
        jButtonDescarga = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButtonAgregarArchivo = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNombreArchivo = new javax.swing.JTextField();
        jButtonRetornar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(580, 630));
        setResizable(false);
        setSize(new java.awt.Dimension(580, 630));
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
            .addGap(0, 556, Short.MAX_VALUE)
        );
        jPanelCabeceraLayout.setVerticalGroup(
            jPanelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 0, 0), new java.awt.Color(255, 153, 0)));

        jTableArchivos.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableArchivos.setForeground(new java.awt.Color(0, 102, 51));
        jTableArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Descripcion"
            }
        ));
        jTableArchivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableArchivosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableArchivos);

        jComboBoxSemana.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jComboBoxSemana.setForeground(new java.awt.Color(51, 0, 102));
        jComboBoxSemana.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semana 1", "Semana 2", "Semana 3", "Semana 4", "Semana 5", "Semana 6", "Semana 7", "Semana 8", "Semana 9", "Semana 10", "Semana 11", "Semana 12", "Semana 13", "Semana 14", "Semana 15", "Semana 16" }));

        jButton1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(51, 0, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Seleccionar.png"))); // NOI18N
        jButton1.setText("Seleccionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButtonEliminarArchivo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonEliminarArchivo.setForeground(new java.awt.Color(51, 0, 102));
        jButtonEliminarArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/eliminarArchivo.png"))); // NOI18N
        jButtonEliminarArchivo.setText("Eliminar Archivo");
        jButtonEliminarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEliminarArchivoActionPerformed(evt);
            }
        });

        jButtonDescarga.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonDescarga.setForeground(new java.awt.Color(51, 0, 102));
        jButtonDescarga.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/descargarArchivo.png"))); // NOI18N
        jButtonDescarga.setText("Descarga");
        jButtonDescarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDescargaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jComboBoxSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jButton1)
                        .addGap(36, 209, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonDescarga)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonEliminarArchivo)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxSemana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonEliminarArchivo)
                    .addComponent(jButtonDescarga))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 0, 153), new java.awt.Color(51, 102, 255)), "Subir Archivos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(0, 102, 51));
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jButtonAgregarArchivo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonAgregarArchivo.setForeground(new java.awt.Color(51, 0, 102));
        jButtonAgregarArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/subirSilabu.png"))); // NOI18N
        jButtonAgregarArchivo.setText("Agregar Archivo");
        jButtonAgregarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarArchivoActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(51, 0, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/seleccionarArchivo.png"))); // NOI18N
        jButton2.setText("Subir Archivo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 0, 51));
        jLabel1.setText("Descripcion :");

        jTextFieldNombreArchivo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTextFieldNombreArchivo.setEnabled(false);

        jButtonRetornar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonRetornar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/salida.png"))); // NOI18N
        jButtonRetornar.setText("Retornar");
        jButtonRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jButtonRetornar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonAgregarArchivo))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldNombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jTextFieldNombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAgregarArchivo)
                    .addComponent(jButtonRetornar))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCabecera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        seleccionarSemena();
        refrescar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        selecionarArchivo();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonAgregarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarArchivoActionPerformed
        // TODO add your handling code here:
        subirArchivo(fis,longitudBytes);
        refrescar();
        //limpiar los campos
        fis=null;
        longitudBytes=0;
        jTextFieldNombreArchivo.setText("");
        jTextArea1.setText("");
    }//GEN-LAST:event_jButtonAgregarArchivoActionPerformed

    private void jTableArchivosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableArchivosMouseClicked
        // TODO add your handling code here:
        int seleccion = jTableArchivos.rowAtPoint(evt.getPoint()); //muestra el numero de la fila selecionada
        setNombreSeleccionado(String.valueOf(jTableArchivos.getValueAt(seleccion, 0)));
        setDescripcionSeleccionado(String.valueOf(jTableArchivos.getValueAt(seleccion, 1)));
    }//GEN-LAST:event_jTableArchivosMouseClicked

    private void jButtonEliminarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarArchivoActionPerformed
        // TODO add your handling code here:
        eliminarArchivo();
        refrescar();
    }//GEN-LAST:event_jButtonEliminarArchivoActionPerformed

    private void jButtonDescargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescargaActionPerformed
        // TODO add your handling code here:
        descargarArchivo();
    }//GEN-LAST:event_jButtonDescargaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        obtenercodCursoEstudianteSemestre();
    }//GEN-LAST:event_formWindowOpened

    private void jButtonRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetornarActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButtonRetornarActionPerformed

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
            java.util.logging.Logger.getLogger(ProfesorArchivos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProfesorArchivos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProfesorArchivos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProfesorArchivos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProfesorArchivos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonAgregarArchivo;
    private javax.swing.JButton jButtonDescarga;
    private javax.swing.JButton jButtonEliminarArchivo;
    private javax.swing.JButton jButtonRetornar;
    private javax.swing.JComboBox<String> jComboBoxSemana;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelCabecera;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableArchivos;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldNombreArchivo;
    // End of variables declaration//GEN-END:variables
}
