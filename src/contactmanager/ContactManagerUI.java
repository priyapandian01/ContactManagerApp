package contactmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ContactManagerUI extends JFrame {
    private ContactDAO dao = new ContactDAO();
    private JTable table;
    private DefaultTableModel model;

    public ContactManagerUI() {
        dao.createTable();

        setTitle("Contact Manager");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Address", "Email", "Phone"}, 0);
        table = new JTable(model);
        loadContacts();

        JScrollPane pane = new JScrollPane(table);
        add(pane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        add(panel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> openForm(null));
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Contact c = getContactFromRow(row);
                openForm(c);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                dao.deleteContact(id);
                loadContacts();
            }
        });

        setVisible(true);
    }

    private void loadContacts() {
        model.setRowCount(0);
        ArrayList<Contact> list = dao.getAllContacts();
        for (Contact c : list) {
            model.addRow(new Object[]{c.getId(), c.getFirstName(), c.getLastName(), c.getAddress(), c.getEmail(), c.getPhone()});
        }
    }

    private Contact getContactFromRow(int row) {
        return new Contact(
                Integer.parseInt(model.getValueAt(row, 0).toString()),
                model.getValueAt(row, 1).toString(),
                model.getValueAt(row, 2).toString(),
                model.getValueAt(row, 3).toString(),
                model.getValueAt(row, 4).toString(),
                model.getValueAt(row, 5).toString()
        );
    }

    private void openForm(Contact contact) {
        JTextField fn = new JTextField(), ln = new JTextField(), addr = new JTextField(), email = new JTextField(), phone = new JTextField();
        if (contact != null) {
            fn.setText(contact.getFirstName());
            ln.setText(contact.getLastName());
            addr.setText(contact.getAddress());
            email.setText(contact.getEmail());
            phone.setText(contact.getPhone());
        }

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("First Name")); form.add(fn);
        form.add(new JLabel("Last Name")); form.add(ln);
        form.add(new JLabel("Address")); form.add(addr);
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Phone")); form.add(phone);

        int result = JOptionPane.showConfirmDialog(this, form,
                contact == null ? "Add Contact" : "Update Contact", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Contact newC = new Contact(contact == null ? 0 : contact.getId(),
                    fn.getText(), ln.getText(), addr.getText(), email.getText(), phone.getText());

            if (contact == null) dao.addContact(newC);
            else dao.updateContact(newC);
            loadContacts();
        }
    }
}
