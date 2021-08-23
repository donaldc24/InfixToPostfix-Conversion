package edu.iastate.cs228.hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author donald24
 * Class to convert infix to postfix
 */
public class infix2postfixConversion {
	
	static ArrayList<String> postfixes = new ArrayList<String>();	// Array List to store conversions of postfix
	static String error = " ";		// String to store error values
	static boolean numericError = false;	// boolean to check numeric errors
	
	/**
	 * Sets name of file to input.txt and calls scanner method
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException 
    {
        String input = "input.txt"; 
        scanner(input);
    }
	
	/**
	 * Takes a input String when called and sorts the infix to postfix and
	 * returns the result in stack after calling stack2String
	 * @param input	 Takes a input String
	 * @return	 The postfix in string format
	 */
	static String Infix2Postfix(String input) {
		Stack<String> output = new Stack<>();
		Stack<String> stack = new Stack<>();
		Scanner scn = new Scanner(input);
		String s = "";
		boolean numeric = true;
		int cumulativeRank = 0;
		boolean lastOperator = false;
		boolean lPara = false;
		boolean lParaLast = false;
		error = " ";
		
		while (scn.hasNext()) {
			s = scn.next();
			numericError = false;	// Sets numericError to false at begining of every loop
			
			try {		// Checks to see if s string is a numeric value or not
				int value = Integer.parseInt(s);
				numeric = true;
			} catch (NumberFormatException e) {
			    numeric = false;
			}
			
			if (numeric) {		// If numeric then it adds to output stack
				cumulativeRank += 1;
				lastOperator = false;
				lParaLast = false;
				
				if (cumulativeRank > 1) {
					error = s;
					numericError = true;
					break;
				}
				output.push(s);
			} else if (s.equals("(")) {		// If ( then it adds to stack and sets lPara and lParaLast to true to show there is a (
				lastOperator = false;
				stack.push(s);
				lPara = true;
				lParaLast = true;
			} else if (s.equals(")")) {		// If ) then it checks if ( also exists and then either adds top of stack to output or just removes top of stack
				if (!lPara) {
					error = "(";
					break;
				}
				
				if (lParaLast) {
					error = "NA";
					break;
				}
				
				if(lastOperator) {
					error = stack.peek();
					break;
				}
				
				lastOperator = false;
				lParaLast = false;
				
				while (!stack.isEmpty() && !stack.peek().equals("(")) {		
					output.push(stack.pop());
				}
				
				if (!stack.isEmpty()) {
					stack.pop();
				}
				
			} else {		// If operator then it compares ranks then either adds top of stack to output or to the top of stack 
				cumulativeRank -= 1; 
				
				if (lParaLast) {
					error = s; 
					break;
				}
				
				lastOperator = true;
				lParaLast = false; 
				
				if (cumulativeRank <= -1) {
					error = s;
					break; 
				}
				
				while (!stack.isEmpty() && evaluateRank(s) <= evaluateRank(stack.peek())) {
					if (s.equals("^") && stack.peek().equals("^")) {		// Applies right associative properties to ^
						break;
					}
					output.push(stack.pop());
				} 
				
				stack.push(s); 
			} 
		}
		
		
		while (!stack.isEmpty()) {		// Fills remaining in stack to output unless there is a ( 
			if (stack.peek().equals("(") && error.equals(" ")) {
				error = ")";
				break;
			}
			if (cumulativeRank != 1 && error.equals(" ")) {
				error = stack.peek();
				break;
			}
			output.push(stack.pop());
		}
		
		scn.close();
		return stack2String(output);
	}
	
	/**
	 * Takes the input stack and sorts it and puts it into a string
	 * @param stack	 input resulting stack from Infix2Postfix method
	 * @return  the stack in a string sorted
	 */
	static String stack2String (Stack<String> stack) {
		String output = "";
		
		for (int i = 0; i <= stack.size()-1; i++) {		// Sorts the stack into a string
			if (i == 0) {
				output += stack.get(i);
			} else {
				output += " " + stack.get(i);
			}
		}
		
		return output;
	}
	
    /**
     * Method used to apply a rank to each character + - / * ^, with everything else having rank of -1
     * @param s	 input a string 
     * @return	 the rank of the given string
     */
    static int evaluateRank(String s) {
    	if (s.equals("+") || s.equals("-")) {
    		return 1;
    	} else if (s.equals("*") || s.equals("/") || s.equals("%")) {
    		return 2; 
    	} else if (s.equals("^")) {
    		return 3;
    	}
        return -1;
    }
    
	/**
	 * Takes a file name and scans each line into a string and calls my infix2postfix method
	 * then adds the result to postfixes array list, and then lastly calls writeFile to write file to output.txt
	 * @param inputFileName  input file name to retrieve and scan into string
	 * @throws FileNotFoundException
	 */
	static void scanner(String inputFileName) throws FileNotFoundException {
		String infix = new String("");
		String postfix = new String("");
		File f = new File(inputFileName);
		Scanner scn = new Scanner(f);
		
		while (scn.hasNextLine()) {
			infix = scn.nextLine();		// sets each new line to infix string
			postfix = Infix2Postfix(infix);		// calls Infix2Postfix method and applies result to postfix
			if (error.equals("(")) {		// The if else statements check for errors assigned to error variable
				String errorCode = "Error: no opening parenthesis detected";
				postfixes.add(errorCode);
			} else if (error.equals(")")) {
				String errorCode = "Error: no closing parenthesis detected";
				postfixes.add(errorCode);
			} else if (error.equals("+") || error.equals("-") || error.equals("*") || error.equals("/") || error.equals("^") || error.equals("%")) {
				String errorCode = "Error: too many operators (" + error + ")";
				postfixes.add(errorCode);
			} else if (numericError) {
				String errorCode = "Error: too many operands (" + error + ")";
				postfixes.add(errorCode);
			} else if (error.equals("NA")) {
				String errorCode = "Error: no subexpression detected ()";
				postfixes.add(errorCode);
			} else {
				postfixes.add(postfix); 
			}
		}
		
		writeFile();	// Calls writeFile to write file to output.txt
		scn.close();
	}
	
	/**
	 * This method is called and takes the inputed string and writes the data onto a file named output.txt
	 * @throws FileNotFoundException
	 */
	static void writeFile() throws FileNotFoundException
	{
		String outputFileName = "output.txt";	// Name of output file
		
		try (PrintWriter out = new PrintWriter(outputFileName)) {
			if (!postfixes.isEmpty()) {
				for (int i = 0; i < postfixes.size(); i++) {	// Loops through postfixes array list and writes data to file
					if (i == postfixes.size()-1) {
						out.print(postfixes.get(i));
					} else {
						out.println(postfixes.get(i));
					}
				}
			}
		}
		
	}
}
