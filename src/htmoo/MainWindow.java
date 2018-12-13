package htmoo;


import htmoo.debug.DbgTextToClient;
import htmoo.handlers.ANSIHandler;
import htmoo.handlers.MCPHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

public class MainWindow extends JFrame implements KeyListener, ActionListener {

	private static final long serialVersionUID = -6473616212358030756L;
	HTMLPanel htmlPanel = new HTMLPanel(this);
	JTextField input = new JTextField();
	JScrollPane scroller = new JScrollPane(htmlPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JScrollBar myScrollBar = scroller.getVerticalScrollBar();
	String mudname;
	String address;
	int port;
	Color oldColor;
	Color grayColor = new Color(128, 128, 128);
	Font myFont = new Font("Courier", Font.PLAIN, 12);
	List<String> history = new ArrayList<String>();
	int cmdNumber = 0; // What history command we're viewing.
	int lastCmd = -1;
	Thread netThread;
	MUDConnection myConnection;

	JDesktopPane desktop = new JDesktopPane();
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem connect = new JMenuItem("Connect...");
	JMenuItem exit = new JMenuItem("Exit");
	JMenu worldsMenu = new JMenu("Worlds");
	JMenuItem newWorld = new JMenuItem("New World...");
	JMenuItem editWorlds = new JMenuItem("Edit Worlds...");
	JMenu windowMenu = new JMenu("Window");
	JMenuItem windowClear = new JMenuItem("Clear");
	JMenu debugMenu = new JMenu("Debug");
	JCheckBoxMenuItem dbgShowMCP = new JCheckBoxMenuItem("Show MCP text");
	JMenuItem dbgSendToClient = new JMenuItem("Send manual to client");
	private DbgTextToClient textToClient;
	
	private UserListPanel userListPanel = new UserListPanel();
	
	boolean connected = false;
	Hashtable<String, Hashtable<JWorldItem,String>> worldList;
	String worldsFileName = new String("worldlist");
	
	private MCPHandler mcp;
	private ANSIHandler ansi;	

	public MCPHandler getMcp() {
		return mcp;
	}

	PConnectDialog connectDialog;
	PEditWorldDialog editWorldDialog;

	public MainWindow() {
		init();
	}

	public HTMLPanel getView() {
		return htmlPanel;
	}
	
	
	public UserListPanel getUserListPanel() {
		return userListPanel;
	}
	
	public MUDConnection getConnection() {
		return myConnection;
	}


	public void init() {
		setTitle("HTMoo - MainWindow");
		myScrollBar.setBlockIncrement(50); // Default is 10.
		
		input.addActionListener(this);
		input.addKeyListener(this);
		input.setFont(myFont);
		

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroller, "Center");
		getContentPane().add(input, "South");
		getContentPane().add(userListPanel, "East");
		
		// Must do this, or specify row/columns for JTextArea, which
		// is hard to translate into pixel sizes.
		setPreferredSize(new Dimension(800, 400));

		initToolbar();
		
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

		input.requestFocus();
	}
	
	private void initToolbar() {
		connect.addActionListener(this);
		fileMenu.add(connect);
		exit.addActionListener(this);
		fileMenu.add(exit);
		menuBar.add(fileMenu);

		newWorld.addActionListener(this);
		worldsMenu.add(newWorld);

		editWorlds.addActionListener(this);
		worldsMenu.add(editWorlds);
		worldsMenu.addSeparator();
		
		windowClear.addActionListener(this);
		windowMenu.add( windowClear );
		
		dbgShowMCP.setSelected(false);
		dbgShowMCP.addActionListener(this);
		debugMenu.add( dbgShowMCP );
		
		dbgSendToClient.addActionListener(this);
		debugMenu.add( dbgSendToClient );
		
		

		// Populate the drop-down with worlds.
		loadWorldList();
		generateWorldsMenu();

		menuBar.add(worldsMenu);

		menuBar.add(windowMenu);
		
		menuBar.add( debugMenu );
		
		this.setJMenuBar(menuBar);
		connectDialog = new PConnectDialog(this, false); // Not Modal.
		connectDialog.setLocationRelativeTo(this); // Show atop MUDDesktop.

		editWorldDialog = new PEditWorldDialog(this);
		editWorldDialog.setLocationRelativeTo(this);
	}
	
	public boolean isDebug() {
		return dbgShowMCP.isSelected();
	}

