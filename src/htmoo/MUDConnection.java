package htmoo;

import htmoo.handlers.ANSIHandler;
import htmoo.handlers.HandleResult;
import htmoo.handlers.Texthandler;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MUDConnection implements Runnable {

	String host;
	int port;
	Socket mySocket;
	DataOutputStream dout;
	InputStreamReader din;
	BufferedReader br;
	MainWindow mainWindow;
	HashMap textTriggers = new HashMap();

	private boolean buffering = false;
	private String bufferedText = "";
	private int dispatcher = -1;
	private String header;
	private String user = "";
	private String password = "";

	List<Texthandler> handlers;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private final static int BUFF_VERB = 1;

	public MUDConnection() {
		handlers = new ArrayList<Texthandler>();
	}

	public void addTextHandler(Texthandler h) {
		handlers.add(h);
	}

	public void removeTextHandler(Texthandler h) {
		handlers.remove(h);
	}

	public void setHost(String h) {
		host = h;
	}

	public void setPort(int p) {
		port = p;
	}

	public void run() {

		String hostAndPort = new String(host + ":" + port);

		mainWindow.firstAppend("*** Connecting to " + hostAndPort + " ***");

		try {
			mySocket = new Socket(host, port);
			dout = new DataOutputStream(mySocket.getOutputStream());
			din = new InputStreamReader(mySocket.getInputStream());
			br = new BufferedReader(din);
		} catch (Exception e) {
			showStatus("Unable to connect to " + host + ":" + port + " " + e);

			mainWindow.showDialog("Unable to connect to " + hostAndPort + "\n"
					+ e);

			mainWindow.getView().addText(
					"*** Not connected to " + hostAndPort + " ***", false);

			return;
		}

		mainWindow.getView()
				.addText("*** Connected to " + hostAndPort + " ***", false);

		// Autologin?
		if (!user.equals("") && !password.equals("")) {
			try {
				dout.writeBytes("co " + user + " " + password + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		while (true) {
			String line;
			try {
				line = br.readLine();
				System.out.println("Recieved:" + line);
			} catch (Exception e) {
				showStatus("Unable to read line from socket: " + e);

				mainWindow.getView().addText(
						"*** Connection dropped: " + hostAndPort + " ***", false);

				return;
			}

			if (line == null) {
				mainWindow.getView().addText(
						"*** Disconnected from " + hostAndPort + " ***", false);

				return;
			}
			
			textRecieved( line );

		}

	}

	public void textRecieved(String line) {
		boolean isProtocol = false;
		// Send the line through the handlers
		for (Texthandler handler : handlers) {
			HandleResult result = handler.handleText(line);
			if (result.getText() != null)
				line = result.getText();
			if (result.isHandled()) {
				isProtocol = true;
				break;
			}

		}
		mainWindow.getView().addText(line, isProtocol);
	}

	private boolean handleText(String line) {
		boolean isProtocol = false;
		if (buffering) {
			if (line.trim().equals(".")) {
				buffering = false;
				// end of buffering, discard "."
				if (dispatcher == BUFF_VERB) {
					// Start VerbEditor
					VerbEditor e = new VerbEditor();
					e.setMainWindow(getMainWindow());
					e.setCode(bufferedText);
					bufferedText = "";
					e.setMooHeader(header);
					e.setVisible(true);
					e.toFront();
					if (!getMainWindow().isDebug())
						isProtocol = true;
				}
			} else {
				// just buffer
				bufferedText = bufferedText.concat(line + "\n");
				if (!getMainWindow().isDebug())
					isProtocol = true;
			}

		} else {
			if (line.startsWith("#$# edit")) {
				// @edit used
				buffering = true;
				header = line;
				bufferedText = "";
				dispatcher = BUFF_VERB;
				if (!getMainWindow().isDebug())
					isProtocol = true;
			}
		}

		return isProtocol;

	}

	public void sendLine(String line) {
		try {
			dout.writeBytes(line + "\n");
		} catch (Exception e) {
			showStatus("Can't write line: " + e);
		}
	}

	public void showStatus(String s) {
		System.out.println("MUDConnection: " + s);
	}

	public void doDisconnect() {
		try {
			mySocket.close();
		} catch (Exception e) {
		}
		;

	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

}
