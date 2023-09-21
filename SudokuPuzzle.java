package ga_Algorithm;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.PrintWriter;


public class SudokuPuzzle {
	
	// Constants
	private static final int MAX_FITNESS_SCORE = 162;
	private static final int GENERATION_LENGTH = 450;
	
	// Lists to store fitness values and populations
	// Stores the average fitness values for each generation
    private List<Long> avgFitnesses = new ArrayList<>();
    // Stores the best fitness value of each generation
    private List<Integer> BestOfSolutionFitnesses = new ArrayList<>();
    private List<int[][]> population;
    private List<Long> probabilities;
    private Random rand = new Random();

    // Sudoku puzzle and solution grids
    private int puzzle[][] = new int[9][9];;
    private int solution[][] = new int[9][9];
    
    // Parameters for genetic algorithm
    private int POPULATION_SIZE = 2000;
    private int MUTATION_RATE = 5000;
    
    // Store the best solution
    private int[][] BestOfSolution = new int[9][];;
    // Boolean flag to indicate if a solution is found
    private boolean found = false;
    
    private PrintWriter writer;

    public SudokuPuzzle(String filename) throws FileNotFoundException {
    	
        writer = new PrintWriter(new File(filename));
        
        int position = 0;
        int[][] empties = new int[9][];
        int[] TablePuzzle = PuzzleGenerator.computePuzzleWithNHolesPerRow(5);
        // Initialize the puzzle with provided data and count the number of holes
        for(int i = 0; i < 9; i++) {
            int sizeofarray = 0;
            for(int j = 0; j < 9; j++) {
                if(TablePuzzle[position] == 0) {
                    sizeofarray++;
                }
                
                this.puzzle[i][j] = TablePuzzle[position];
                position++;
            }
            empties[i] = new int[sizeofarray];
            BestOfSolution[i] = new int[sizeofarray];
        }
        
        // Initialize the population with random individuals based on the number of holes
        population = new ArrayList<>();
        probabilities = new ArrayList<>();
        for(int m = 0; m < POPULATION_SIZE; m++) {
            int table[][] = new int[9][];
            for(int i = 0; i < 9; i++) {
                table[i] = new int[empties[i].length];
            }
            for(int i = 0; i < 9; i++) {
                Set<Integer> forbidden = new HashSet<>();
                for(int j = 0; j < 9; j++) {
                    if(puzzle[i][j] != 0) { forbidden.add(puzzle[i][j]); }
                }
                Set<Integer> visitedposition = new HashSet<>();
                int var_empties = table[i].length;
                for(int j = 1; j <= 9; j++) {
                    if(!forbidden.contains(j)) {
                        int newposition = rand.nextInt(var_empties);
                        while(visitedposition.contains(newposition)) {
                            newposition = rand.nextInt(var_empties);
                        }
                        visitedposition.add(newposition);
                        table[i][newposition] = j;
                    }
                }
            }
            int scoretable = score(table);
            if(scoretable == MAX_FITNESS_SCORE) { found = true; BestOfSolution = table; }
            probabilities.add(Long.valueOf(0));
            population.add(table);
        }
        writer.print(this.toString());
        GeneticAlgorithmSolver();
    }

    // Calculate the fitness score of an individual
    private int score(int[][] table) {
        int[][] table1 = new int[9][9];
        solver(table1, table);
        int fitting = 0;
        for(int i = 0; i < 9; i++) {
            fitting += FitnessOfColumns(table1, i);
            if(i == 6 || i == 3 || i == 0) {
                fitting += FitnessOfDomains(table1, i, 0);
                fitting += FitnessOfDomains(table1, i, 3);
                fitting += FitnessOfDomains(table1, i, 6);
            }
        }
        return fitting;
    }

    private int FitnessOfDomains(int[][] copy, int i, int j) {
        int fitting = 0;
        // Iterate over numbers 1 to 9
        for(int number = 1 ; number <= 9 ; number++) {
            int count = 0;
            // Count the occurrences of the number in the domain
            for(int row = i ; row < i + 3 ; ++row) {
                for(int col = j ; col < j + 3 ; ++col) {
                    if(copy[row][col] == number) {
                        count++;
                    }
                }
            }
            if(count == 1) {
                fitting++;
            }
        }
        return fitting;
    }

