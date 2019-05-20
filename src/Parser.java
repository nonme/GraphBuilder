import java.util.ArrayList;
import java.util.HashMap;

import java.util.Scanner;
/**
 * The next grammar was made for my Programming Summer practice course 
 * 													by O. M. Pechkurova.
 * The grammar is G = {Vn, Vt, P, S}
 * Vn = {E,T,A,D,nD,L}
 * Vt = {0,1,2,3,4,5,6,7,8,9,{,},+,*,-,cos,sin,tan,log,sqrt,^}
 * P = { E -> TA,
 * 		 A -> +TA, A -> -TA, A -> emtpy,
 * 		 T -> (E), T -> [1-9]D, T -> L, T -> cosT, T -> sinT, T -> tanT,
 * 		 T -> log[2-10]T, T -> T^T
 * 		 D -> [0-9], L -> [a-zA-Z] }
 * S = {E}
 * 		 
 * @author Dmitry Larin, 05.2019, SE-1
 *
 * P.S. Thanks to Volodymyr Protsenko for the wonderful course in Theory of Computations.
 */
public class Parser {
	private Function input;
	private HashMap<Character, String> arguments;
	private char next;
	/**
	 * Checks if the expression is correct in terms of math syntax
	 * @param str - string representation of the formula
	 * @return true if correct else false
	 * @throws SyntaxError
	 */
	public boolean isCorrect(String str) throws SyntaxError {
		input = new Function(str);
		try {
			next = input.nextChar();
			E("You cannot input emtpy expression!");
			match('$');
		} catch(SyntaxError ex) {
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	/**
	 * Evaluates the expression str with arguments given in args in format
	 * char = value, for example: 'p' = 3.1415926, etc.
	 * @param str - string representation of the formula
	 * @param args - array with parameter tuples
	 * @return
	 */
	public double calculate(String str, HashMap<Character, String> args) {
		input = new Function(str);
		double result = 0;
		try {
			next = input.nextChar();
			arguments = args;			
			result = E("You cannot input empty expression!");
			match('$');
		} catch(SyntaxError ex) {
			System.out.println(ex.getMessage());
			return -1;
		}
		return result;
	}
	public double calculate(String str) {
		input = new Function(str);
		double result = 0;
		try {
			next = input.nextChar();
			result = E("You cannot input empty expression!");
			match('$');
		} catch(SyntaxError ex) {
			System.out.println(ex.getMessage());
			return -1;
		}
		return result;
	}
	/**
	 * This is the main method to get points to draw graphs.
	 * @param value - value to range, usually x or t
	 * @param step - step, usually 0.1, 0.5 or 1
	 * @param l_range - range from
	 * @param r_range - range to
	 * @param equations - an ArrayList of equations 
	 * @return array with points found by program in formay (x,y)
	 */
	public Tuple<ArrayList<Point>, String> solveEquations(char value, double step,
				double l_range, double r_range, ArrayList<String> equations) {
		HashMap<Character, String> args = 
				new HashMap<Character, String>();
		boolean algebraicEquation = (value == 'x' ? true : false);
		if(algebraicEquation) {
			int index = -1;
			//We need to find equation with x and y here and parameters to it
			for(int i = 0; i < equations.size(); ++i) {
				if(equations.get(i).contains("x") &&
						equations.get(i).contains("y") && index == -1) {
					index = i;
				}
				else if(equations.get(i).contains("x") &&
						equations.get(i).contains("y") && index != -1) {
						System.out.println("More then one equation with x and y.");
						return new Tuple<ArrayList<Point>, String>(
								null,
								"More then one equation with x and y.");
				}
				else {
					String[] parameter = equations.get(i).replaceAll("\\s","").split("=");
					if(parameter.length > 2) {
						System.out.println("Syntax error: two or more = were found.");
						return new Tuple<ArrayList<Point>, String>(
								null,
								"Syntax error: two or more = were found.");
					}
					parameter[0] = parameter[0].trim();
					if(parameter[0].length() != 1) {
						System.out.println("Please input parameter in the next form:");
						System.out.println("p = ...");
						return new Tuple<ArrayList<Point>, String>(
								null,
								"Please input parameter in the next form: p = ...");
					}
					args.put(parameter[0].charAt(0), parameter[1]);
				}
			}
			if(index == -1) {
				System.out.println("Algebraic or parameter equations were not found.");
				return null;
			}
			return new Tuple<ArrayList<Point>, String>(
					solveAlgebraicEquation(l_range, r_range, step, equations.get(index), args),
					"");
		}
		else {
			//indexes of equation with x and equation with y
			int xindex = -1, yindex = -1;
			for(int i = 0; i < equations.size(); ++i) {
				String[] equation = equations.get(i).replaceAll("\\s", "").split("=");
				if(equation.length != 2) {
					return new Tuple<ArrayList<Point>, String>(
							null,
							"You need to input exactly = in every equation.");
				}
				if(equation[0].length() != 1) {
					return new Tuple<ArrayList<Point>, String>(
							null,
							"Please, input equations in form: p = ...");
				}
				if(equation[1].isEmpty()) {
					return new Tuple<ArrayList<Point>, String>(
							null,
							"Please, input expressions after = signs.");
				}
				if(equation[0].equals("x") && xindex == -1)
					xindex = i;
				else if(equation[0].equals("x") && xindex != -1) {
					return new Tuple<ArrayList<Point>, String>(
							null,
							"You need to input exactly one equation in form: x = ...");
				}
				else if(equation[0].equals("y") && yindex == -1)
					yindex = i;
				else if(equation[0].equals("y") && yindex != -1) {
					return new Tuple<ArrayList<Point>, String>(
							null,
							"You need to input exactly one equation in form: y = ...");
				}
				else args.put(equation[0].charAt(0), equation[1]);
			}
			if(xindex == -1)
				return new Tuple<ArrayList<Point>, String>(
						null,
						"You need to input x equation in form x = ...");
			if(yindex == -1)
				return new Tuple<ArrayList<Point>, String>(
						null,
						"You need to input y equation in form y = ...");
			return solveParametricEquations(value, l_range, r_range, step,
					equations.get(xindex), equations.get(yindex), args);
		}
	}
	private Tuple<ArrayList<Point>, String> solveParametricEquations(
			char value, double l_range, double r_range, double step,
			String yEquation, String xEquation, HashMap<Character, String> args) {
		ArrayList<Point> points = new ArrayList<Point>();
		for(double i = l_range; i <= r_range+step; i+=step) {
			args.put('t', String.valueOf(i));
			System.out.println(i);
			points.add(new Point(
					calculate(xEquation.split("=")[1].trim(), args),
					calculate(yEquation.split("=")[1].trim(), args)));
		}
		return new Tuple<ArrayList<Point>, String>(points, "");
	}
	private ArrayList<Point> solveAlgebraicEquation(double l_range, double r_range,
			double step, String fullEquation, HashMap<Character, String> args) {
		ArrayList<Point> points = new ArrayList<Point>();
		String[] equation = fullEquation.replaceAll("\\s","").split("=");
		if(equation.length != 2) {
			System.out.println("More than two '=' in main equation.");
			return null;
		}
		args.put('x', "");
		args.put('y', "");
		int n = args.size();
		for(double i = l_range; i <= r_range; i+=step) {
			args.put('x', String.valueOf((Math.abs(i) < step/10 ? 0 : i)));
			double y = 0;
			if(equation[0].equals("y") && !equation[1].contains("y")) {
				y = calculate(equation[1], args);
			}
			else if(equation[1].equals("y") && !equation[0].contains("y")) {
				y = calculate(equation[0], args);
			}
			else {
				double min_difference = 1e9, ystep = 0.5;
				for(double j = -100; j <= 100; j+=ystep) {
					if(i >= -3 && i <= 3) {
						ystep = 0.1;
					}
					else ystep = 0.5;
					args.put('y', String.valueOf((Math.abs(j) < 0.1 ? 0 : j)));
					double left = calculate(equation[0], args),
							right = calculate(equation[1], args);
					if(Math.abs(right-left) < min_difference) {
						min_difference = Math.abs(right-left);
						y = j;
					}
				}
			}
			if(Double.isNaN(y) || Double.isInfinite(y))
				continue;
			points.add(new Point(i,y));
			if(equation[0].contains("y^2") || equation[1].contains("y^2"))
				points.add(new Point(i,-y));
		}
		return points;
	}
	private double E(String errorMessage) throws SyntaxError{
		double result = T(errorMessage);
		return A(errorMessage, result);
	}
	private double T(String errorMessage) throws SyntaxError {
		double result = S(errorMessage);
		return B(errorMessage, result);
	}
	private double A(String errorMessage, double r) throws SyntaxError {
		double result = r;
		if(next == '+') {
			next = input.nextChar();
			result+=T("Input after '+' sign is empty!");
			result = A(errorMessage, result);
		}
		else if(next == '-') {
			next = input.nextChar();
			result-=T("Input after '-' sign is empty!");
			result = A(errorMessage, result);
		}
		return result;
	}
	private double B(String errorMessage, double r) throws SyntaxError {
		double result = r;
		if(next == '*') {
			next = input.nextChar();
			result*=S("Input after '*' sign is emtpy!");
			result = B(errorMessage, result);
		}
		else if(next == '/') {
			next = input.nextChar();
			result/=S("Input after '/' sign is empty!");
			result = B(errorMessage, result);
		}
		return result;
	}
	private double S(String errorMessage) throws SyntaxError {
		double result = 0;
		if(next == '(') {
			next = input.nextChar();
			result = E(errorMessage);
			match(')');
		}
		else if(next == 'c') {
			//In case if c is parameter, not cos function
			char temp = next;
			next = input.nextChar();
			if(next == 'o') {
				next = input.nextChar();
				match('s');
				if(next == '(') {
					next = input.nextChar();
					result = Math.cos(E("You cannot input cos function without it's arguments!"));
					match(')');
				}
				else
					result = Math.cos(S("You cannot input cos function without it's arguments!"));
			}
			else {
				result = getValueOf(temp);
			}
		}
		else if(next == 's') {
			//In case if s is parameter, not sin function
			char temp = next;
			next = input.nextChar();
			if(next == 'i') {
				next = input.nextChar();
				match('n');
				if(next == '(') {
					next = input.nextChar();
					result = Math.sin(E("You cannot input sin function without it's arguments!"));
					match(')');
				}
				else result = Math.sin(S("You cannot input sin function without it's arguments!"));
			}
			else if(next == 'q') {
				next = input.nextChar();
				match('r');
				match('t');
				result = Math.sqrt(S("You cannot input sqrt function without it's arguments!"));
			}
			else {
				result = getValueOf(temp);
			}
		}
		else if(next == 't') {
			//In case if t is parameter, not tan function.
			char temp = next;
			next = input.nextChar();
			if(next == 'a') {
				next = input.nextChar();
				match('n');
				result = Math.tan(E("You cannot input tan function without it's arguments!"));
			}
			else {
				result = getValueOf(temp);
			}
		}
		else if(next == '1' || next == '2' || next == '3' || next == '4' ||
				next == '5' || next == '6' || next == '7' || next == '8' ||
				next == '9' || next == '0') {
			result = next-'0';
			next = input.nextChar();
			Tuple<Integer, Double> nextRecursionResult = D(0);
			result = result*Math.pow(10,nextRecursionResult.first)
							+ nextRecursionResult.second;
		}
		else if(next == '-') {
			next = input.nextChar();
			if(next == '1' || next == '2' || next == '3' || next == '4' ||
					next == '5' || next == '6' || next == '7' || next == '8' ||
					next == '9' || next == '0') {
				result = (next-'0')*-1;
				next = input.nextChar();
				Tuple<Integer, Double> nextRecursionResult = D(0);
				result = result*Math.pow(10,nextRecursionResult.first)
								- nextRecursionResult.second;
			}
			else throw new SyntaxError("Expected numbers after - sign.");
		}
		else if(next == 'P') {
			char temp = next;
			next = input.nextChar();
			if(next == 'I') {
				next = input.nextChar();
				result = Math.PI;
			}
			else result = getValueOf(temp);
		}
		else if(Character.isLetter(next)) {
			result = getValueOf(next);
			next = input.nextChar();
		}
		else throw new SyntaxError(errorMessage);
		
		return U(result);
		//LETTERS AS PARAMETERS WILL BE THERE SOON
	}
	//A non-terminal for unary operators like ^
	private double U(double r) throws SyntaxError {
		if(next == '^') {
			next = input.nextChar();
			return Math.pow(r, S("Expected input after ^ sign."));
		}
		return r;
	}
	private Tuple<Integer, Double> D(int ratioIndex) throws SyntaxError {
		if((next == '0' || next == '1' || next == '2' || next == '3' ||
		   next == '4' || next == '5' || next == '6' || next == '7' ||
		   next == '8' || next == '9') && ratioIndex == 0) {
			double result = next-'0';
			next = input.nextChar();
			Tuple<Integer, Double> nextRecursionResult = D(ratioIndex);
			return new Tuple<Integer, Double> (
					nextRecursionResult.first+1,
					result*Math.pow(10, nextRecursionResult.first)	
									+ nextRecursionResult.second);
		}
		else if(next == '0' || next == '1' || next == '2' || next == '3' ||
		   next == '4' || next == '5' || next == '6' || next == '7' ||
		   next == '8' || next == '9') {
			char val = next;
			next = input.nextChar();
			double result = (val-'0')/Math.pow(10, ratioIndex) + D(ratioIndex+1).second;
			return new Tuple<Integer, Double> (0, result);
		}
		else if(next == '.') {
			if(ratioIndex == 0) {
				next = input.nextChar();
				return new Tuple<Integer, Double> (
						0,
						D(1).second);
			}
			else throw new SyntaxError("Input expected after . in number");
		}
		else return new Tuple<Integer, Double> (0,0.d);
	}
	private double getValueOf(char parameter) throws SyntaxError{
		if(arguments != null && arguments.containsKey(parameter)) {
			return new Parser().calculate(arguments.get(parameter), arguments);
		}
		else throw new SyntaxError("The parameter " + parameter + " is missing!");
	}
	private void match(char c) throws SyntaxError {
		if(next == c)
			next = input.nextChar();
		else
			throw new SyntaxError("Expecting " + c + ", found " + next);
	}
}
