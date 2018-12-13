package test;

import htmoo.MooUser;
import htmoo.UserListPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

public class TestUserList extends JFrame {
	
	private UserListPanel userListPanel;
	private boolean bool = true;

	public TestUserList() {
		
		userListPanel = new UserListPanel();
		JButton btnAdd = new JButton("Add User");
		btnAdd.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				addUser();
			}});
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(btnAdd, "South");
		getContentPane().add(userListPanel, "Center");

		
		addWindowListener( new WindowListener(){
			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}});
		pack();

	}
	
	public void addUser() {
		Random r = new Random();
		int n = r.nextInt(1000000);
		MooUser user = new MooUser();
		user.setName("User"+n);
		user.setId(""+n);
		user.setAway( bool );
		user.setIdle( bool );
		userListPanel.addOnlineUser(user);
		
		bool = !bool;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestUserList t = new TestUserList();
		t.setVisible( true );
	}

}
