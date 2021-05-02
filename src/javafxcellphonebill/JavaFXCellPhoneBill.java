/*
 *    Program Name: Cell Phone Bill GUI
 *    Programmer:   Sintumnyuy Joseph
 *    Class:        CIS-084 Java Programming
 *    Date:         April 25, 2021
 *    Version:      1.0
 *    INPUTS:       Name, Plan, GB used
 *    PROCESSING:   Compute customer bill
 *    OUTPUTS:      Display Name, Customer bill
 */


package javafxcellphonebill;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Dan McElroy
 */
public class JavaFXCellPhoneBill extends Application {

    // define row index names into the PLAN array
    private static final int PLAN_A = 0;   // Plan-A
    private static final int PLAN_B = 1;   // Plan-B
    private static final int PLAN_C = 2;   // Plan-C
    private static final int PLAN_D = 3;   // Plan-D
    private static final double OVER_LIMIT_CHARGE = 15.0;  // price per GB over limit
    private static final char[] PLAN = {'A', 'B', 'C', 'D'};
    // define column index names into the PLAN array
    private static final int LIMIT = 0;
    private static final int PRICE = 1;
    private static final double[][] OPTION = {
            {0.0, 50.00}, // Plan-A
            {2.0, 60.00}, // Plan-B
            {4.0, 70.00}, // Plan-C
            {10.0, 90.00}, // Plan-D
    };
    final double PRICE_PER_GB = 15.00;
    // controls for the top pane
    Label lblTitle;
    // controls for the left pane
    Label lblPlansAndPrices;
    // controls for the center pane
    Label lblCustomerData;  // title at the top
    Label lblName;          // prompt at top for Name
    TextField txtName;      // input the name
    TextField txtPlan;      // input the plan
    Label lblPlan;          // prompt at right for plan
    TextField txtGBused;    // input for GB used
    Label lblGBused;        // prompt at right for GB used
    // controls for the right pane
    Label lblCustomerBill;
    Label lblCustomerName;
    Label lblPleasePay;
    // controls for the bottom pane
    Button btnCompute;
    Button btnClear;
    Button btnExit;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static double computeBill(int used, double limit, double rate) {
        double overLimit;
        double bill;

        if (used <= limit) { // see if customer used more GB than is on the plan
            overLimit = 0.0;
        } else {
            overLimit = Math.ceil(used - limit);
        }
        // the bill is the plan's base rate + any charge for GB over the plan limit
        bill = rate + overLimit * OVER_LIMIT_CHARGE;
        return bill;
    }

    @Override
    public void start(Stage primaryStage) {
        // 1. use a border pane to layout the scene on the stage
        BorderPane root = new BorderPane();
        root.setTop(createTop());       // contains an HBox control
        root.setLeft(createLeft());     // contains a VBox control
        root.setCenter(createCenter()); // contains a VBox control
        root.setRight(createRight());   // contains a VBox control
        root.setBottom(createBottom()); // contains an HBox control

        // 2. create a JavaFX scene that holds the border pane, stored in root
        Scene scene = new Scene(root);

        // 3. the primaryStage holds the scene that is to be displayed
        primaryStage.setTitle("JavaFX Cell Phone Bill"); // title at top of window
        primaryStage.setScene(scene);   // put the scene on the primaryStage
        primaryStage.show();            // display the primaryStage
    }

    private HBox createTop() {  // put a title at the top - big bold letters
        // use an HBox named hbox to hold the title. HBox is used to get the centering
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Font font36B = Font.font("Ariel", FontWeight.BOLD, 36); // title
        lblTitle = new Label("Joseph's Cell Phone Billing");
        lblTitle.setFont(font36B);
        hbox.getChildren().add(lblTitle);
        return hbox;
    } // end of createTop() method

