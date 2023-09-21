package ga_Algorithm;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		boolean again = true;
		try (Scanner scanner = new Scanner(System.in)) {
			while (again) {
				//Generate Puzzle and trying to solve it with the Genetic Algorithm
			    SudokuPuzzle puzzle = new SudokuPuzzle("solution.txt");
			    
			    if(!puzzle.isFound()) {
			        System.out.println("\n\tNo solution was found, want to try again? YES: 1 : NO: 0\n\t");
			        int answer = scanner.nextInt();
			        if (answer == 0)
			        {
			        	again = false;
			        }
			        
			        
			    }
			    else {
			        System.out.println("\n\tA solution was found, open the text file: solution.txt!");
			        again = false;
			    }
			    System.out.println();
			}
		}
	}

}
