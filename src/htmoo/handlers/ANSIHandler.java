package htmoo.handlers;


public class ANSIHandler implements Texthandler {

	@Override
	public HandleResult handleText(String line) {		
		final String CSI  = "" + (char)27 + "[0m";
		String s2 = line.replace(CSI, "");		
		return new HandleResult( false, s2); 
	
	}

}
