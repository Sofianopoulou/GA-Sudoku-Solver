# GA-Sudoku-Solver
In this project, I solve Sudoku puzzles by using a genetic algorithm.

The Java console application that I developed does the following:
 - Generates a 9 x 9 matrix that is a valid Sudoku puzzle with the help of the QQWing library. The puzzle and its solution are printed out.
 - Runs a genetic algorithm that tries to solve the generated Sudoku puzzle. Each time that a new generation is obtained, the application prints the mean fitness of the individuals, and the fitness of the best individual.
 - Once the genetic algorithm has finished, a message is being printed indicating whether the solution to the puzzle was found. After that, the best individual of the last generation is being printed out.

A plain text file named solution.txt is being generated which contains the full output of the program.
