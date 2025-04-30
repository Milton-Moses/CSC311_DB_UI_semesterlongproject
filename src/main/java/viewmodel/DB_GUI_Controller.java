package viewmodel;

import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Controller for the main database GUI interface.
 * Manages the table view and form for person records.
 */
public class DB_GUI_Controller implements Initializable {
    public Label statusLabel;
    public Button clrBtn;
    public Button addBtn;
    public Button delBtn;
    public Button editBtn;

    @FXML private MenuItem newItem;
    @FXML private MenuItem ChangePic;
    @FXML private MenuItem logOut;
    @FXML private MenuItem editItem;
    @FXML private MenuItem deleteItem;
    @FXML private MenuItem ClearItem;
    @FXML private MenuItem CopyItem;

    @FXML private TextField first_name;
    @FXML private TextField last_name;
    @FXML private TextField department;
    @FXML private TextField email;
    @FXML private TextField imageURL;

    @FXML private ComboBox<Major> majorDropdownMenu;
    @FXML private ImageView img_view;
    @FXML private MenuBar menuBar;

    @FXML private TableView<Person> tv;
    @FXML private TableColumn<Person, Integer> tv_id;
    @FXML private TableColumn<Person, String> tv_fn;
    @FXML private TableColumn<Person, String> tv_ln;
    @FXML private TableColumn<Person, String> tv_department;
    @FXML private TableColumn<Person, String> tv_major;
    @FXML private TableColumn<Person, String> tv_email;

    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    /**
     * Initializes the controller class.
     * @param url The location used to resolve relative paths
     * @param resourceBundle The resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            majorDropdownMenu.getItems().addAll(Major.values());

            // Initialize table columns
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);

            // Set up keyboard shortcuts for menu items
            editItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
            deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
            ClearItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
            CopyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));

            // Bind menu items to their corresponding actions
            editItem.setOnAction(event -> editRecord());
            deleteItem.setOnAction(event -> deleteRecord());
            ClearItem.setOnAction(event -> clearForm());
            CopyItem.setOnAction(event -> copySelectedRecord());

            // Create binding for form validation
            BooleanBinding isFormValid = Bindings.createBooleanBinding(() ->
                            !first_name.getText().isEmpty() &&
                                    !last_name.getText().isEmpty() &&
                                    !department.getText().isEmpty() &&
                                    majorDropdownMenu.getValue() != null &&
                                    !email.getText().isEmpty() &&
                                    !imageURL.getText().isEmpty() &&
                                    first_name.getText().matches("^[a-zA-Zà-üÀ-Ü\\s'-]+$") &&
                                    last_name.getText().matches("^[a-zA-Zà-üÀ-Ü\\s'-]+$") &&
                                    department.getText().matches("^[a-zA-Z\\s-]+$") &&
                                    email.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$") &&
                                    imageURL.getText().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"),
                    first_name.textProperty(),
                    last_name.textProperty(),
                    department.textProperty(),
                    majorDropdownMenu.valueProperty(),
                    email.textProperty(),
                    imageURL.textProperty()
            );

            // Enable Add button and menu item when form is valid
            addBtn.disableProperty().bind(isFormValid.not());
            newItem.disableProperty().bind(isFormValid.not());

            // Bind edit/delete buttons and menu items to table selection
            BooleanBinding isItemSelected = Bindings.createBooleanBinding(() ->
                            tv.getSelectionModel().getSelectedItem() != null,
                    tv.getSelectionModel().selectedItemProperty()
            );

            editBtn.disableProperty().bind(isItemSelected.not());
            delBtn.disableProperty().bind(isItemSelected.not());
            editItem.disableProperty().bind(isItemSelected.not());
            deleteItem.disableProperty().bind(isItemSelected.not());

            // Initial state
            updateButtonStyles();

        } catch (Exception e) {
            throw new RuntimeException("Initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Updates button styles based on their enabled state.
     */
    private void updateButtonStyles() {
        if (addBtn.isDisabled()) {
            addBtn.setStyle("-fx-background-color: #808080;");
        } else {
            addBtn.setStyle("-fx-background-color: #a73;");
        }

        if (editBtn.isDisabled()) {
            editBtn.setStyle("-fx-background-color: #808080;");
        } else {
            editBtn.setStyle("-fx-background-color: #a73;");
        }

        if (delBtn.isDisabled()) {
            delBtn.setStyle("-fx-background-color: #808080;");
        } else {
            delBtn.setStyle("-fx-background-color: #a73;");
        }
    }

