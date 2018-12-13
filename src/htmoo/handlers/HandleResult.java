package htmoo.handlers;

public class HandleResult {
	
	private boolean handled = false;
	private String text;
	
	public HandleResult() {}
	
	public HandleResult( boolean h, String s ) {
		this.handled = h;
		this.text = s;
	}
	
	public boolean isHandled() {
		return handled;
	}
	public void setHandled(boolean handled) {
		this.handled = handled;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	

}
