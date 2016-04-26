package csp;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Controller {

    private int size = 9;
    private int sudokuSize = 3;
    private int sceneSize = 500;
    private int rectangleSize = sceneSize/size;

    @FXML
    GridPane showResultGridPane;
    @FXML
    Label queensNumberLabel, nQueensLabel, nQueensVisitedTextLabel, nQueensVisitedTimeLabel, sudokuLabel,
            sudokuNumberLabel, sudokuVisitedTextLabel, sudokuVisitedTimeLabel;
    @FXML
    Slider nQueensSlider, sudokuSlider;
    @FXML
    Button startQueensButton, startSudokuButton;
    @FXML
    Text nQueensVisitedText, algorithmNameText, nQueensVisitedTime,
            sudokuVisitedText, sudokualgorithmNameText, sudokuVisitedTime;

    private void createQueensElements() {
        showResultGridPane.getChildren().clear();
        showResultGridPane.setMinSize(size* rectangleSize, size* rectangleSize);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Rectangle rect = createQueensElement(i, j);
                showResultGridPane.getChildren().add(rect);
                showResultGridPane.setColumnIndex(rect, i);
                showResultGridPane.setRowIndex(rect, j);
            }
        }
    }

    private Rectangle createQueensElement(int i, int j) {
        Rectangle rectangle = new Rectangle(rectangleSize, rectangleSize);
        rectangle.setStroke(Color.BLACK);
        if((i+j)%2==0){
            rectangle.setFill(Color.TURQUOISE);
        }else{
            rectangle.setFill(Color.WHITESMOKE);
        }

        return rectangle;
    }

    private void colourQueens(Cell[] queens){
        for (int i = 0; i < queens.length; i++) {
            for (int j = 0; j < queens.length; j++) {
                if (queens[j].value == i + 1) this.getChild(j, i).setFill(Color.GRAY);
            }
        }
    }

    private Rectangle getChild(int i, int j){
        Node result = null;
        for(Node node : showResultGridPane.getChildren()) {
            if(showResultGridPane.getRowIndex(node) == j && showResultGridPane.getColumnIndex(node) == i) {
                result = node;
                break;
            }
        }
        return (Rectangle) result;
    }


    public void setNNumber() {

        nQueensSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            queensNumberLabel.setText(String.valueOf(newValue.intValue()));
            size = newValue.intValue();
            rectangleSize = sceneSize/size;
        });
    }

    public void startQueens(ActionEvent actionEvent) {
        nQueensSlider.setVisible(true);
        nQueensLabel.setVisible(true);
        startQueensButton.setVisible(false);
        nQueensVisitedText.setVisible(false);
        nQueensVisitedTextLabel.setVisible(false);
        nQueensVisitedTime.setVisible(false);
        nQueensVisitedTimeLabel.setVisible(false);
        createQueensElements();
        setNNumber();

    }

    public void doForwardQueens(ActionEvent actionEvent) {
        Queens queens = new Queens(size);
        Long time = System.currentTimeMillis();
        createQueensElements();
        Cell[] queensSolved = queens.solveQueensForward();
        time = System.currentTimeMillis() - time;
        System.out.println("FORWARD visited : " + queens.getVisitedFC() + "\ntime : "+time+"\n");
        algorithmNameText.setText("FORWARD CHECKING DONE.");

        this.showQueensStatistics(queens.getVisitedFC(), time);
        this.colourQueens(queensSolved);
    }

    public void doBacktrackingQueens(ActionEvent actionEvent) {

        Queens queens = new Queens(size);
        Long time = System.currentTimeMillis();
        createQueensElements();
        Cell[] queensSolved = queens.solveQueensBacktracking();
        time = System.currentTimeMillis() - time;
        System.out.println("BACKTRACKING visited : " + queens.getVisitedBT() + "\ntime : "+time+"\n");
        algorithmNameText.setText("BACKTRACKING DONE.");

        this.showQueensStatistics(queens.getVisitedBT(), time);
        this.colourQueens(queensSolved);

    }


    private void showQueensStatistics(int visited, long time){
        nQueensVisitedText.setVisible(true);
        nQueensVisitedTextLabel.setVisible(true);
        nQueensVisitedTime.setVisible(true);
        nQueensVisitedTimeLabel.setVisible(true);
        nQueensVisitedText.setText(String.valueOf(visited));
        nQueensVisitedTime.setText(String.valueOf(time));

    }

    public void doBacktrackingSudoku(ActionEvent actionEvent) throws Exception {
        Sudoku sudokuCsp = new Sudoku(sudokuSize);
        Long time = System.currentTimeMillis();
        createSudokuElements(new Cell[size][size]);
        Cell[][] solvedSudoku = sudokuCsp.solveSudokuBacktracking();
        time = System.currentTimeMillis() - time;
        System.out.println("BACKTRACKING visited : " + sudokuCsp.getVisitedBT() + "\ntime : "+time+"\n");
        sudokualgorithmNameText.setText("BACKTRACKING DONE.");

        this.showSudokuStatistics(sudokuCsp.getVisitedBT(), time);
        this.createSudokuElements(solvedSudoku);
    }

    public void startSudoku(ActionEvent actionEvent) {
        sudokuSlider.setVisible(true);
        sudokuLabel.setVisible(true);
        startSudokuButton.setVisible(false);
        sudokuVisitedText.setVisible(false);
        sudokuVisitedTextLabel.setVisible(false);
        sudokuVisitedTime.setVisible(false);
        sudokuVisitedTimeLabel.setVisible(false);
        sudokuNumberLabel.setText(String.valueOf(2));
        createSudokuElements(new Cell[size][size]);
        setSudokuSize();
    }

    private void showSudokuStatistics(int visited, long time){
        sudokuVisitedText.setVisible(true);
        sudokuVisitedTextLabel.setVisible(true);
        sudokuVisitedTime.setVisible(true);
        sudokuVisitedTimeLabel.setVisible(true);
        sudokuVisitedText.setText(String.valueOf(visited));
        sudokuVisitedTime.setText(String.valueOf(time));

    }

    public void setSudokuSize() {

        sudokuSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            sudokuNumberLabel.setText(String.valueOf(newValue.intValue()));
            size = newValue.intValue()*newValue.intValue();
            rectangleSize = sceneSize/(2*size);
            sudokuSize = newValue.intValue();
        });
    }

    private void createSudokuElements(Cell [][] sudoku) {
        showResultGridPane.getChildren().clear();
        showResultGridPane.setMinSize(size* rectangleSize, size* rectangleSize);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Rectangle rect = createSudokuElement(i, j);

                Text text = new Text("");
                text.setFont(Font.font(rectangleSize*2/3));
                text.setTextAlignment(TextAlignment.CENTER);
                if(sudoku[i][j].value!=0) text.setText("  "+String.valueOf(sudoku[i][j]));
                showResultGridPane.getChildren().addAll(rect, text);
                showResultGridPane.setColumnIndex(rect, j);
                showResultGridPane.setRowIndex(rect, i);
                showResultGridPane.setRowIndex(text, i);
                showResultGridPane.setColumnIndex(text, j);

            }
        }
    }

    private Rectangle createSudokuElement(int i, int j) {
        Rectangle rectangle = new Rectangle(rectangleSize, rectangleSize);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.WHITE);
        return rectangle;
    }

}
