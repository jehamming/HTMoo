package htmoo;

import javax.swing.JScrollBar;

public class MUDScroller implements Runnable {

    JScrollBar myScrollBar ;
    public void setScrollBar(JScrollBar sb) { myScrollBar = sb ; }

    public void run() {
	myScrollBar.setValue(myScrollBar.getMaximum()) ;
    }

}
