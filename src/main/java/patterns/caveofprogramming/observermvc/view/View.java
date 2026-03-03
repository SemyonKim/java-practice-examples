package patterns.caveofprogramming.observermvc.view;


import patterns.caveofprogramming.observermvc.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The View represents the GUI.
 * It holds a reference to the Model (to display data) but sends user actions
 * to the Controller via the LoginListener (Observer Pattern).
 */
public class View extends JFrame implements ActionListener {
    private Model model;
    private JButton okButton;
    private JTextField nameField;
    private JPasswordField passwordField;

    // The reference to the observer (Controller)
    private LoginListener loginListener;

    public View(Model model) throws HeadlessException {
        super("MVC+Swing Demo");
        this.model = model;

        this.nameField = new JTextField(10);
        this.passwordField = new JPasswordField(10);
        this.okButton = new JButton("OK");

        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(100, 0, 0, 10);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        add(new JLabel("Name: "), gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(100, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        add(nameField, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        add(new JLabel("Password: "), gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        add(passwordField, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 100;
        gridBagConstraints.fill = GridBagConstraints.NONE;

        add(okButton, gridBagConstraints);

        okButton.addActionListener(this);

        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * This handles the internal Swing button click.
     * It then "notifies" the external LoginListener (the Observer).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String password = new String(passwordField.getPassword());
        String name = nameField.getText();
        if (loginListener != null) loginListener.loginPerformed(new LoginFormEvent(name, password));
    }

    /**
     * Standard method for the Observer pattern to register an observer.
     */
    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }
}