    private VBox createLeft() {
        // use a VBox named vbox to hold the display of plans and prices
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(0, 20, 0, 20));
        // use a Label control to hold the text that will be displayed
        lblPlansAndPrices = new Label(
                """
                        PLANS & PRICES
                        --------------------
                        A =  0 GB $50.00
                        B =  2 GB $60.00
                        C =  4 GB $70.00
                        D = 10 GB $90.00
                        +$15.00/GB over plan limit"""
        );
        // put the Label in the VBox
        vbox.getChildren().add(lblPlansAndPrices);
        return vbox;
    } // end of createLeft()

    private VBox createCenter() {
        // Use a VBox named vbox to hold everything for the center pane
        VBox vbox = new VBox();
        vbox.setSpacing(10.0);

        // inside  vbox  create a Label for the title
        lblCustomerData = new Label("Customer Data");

        // inside vbox, add another VBox named vbox1 to hold the prompt = "  Name"
        //    and a TextField for the user to type the name
        VBox vbox1 = new VBox();
        lblName = new Label("  Name");
        txtName = new TextField();
        txtName.setPrefSize(200, 20);
        txtName.setMaxWidth(200);
        vbox1.getChildren().addAll(lblName, txtName);

        // also inside vbox, use an HBox named hbox2 with a Label and TextField to get the Plan
        HBox hbox2 = new HBox();
        txtPlan = new TextField();
        txtPlan.setPrefSize(40, 20);
        lblPlan = new Label("  Plan  (A-D)");   // lblPlan defined global - used more than one place
        hbox2.getChildren().addAll(txtPlan, lblPlan);

        // also inside vbox, one more HBox with a Label and TextField to get the GB Used
        HBox hbox3 = new HBox();
        txtGBused = new TextField();
        txtGBused.setPrefSize(40, 20);
        lblGBused = new Label("  GB Used"); // lblGBused defined global - used more than one place
        hbox3.getChildren().addAll(txtGBused, lblGBused);

        // put the title "Customer Data", and inputs for Name, Plan and GB Used in vbox
        vbox.getChildren().addAll(lblCustomerData, vbox1, hbox2, hbox3);
        return vbox;
    } // end of createCenter()

    private VBox createRight() {    // "Customer Bill", Name and amount of the bill
        // use VBox named vbox to hold everything for the right pane
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(0, 20, 0, 20));
        vbox.setMinWidth(250);
        vbox.setSpacing(10.0);
        vbox.setAlignment(Pos.TOP_LEFT);

        // use Labels to display "Customer Bill", name and amount to pay
        // these will be filled in with more info when the [Compute] button is clicked
        lblCustomerBill = new Label("Customer Bill");
        lblCustomerName = new Label("Name: ");
        lblPleasePay = new Label("Please Pay:");
        vbox.getChildren().addAll(lblCustomerBill, lblCustomerName, lblPleasePay);
        return vbox;
    } // end of createRight()

    // the createBottom() method creates three buttons, [Compute], [Clear], and [Exit]
    // after the buttons are created, they are added to an HBox which is used in the BorderPane
    private HBox createBottom() {
        // use HBox named hbox to hold everything for the bottom pane
        HBox hbox = new HBox();
        hbox.setSpacing(20.0);
        hbox.setPrefHeight(50);
        hbox.setAlignment(Pos.CENTER);

        btnCompute = new Button("Compute");  // create a button for [Compute]
        btnCompute.setPrefSize(110, 20);             // set sizes uniform for each button
        btnCompute.setOnAction(e -> compute());    // event handler for [Compute] button

        btnClear = new Button("Clear");
        btnClear.setPrefSize(110, 20);
        btnClear.setOnAction(e -> clear());        // event handler for [Clear] button

        btnExit = new Button("Exit");
        btnExit.setPrefSize(110, 20);
        btnExit.setOnAction(e -> System.exit(0));  // Exit button ends the program

        // put each of the buttons in the HBox
        hbox.getChildren().addAll(btnCompute, btnClear, btnExit);
        return hbox;
    } // end of createBottom()

    /********** to function as desired                        ************/
    private void compute() {
        double bill = 0;

        if (txtName.getText().equals("")) lblCustomerName.setText("Name: Enter Name");
        else lblCustomerName.setText("Name: " + txtName.getText()); // Set Customer name

        try {
            // Put 0.0 in txtGBused if left blank
            if (txtGBused.getText().equals("")) txtGBused.setText("0");

            int GBused = Integer.parseInt(txtGBused.getText());

            if (GBused < 0) lblGBused.setText("  Value must be positive");
            else lblGBused.setText("  GB Used");

            if (txtPlan.getText().equals("")) lblPlan.setText("  No plan selected");
            else {
                bill = switch (txtPlan.getText().toUpperCase().charAt(0)) {
                    case 'A' -> computeBill(GBused, OPTION[PLAN_A][LIMIT], OPTION[PLAN_A][PRICE]);
                    case 'B' -> computeBill(GBused, OPTION[PLAN_B][LIMIT], OPTION[PLAN_B][PRICE]);
                    case 'C' -> computeBill(GBused, OPTION[PLAN_C][LIMIT], OPTION[PLAN_C][PRICE]);
                    case 'D' -> computeBill(GBused, OPTION[PLAN_D][LIMIT], OPTION[PLAN_D][PRICE]);
                    default -> {
                        lblPlan.setText("*** Illegal Plan Selected");
                        lblPleasePay.setText("Please Pay:");
                        yield 0;
                    }
                };

                if (bill != 0) {
                    lblPlan.setText("  Plan  (A-D)");
                    lblPleasePay.setText(String.format("Please Pay: $%.2f", bill));
                }
            }

        } catch (NumberFormatException e) {
            lblGBused.setText("  Value must be numeric");
            lblPleasePay.setText("Please Pay:");
        }


    }

    private void clear() {
        txtName.setText("");
        txtPlan.setText("");
        lblPlan.setText("  Plan  (A-D)");
        txtGBused.setText("");
        lblGBused.setText("  GB Used");
        lblCustomerName.setText("Name: ");
        lblPleasePay.setText("Please Pay:");
    }


} // end of the class definition