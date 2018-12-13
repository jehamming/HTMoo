package htmoo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class HTMLPanel extends JTextPane {

	private static final long serialVersionUID = 2840981881960475392L;
	// private JTextPane output = new JTextPane();
	private Font myFont = new Font("Courier", Font.PLAIN, 12);
	private String htmlFace = "Courier";
	private String htmlSize = "12px";
	private MainWindow parent;

	public HTMLPanel(MainWindow parent) {
		this.parent = parent;
		setContentType("text/html");
		setText("<html>\n</html>");
		setEditable(false);
		setFont(myFont);
		setAutoscrolls(true);
	}

	public void scrollToBottom() {
		if (SwingUtilities.isEventDispatchThread()) {
			// setCaretPosition( getDocument().getLength() );
			scrollRectToVisible(new Rectangle(0, getHeight() - 2, 1, 1));

		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						// setCaretPosition( getDocument().getLength() );;
						scrollRectToVisible(new Rectangle(0, getHeight() - 2,
								1, 1));
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void addHTML(String html) {
		// System.out.println("HTMLPanel: received HTML:\n" + html);
		String s = getText();

		s = s.replaceAll("</body>\n</html>", "");
		s = s.concat(html + "<br/>\n</body>\n</html>");
		// System.out.println("Add html:" + html);
		final String text = s;
		if (SwingUtilities.isEventDispatchThread()) {
			// Can't call invokeAndWait from dispatch thread.
			setText(text);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						setText(text);
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		scrollToBottom();
	}

	public void addText(String line, boolean isProtocol) {
		if (!isProtocol || ( isProtocol && parent.isDebug() ) ) {
			addHTML("<font face=\"" + htmlFace + "\" size=\"" + htmlSize + "\">" + line + "</font>\n");
			scrollToBottom();
		}
	}

	public void clear() {
		setText("<html>\n</html>");
	}

}