    private int FitnessOfColumns(int[][] copy, int j) {
        int fitting = 0;
        // Iterate over numbers 1 to 9
        for(int number = 1 ; number <= 9 ; number++){
            int count = 0;
            // Count the occurrences of the number in the column
            for(int position = 0 ; position < 9; position++) {
                if(copy[position][j] == number) {
                    count++;
                }
            }
            
            // If the number appears exactly once in the column, increment the fitting value
            if(count == 1) {
                fitting++;
            }
        }
        return fitting;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n      Sudoku Puzzle: \n\n");
        
        for (int i = 0; i < this.puzzle.length; i++) {
            if (i % 3 == 0) {
            	sb.append("+-------+-------+-------+\n");
            }
            for (int j = 0; j < this.puzzle[i].length; j++) {
            	
                if (j % 3 == 0) {
                	sb.append("| ");
                }
                sb.append(this.puzzle[i][j] + " ");
            
                
            }
            sb.append("|\n");
        }
        sb.append("+-------+-------+-------+\n\n");
       
        return sb.toString();
    }

    // Perform the genetic algorithm to find the solution
    public void GeneticAlgorithmSolver() {
        writer.println("---Using the Genetic Algorithm--- \n");
        
        // Iterate for the specified number of generations
        for(int i = 0; i < GENERATION_LENGTH; i++) {
            writer.print("Generation " + i + ":\nAverage Fitness: ");
            double AverageOfScore = 0;
            int MaximumScore = 18; 
            int count = 0;
            
            // Calculate fitness scores and probabilities for each individual in the population
            for(int j = 0; j < this.POPULATION_SIZE; j++) {
                count++;
                int score = score(population.get(j));

                // Calculate probability for selection based on fitness score
                Long probability = Math.round((double) (100 * score - 1800) / 144);
                
                AverageOfScore += score;
                if(score > MaximumScore) {
                    MaximumScore = score;
                }
                
                // If maximum fitness score is reached, store the solution and break
                if(score == MAX_FITNESS_SCORE) {
                    AverageOfScore /= count;
                    long AverageOfScoreRound = Math.round(AverageOfScore);
                    this.avgFitnesses.add(AverageOfScoreRound);
                    this.BestOfSolutionFitnesses.add(MaximumScore);
                    writer.println(AverageOfScoreRound + "\nBest Fitness:" + MaximumScore + "\n");
                    found = true;
                    BestOfSolution = population.get(j);
                    break;
                }
                this.probabilities.set(j, probability);
            }
            
            // If maximum fitness score is reached, break the loop
            if(found) {
            	break;
            }
            
            // Calculate average fitness score and store it
            AverageOfScore = AverageOfScore/POPULATION_SIZE;
            this.avgFitnesses.add(Math.round(AverageOfScore));
            this.BestOfSolutionFitnesses.add(MaximumScore);
            writer.println(AverageOfScore + "\nBest Fitness: " + MaximumScore + "\n");
            
            // Perform selection, crossover, and mutation for the next generation
            selection();
            crossover();
            mutate();
        }
        
        // If solution is found, print the solution and the best individual
        if(found) {
        	writer.println("---There is a solution for the provided Sudoku puzzle---");
            writer.println("\n\n   Solved Sudoku Puzzle: \n");
            solver(this.solution, this.BestOfSolution);
            
            // Print the solved Sudoku puzzle
            for (int i = 0; i < this.solution.length; i++) {
                if (i % 3 == 0) {
                    writer.print("+-------+-------+-------+\n");
                }
                for (int j = 0; j < this.solution[i].length; j++) {
                	
                    if (j % 3 == 0) {
                    	writer.print("| ");
                    }
                    writer.print(this.solution[i][j] + " ");
                    
                }
                writer.print("|\n");
            }
            writer.print("+-------+-------+-------+");
            writer.println();
            // Print the best individual
            writer.println("\n    The Best Individual is:");
            for(int i = 0; i < 9; i++) {
            	writer.print("\t");
                for(int j = 0; j < this.BestOfSolution[i].length; j++) {
                    writer.print(this.BestOfSolution[i][j] + ", ");
                 
                }
                writer.print("\n");
            }
            writer.close();
        }
        else {
        	// If no solution is found, print a message
            writer.println("---No solution found for the provided Sudoku puzzle---");
            writer.close();
        }        
    }

