/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;

import DAO.RoomDAO;
import UI.RoomDialog;
import model.Room;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends javax.swing.JFrame {
    private RoomDAO roomDAO;
    private DefaultTableModel tableModel;

    /**
     * Creates new form AdminPanel
     */
    public AdminPanel() {
        initComponents();
        setLocationRelativeTo(null);
        roomDAO = new RoomDAO();
        initializeTable();
        loadRoomsData();
        
        // Enable auto-resize for better table display
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    
    private void initializeTable() {
        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Name", "Type", "Capacity", "Hourly Rate"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        jTable1.setModel(tableModel);
    }

    private void loadRoomsData() {
        // Clear existing data while preserving column headers
        tableModel.setRowCount(0);
        
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room room : rooms) {
            tableModel.addRow(new Object[]{
                room.getId(),
                room.getName(),
                room.gettype(),  
                room.getCapacity(),
                String.format("$%.2f", room.getHourlyRate())  
            });
        }
        
        // Auto-size columns after loading data
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
    }
    
    private void addRoom() {
        RoomDialog dialog = new RoomDialog(this, true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            String name = dialog.getName();
            String type = dialog.gettype();  // Fixed to getType()
            int capacity = dialog.getCapacity();
            double hourlyRate = dialog.getHourlyRate();

            if (roomDAO.addRoom(name, type, capacity, hourlyRate)) {
                // Refresh the table with new data
                refreshTableWithNewRoom(name, type, capacity, hourlyRate);
                JOptionPane.showMessageDialog(this,
                        "Room added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add room",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    
    
    
    private void editRoom() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Room room = roomDAO.getRoomById(id);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RoomDialog dialog = new RoomDialog(this, true, room);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Room updatedRoom = new Room(
                id,
                dialog.getName(),
                dialog.gettype(),
                dialog.getCapacity(),
                dialog.getHourlyRate()
            );

            if (roomDAO.updateRoom(updatedRoom)) {
                JOptionPane.showMessageDialog(this, "Room updated successfully!");
                loadRoomsData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update room", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteRoom() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete this room?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (roomDAO.deleteRoom(id)) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                loadRoomsData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshTableWithNewRoom(String name, String type, int capacity, double hourlyRate) {
        // Get the latest data including the newly added room
        List<Room> rooms = roomDAO.getAllRooms();
        
        // Find the newly added room (last one in list)
        Room newRoom = rooms.get(rooms.size() - 1);
        
        // Add to table model
        tableModel.addRow(new Object[]{
            newRoom.getId(),
            newRoom.getName(),
            newRoom.gettype(),
            newRoom.getCapacity(),
            String.format("$%.2f", newRoom.getHourlyRate())
        });
        
        // Scroll to the new row
        jTable1.scrollRectToVisible(jTable1.getCellRect(tableModel.getRowCount()-1, 0, true));
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Title = new javax.swing.JLabel();
        TittlePanel = new javax.swing.JPanel();
        Title1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        EditButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        Title.setBackground(new java.awt.Color(254, 93, 38));
        Title.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        Title.setForeground(new java.awt.Color(254, 93, 38));
        Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Title.setText("KKK");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TittlePanel.setBackground(new java.awt.Color(242, 192, 120));
        TittlePanel.setForeground(new java.awt.Color(250, 237, 202));

        Title1.setBackground(new java.awt.Color(254, 93, 38));
        Title1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        Title1.setForeground(new java.awt.Color(254, 93, 38));
        Title1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Title1.setText("KKK");

        javax.swing.GroupLayout TittlePanelLayout = new javax.swing.GroupLayout(TittlePanel);
        TittlePanel.setLayout(TittlePanelLayout);
        TittlePanelLayout.setHorizontalGroup(
            TittlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TittlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Title1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(35, 35, 35))
        );
        TittlePanelLayout.setVerticalGroup(
            TittlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Title1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.setBackground(new java.awt.Color(250, 237, 202));
        jPanel1.setForeground(new java.awt.Color(250, 237, 202));

        EditButton.setBackground(new java.awt.Color(254, 93, 38));
        EditButton.setText("Edit");
        EditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditButtonActionPerformed(evt);
            }
        });

        AddButton.setBackground(new java.awt.Color(254, 93, 38));
        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        DeleteButton.setBackground(new java.awt.Color(254, 93, 38));
        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Name", "Type", "Capacity ", "Hourly Rate (USD)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(0, 0, 0));
        jTable1.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(EditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EditButton)
                    .addComponent(AddButton)
                    .addComponent(DeleteButton))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TittlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(TittlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        addRoom();
        
        if (tableModel.getRowCount() > 0) {
        jTable1.setRowSelectionInterval(tableModel.getRowCount()-1, tableModel.getRowCount()-1);
    }
    }//GEN-LAST:event_AddButtonActionPerformed

    private void EditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditButtonActionPerformed
        editRoom();
    }//GEN-LAST:event_EditButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        deleteRoom();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    
    
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
            java.util.logging.Logger.getLogger(AdminPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminPanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton EditButton;
    private javax.swing.JLabel Title;
    private javax.swing.JLabel Title1;
    private javax.swing.JPanel TittlePanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    
    
    
}