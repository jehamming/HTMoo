package htmoo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MooUserCellRenderer extends JLabel implements ListCellRenderer {
	private Font stdFont = new Font("Courier", Font.PLAIN, 12);
	private Font boldFont = new Font("Courier", Font.BOLD, 12);
	private Font idleFont = new Font("Courier", Font.ITALIC, 12);

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        MooUser user = (MooUser) value;

        setText(user.getName());

        if ( user.isIdle() ) {
        	setFont( idleFont );
        } else {
        	setFont( boldFont );
        }
        
        this.setBackground(Color.RED);

        return this;
		
	}

}
