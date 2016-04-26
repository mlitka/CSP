package csp;

import java.text.NumberFormat;

/**
 * Created by Martyna on 08.04.2016.
 */
public class CSP {

    public static void main(String[] args) throws Exception {
        int size = 18;
        int sudokuSize = 3;
        int remove = 0;
        /*Queens queens = new Queens(size);

//        Long time = System.currentTimeMillis();
        System.out.println("FORWARD\n");
        queens.solveQueensForward();
//        time = System.currentTimeMillis() - time;
        System.out.println("FORWARD visited : " + NumberFormat.getInstance().format(queens.getVisitedFC()) + "\n");

        System.out.println("FORWARD time : " + NumberFormat.getInstance().format(queens.time));

        queens = new Queens(size);
//        time = System.currentTimeMillis();
        System.out.println("BACKTRACKING\n");
        queens.solveQueensBacktracking();
        System.out.println("BACKTRACKING visited : " + NumberFormat.getInstance().format(queens.getVisitedBT()) + "\n");
        System.out.println("BACKTRACKING time : " +  NumberFormat.getInstance().format(queens.time));
*/
        Sudoku sudoku = new Sudoku(sudokuSize, remove);

        Cell[][] sudokuBoard = sudoku.readSudoku();
//        sudoku.printSudoku(sudokuBoard);
//
//        Long time = System.currentTimeMillis();

        System.out.println("BACKTRACKING\n");
        sudoku.solveSudokuBacktracking();
        System.out.println("BACKTRACKING visited : " + NumberFormat.getInstance().format(sudoku.getVisitedBT()) + "\n");
        System.out.println("BACKTRACKING time : " + NumberFormat.getInstance().format(sudoku.time));

        sudoku = new Sudoku(sudokuSize, remove);
        sudoku.readSudoku();
        System.out.println("FORWARD\n");
        sudoku.solveSudokuFC();
        System.out.println("FORWARD visited : " + NumberFormat.getInstance().format(sudoku.getVisitedFC()) + "\n");
        System.out.println("FORWARD time : " + NumberFormat.getInstance().format(sudoku.time));

    }
}