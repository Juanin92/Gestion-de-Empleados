package com.javaFX.fx.controller;

import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.Entity.Register;
import com.javaFX.fx.FxApplication;
import com.javaFX.fx.service.interfaces.IEmployeeService;
import com.javaFX.fx.service.interfaces.IRegisterService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable{

    @FXML private StackPane stackPane;
    @FXML private TableView<Employee> tvEmployeeList;
    @FXML private TableColumn<Employee,String> colName;
    @FXML private TableColumn<Employee,String> colSurname;
    @FXML private TableColumn<Employee,String> colEmail;
    @FXML private TableColumn<Employee,Integer> colIncome;
    @FXML private TableColumn<Employee,Boolean> colStatus;
    @FXML private TableColumn<Employee,Integer> colId;
    @FXML private TableColumn<Employee,Void> colAction;
    @FXML private TextField txtSearchEmployee;
    @FXML private Button btnCreateEmployee, btnListView;
    @FXML private Label lblWarning;

    private ObservableList<Employee> employeesList;
    @Setter  private Employee currentEmployee;
    @Autowired private IEmployeeService employeeService;
    @Autowired private IRegisterService registerService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCreateEmployee.setCursor(Cursor.HAND);
        btnListView.setCursor(Cursor.HAND);
        listEmployee();
        configurationTableView();
        setupSearchFilter();
        URL stylesheet = getClass().getResource("/com/javaFX/fx/CSS/style.css");
        tvEmployeeList.getStylesheets().add(stylesheet.toExternalForm());
    }

    @FXML
    public void switchView(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnCreateEmployee)){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/newEmployee.fxml"));
            loader.setControllerFactory(FxApplication.getContext()::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) stackPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Registro de empleado");
            stage.show();
        } else if (event.getSource().equals(btnListView)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/listView.fxml"));
            loader.setControllerFactory(FxApplication.getContext()::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Lista Completa");
            stage.show();
        }
    }

    @FXML
    private void departmentView(Employee employee) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/departmentView.fxml"));
        loader.setControllerFactory(FxApplication.getContext()::getBean);
        Parent root = loader.load();

        DepartmentController departmentController = loader.getController();
        departmentController.setEmployee(employee);

        Scene scene = new Scene(root);
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Departamentos - Empleados");
        stage.show();
    }

    @FXML
    private void editWindow(Employee employee) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/editEmployee.fxml"));
        loader.setControllerFactory(FxApplication.getContext()::getBean);
        Parent root = loader.load();

        EmployeeController controller = loader.getController();
        controller.setCurrentEmployee(employee);

        Scene scene =  new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Editar info Empleado");
        stage.setScene(scene);
        stage.show();
    }

    public void listEmployee(){
//        tvEmployeeList.setItems(FXCollections.observableArrayList(employeeService.getAll()));
        employeesList = FXCollections.observableArrayList(employeeService.getAll());
        tvEmployeeList.setItems(employeesList);

        if (employeesList.isEmpty()){
            lblWarning.setVisible(true);
            lblWarning.setText("No hay Empleado. Debe crear alguno");
        }else {
            lblWarning.setVisible(false);
        }

        colName.setCellValueFactory(new PropertyValueFactory<Employee,String>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<Employee,String>("surname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Employee,String>("email"));
        colIncome.setCellValueFactory(new PropertyValueFactory<Employee,Integer>("income"));
        colStatus.setCellValueFactory(new PropertyValueFactory<Employee,Boolean>("status"));
        colId.setCellValueFactory(new PropertyValueFactory<Employee,Integer>("id"));
        configurationTableView();
    }

    private void configurationTableView() {
        tvEmployeeList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> cellFactory = new Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>>() {

            @Override
            public TableCell<Employee, Void> call(final TableColumn<Employee, Void> param) {
                final TableCell<Employee, Void> cell = new TableCell<Employee, Void>() {
                    private final Button btnDepto = new Button("+ Datos");
                    private final Button btnEdit = new Button("Editar");
                    private final Button btnEliminate = new Button("Eliminar");
                    {
                        btnEliminate.setOnAction((event) -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            employeeService.eliminateEmployee(employee);
                            listEmployee();
                        });
                        btnEdit.setOnAction((event) -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            try {
                                editWindow(employee);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        btnDepto.setOnAction((event) -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            try {
                                departmentView(employee);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Employee employee = getTableView().getItems().get(getIndex());
                            btnDepto.setVisible(employee.isStatus());
                            btnEliminate.setVisible(employee.isStatus());

                            List<Register> registers = registerService.getEmployeeId(employee.getId());
                            if (registers.isEmpty()){
                                btnEdit.setVisible(false);
                                btnEliminate.setVisible(false);
                            }else {
                                btnDepto.setVisible(false);
                            }

                            HBox buttons = new HBox(btnDepto, btnEdit, btnEliminate);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.setSpacing(5);// Espaciado entre botones
                            btnEliminate.setCursor(Cursor.HAND); // Tipo de cursor de botón
                            btnEdit.setCursor(Cursor.HAND);
                            btnDepto.setCursor(Cursor.HAND);
                            btnEliminate.setStyle("-fx-background-color: red;"); // color para el botón
                            btnEdit.setStyle("-fx-background-color: yellow;");
                            btnDepto.setStyle("-fx-background-color: green;");
                            btnEliminate.setTextFill(Color.WHITE); // color para el texto dentro del botón
                            btnEdit.setTextFill(Color.BLACK);
                            btnDepto.setTextFill(Color.WHITE);
                            setGraphic(buttons);
                        }
                    }
                };
                return cell;
            }
        };
        colAction.setCellFactory(cellFactory);

        colStatus.setCellFactory(employeeBooleanTableColumn -> new TableCell<Employee,Boolean>(){
            @Override
            protected void updateItem(Boolean item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null){
                    setText("");
                }else {
                    setText(item ? "Activo" : "Inactivo");
                }
            }
        });
        colIncome.setCellFactory(column -> new TableCell<Employee, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
                    setText(currencyFormat.format(item));
                }
            }
        });
    }

    @FXML
    private void setupSearchFilter() {
        txtSearchEmployee.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                tvEmployeeList.setItems(employeesList);
                tvEmployeeList.refresh();
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                ObservableList<Employee> filteredList = FXCollections.observableArrayList();
                for (Employee employee : employeesList) {
                    String employeeStatus = employee.isStatus() ? "activo" : "inactivo";
                    if (employee.getName().toLowerCase().contains(lowerCaseFilter) ||
                            employee.getSurname().toLowerCase().equals(lowerCaseFilter) ||
                            employeeStatus.equals(lowerCaseFilter) ||
                            employee.getEmail().toLowerCase().equals(lowerCaseFilter)) {
                        filteredList.add(employee);
                    }
                }
                tvEmployeeList.setItems(filteredList);
            }
        });
    }
}
