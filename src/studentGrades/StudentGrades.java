/*
 *    Program Name: Student Grades GUI
 *    Programmer:   Sintumnyuy Joseph
 *    Class:        CIS-084 Java Programming
 *    Date:         May 21, 2021
 *    Version:      1.0
 *    INPUTS:       Name, Scores
 *    PROCESSING:   Compute average and grade
 *    OUTPUTS:      Display Name, average, grade and records
 */
package studentGrades;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StudentGrades extends Application {

    private final String studentGradesFilename = "/Documents/StudentRecords/StudentGrades.txt";
    private final String Title = "Java Final - Student Grades";
    private final int numOfScores = 5;
    private Label lblTitle;
    private TextArea studentList;
    private TextField name;
    private HBox hboxScores = null;
    private Label lblScoreErrorMsg = null;
    private Label lblAverageGrade = null;
    private double average = 0;
    private char grade;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setTop(createTitle());
        root.setBottom(createButtons());
        root.setRight(createRightPosition());
        root.setLeft(createLeftPosition());
        root.setStyle("-fx-background-color: #F0E0C0;");
        root.setPadding(new Insets(5, 10, 5, 10)); // spacing between nodes
        BorderPane.setMargin(studentList, new Insets(5, 10, 5, 5));

        Scene scene = new Scene(root);
        primaryStage.setTitle(Title);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    /**
     * HBox createTitle()
     * @return HBox for title in a Label at the top BorderPane
     */
    private HBox createTitle() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        Font font = Font.font("Helvetica", FontWeight.BOLD, 32); // title
        lblTitle = new Label(Title);
        lblTitle.setFont(font);
        hBox.getChildren().add(lblTitle);
        return hBox;
    }
    /**
     * HBox createButtons()
     * @return HBox for buttons at the bottom BorderPane
     */
    private HBox createButtons() {
        HBox hBox = new HBox();
        hBox.setSpacing(20.0);
        hBox.setPrefHeight(50.0);
        hBox.setAlignment(Pos.CENTER);

        // The Display List button
        Button btnDisplayList = new Button("Display List");
        btnDisplayList.setPrefSize(110, 20);    // make each button the same size
        btnDisplayList.setOnAction(e -> displayList());
        Tooltip tt = new Tooltip();
        tt.setText("Display list of all Grades");
        btnDisplayList.setTooltip(tt);

        Button btnClear = new Button("Clear");
        btnClear.setPrefSize(110, 20);
        btnClear.setOnAction(e -> clear());

        Button btnExit = new Button("Exit");
        btnExit.setPrefSize(110, 20);
        btnExit.setOnAction(e -> System.exit(0));

        hBox.getChildren().addAll(btnDisplayList, btnClear, btnExit);

        return hBox;
    }

    private VBox createRightPosition() {
        VBox vBox = new VBox();
        Label lblStudentList = new Label("Student List");
        studentList = new TextArea();
        Font fontCourierNew = Font.font("Courier New", FontWeight.BOLD, 14);
        studentList.setFont(fontCourierNew);
        studentList.setPrefWidth(340.0);
        studentList.setWrapText(true);
        vBox.getChildren().addAll(lblStudentList, studentList);
        return vBox;
    }

    private VBox createLeftPosition() {
        VBox vBox = new VBox();
        Label lblName = new Label("Name");
        name = new TextField();
        Label lblScores = new Label("Scores");
        lblScores.setPadding(new Insets(10, 0, 0, 0));
        lblScoreErrorMsg = new Label("");   // label for error messages
        lblScoreErrorMsg.setTextFill(Color.color(1, 0, 0)); // set text color to red
        lblScoreErrorMsg.setPadding(new Insets(5, 0, 10, 0));

        hboxScores = new HBox();
        hboxScores.setSpacing(5.0);
        hboxScores.setMaxWidth(250.0);

        // create score text fields
        for (int i = 0; i < numOfScores; i++) {
            hboxScores.getChildren().add(new TextField());
        }


        Button btnStudentInfo = new Button("Enter Student Information");
        btnStudentInfo.setOnAction(e -> computeAvg());

        lblAverageGrade = new Label("""
                Enter name and scores and
                click Enter Student Information
                \s""");
        lblAverageGrade.setPadding(new Insets(10, 0, 0, 0));

        vBox.getChildren().addAll(lblName, name, lblScores, hboxScores, lblScoreErrorMsg, btnStudentInfo, lblAverageGrade);
        return vBox;
    }

    /**
     * void computeAvg()
     * <p>
     *     Activated by the Enter Student Information button.
     *     Checks if the name and score text fields have data, validates the scores and
     *     computes the average and grade.
     *     Displays results and saves record to StudentRecord.txt
     * </p>
     */
    private void computeAvg() {

        double total = 0;
        String scoresList = "";
        String record;
        lblScoreErrorMsg.setText("");

        try {

            if (name.getText().equals("")) lblScoreErrorMsg.setText("Student name is empty!");

            for (Node node : hboxScores.getChildren()) {
                double score = Double.parseDouble(((TextField) node).getText()); // getting score from textfield

                if (score < 0) {// check if score is positive
                    lblScoreErrorMsg.setText("Scores must be positive");
                    break;
                } else if (score > 100) { // check if score is within range
                    lblScoreErrorMsg.setText("Scores must be between 0 to 100");
                    break;
                }
                total += score; // update total score
                scoresList = scoresList.concat(" " + score); // update score list

            }

            // check for errors, then compute average and grade
            if (lblScoreErrorMsg.getText().equals("")) {
                average = total / numOfScores; // compute average
                // Computing grade
                if (average >= 90) grade = 'A';
                else if (average >= 80) grade = 'B';
                else if (average >= 70) grade = 'C';
                else if (average >= 60) grade = 'D';
                else grade = 'E';

                // displaying average and grade
                lblAverageGrade.setText("The average is " + average + ", grade " + grade);
                record = name.getText() + "\nScores: " + scoresList + "\nAverage: " + average + "\nGrade: " + grade;
                studentList.setText(record);    // display record in student list text area
                saveRecord(record); // save record to file
            }

        } catch (Exception e) {
            if (e.getMessage().equals("empty String"))
                lblScoreErrorMsg.setText("Enter a number (0 - 100)");
            else lblScoreErrorMsg.setText("Score must be numeric (0 - 100)");

        }
    }

    /**
     * void clear()
     *
     * Clears all text fields and the error message label
     */
    private void clear() {

        name.setText("");   // clear name textfield
        for (Node node : hboxScores.getChildren()) ((TextField) node).setText("");  // clear Scores
        lblScoreErrorMsg.setText(""); // clear error message
        lblAverageGrade.setText("");    // clear student average and mean
        studentList.clear();    // clear student list textarea

    }

    /**
     * void void saveRecord(String record)
     *
     * Saves student records to a file - StudentGrades.txt
     *
     * @param record contains the student record entered and computed which is to be saved to file
     */
    private void saveRecord(String record) {

        // create a string that contains the fully qualified filename
        //   /User/username/Documents/EventRegistry/EventRegistry.log
        //     where: username is the ID of the person who logged in to the system
        String homePath = System.getenv("HOMEPATH"); // System environment variable
        if (homePath == null) // maybe it is a Mac or Linux system
            homePath = System.getenv("HOME");
        String fileName = homePath + studentGradesFilename;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(record + "\n----------------------------------\n");
            writer.close();
        } catch (IOException e) {
            studentList.setText("Error occured: \n" +
                    " - Check if ~/Documents/StudentRecords/ exist\n\n" +
                    e.getMessage());
        }
    }

    /**
     * displayList()
     *
     * Displays Student all records from StudentGrades.txt file in the student list textarea
     */
    private void displayList() {
        Scanner infile;

        // create a string that contains the fully qualified filename
        //   /User/username/Documents/EventRegistry/EventRegistry.log
        //     where: username is the ID of the person who logged in to the system
        String homePath = System.getenv("HOMEPATH"); // System environment variable
        if (homePath == null) // maybe it is a Mac or Linux system
            homePath = System.getenv("HOME");
        String fileName = homePath + studentGradesFilename;

        try {
            infile = new Scanner(new File(fileName));
            studentList.setText(infile.useDelimiter("\\Z").next());

        } catch (IOException e) {
            studentList.setText("Error Occured: \n" +
                    " - Try Entering Student Information first" +
                    " - Check if ~/Documents/StudentRecords/ exist\n\n" +
                    e.getMessage());
        }

    }
} // end of class
