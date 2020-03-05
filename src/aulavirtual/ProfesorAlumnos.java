/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aulavirtual;

import Conexion.Conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author apaza
 */
public class ProfesorAlumnos extends javax.swing.JFrame {

    /**
     * Creates new form ProfesorAlumnos
     */
    public ProfesorAlumnos() {
        initComponents();
    }
    
    //AGREGO VARIABLE PARA ALMACENAR EL CODIGO DEL CURSO Y OBTENERLO DEL FORM PROFESOR
    private String codCurso;
    private String codSemana;
    private String codAsistencia;
    

    public String getCodCurso() {
        return codCurso;
    }

    public void setCodCurso(String codCurso) {
        this.codCurso = codCurso;
    }

    public String getCodSemana() {
        return codSemana;
    }

    public void setCodSemana(String codSemana) {
        this.codSemana = codSemana;
    }

    public String getCodAsistencia() {
        return codAsistencia;
    }

    public void setCodAsistencia(String codAsistencia) {
        this.codAsistencia = codAsistencia;
    }
    
    public void rellenarCurso(){
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select nombre_curso \n" +
                "from curso \n" +
                "where codCurso=\""+getCodCurso()+"\"";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                jLabelNombreCurso.setText(rs.getString(1));
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
    
    public void seleccionarSemana(){
        switch((String)jComboBoxSemana.getSelectedItem()){
            case "Semana 1":
                setCodSemana("01");
                jLabelSemana.setText("Semana 1");
                break;
            case "Semana 2":
                setCodSemana("02");
                jLabelSemana.setText("Semana 2");
                break;
            case "Semana 3":
                setCodSemana("03");
                jLabelSemana.setText("Semana 3");
                break;
            case "Semana 4":
                setCodSemana("04");
                jLabelSemana.setText("Semana 4");
                break;
            case "Semana 5":
                setCodSemana("05");
                jLabelSemana.setText("Semana 5");
                break;
            case "Semana 6":
                setCodSemana("06");
                jLabelSemana.setText("Semana 6");
                break;
            case "Semana 7":
                setCodSemana("07");
                jLabelSemana.setText("Semana 7");
                break;
            case "Semana 8":
                setCodSemana("08");
                jLabelSemana.setText("Semana 8");
                break;
            case "Semana 9":
                setCodSemana("09");
                jLabelSemana.setText("Semana 9");
                break;
            case "Semana 10":
                setCodSemana("10");
                jLabelSemana.setText("Semana 10");
                break;
            case "Semana 11":
                setCodSemana("11");
                jLabelSemana.setText("Semana 11");
                break;
            case "Semana 12":
                setCodSemana("12");
                jLabelSemana.setText("Semana 12");
                break;
            case "Semana 13":
                setCodSemana("13");
                jLabelSemana.setText("Semana 13");
                break;
            case "Semana 14":
                setCodSemana("14");
                jLabelSemana.setText("Semana 14");
                break;
            case "Semana 15":
                setCodSemana("15");
                jLabelSemana.setText("Semana 15");
                break;
            case "Semana 16":
                setCodSemana("16");
                jLabelSemana.setText("Semana 16");
        } 
    }
    
    public void refrescar(){//Listo, revisado
        DefaultTableModel modelo=(DefaultTableModel) jTableAlumnos.getModel();
        modelo.setRowCount(0);//limpiar el modelo
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select curso_estudiantesemestre.codCursoEstudianteSemestre, estudiante.codEstudiante, estudiante.dni, persona.nombre, asistencia.estado\n" +
                "from curso_estudiantesemestre \n" +
                "inner join estudiante\n" +
                "on curso_estudiantesemestre.dni=estudiante.dni\n" +
                "inner join persona\n" +
                "on estudiante.dni=persona.dni\n" +
                "inner join asistencia\n" +
                "on curso_estudiantesemestre.codCursoEstudianteSemestre=asistencia.codCursoEstudianteSemestre\n" +
                "where codCurso=\""+getCodCurso()+"\" AND codSemestre=\"2019A\" AND codSemana=\""+getCodSemana()+"\"";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            while(rs.next()){
                Vector v=new Vector();
                v.add(rs.getString(2));
                v.add(rs.getString(3));
                v.add(rs.getString(4));
                if(rs.getObject(5)==null){//para ver si es null
                    v.add("No asignado");
                }else{
                    v.add(rs.getString(5));
                }
                modelo.addRow(v);
            }  
            jTableAlumnos.setModel(modelo);
            //Cerrar el resultset y statement
            rs.close();
            st.close();
            //Cierro la conexion
            cnx.desconectar();
        }catch(SQLException e){
            System.out.println("Error"+e.getMessage());
        }
    }
    
