package htmoo;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

public class PEditWorldDialog extends JDialog implements ActionListener {

	Hashtable world;
	boolean savePressed;

	JTextField name = new JTextField(50);
	JTextField address = new JTextField(50);
	JTextField port = new JTextField(5);
	JTextField user = new JTextField(8);
	JTextField password = new JTextField(8);

	JButton saveButton = new JButton("save");

	JLabel nameLabel = new JLabel("Name:");
	JLabel addressLabel = new JLabel("Address:");
	JLabel portLabel = new JLabel("Port:");
	JLabel userLabel = new JLabel("User:");
	JLabel passwordLabel = new JLabel("Password:");
	JLabel blankLabel = new JLabel("");
	MainWindow parent;

	public PEditWorldDialog(MainWindow p) {
		super(p, "Editing World");
		this.parent = p;
		getContentPane().setLayout(new GridLayout(6, 2));
		getContentPane().add(nameLabel);
		getContentPane().add(name);
		getContentPane().add(addressLabel);
		getContentPane().add(address);
		getContentPane().add(portLabel);
		getContentPane().add(port);
		getContentPane().add(userLabel);
		getContentPane().add(user);
		getContentPane().add(passwordLabel);
		getContentPane().add(password);
		getContentPane().add(blankLabel);
		getContentPane().add(saveButton);

		saveButton.addActionListener(this);

		pack();

	}

	public void setWorld(Hashtable w) {
		world = w;
		savePressed = false;

		name.setText((String) world.get("name"));
		address.setText((String) world.get("address"));
		port.setText((String) world.get("port"));
		user.setText((String) world.get("user"));
		password.setText((String) world.get("password"));
	}

	public boolean savePressed() {
		return savePressed;
	}

	public Dimension getPreferredSize() {
		return new Dimension(280, 180);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == saveButton) {
			world.put("name", name.getText());
			world.put("address", address.getText());
			world.put("port", port.getText());
			world.put("user", user.getText());
			world.put("password", password.getText());
			parent.addWorld( world );
			parent.saveWorldList();
			savePressed = true;
		}

		setVisible(false);
	}

	public String getName() {
		return name.getText();
	}

	public String getAddress() {
		return address.getText();
	}

	public String getPort() {
		return port.getText();
	}
	
	public String getUser() {
		return user.getText();
	}

	public String getPassowrd() {
		return password.getText();
	}

	
}
