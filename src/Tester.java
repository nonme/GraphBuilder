import java.util.ArrayList;
import java.util.Scanner;
//import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class Tester {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Parser parser = new Parser();
		//System.out.println(parser.isCorrect(sc.nextLine()));
		System.out.println(parser.calculate("34.045")); //34.045
		System.out.println(parser.calculate("65.3+34.045")); //99.345
		System.out.println(parser.calculate("-34.045")); //-34.045
		System.out.println(parser.calculate("cos(1)^3"));
		sc.close();
	}
}
