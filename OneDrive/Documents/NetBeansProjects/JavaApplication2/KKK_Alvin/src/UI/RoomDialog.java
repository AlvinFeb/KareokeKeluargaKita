package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import model.Room;

public class RoomDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JSpinner capacitySpinner;
    private JSpinner rateSpinner;
    private boolean confirmed = false;

    public RoomDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initializeComponents();
    }

    RoomDialog(AdminPanel aThis, boolean b, Room room) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void initializeComponents() {
        setLayout(new GridLayout(5, 2, 5, 5));
        
        add(new JLabel("Username:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(new String[]{"small", "medium", "large", "vip"});
        add(typeComboBox);

        add(new JLabel("Capacity:"));
        capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        add(capacitySpinner);

        add(new JLabel("Hourly Rate:"));
        rateSpinner = new JSpinner(new SpinnerNumberModel(10.0, 1.0, 1000.0, 0.5));
        add(rateSpinner);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(this::confirmAction);
        add(confirmButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        add(cancelButton);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void confirmAction(ActionEvent e) {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        confirmed = true;
        setVisible(false);
    }

    public boolean isConfirmed() { return confirmed; }
    public String getName() { return nameField.getText().trim(); }
    public String gettype() { return (String) typeComboBox.getSelectedItem(); }  
    public int getCapacity() { return (Integer) capacitySpinner.getValue(); }
    public double getHourlyRate() { return (Double) rateSpinner.getValue(); }
}