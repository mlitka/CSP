package csp;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by Martyna on 08.04.2016.
 */
public class Queens {

    private int size;

    private Cell[] queensSolved;
    private int visitedBT = 0;
    private int visitedFC = 0;
    private boolean heuristicLCV = false;
    private boolean heuristicMRV = true;
    private int[] preparedDomain;
    public Long time;
    private int columnsSet =0;

    public Queens(int size) {

        this.size = size;
        this.prepareDomain();
        time = new Long(0);
    }

    public void setHeuristic1(boolean heuristic1) {
        this.heuristicLCV = heuristic1;
    }

    public int getVisitedBT() {
        return visitedBT;
    }

    public int getVisitedFC() {
        return visitedFC;
    }

    private void prepareDomain(){
        preparedDomain = new int[size];
        IntStream.range(1, size + 1).forEach(val -> preparedDomain[val-1] = new Integer(val));
    }

    public Cell[] solveQueensBacktracking() {
        Cell[] queens = this.prepareCells();
        time = System.nanoTime();
        if (!this.backtracking(queens, 0, 0, 0)) {
            time = System.nanoTime()-time;
            System.out.println("No solution!");
            this.printQueens(queens);
        } else {
            time = System.nanoTime()-time;
            this.printQueens(queens);
        }

        return queens;
    }

    public Cell[] solveQueensForward() {
        Cell[] queens = this.prepareCells();

        time = System.nanoTime();
        if (!this.forwardChecking(queens, 0, 0, 0, this.prepareHashSet(queens))) {
            time = System.nanoTime()-time;
            System.out.println("No solution!");
            this.printQueens(queens);
            return queens;
        } else {
            time = System.nanoTime()-time;
            this.printQueens(queens);
            return queensSolved;
        }

    }

    private HashMap<Integer, HashMap<Integer, Cell>> prepareHashSet(Cell[] queens){
        HashMap<Integer, HashMap<Integer, Cell>> domains = new HashMap<>();
        HashMap<Integer, Cell> level0 = this.copyLevel(queens);
        domains.put(0, level0);
        return domains;
    }

    private Cell[] prepareCells() {
        Cell[] nQueensBoard = new Cell[size];
        for (int i = 0; i < size; i++) {
            nQueensBoard[i] = new Cell(0, preparedDomain.clone(), size);
        }
        return nQueensBoard;
    }

    private HashMap<Integer, Cell> copyLevel(Cell[] queens){
        HashMap<Integer, Cell> copy = new HashMap<>();
        for(int i = 0; i<size; i++){
            copy.put(i, this.copyQueen(queens[i]));
        }
        return copy;
    }

    private void readHashMap(HashMap<Integer, Cell> domains, Cell[] queens){
        for (Map.Entry<Integer, Cell> entry:domains.entrySet()){
            queens[entry.getKey()] = entry.getValue();
        }
    }


    public boolean backtracking(Cell[] queens,
                                int column,
                                int next,
                                int lastValue) {
        if (columnsSet== size) {
            return true;
        }

//        for (int i = 0; i < queens.length; i++) {
        while (next<size) {
            visitedBT++;
            int value = this.giveNextValue(queens[column], lastValue, next, 1);
//            System.out.println("VAL: "+value);
//            System.out.println("COL: "+column);
            if (checkConstraints(queens, value, column)) {
                queens[column].value = value;
                this.columnsSet++;
//                domains.put(level, this.copyLevel(queens));
                for (int j = 0; j < queens.length; ++j) {
                    if (j == column || queens[j].value != 0)
                        continue;
                    this.verifyDomain(queens, j);
                    if (this.checkEmptyDomains(queens, j)) {
                        break;
                    }
                }
                int col = this.giveNextVariable(queens, column);
//                this.printQueens(queens);
//                System.out.println("NEXT: "+col);
                if (backtracking(queens, col, 0, queens[column].value)) return true;
                queens[column].value = 0;
//                this.readHashMap(domains.get(level), queens);
                this.columnsSet--;
            }
            next++;
//        }
        }
        return false;
    }

    public boolean forwardChecking(Cell[] queens, int column, int next, int level, HashMap<Integer, HashMap<Integer, Cell>> domains) {
        if (columnsSet == size) {
            queensSolved = this.copyQueens(queens);
            return true;
        }

//        Cell backup = this.copyQueen(queens[column]);
        while (next < (size - queens[column].numberOfZeroElementsInDomain)) {
            visitedFC++;
            int lastVal = column>0?queens[column-1].value:-1;

            int valFromDomain = giveNextValue(queens[column], lastVal,  next, 0);
            queens[column].value = valFromDomain;
            this.columnsSet++;
//            System.out.println("NEXT: "+col);

            domains.put(level, this.copyLevel(queens));

//            System.out.println("LEVEL "+level);
            boolean domainsNotEmpty = true;
            for (int j = 0; j < queens.length; ++j) {
                if (j == column || queens[j].value != 0)
                    continue;
                this.verifyDomain(queens, j);
                if (this.checkEmptyDomains(queens, j)) {
                    domainsNotEmpty = false;
                    break;
                }
//            this.printQueens(queens);
            }
            int col = this.giveNextVariable(queens, column);
            //if (!this.checkEmptyDomains(queens, j)) {
            if (domainsNotEmpty && forwardChecking(queens, (col), 0, level + 1, domains)) {
                return true;
            }

            this.readHashMap(domains.get(level), queens);
            next++;
            this.columnsSet--;
        }
        //queens[column]=backup;
//        backup.value = 0;
//        }

        return false;
    }