    private void crossover() {
        List<int[][]> arrayOfPopulation = new ArrayList<>();
        for(int i = 0; i+1 < this.POPULATION_SIZE; i += 2) {
        	// Randomly select a start and end position for crossover
            int start = rand.nextInt(9);
            int end = rand.nextInt(9);
            
            // Ensure that start and end positions are different
            while(start == end) {
                start = rand.nextInt(9);
                end = rand.nextInt(9);
            }
            
            // Determine the table value for crossover
            int table = Math.max(start, end);
            if(table == start) { start = end; end = table; }
            int[][] first = new int[9][], second = new int[9][];
            for(int j = 0; j < 9; j++) {
            	// Perform crossover between the selected start and end positions
                if(j >= start && j <= end) {
                    first[j] = population.get(i)[j];
                    second[j] = population.get(i+1)[j];
                }
                else {
                    first[j] = population.get(i+1)[j];
                    second[j] = population.get(i)[j];
                }
            }
            
            // Add the crossover individuals to the new population
            arrayOfPopulation.add(first);
            arrayOfPopulation.add(second);
        }
        // Handle the case when the population size is odd
        if(this.POPULATION_SIZE % 2 != 0) {
        	arrayOfPopulation.add(this.population.get(this.POPULATION_SIZE-1)); 
        }
        
        // Update the population with the new individuals
        population.removeAll(population);
        population.addAll(arrayOfPopulation);
    }
    
    
    private boolean swapping(int table, int x_position, int y_position2) {
        int i = 0, j;
        // Find the column index (j) corresponding to the y_position2 in the puzzle
        for(j = 0; j < 9; j++) {
        	 // Check if the current cell in the puzzle is empty (represented by 0)
            if(this.puzzle[x_position][j] == 0) {
                if(i == y_position2) {
                	// Found the column index corresponding to y_position2
                    break;
                }
                i++;
            }
        }
        
        // Check if the table value already exists in the same row
        for(i = 0; i < 9; i++) {
            if(puzzle[i][j] == table) {
            	// The table value already exists in the same row, so swapping is not allowed
                return false;
            }
        }
        
        // Determine the starting row and column indices of the 3x3 subgrid
        int row = x_position - x_position % 3;
        int column = j - j % 3;
        // Check if the table value already exists in the 3x3 subgrid
        for(int z = row; z < row + 3; ++z) {
            for(int m = column; m < column + 3; ++m) {
                if(this.puzzle[z][m] == table) {
                	// The table value already exists in the 3x3 subgrid, so swapping is not allowed
                    return false;
                }
            }
        }
        // If the table value doesn't exist in the same row or the 3x3 subgrid, swapping is allowed
        return true;
    }

    private void selection() {
    	// Create a new list to store the selected individuals
        List<int[][]> arrayOfPopulation = new ArrayList<>();
        // Initialize a count variable
        int count = 0;
        
        // Select individuals for the next generation
        while(count != this.POPULATION_SIZE) {
            for(int i = 0; i < this.POPULATION_SIZE && count != this.POPULATION_SIZE; i++) {
            	// Check if an individual is selected based on its probability
                if(this.rand.nextInt(100) <= probabilities.get(i)-18) {	
                	// Add the selected individual to the new population
                    arrayOfPopulation.add(population.get(i));
                    count++;
                }
            }
        }
        
        // Replace the current population with the selected individuals
        population.removeAll(population);
        population.addAll(arrayOfPopulation);
    }
    
    private void solver(int[][] fitting, int[][] table) {
    	 // Iterate over the rows of the puzzle
        for(int i = 0; i < 9; i++) {
        	
            int y_position = 0; // Initialize the y_position variable
            // Iterate over the columns of the puzzle
            for(int j = 0; j < 9; j++) {
            	// Check if the current cell in the puzzle is empty (represented by 0)
                if(this.puzzle[i][j] == 0) {
                	// If the cell is empty, assign a value from the table array to the fitting array
                    fitting[i][j] = table[i][y_position];
                    y_position++; // Increment the y_position to move to the next value in the table array
                }
                else {
                    fitting[i][j] = this.puzzle[i][j];
                }
            }
        }
    }
    
    private void mutate() {
    	
        for(int i = 0; i < POPULATION_SIZE; i++) {
            int[][] table = population.get(i);
            boolean done = false;
            while(!done) {
            	 // Check if mutation should occur based on the MUTATION_RATE
                if(rand.nextInt(this.MUTATION_RATE) == 0) {
                    int x_position = rand.nextInt(9);
                    int y_position1 = rand.nextInt(table[x_position].length);
                    int y_position2 = rand.nextInt(table[x_position].length);
                    // Ensure that y_position1 and y_position2 are different
                    while(y_position1 == y_position2) {
                        y_position1 = rand.nextInt(table[x_position].length);
                        y_position2 = rand.nextInt(table[x_position].length);
                    }
                    int tableptp = table[x_position][y_position1];
                    // Check if swapping the table values is allowed
                    if(swapping(table[x_position][y_position1], x_position, y_position2)
                    		&& swapping(table[x_position][y_position2], x_position, y_position1)) {
                    	// Swap the table values
                        table[x_position][y_position1] = table[x_position][y_position2];
                        table[x_position][y_position2] = tableptp;
                    }
                }
                else {
                    done = true;
                }
            }
        }
    }

    public List<Long> getavgFitnesses() {
        return avgFitnesses;
    }

    public List<Integer> getBestOfSolutionFitnesses() {
        return BestOfSolutionFitnesses;
    }

    public boolean isFound() {
        return found;
    }
}
