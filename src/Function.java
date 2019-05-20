import java.util.ArrayList;

public class Function {
	String input; //$ - end of string
	int p;
	char c;
	public Function(String input) {
		this.input = input;
		p = 0;
		c = ' ';
	}
	public char nextChar() {
		if(p < input.length())
			c = input.charAt(p++);
		else
			c = '$';
		return c;
	}
}