	public void saveWorldList() {

		try {
			FileOutputStream fos = new FileOutputStream(worldsFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(worldList);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			showStatus("Problem saving worldList.");
		}

	}

	public void loadWorldList() {

		try {
			FileInputStream fis = new FileInputStream(worldsFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);

			worldList = (Hashtable) ois.readObject();
			if (worldList == null) {
				worldList = new Hashtable();
			}
		} catch (Exception e) {
			showStatus("** Problem reading worldList: " + e);
			worldList = new Hashtable(10);
		}

	}

	public void generateWorldsMenu() {
		Enumeration<Hashtable<JWorldItem,String>> e = worldList.elements();
		while (e.hasMoreElements()) {
			Hashtable<JWorldItem,String> h = e.nextElement();
			JWorldItem mi = new JWorldItem((String) h.get("name"));
			worldsMenu.add(mi);
			mi.addActionListener(this);

		}
	}

	public void sendToMoo(String s) {
		myConnection.sendLine(s);
	}

	// ActionListener interface.
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == input) {
			String cmd = e.getActionCommand();
			//getView().addText(cmd);
			input.setText("");
			input.requestFocus();

			if (cmd.length() > 0) {
				myConnection.sendLine(cmd);
				history.add(cmd);
				lastCmd++;
				cmdNumber = lastCmd + 1;
			}
		} else if (source == exit) {
			System.exit(0);
		} else if (source == connect) {

			connectDialog.setVisible(true);
			connectDialog.toFront();
			
		} else if (source == newWorld) {

			Hashtable world = new Hashtable();
			editWorldDialog.setWorld(world);
			editWorldDialog.setVisible(true);
			editWorldDialog.toFront();
		} else if ( source == dbgSendToClient ) {
			if ( textToClient == null) textToClient = new DbgTextToClient(this);
			textToClient.setVisible( true );
		} else if (source == windowClear) {
			getView().clear();
		} else if (source instanceof JWorldItem) {

			String bname = ((JMenuItem) source).getText();
			Hashtable world = (Hashtable) worldList.get(bname);
			if (world != null) {
				showStatus("Got world: " + world);

				// This was all cut and pasted from above.
				// I should be able to generate a subclass of
				// AbstractAction to use in both cases, right?
				// How do I assocate an action with a JDialog?
				// Is it just as easy as:
				//
				// dialog.addActionListener(new ConnectAction())?
				// Probably.

				// String name = (String)world.get("name") ;
				String address = (String) world.get("address");
				int port = Integer.parseInt((String) world.get("port"));
				String user = (String) world.get("user");
				String password = (String) world.get("password");

				if (connected)
					doDisconnect();
				doConnect(address, address, port, user, password);

			}

		}

	}

	public boolean doConnect(String nym, String addr, int p, String user, String passwd) {
		mudname = nym;
		address = addr;
		port = p;

		showStatus("Asked to connect to: " + mudname + " " + port);

		myConnection = new MUDConnection();
		myConnection.setMainWindow(this);
		myConnection.setHost(address);
		myConnection.setPort(port);
		myConnection.setUser(user);
		myConnection.setPassword(passwd);
		
		mcp = new MCPHandler(myConnection, this);
		myConnection.addTextHandler( mcp );
		ansi = new ANSIHandler();
		myConnection.addTextHandler( ansi );

		netThread = new Thread(myConnection);
		netThread.start();
		connected = true;
		return true;

	}

	public void doDisconnect() {
		myConnection.doDisconnect();
		connected = false;
	}

	public void showStatus(String s) {
		System.out.println("MUDWindow: " + s);
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_PAGE_UP) {
			// showStatus("keyReleased: page up.") ;
			scrollUp();
		} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
			// showStatus("keyReleased: page down.") ;
			scrollDown();
		} else if (keyCode == KeyEvent.VK_UP) {

			// showStatus("keyReleased: up arrow.") ;

			if (cmdNumber > 0) {
				cmdNumber--;
				input.setText((String) history.get(cmdNumber));
			}
		} else if (keyCode == KeyEvent.VK_DOWN) {

			// showStatus("keyReleased: down arrow: "
			// + cmdNumber + " " + lastCmd + " " + history.size()) ;

			if (cmdNumber < lastCmd) {
				cmdNumber++;
				input.setText((String) history.get(cmdNumber));
			} else {
				input.setText("");
			}

		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void firstAppend(String line) {
		htmlPanel.addHTML(line + "\n");
		input.requestFocus();
		getView().scrollToBottom();
	}

	public void requestFocus() {
		input.requestFocus();
		getView().scrollToBottom();
	}

	public void scrollUp() {
		int start = myScrollBar.getValue();
		int inc = myScrollBar.getBlockIncrement();
		int min = 0;
		int newValue;

		// showStatus("scrollUp: start = " + start + " increment: " + inc) ;

		if (start == min) {
			return;
		}

		if ((start - inc) >= min) {
			newValue = start - inc;
		} else {
			newValue = 0;
		}

		myScrollBar.setValue(newValue);

	}

	public void scrollDown() {
		int start = myScrollBar.getValue();
		int inc = myScrollBar.getBlockIncrement();
		int max = myScrollBar.getMaximum();
		int newValue;

		// showStatus("scrollDown: start = " + start + " increment: " + inc) ;

		if (start == max) {
			return;
		}

		if ((start + inc) <= max) {
			newValue = start + inc;
		} else {
			newValue = max;
		}

		myScrollBar.setValue(newValue);

	}

	public void scrollToBottomOLD() {
		// Threadsafe version. A bit of a pain to do, but.
		if (SwingUtilities.isEventDispatchThread()) {
			// Can't call invokeAndWait from dispatch thread.
			myScrollBar.setValue(myScrollBar.getMaximum());

		} else {
			MUDScroller scr = new MUDScroller();
			scr.setScrollBar(myScrollBar);
			try {
				SwingUtilities.invokeAndWait(scr);
			} catch (Exception e) {
				showStatus("invokeAndWait errored: " + e);
			}
		}

	}

	public void showDialog(String line) {
		JOptionPane.showMessageDialog(this, line);
	}


	public void addHTML(String bufferedText) {
		htmlPanel.addHTML( bufferedText );
		input.requestFocus();
		getView().scrollToBottom();
		
	}

	public void addWorld(Hashtable world) {
		String name = (String) world.get("name");
		JWorldItem newItem = new JWorldItem(name);
		worldList.put(name, world);
		worldsMenu.add(newItem);
		newItem.addActionListener(this);
	}

}
