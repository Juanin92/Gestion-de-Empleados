package com.javaFX.fx.controller;

import com.javaFX.fx.Entity.DTO.EmployeeDTO;
import com.javaFX.fx.FxApplication;
import com.javaFX.fx.service.interfaces.IEmployeeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class RegisterController implements Initializable {

    @FXML private TextField txtName, txtSurname, txtEmail, txtIncome;
    @FXML private CustomTextField txtName2;
    @FXML private Button btnRegister, btnClean;
    @FXML private MenuButton mMenu;
    @FXML private Label lblMessage;
    @FXML private StackPane stackPane;

    private EmployeeDTO employeeDTO;
    @Autowired private IEmployeeService employeeService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtName2.textProperty().addListener((observable, oldValue, newValue) -> {
//            txtName2.setStyle("-fx-border-color: " + (!newValue.trim().isEmpty() ? "green;" : "transparent;"));
            setBorderStyleAndErrorIcon(txtName2, false, "", "");
        });
    }

    @FXML
    public void createEmployee() {
        try{
            employeeDTO = new EmployeeDTO();
//            employeeDTO.setName(txtName.getText());
            employeeDTO.setName(txtName2.getText());
            employeeDTO.setSurname(txtSurname.getText());
            employeeDTO.setEmail(txtEmail.getText());

            if (txtIncome.getText().trim().isEmpty()){
                employeeDTO.setIncome(500000);
            }else {
                try{
                    employeeDTO.setIncome(Integer.parseInt(txtIncome.getText()));
                }catch (NumberFormatException e){
                    lblMessage.setVisible(true);
                    lblMessage.setText("Ingreso inválido. Debe ser un número.");
                }
            }
            employeeService.createEmployee(employeeDTO);

            Notifications.create()
                    .title("Registro Exitoso!")
                    .text("Empleado " + employeeDTO.getName() + " " + employeeDTO.getSurname() + ", el registro fue exitoso")
                    .darkStyle()
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(7))
                    .showInformation();
        }catch (IllegalArgumentException e){
            setBorderStyleAndErrorIcon(txtName2, true, "com/javaFX/fx/img/errorIcon.png", e.getMessage());
        }
    }

    @FXML
    private void clean(){
        txtName.clear();
        txtSurname.clear();
        txtIncome.clear();
        txtEmail.setText("ejemplo@gmail.com");
    }

    @FXML
    public void menu(ActionEvent event) throws IOException {
        MenuItem item = (MenuItem) event.getSource();
        if (item.getId().equals("IHome")) {
            // Cargar la vista principal utilizando el ControllerFactory de Spring
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/Main.fxml"));
            fxmlLoader.setControllerFactory(FxApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            // Establecer la nueva escena
            Scene scene = new Scene(root);
            Stage stage = (Stage) stackPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Página Principal");
            stage.show();
        } else if (item.getId().equals("IListas")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/listView.fxml"));
            loader.setControllerFactory(FxApplication.getContext()::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Lista Completa");
            stage.show();
        } else if (item.getId().equals("IPrueba")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/prueba.fxml"));
            loader.setControllerFactory(FxApplication.getContext()::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ventana Prueba");
            stage.initStyle(StageStyle.UNDECORATED); //
            stage.show();

            root.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY){
                    stage.close();
                }
            });
        }
    }

    private void setBorderStyleAndErrorIcon(CustomTextField customTextField, boolean hasError, String errorMessage, String exceptionMessage) {
        String borderColor = hasError ? "red" : (!customTextField.getText().trim().isEmpty() ? "green" : "transparent");
        customTextField.setStyle("-fx-border-color: " + borderColor + ";");
        if (hasError) {
            Image icon = new Image(errorMessage); // Asumimos que errorMessage es la ruta a la imagen
            ImageView error = new ImageView(icon);
            error.setFitWidth(16);
            error.setFitHeight(16);
            customTextField.setRight(error);
            Popup popup = new Popup();
            popup.setAutoHide(true);
            Label label = new Label(exceptionMessage);
            label.setStyle("-fx-text-fill: red;");
            popup.getContent().add(label);
            Stage primaryStage = (Stage) customTextField.getScene().getWindow();
            popup.show(primaryStage);
        } else {
            customTextField.setRight(null);
        }
    }
}