    public void seleccionarCodAsistencia(String dni){
        try{
            Conectar cnx = new Conectar();
            Connection registros = cnx.getConnection();
            String sql="select asistencia.codAsistencia\n" +
                "from curso_estudiantesemestre \n" +
                "inner join estudiante\n" +
                "on curso_estudiantesemestre.dni=estudiante.dni\n" +
                "inner join persona\n" +
                "on estudiante.dni=persona.dni\n" +
                "inner join asistencia\n" +
                "on curso_estudiantesemestre.codCursoEstudianteSemestre=asistencia.codCursoEstudianteSemestre\n" +
                "where codCurso=\""+getCodCurso()+"\" AND codSemestre=\"2019A\" AND codSemana=\""+getCodSemana()+"\" AND curso_estudiantesemestre.dni=\""+dni+"\"";
            PreparedStatement st = registros.prepareStatement(sql);
            ResultSet rs= st.executeQuery();
            if(rs.next()){
                setCodAsistencia(rs.getString(1));
            }else{
                JOptionPane.showMessageDialog(null, "No se encontró el codigo de asistencia");
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
    
    public void Asistio(){
        try{
            Conectar cnx = new Conectar();
            Connection sql = cnx.getConnection();
            String sqlsilabu="UPDATE asistencia SET estado = ? " +  "WHERE codAsistencia =\"" +getCodAsistencia()+"\";";
            PreparedStatement st = sql.prepareStatement(sqlsilabu);
            st.setString(1,"Asistió");
            st.executeUpdate();
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el statement
            st.close();
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    
    public void noAsistio(){
        try{
            Conectar cnx = new Conectar();
            Connection sql = cnx.getConnection();
            String sqlsilabu="UPDATE asistencia SET estado = ? " +  "WHERE codAsistencia =\"" +getCodAsistencia()+"\";";
            PreparedStatement st = sql.prepareStatement(sqlsilabu);
            st.setString(1,"No asistió");
            st.executeUpdate();
            //Cierro la conexion
            cnx.desconectar();
            //Cierro el statement
            st.close();
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAlumnos = new javax.swing.JTable();
        jLabelNombreCurso = new javax.swing.JLabel();
        jButtonAsistencia = new javax.swing.JButton();
        jButtonInasistencia = new javax.swing.JButton();
        jLabelSemana = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jComboBoxSemana = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButtonRetornar = new javax.swing.JButton();

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
            .addGap(0, 675, Short.MAX_VALUE)
        );
        jPanelCabeceraLayout.setVerticalGroup(
            jPanelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Dni", "Nombre", "Asistencia"
            }
        ));
        jTableAlumnos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableAlumnosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableAlumnos);

        jLabelNombreCurso.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButtonAsistencia.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonAsistencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tick.png"))); // NOI18N
        jButtonAsistencia.setText("Asistió");
        jButtonAsistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAsistenciaActionPerformed(evt);
            }
        });

        jButtonInasistencia.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonInasistencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cross.png"))); // NOI18N
        jButtonInasistencia.setText("No asistió");
        jButtonInasistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInasistenciaActionPerformed(evt);
            }
        });

        jLabelSemana.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSemana.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelNombreCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAsistencia)
                    .addComponent(jButtonInasistencia))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelNombreCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSemana, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jButtonAsistencia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonInasistencia))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        jButtonRetornar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonRetornar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/salida.png"))); // NOI18N
        jButtonRetornar.setText("Retornar");
        jButtonRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonRetornar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButtonRetornar)
                    .addComponent(jComboBoxSemana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCabecera, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        rellenarCurso();//relleno el nombre del curso
        seleccionarSemana();//asigno el codigo de semana
        refrescar();
    }//GEN-LAST:event_formWindowOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        seleccionarSemana();
        refrescar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTableAlumnosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableAlumnosMouseClicked
        // TODO add your handling code here:
        jTableAlumnos.setRowSelectionAllowed(true);
        jTableAlumnos.setColumnSelectionAllowed(false);
        int seleccion=jTableAlumnos.rowAtPoint(evt.getPoint());
        seleccionarCodAsistencia(String.valueOf(jTableAlumnos.getValueAt(seleccion,1)));//se obtiene el codigo del curso y se muestra el nombre
    }//GEN-LAST:event_jTableAlumnosMouseClicked

    private void jButtonAsistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAsistenciaActionPerformed
        // TODO add your handling code here:
        Asistio();
        refrescar();
    }//GEN-LAST:event_jButtonAsistenciaActionPerformed

    private void jButtonInasistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInasistenciaActionPerformed
        // TODO add your handling code here:
        noAsistio();
        refrescar();
    }//GEN-LAST:event_jButtonInasistenciaActionPerformed

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
            java.util.logging.Logger.getLogger(ProfesorAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProfesorAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProfesorAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProfesorAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProfesorAlumnos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAsistencia;
    private javax.swing.JButton jButtonInasistencia;
    private javax.swing.JButton jButtonRetornar;
    private javax.swing.JComboBox<String> jComboBoxSemana;
    private javax.swing.JLabel jLabelNombreCurso;
    private javax.swing.JLabel jLabelSemana;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCabecera;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableAlumnos;
    // End of variables declaration//GEN-END:variables
}
