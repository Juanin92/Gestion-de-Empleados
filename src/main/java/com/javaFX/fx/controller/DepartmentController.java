package com.javaFX.fx.controller;

import com.javaFX.fx.Entity.DTO.RegisterDTO;
import com.javaFX.fx.Entity.Departamentos;
import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.FxApplication;
import com.javaFX.fx.service.interfaces.IRegisterService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DepartmentController implements Initializable {

    @FXML private StackPane stackPane;
    @FXML private Button btnBack, btnDptoCreation;
    @FXML private ComboBox<Departamentos> cbDepto;
    @FXML private CheckBox cxbPassShow;
    @FXML private Label lblWarning;
    @FXML private RadioButton rbMatriz, rbNorte, rbSur;
    @FXML private ToggleGroup sucursalGroup;
    @FXML private PasswordField txtPass, txtPassConfirm;
    @FXML private TextField txtPassConfirmVisible, txtPassVisible, txtUsername;

    @Setter private Employee employee;
    @Autowired private IRegisterService registerService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbDepto.getItems().setAll(Departamentos.values());
        sucursalGroup = new ToggleGroup();
        rbMatriz.setToggleGroup(sucursalGroup);
        rbNorte.setToggleGroup(sucursalGroup);
        rbSur.setToggleGroup(sucursalGroup);
        passwordVisibility();
        Platform.runLater(this::usernameCreation);
    }

    @FXML
    public void creationRegister(Employee employee){
        try{
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setDepartamento(cbDepto.getValue());

            RadioButton selectedRadioButton = (RadioButton) sucursalGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                registerDTO.setSucursal(selectedRadioButton.getText());
            } else {
                lblWarning.setVisible(true);
                lblWarning.setText("Debe seleccionar una sucursal.");
            }

            registerDTO.setUsername(txtUsername.getText());
            registerDTO.setPassword(cxbPassShow.isSelected() ? txtPass.getText() : txtPassVisible.getText());
            registerDTO.setPasswordConfirm(cxbPassShow.isSelected() ? txtPassConfirm.getText() : txtPassConfirmVisible.getText());
            registerService.create(registerDTO,employee);

            Notifications.create()
                    .title("Departamento agregado Exitoso")
                    .text("Departamento agregado a " + employee.getName() + " " + employee.getSurname() + " fue exitosa")
                    .darkStyle()
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(7))
                    .showInformation();

        }catch (IllegalArgumentException e){
            lblWarning.setVisible(true);
            lblWarning.setText(e.getMessage());

            Notifications.create()
                    .title("Error!")
                    .text(" " + e.getMessage())
                    .text("Ha habido un problema con el proceso, intente de nuevo")
                    .darkStyle()
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(7))
                    .showInformation();
        }
    }

    @SuppressWarnings("resource")
    @FXML
    private void homeView(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnBack)){
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
        } else if (event.getSource().equals(btnDptoCreation)) {
            creationRegister(employee);
        }

    }

    private void passwordVisibility(){
        cxbPassShow.selectedProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue){
                txtPassVisible.setVisible(true);
                txtPassVisible.setManaged(true);
                txtPassVisible.setText(txtPass.getText());
                txtPass.setVisible(false);
                txtPass.setManaged(false);

                txtPassConfirmVisible.setVisible(true);
                txtPassConfirmVisible.setManaged(true);
                txtPassConfirmVisible.setText(txtPassConfirm.getText());
                txtPassConfirm.setVisible(false);
                txtPassConfirm.setManaged(false);
            }else {
                txtPass.setVisible(true);
                txtPass.setManaged(true);
                txtPassVisible.setVisible(false);
                txtPassVisible.setManaged(false);
                txtPass.setText(txtPassVisible.getText());

                txtPassConfirm.setVisible(true);
                txtPassConfirm.setManaged(true);
                txtPassConfirmVisible.setVisible(false);
                txtPassConfirmVisible.setManaged(false);
                txtPassConfirm.setText(txtPassConfirmVisible.getText());
            }
        });
    }

    private void usernameCreation(){
        String name = employee.getName();
        String surname = employee.getSurname();
        String username;

        if (name.length() < 3 || surname.length() < 3){
            username = name + surname;
            txtUsername.setText(username);
            System.out.println(username);
        } else {
            username = name.substring(0,3) + surname.substring(0,3);
            txtUsername.setText(username);
            System.out.println(username);
        }
    }
}
