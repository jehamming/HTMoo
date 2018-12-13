package htmoo.debug;

import htmoo.MUDConnection;
import htmoo.MainWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DbgTextToClient extends JFrame {
	
	private JTextArea ta = new JTextArea(10,40);
	private MainWindow parent;
	
	public DbgTextToClient( MainWindow w ) {
		this.parent = w;
		setTitle("Debug - Send text to client");
		getContentPane().setLayout( new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		ta.setText("Text to send to client");
		JScrollPane sp = new JScrollPane( ta );
		getContentPane().add( sp );
		
		JPanel buttonPanel = new JPanel();
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				sendToClient();
			}});
		buttonPanel.add( btnSend );
		
		JButton btnSendSel = new JButton("Send selected text");
		btnSendSel.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				sendSelectedToClient();
			}});
		buttonPanel.add( btnSendSel );
		getContentPane().add( buttonPanel );
		
		pack();
		
	}
	
	protected void sendToClient() {
		if ( parent.getConnection() == null ) {
			JOptionPane.showMessageDialog(this, "No connection (yet)!", "Debug", JOptionPane.OK_OPTION);
			return;
		}
		parent.getConnection().textRecieved( ta.getText().trim());	
	}

	protected void sendSelectedToClient() {
		if ( parent.getConnection() == null ) {
			JOptionPane.showMessageDialog(this, "No connection (yet)!", "Debug", JOptionPane.OK_OPTION);
			return;
		}
		parent.getConnection().textRecieved( ta.getSelectedText().trim());	
	}

	
	public static void main( String[] args ) {
		DbgTextToClient c = new DbgTextToClient(null);
		c.setVisible( true );
	}

}