    /**
     * Validates form inputs.
     * @return true if all inputs are valid, false otherwise
     */
    protected boolean validateForm() {
        if (first_name.getText().isEmpty() || last_name.getText().isEmpty() ||
                department.getText().isEmpty() || majorDropdownMenu.getValue() == null ||
                email.getText().isEmpty() || imageURL.getText().isEmpty()) {
            statusLabel.setText("Error: All fields must be filled");
            return false;
        }

        if (!first_name.getText().matches("^[a-zA-Zà-üÀ-Ü\\s'-]+$")) {
            statusLabel.setText("Error: Invalid first name");
            return false;
        }

        if (!last_name.getText().matches("^[a-zA-Zà-üÀ-Ü\\s'-]+$")) {
            statusLabel.setText("Error: Invalid last name");
            return false;
        }

        if (!department.getText().matches("^[a-zA-Z\\s-]+$")) {
            statusLabel.setText("Error: Invalid department");
            return false;
        }

        if (!email.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            statusLabel.setText("Error: Invalid email format");
            return false;
        }

        if (!imageURL.getText().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            statusLabel.setText("Error: Invalid image URL");
            return false;
        }

        statusLabel.setText("Validation successful");
        return true;
    }

    /**
     * Adds a new record to the database.
     */
    @FXML
    protected void addNewRecord() {
        if (validateForm()) {
            Major selectedMajor = Major.fromDisplayName(majorDropdownMenu.getValue().getDisplayName());
            if (selectedMajor == null) {
                statusLabel.setText("Error: Invalid major selected");
                return;
            }

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    selectedMajor.name(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            statusLabel.setText("Record added successfully");
        }
    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        majorDropdownMenu.setValue(null);
        email.setText("");
        imageURL.setText("");
        statusLabel.setText("Form cleared");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p != null) {
            Major selectedMajor = Major.fromDisplayName(majorDropdownMenu.getValue().getDisplayName());
            if (selectedMajor == null) {
                statusLabel.setText("Error: Invalid major selected");
                return;
            }

            int index = data.indexOf(p);
            Person p2 = new Person(p.getId(), first_name.getText(), last_name.getText(),
                    department.getText(), selectedMajor.name(), email.getText(), imageURL.getText());
            cnUtil.editUser(p.getId(), p2);
            data.remove(p);
            data.add(index, p2);
            tv.getSelectionModel().select(index);
            statusLabel.setText("Record updated successfully");
        }
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p != null) {
            int index = data.indexOf(p);
            cnUtil.deleteRecord(p);
            data.remove(index);
            if (index < data.size()) {
                tv.getSelectionModel().select(index);
            } else if (!data.isEmpty()) {
                tv.getSelectionModel().select(index - 1);
            }
            statusLabel.setText("Record deleted successfully");
        }
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
            imageURL.setText(file.toURI().toString());
        }
    }

    @FXML
    protected void addRecord() {
        addNewRecord();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p != null) {
            first_name.setText(p.getFirstName());
            last_name.setText(p.getLastName());
            department.setText(p.getDepartment());
            try {
                Major major = Major.valueOf(p.getMajor());
                majorDropdownMenu.setValue(major);
            } catch (IllegalArgumentException e) {
                majorDropdownMenu.setValue(null);
            }
            email.setText(p.getEmail());
            imageURL.setText(p.getImageURL());
        }
        updateButtonStyles();
    }

    private void copySelectedRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p != null) {
            first_name.setText(p.getFirstName());
            last_name.setText(p.getLastName());
            department.setText(p.getDepartment());
            try {
                Major major = Major.valueOf(p.getMajor());
                majorDropdownMenu.setValue(major);
            } catch (IllegalArgumentException e) {
                majorDropdownMenu.setValue(null);
            }
            email.setText(p.getEmail());
            imageURL.setText(p.getImageURL());
            statusLabel.setText("Record copied to form");
        }
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options = FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2, textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(), textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(results.fname + " " + results.lname + " " + results.major);
        });
    }

    public void onMajorPressed(ActionEvent actionEvent) {
        System.out.println("Major selection changed");
    }

    @FXML
    protected void importCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                data.clear(); // Clear existing data if you want fresh import
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(","); // Assuming fields are comma-separated
                    if (fields.length == 6) { // id is generated separately usually
                        Person p = new Person(
                                fields[0], // firstName
                                fields[1], // lastName
                                fields[2], // department
                                fields[3], // major (string name)
                                fields[4], // email
                                fields[5]  // imageURL
                        );
                        cnUtil.insertUser(p);
                        cnUtil.retrieveId(p);
                        p.setId(cnUtil.retrieveId(p));
                        data.add(p);
                    }
                }
                statusLabel.setText("Import successful");
            } catch (Exception e) {
                statusLabel.setText("Error importing CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Person p : data) {
                    writer.println(p.getFirstName() + "," +
                            p.getLastName() + "," +
                            p.getDepartment() + "," +
                            p.getMajor() + "," +
                            p.getEmail() + "," +
                            p.getImageURL());
                }
                statusLabel.setText("Export successful");
            } catch (Exception e) {
                statusLabel.setText("Error exporting CSV: " + e.getMessage());
            }
        }
    }


    private enum Major {
        BUSINESS("Business"),
        CSC("Computer Science"),
        CPIS("Computing & Information Systems");

        private final String displayName;

        Major(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Major fromDisplayName(String displayName) {
            for (Major major : values()) {
                if (major.displayName.equals(displayName)) {
                    return major;
                }
            }
            return null;
        }
    }

    private static class Results {
        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
}