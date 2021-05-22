// Program Name: JavaFX Event Registry and Printing
// Programmer:   Sintumnyuy Joseph
// Class:        CIS-084 Java Programming
// Date:         May 13, 2021
// Version:      1.0

package javafxeventregistry;

/**
 * JavaFXEventRegistry.java
 * <p>
 * The program reads the control file   /Documents/EventRegistry/ItemList.txt
 * and lets the user select items from the list. A TextArea control is used to
 * display the name of the user and the user's selections. Multiple users (guests)
 * can add their own selections to the same list.
 * <p>
 * The list is printed to the default printer when the PRINT button is clicked.
 * <p>
 * The list can be saved to disk if code is written for the saveSelectionList() routine
 * The list cleared is when the SAVE/CLEAR button is clicked.
 *
 * @author Dan McElroy, San Jose City College
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.print.PrinterJob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javafx.geometry.Pos;
import javafx.stage.Stage;

public class JavaFXEventRegistry extends Application {
    // Error messages appear if the program is not able to open  ItemList.txt
    private static final String ERROR_MESSAGE1 =  // in the LEFT position
            """
                    Unable to access the "ItemList.txt" file\s

                    Make sure  ItemList.txt  file is placed in a subfolder
                    named EventRegistry in the Documents folder

                    The format of the  ItemList.txt  file is:
                    Name of the Event Registry
                    List of items, one per line
                    --Use a blank line to separate groups of items
                    --Enter   NEW COLUMN   to start a new column
                    """;
    private static final String ERROR_MESSAGE2 =  // in the RIGHT position
            """
                    Sample  ItemList.txt  file
                    Scroll to see full description
                    ------------------------------
                    Dollar Shoppe Wedding Registry

                    8 Plates
                    8 Kinves

                    1 Spatula
                    2 Serving Spoons
                    1 Potato Peeler

                    NEW COLUMN
                    1 Dish Soap
                    1 Box of Soap Bars
                    1 Shampoo

                    6 Light Bulbs
                    2 Wash Cloths
                    """;
    private final String itemListFilename = "/Documents/EventRegistry/ItemList.txt";
    private final String eventRegistryLogFilename = "/Documents/EventRegistry/EventRegistry.log";
    private final String Title = "Event Registry"; // Default title if no file
    private Label lblTitle;         // title at the top of the scene
    private TextField guest;        // to contain the guest's name
    private TextArea selectionList; // user's selection of items - BorderPane right position
    private GridPane grid = null;   // contains list of options

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setTop(createTitle());
        root.setBottom(createButtons());
        root.setRight(createRightPosition());
        root.setLeft(createLeftPosition());
        root.setStyle("-fx-background-color: #F0E0C0;");
        root.setPadding(new Insets(5, 10, 0, 10)); // spacing between nodes
        BorderPane.setMargin(selectionList, new Insets(5, 10, 0, 5));

        Scene scene = new Scene(root);
        primaryStage.setTitle(Title);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> saveSelectionList());
        primaryStage.show();
    }

    /**
     * HBox createTitle()
     *
     * @return HBox that contains the title in a Label at the top BorderPane position
     */
    private HBox createTitle() {  // BorderPane TOP position
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Font font36B = Font.font("Ariel", FontWeight.BOLD, 36); // title
        lblTitle = new Label(Title);
        lblTitle.setFont(font36B);
        hbox.getChildren().add(lblTitle);
        return hbox;
    }

    /**
     * TextArea createRightPosition()
     *
     * @return TextArea that displays the user's selection of items
     */
    private TextArea createRightPosition() {
        selectionList = new TextArea();  // reference created at top of file
        Font fontCourierNew = Font.font("Courier New", FontWeight.BOLD, 14);
        selectionList.setFont(fontCourierNew);
        selectionList.setPrefWidth(300.0);
        selectionList.setWrapText(true);
        return selectionList;
    }

    /**
     * VBox createLeftPosition()
     *
     * @return VBox that contains TextField for user's name and list of possible items
     */
    private VBox createLeftPosition() { // BorderPane LEFT position
        VBox vbox = new VBox();

        // create a string that contains the fully qualified filename
        //   /User/username/Documents/GiftRegistry/GiftItems.txt
        //     where: username is the ID of the person who logged in to the system
        String homePath = System.getenv("HOMEPATH"); // System environment variable
        if (homePath == null) // maybe it is a Mac or Linux system
            homePath = System.getenv("HOME");
        String filename = homePath + itemListFilename;

        // read a list of all the available options from the disk file
        ReadTextFile selectionOptions = new ReadTextFile(filename);
        // display error messages if unable to read the file
        if (selectionOptions.isFileError()) {
            Label errorMessage = new Label(ERROR_MESSAGE1);
            vbox.getChildren().add(errorMessage);
            selectionList.setText(ERROR_MESSAGE2);
            return vbox;    // the return statement causes the createLeftPosition()
            // method to end, but the program does not exit
        }
        // get the item list from the file and place in an array of strings
        String[] itemList = selectionOptions.getFileContents();
        int lineCount = selectionOptions.getLineCount(); // from file

        int line = 0;   // line of the String array from the file
        int column = 0;   // X position in grid for adding items
        int row = 0;      // Y position in grid for adding items
        // The first thing in the  ItemList.txt  file should be the registry name
        //   -- skipping any leading blank lines
        do {
            lblTitle.setText(itemList[line]);
        } while (itemList[line++].trim().equals(""));
        // now, skip any empty lines until the first option is read
        while (itemList[line++].trim().equals("")) ; // skip blank lines

        // Provide a TextField for the guest's name
        Label lblGuestName = new Label("Guest Name");
        guest = new TextField();    // reference created at top of file
        Label lblBlankLine = new Label(""); // blank line before list of items

        // time to add the items to the BorderPane left position using a grid
        // if "NEW COLUMN" appears in the file, start a new column
        grid = new GridPane();
        grid.setStyle("-fx-background-color: #C0E0FF;");
        grid.setHgap(10);                   // spacing between grid elements
        grid.setVgap(5);
        grid.setPadding(new Insets(10));    // spacing around the grid

        while (line < lineCount) {
            if (itemList[line].trim().toUpperCase().equals("NEW COLUMN")) {
                row = 0;
                column++;   // move to the next column if "NEW COLUMN"
            } else if (itemList[line].trim().equals("")) { // if a blank line
                grid.add(new Label(""), column, row++);  // -use it as a separator
            } else {
                CheckBox chkOption = new CheckBox(itemList[line]);
                grid.add(chkOption, column, row++);     // anything else is a CheckBox
            }
            line++;
        }
        vbox.getChildren().addAll(lblGuestName, guest, lblBlankLine, grid);
        return vbox;
    }

    /**
     * HBox createButtons()
     * <p>
     * Creates the Submit, Print, Save/Clear and Exit buttons
     *
     * @return HBox that contains buttons for the bottom of the scene
     */
    private HBox createButtons() {  // Border BOTTOM
        HBox hbox = new HBox();
        hbox.setSpacing(20.0);  // spacing between the buttons
        hbox.setPrefHeight(50); // spacing around the top/bottom of buttons
        hbox.setAlignment(Pos.CENTER); // center the buttons on the row

        // The Submit button also has a tool tip "Enter a GUEST name"
        Button btnSubmit = new Button("Submit");
        btnSubmit.setPrefSize(110, 20);    // make each button the same size
        btnSubmit.setOnAction(e -> submitToSelectionList());
        Tooltip tt = new Tooltip();
        tt.setText("Enter a GUEST name");
        btnSubmit.setTooltip(tt);

        // Button to print the TextArea node
        Button printTextButton = new Button("Print List");
        printTextButton.setPrefSize(110, 20);
        printTextButton.setOnAction(e -> printSelections());

        Button btnSaveClear = new Button("Save/Clear");
        btnSaveClear.setPrefSize(110, 20);
        btnSaveClear.setOnAction(e -> saveAndClear());

        Button btnExit = new Button("Exit");
        btnExit.setPrefSize(110, 20);
        btnExit.setOnAction(e -> System.exit(0));

        hbox.getChildren().addAll(btnSubmit, printTextButton, btnSaveClear, btnExit);
        return hbox;
    }

    /**
     * void submitToSelectionList()
     * <p>
     * Each time the SUBMIT button is clicked,
     * 1) build a new String that contains the guest's name and each item that is selected
     * 2) append the string to the selection
     * 3) prepare for next guest by disabling current selections
     */
    private void submitToSelectionList() {
        String currentSelections; // receipt for a single item, append to full receipt
        // put Guest's name at the top of the selections
        currentSelections = guest.getText() + "\n";
        // Add any checked items into String currentUser
        if (grid != null)  // make sure the grid has been build
            for (Node node : grid.getChildren()) { // get an entry in the grid
                if (node instanceof CheckBox) {       // see if it is a CheckBox
                    // cast to a grid item to a CheckBox
                    if (((CheckBox) node).isSelected() &&   // if selected and
                            !((CheckBox) node).isDisabled()) {  //   not disabled
                        // append item to the currentSelections string
                        currentSelections += ((CheckBox) node).getText() + "\n";
                    } // end of if(isSelected())  test
                } // end of checking to see if it is a CheckBox
            } // end of for loop
        // add currentSelections to the top of the selectionList in BorderPane right position
        selectionList.setText(currentSelections + "\n" + selectionList.getText());

        // prepare for next guest by disabeling current selections
        if (grid != null)  // make sure the grid has been build
            for (Node node : grid.getChildren()) { // get an entry in the grid
                if (node instanceof CheckBox)         // see if it is a CheckBox
                    if (((CheckBox) node).isSelected()) {  // if CheckBox is selected
                        ((CheckBox) node).setDisable(true);  //  -then disable it
                    }
            } // end of for loop
    } // end of submitToSelectionList()

    /**
     * void printSelections()
     * <p>
     * Activated by the "Print List" button. Takes the list of selected items
     * from the selectionList TextArea and places them in an array of Strings.
     * Sends a Label containing a header and a list of items to the print() method
     * <p>
     * The header is placed at the top of each page with the event title, date and page #
     * A new page is printed for each block of 40 items
     */
    private void printSelections() {
        final int LINES_ON_PAGE = 40;
        int page = 1;

        // Use a single label to collect all the lines for a page to be printed
        Label linesToPrint = new Label();
        Font fontCourierNew = Font.font("Courier New", FontWeight.NORMAL, 11);
        linesToPrint.setFont(fontCourierNew);
        // Separate the TextArea selectionList into an array of Strings
        String[] listOfItems = selectionList.getText().split("\n");
        int totalLines = listOfItems.length;

        // print lines on a page with the title and date header at the top of the page
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
        Date date = new Date();
        int line = 0;  // index into listOfItems[]
        do {
            // create the header at top of each page
            linesToPrint.setText(                       // header a top of page
                    lblTitle.getText() + "   " +        // event title
                            dateFormat.format(date) +           // date
                            "   Page " +                        // "Page"
                            Integer.toString(page++) + "\n\n"); //   page #
            // create the printable list of items
            do {
                linesToPrint.setText(
                        linesToPrint.getText() +        // build the print string
                                listOfItems[line] + "\n");     // add an item to the string
                line++;                                 // next line
            } while (line % LINES_ON_PAGE != 0 && line < totalLines);
            print(linesToPrint);                        // send page to printer
        } while (line < totalLines); // keep going until the entire TextArray is printed
    }

    /**
     * void print(Label text)
     *
     * @param text - contains text of a full page to be printed
     *              <p>
     *              the printSelections() method places a full page into the text parameter
     */
    private void print(Label text) {
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) { // then the PrinterJob was created successfully
            boolean printed = job.printPage(text);
            if (printed) {
                job.endJob(); // End the printer job
            } else {
                System.out.println("Printing failed.");
            }
        } else {
            System.out.println("Could not create a printer job.");
        }
    }

    /**
     * void SaveAndClear()
     * <p>
     * The SaveAndClear() method is called when the SAVE/CLEAR button is clicked
     * 1) Calls a stub routine for saving the SelectionList to a disk file
     * 2) Clears any check marks on the CheckBoxes and re-enables each CheckBox
     */
    private void saveAndClear() {
        // call routine for saving the SelectionList to disk
        saveSelectionList();

        // clear any check marks on the CheckBoxes
        if (grid != null)  // make sure the grid has been build
            for (Node node : grid.getChildren()) { // get an entry in the grid
                if (node instanceof CheckBox) {       // see if it is a CheckBox
                    ((CheckBox) node).setSelected(false); // cast to CheckBox
                    ((CheckBox) node).setDisable(false);
                }
            }
        guest.setText("");      // clear the Guest's name
        selectionList.clear();  // clear the TextArea used to display guest's selections
    }

    /**
     * void saveSelectionList()
     * <p>
     * stub routine for saving the selectionList to a disk file
     */
    private void saveSelectionList() {
        // Enter the code here to append the contents of the
        //   TextArea selectionList onto the end of a file

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
        Date date = new Date();

        String list = "========\n" +
                lblTitle.getText() + "   " +
                dateFormat.format(date)
                + "\n" + selectionList.getText();

        // create a string that contains the fully qualified filename
        //   /User/username/Documents/EventRegistry/EventRegistry.log
        //     where: username is the ID of the person who logged in to the system
        String homePath = System.getenv("HOMEPATH"); // System environment variable
        if (homePath == null) // maybe it is a Mac or Linux system
            homePath = System.getenv("HOME");
        String fileName = homePath + eventRegistryLogFilename;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(list);
            writer.close();
        } catch (IOException e) {
            System.out.println("An Error Occured");
        }

    }

}  // end of the class definition