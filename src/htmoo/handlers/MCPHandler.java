package htmoo.handlers;

import htmoo.MUDConnection;
import htmoo.MainWindow;
import htmoo.MooUser;
import htmoo.VerbEditor;

import java.util.Random;
import java.util.UUID;

public class MCPHandler implements Texthandler {

	private int auth_key;
	private static final String VERSION = "2.1";
	private MUDConnection connection;
	private String userlist_datatag = "NOT KNOWN YET";
	private MainWindow window;
	private boolean buffering = false;
	private String bufferedText;
	private String header;

	public MCPHandler(MUDConnection con, MainWindow w) {
		Random r = new Random();
		this.auth_key = r.nextInt(1000000);
		this.connection = con;
		this.window = w;
	}

	public HandleResult handleText(String line) {
		boolean handled = false;
		if (line.startsWith("#$#"))
			handled = true;

		if (buffering) {
			if (line.trim().equals(".")) {
				buffering = false;
				// end of buffering, discard "."
				// Start VerbEditor
				VerbEditor e = new VerbEditor();
				e.setMainWindow(window);
				e.setCode(bufferedText);
				bufferedText = "";
				e.setMooHeader(header);
				e.setVisible(true);
				e.toFront();
			} else {
				// just buffer
				bufferedText = bufferedText.concat(line + "\n");
			}
		} else if (line.startsWith("#$# edit")) {
			// @edit used
			buffering = true;
			header = line;
			bufferedText = "";
		} else if (line.startsWith("#$#mcp version:")) {
			// init MCP
			String authline = "#$#mcp authentication-key: " + auth_key
					+ " version: " + VERSION + " to: " + VERSION;
			System.out.println(authline);
			connection.sendLine(authline);
		} else if (line.startsWith("#$#mcp-negotiate-can " + auth_key
				+ " package: dns-com-vmoo-userlist")) {
			// It has a userlist! reply that we can also!
			connection.sendLine(line);
		} else if (line.startsWith("#$#dns-com-vmoo-userlist " + auth_key)) {
			// getting the data_tag!
			userlist_datatag = line.replaceAll(".*_data-tag: ", "");
		} else if (line.startsWith("#$#* " + userlist_datatag + " d: =")) {
			// Got the userlist!
			handleUserList(line.replaceAll(".*=", ""));
		} else if (line.startsWith("#$#* " + userlist_datatag + " d: <")) {
			// Idle update!
			String s = line.replaceAll(".*<", "");
			s = s.replaceAll("\\{", "");
			s = s.replaceAll("\\}", "");
			setUsersIdle( s, true);
		} else if (line.startsWith("#$#* " + userlist_datatag + " d: >")) {
			// Idle update!
			String s = line.replaceAll(".*>", "");
			s = s.replaceAll("\\{", "");
			s = s.replaceAll("\\}", "");
			setUsersIdle( s, false );
		}

		HandleResult r = new HandleResult(handled, null);
		return r;
	}

	private void setUsersIdle(String s, boolean idle) {
		String[] ids = s.split(",");
		for ( String id : ids ) {
			id = id.trim();
			window.getUserListPanel().setIdle( id , idle );
		}
	}

	private void handleUserList(String input) {
		String[] usersRaw = input.split("},");
		for (String s : usersRaw) {
			s = s.replaceAll("\\{", "");
			s = s.replaceAll("\\}", "");
			MooUser m = MooUser.fromString( s );
			window.getUserListPanel().addOnlineUser( m );
		}
	}
	
	

}
