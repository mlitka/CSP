package csp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by Martyna on 12.04.2016.
 */
public class Sudoku {

    private static final String FILE_NAME = "Sudoku/sudoku9.txt";
    private int size = 9;
    private int boxSize = 3;
    private int visitedBT = 0;
    private int visitedFC = 0;
    public Long time;
    private int numberofCellsToRemove;
    private Cell[][] sudokuSolved;
    private int[] preparedDomain;
    private int cellsSet;
    private boolean heuristicMRV =  true;

    public Sudoku(int boxSize) {
        this.boxSize = boxSize;
        this.size = boxSize * boxSize;
        this.prepareDomain();
        time = new Long(0);
    }

    public Sudoku(int boxSize, int numberofCellsToRemove) {
        this.boxSize = boxSize;
        this.numberofCellsToRemove = numberofCellsToRemove;
        this.size = boxSize * boxSize;
        this.prepareDomain();
        time = new Long(0);
    }

    public int getVisitedBT() {
        return visitedBT;
    }

    public int getVisitedFC() {
        return visitedFC;
    }

    private void prepareDomain() {
        preparedDomain = new int[size];
        IntStream.range(1, size + 1).forEach(val -> preparedDomain[val - 1] = new Integer(val));

    }

    public Cell[][] readSudoku() throws IOException {

        Cell[][] sudoku = new Cell[size][size];

        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    sudoku[i][j] = new Cell(scanner.nextInt(), size);
                }
            }
        }

        return sudoku;
    }

    private void removeCells(Cell[][] sudoku){
        int removed = 0;
        while (removed<numberofCellsToRemove){
            int indexR = (int) (Math.random()*(size));
            int indexC = (int) (Math.random()*(size));
            if(sudoku[indexR][indexC].value!=0){
                sudoku[indexR][indexC].value=0;
                removed++;
            }
        }
    }

    public void printSudoku(Cell[][] sudoku) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                stringBuilder.append(String.valueOf(sudoku[i][j].value));
                stringBuilder.append("\t");
            }
            stringBuilder.append("\n\n");
        }

        System.out.println(stringBuilder);
    }

    public Cell[][] startSudoku() throws IOException {
        Cell[][] sudoku = this.readSudoku();
        if(numberofCellsToRemove>0) this.removeCells(sudoku);
        return sudoku;
    }

    public Cell[][] solveSudokuBacktracking() throws Exception {
        Cell[][] sudoku = this.startSudoku();
        time = System.nanoTime();
        if (!this.backtracking(sudoku, 0, 0)) {
            time = System.nanoTime()-time;
            System.out.println("No solution!");
        } else {
            time = System.nanoTime()-time;
            this.printSudoku(sudoku);

        }

        return sudoku;
    }

    public Cell[][] solveSudokuFC() throws Exception {
        Cell[][] sudoku = this.startSudoku();
        HashMap<Integer, HashMap<KeyPair, Cell>> domains = new HashMap<>();
        HashMap<KeyPair, Cell> level0 = this.copyLevel(sudoku);
        domains.put(0, level0);
        time = System.nanoTime();
        if (!this.forwardChecking(sudoku, 0, 0, 0, 0, domains)) {
            time = System.nanoTime()-time;
            System.out.println("No solution!");
            this.printSudoku(sudoku);
        } else {
            time = System.nanoTime()-time;
            this.printSudoku(sudoku);
        }

        return sudoku;
    }

    private HashMap<KeyPair, Cell> copyLevel(Cell[][] sudoku){
        HashMap<KeyPair, Cell> copy = new HashMap<>();
//        HashMap<Integer, Cell> copyCells = new HashMap<>();
        for(int i = 0; i<size; i++){
            for(int j =0; j<size; j++){
                copy.put(new KeyPair(i, j), this.copyCell(sudoku[i][j]));
            }
        }
//        this.printSudoku(sudoku);
        return copy;
    }

    private void readHashMap(HashMap<KeyPair, Cell> domains, Cell[][] sudoku){
        for (Map.Entry<KeyPair, Cell> entry:domains.entrySet()){
                sudoku[entry.getKey().i][entry.getKey().j] = entry.getValue();
        }
    }

    private boolean backtracking(Cell[][] sudoku,
                                 int row,
                                 int column) throws Exception {
        if (row == size)
            return true;

        if (sudoku[row][column].value != 0) {
            int[] rowCol = next(row, column);
            if (backtracking(sudoku, rowCol[0], rowCol[1])) return true;
        } else {
            for (int num = 1; num <= size; num++) {
                visitedBT++;
                if (checkRow(sudoku, row, num) && checkCol(sudoku, column, num) && checkBox(sudoku, row, column, num)) {
                    sudoku[row][column].value = num;
                    this.cellsSet++;
                    this.verifyAllDomains(sudoku, row, column);
//                    this.printSudoku(sudoku);
                    int[] rowCol = next(row, column);
                    if (backtracking(sudoku, rowCol[0], rowCol[1])) return true;
                    sudoku[row][column].value = 0;
                    this.cellsSet--;
                }
            }
        }

        return false;
    }

    private boolean forwardChecking(Cell[][] sudoku,
                                    int row,
                                    int column,
                                    int next,
                                    int level,
                                    HashMap<Integer, HashMap<KeyPair, Cell>> domains) throws Exception {
        if (cellsSet == size*size || row==size) {
//            sudokuSolved = this.copySudoku(sudoku);
            return true;
        }
        if (row<size && column<size && sudoku[row][column].value != 0) {
            int[] rowCol = next(row, column);
//            this.verifyDomain(sudoku, rowCol[0], rowCol[1]);
            if (forwardChecking(sudoku, rowCol[0], rowCol[1], 0, level+1, domains)) return true;
        }
        /*if (sudokuSolved != null) {
            return true;
        }*/
        if (cellsSet == 0) this.verifyDomain(sudoku, row, column);
//        Cell backup = this.copyCell(sudoku[row][column]);
        while (column<size && next < size - sudoku[row][column].numberOfZeroElementsInDomain) {
            visitedFC++;
            int value = this.giveValueFromDomain(sudoku[row][column].domain, next);

            sudoku[row][column].value = value;
            this.cellsSet++;

            domains.put(level, this.copyLevel(sudoku));
//            System.out.println("LEVEL "+level);
            boolean domainsNotEmpty = this.verifyAllDomains(sudoku, row, column);
//            this.printSudoku(sudoku);
            int[] rowCol = next(row, column);
            if (domainsNotEmpty && forwardChecking(sudoku, rowCol[0], rowCol[1], 0, level+1, domains)) {
//                    System.out.println("["+row+", "+column+"]\t"+sudoku[row][column]);
                return true;
            }
//            backup.value = 0;
//            sudoku[row][column] = backup;
            this.readHashMap(domains.get(level), sudoku);
            this.cellsSet--;
            next++;
        }
        return false;
    }

    private boolean verifyAllDomains(Cell[][] sudoku, int row, int column){
        boolean domainsNotEmpty = true;
        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
                if ((i==row && j==column)|| sudoku[i][j].value!=0) {
                    continue;
                }
                this.verifyDomain(sudoku, i, j);
//                    System.out.println("[" + i + ", " + j + "]\t" + sudoku[i][j]);
                if(this.checkEmptyDomain(sudoku, i, j)){
                    domainsNotEmpty = false;
                    break;
                }
            }
            if(!domainsNotEmpty) break;
        }
        return domainsNotEmpty;
    }


    protected boolean checkRow(Cell[][] sudoku, int row, int num) {
        for (int col = 0; col < size; col++)
            if (sudoku[row][col].value == num)
                return false;

        return true;
    }

    protected boolean checkCol(Cell[][] sudoku, int col, int num) {
        for (int row = 0; row < size; row++)
            if (sudoku[row][col].value == num)
                return false;

        return true;
    }

    protected boolean checkBox(Cell[][] sudoku, int row, int col, int num) {
        row = (row / boxSize) * boxSize;
        col = (col / boxSize) * boxSize;

        for (int r = 0; r < boxSize; r++)
            for (int c = 0; c < boxSize; c++)
                if (sudoku[row + r][col + c].value == num)
                    return false;

        return true;
    }

    public int[] next(int row, int col) throws Exception {
        int[] rowColumn = new int[2];
        if (col < size - 1) {
            rowColumn[0] = row;
            rowColumn[1] = col + 1;
        } else {
            rowColumn[0] = row + 1;
            rowColumn[1] = 0;
        }
        return rowColumn;
    }


    private void verifyDomain(Cell[][] sudoku, int row, int column) {
        if (column < size && row < size) {
            if (sudoku[row][column].value == 0) {
//                sudoku[row][column].domain = this.preparedDomain.clone();
//                sudoku[row][column].numberOfZeroElementsInDomain = 0;
                for (int j = 0; j < sudoku[row][column].domain.length; j++) {
                    if(sudoku[row][column].domain[j]!=0) {
                        boolean checkRow = checkRow(sudoku, row, sudoku[row][column].domain[j]);
                        boolean checkCol = checkCol(sudoku, column, sudoku[row][column].domain[j]);
                        boolean checkBox = checkBox(sudoku, row, column, sudoku[row][column].domain[j]);
                        if (!checkRow || !checkCol || !checkBox) {
                            sudoku[row][column].domain[j] = 0;
                            sudoku[row][column].numberOfZeroElementsInDomain++;
                        }
                    }
                }
//                System.out.println("[" + row + ", " + column + "]\t" + sudoku[row][column]);
            } else {
                sudoku[row][column].numberOfZeroElementsInDomain = size;
            }
        }
    }

    private int giveValueFromDomain(int[] domain, int next) {
        int result = 0;
        for (int i = 0; i < domain.length; i++) {
            if (domain[i] != 0) {
                if (result == next) return domain[i];
                else result++;
            }
        }
        return 0;
    }

    private int countNonZeroElemInDomain(int[] domain) {
        int result = 0;
        for (int i = 0; i < domain.length; i++) {
            if (domain[i] != 0) {
                result++;
            }
        }
        return result;
    }

    private Cell[][] copySudoku(Cell[][] sudoku) {
        Cell[][] copy = new Cell[size][size];
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {

                copy[i][j] = new Cell(sudoku[i][j].value, sudoku[i][j].domain.clone(), size,
                        sudoku[i][j].numberOfZeroElementsInDomain);
            }

        }
        return copy;
    }

    private Cell copyCell(Cell cell) {
        return new Cell(cell.value, cell.domain.clone(), size, cell.numberOfZeroElementsInDomain);
    }

    private Cell[] copyRow(Cell[] cells) {
        Cell[] copy = new Cell[size];
        for (int i = 0; i < size; i++) {
            copy[i] = copyCell(cells[i]);
        }
        return copy;
    }

    private boolean checkEmptyDomain(Cell[][] sudoku, int row, int column) {
        boolean result = false;
        if (cellsSet<size*size && column < size && row < size && sudoku[row][column].value == 0)
            result = sudoku[row][column].numberOfZeroElementsInDomain == size;
        return result;
    }


    private int giveNextVariable(Cell[][] sudoku, int lastCol){
        int result = 0;
        if(heuristicMRV){
            result = this.MRV(sudoku);
        } else result = lastCol+1;
        return result;
    }

    //    Minimum Remaining Values – wybór zmiennej o najmniejszej dziedzinie
    private int MRV(Cell[][] sudoku) {
        int minZeroElems = 0;
        int column = 0;
        for (int i =0 ; i<size; i++) {
            for (int j = 0; j<size; j++){
                if(sudoku[i][j].numberOfZeroElementsInDomain>minZeroElems && sudoku[i][j].value==0){
                    minZeroElems = sudoku[i][j].numberOfZeroElementsInDomain;
                    column = i;
                }
            }
        }
        return column;
    }


}