    // 0 <= column < size
    // 0 < row < = size
    private boolean checkConstraints(Cell[] queens, int row, int column) {

        for (int i = 0; i < size; i++) {
            if (queens[i].value == 0)
                continue;
            final boolean sameRow = queens[i].value == row;
            final boolean sameDiagonal = Math.abs(i - column) == Math.abs(queens[i].value - row);
            if (sameRow  || sameDiagonal)
                return false;
        }

        return true;
    }

    public void printQueens(Cell[] queens) {
//        System.out.println("\n" + Arrays.toString(queens));
        String queensStr = "\n";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (queens[j].value == i + 1) queensStr += "1";
                else queensStr += "0";
                queensStr += " ";
            }
            queensStr += '\n';
        }
        System.out.println(queensStr);
    }

    private void verifyDomains(Cell[] queens) {
        for(int i = 0; i<size; i++){
            this.verifyDomain(queens, i);
        }

    }

    private void verifyDomain(Cell[] queens, int column) {
        if(column<size && column>0){
//            queens[column].domain = preparedDomain.clone();
//            queens[column].numberOfZeroElementsInDomain=0;
            for (int i = 0; i < size; i++) {
                if(queens[i].value!=0){
                    for(int j = 0; j<queens[column].domain.length; j++){
                        if (queens[column].domain[j] != 0 && (isDiagonal(i, queens[column].domain[j], queens[i].value, column)
                                || isSet(queens, queens[column].domain[j]))){
                            queens[column].domain[j] = 0;
                            queens[column].numberOfZeroElementsInDomain++;                        }
                    }
                }
            }
//            System.out.println("COL: "+column+"\t"+queens[column]);
        }
    }

    private boolean isDiagonal(int index, int row, int value, int column) {

        return Math.abs(value - row) == Math.abs(column - index);
    }

    private boolean isSet(Cell[] queens, int value) {
        for (Cell cell : queens) {
            if (cell.value == value && cell.value!=0) return true;
        }
        return false;
    }

    private boolean checkEmptyDomains(Cell[] queens, int column) {
//        for (int i = column + 1; i < queens.length; i++) {
//            if (queens[i].numberOfZeroElementsInDomain==size) return true;
//        }
        boolean result = false;
        if(columnsSet<size && column<size-1) result = queens[column].numberOfZeroElementsInDomain==size;
        return result;
    }

    private boolean checkEmptyDomain(Cell cell) {
        boolean result = false;
        for (int i = 0; i < cell.domain.length; i++) {
            if (cell.domain[i] != 0) result = true;
        }
        if (!result) return true;
        return false;
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

    private Cell[] copyQueens(Cell[] queens) {
        Cell[] copy = new Cell[size];
        for (int i = 0; i < queens.length; i++) {
            copy[i] = new Cell(queens[i].value, queens[i].domain.clone(), queens[i].size, queens[i].numberOfZeroElementsInDomain);
        }
        return copy;
    }

    private Cell copyQueen(Cell cell){
        return new Cell(cell.value, cell.domain.clone(), cell.size, cell.numberOfZeroElementsInDomain);
    }

    private int giveValWithMRV(Cell[] queens, int next) {
        int result = 0;
        while (result <= next) {
            int rowConstrainingTheMost = MRV(queens);
            if (rowConstrainingTheMost != 0) {
                if (result == next) return rowConstrainingTheMost;
                else result++;
            }

        }
        return 0;
    }

    private int giveNextValue(Cell cell, int lastRow, int next, int backtracking){
        int result = 0;
        if(heuristicLCV){
            result = this.LCV(lastRow);
        }
        else if(backtracking==1) result = this.giveValFromDomain(this.preparedDomain, next);
        else result = this.giveValFromDomain(cell.domain, next );
        return result;
    }

    private int giveValFromDomain(int[] domain, int next) {
        int result = 0;
        for (int i = 0; i < domain.length; i++) {
            if (domain[i] != 0) {
                if (result == next) {
                     return domain[i];
                }
                else result++;
            }
        }
        return 0;
    }

    private int giveNextVariable(Cell[] queens, int lastCol){
        int result = 0;
        if(heuristicMRV){
            result = this.MRV(queens);
        } else result = lastCol+1;
        return result;
    }

    //    Minimum Remaining Values – wybór zmiennej o najmniejszej dziedzinie
    private int MRV(Cell[] queens) {
        int minZeroElems = 0;
        int column = 0;
        for (int i =0 ; i<size; i++) {
            if(queens[i].numberOfZeroElementsInDomain>minZeroElems && queens[i].value==0){
                minZeroElems = queens[i].numberOfZeroElementsInDomain;
                column = i;
            }
        }
        return column;
    }

    private int LCV(int lastRow){
        int row = 0;
//        if(lastCol == 0 && columnsSet==1) col = 0;
        /*if(columnsSet<size){
            if(lastRow==-1) row = 1;
            else if(lastRow==0)row = size;
            else if(lastRow==size-1) row = 2;
            else if(lastRow==1) row = (size-2)+1;
            else if (columnsSet%2==0) row = (size-lastRow)+1;
            else if(columnsSet%2==1) row = size-lastRow+1;
//            else col = size-lastCol;
        }*/



        return row;
    }
}
