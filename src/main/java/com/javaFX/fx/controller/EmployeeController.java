package com.javaFX.fx.controller;

import com.javaFX.fx.Entity.Departamentos;
import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.Entity.Register;
import com.javaFX.fx.security.TokenSecurity;
import com.javaFX.fx.service.interfaces.IEmployeeService;
import com.javaFX.fx.service.interfaces.IRegisterService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class EmployeeController implements Initializable {

    @FXML private TextField txtId, txtName, txtSurname, txtEmail, txtUsername, txtIncome, txtPassShow;
    @FXML private PasswordField txtPass;
    @FXML private ComboBox<Departamentos> cxDepto;
    @FXML private RadioButton rbMatriz,rbNorte, rbSur;
    @FXML private Button btnAccept, btnCancel, btnActiveEmployee, btnChangePass;
    @FXML private Label lblWarning, lblNameEmployee;
    @FXML private HBox hbRadio;
    @FXML private CheckBox cxbPass;

    @Setter private Employee currentEmployee;
    private Register currentRegister;
    @Autowired private IEmployeeService employeeService;
    @Autowired private IRegisterService registerService;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private MainController mainController;
    @Autowired private TokenSecurity tokenSecurity;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordVisibility();
        Platform.runLater(this::employeeAndRegisterData);
    }

    private void employeeAndRegisterData() {
        if (currentEmployee.isStatus()){
            lblNameEmployee.setText(currentEmployee.getName() + " " + currentEmployee.getSurname());
            txtId.setText(String.valueOf(currentEmployee.getId()));
            txtName.setText(currentEmployee.getName());
            txtSurname.setText(currentEmployee.getSurname());
            txtEmail.setText(currentEmployee.getEmail());
            txtIncome.setText(String.valueOf(currentEmployee.getIncome()));

            List<Register> registers = registerService.getEmployeeId(currentEmployee.getId());
            if (!registers.isEmpty()) {
                Register register = registers.getFirst();
                this.currentRegister = register;
                txtUsername.setText(register.getUsername());
                txtPass.setVisible(false);
                cxbPass.setVisible(false);
                txtPass.setText(null);
                cxDepto.getItems().setAll(Departamentos.values());
                cxDepto.setValue(register.getDepartamento());

                switch (register.getSucursal()) {
                    case "Casa Matriz":
                        rbMatriz.setSelected(true);
                        break;
                    case "Casa Norte":
                        rbNorte.setSelected(true);
                        break;
                    case "Casa Sur":
                        rbSur.setSelected(true);
                        break;
                }
            }else {
                lblWarning.setVisible(true);
                lblWarning.setText("Debe agregar un Departamento");
                txtUsername.setDisable(true);
                txtPass.setDisable(true);
                cxDepto.setDisable(true);
                hbRadio.setDisable(true);
                cxbPass.setDisable(true);
            }
        }else {
            btnActiveEmployee.setVisible(true);
            lblWarning.setVisible(true);
            lblWarning.setText("Si desea ingresar nuevamente el empleado al sistema.");
            txtId.setDisable(true);
            txtName.setDisable(true);
            txtSurname.setDisable(true);
            txtEmail.setDisable(true);
            txtIncome.setDisable(true);
            txtUsername.setDisable(true);
            txtPass.setDisable(true);
            cxDepto.setDisable(true);
            hbRadio.setDisable(true);
            cxbPass.setDisable(true);
            btnAccept.setDisable(true);
        }
    }

    @FXML
    public void switchEditView(ActionEvent event){
        if (event.getSource().equals(btnActiveEmployee)){
            this.currentEmployee.setStatus(true);
            txtId.setText(String.valueOf(currentEmployee.getId()));
            txtName.setText(currentEmployee.getName());
            txtSurname.setText(currentEmployee.getSurname());
            txtEmail.setText(currentEmployee.getEmail());
            txtIncome.setText(String.valueOf(currentEmployee.getIncome()));
            currentEmployee.setEntryDate(LocalDate.now());
            currentEmployee.setExitDate(null);
            employeeService.updateEmployee(currentEmployee);

            txtId.setDisable(false);
            txtName.setDisable(false);
            txtSurname.setDisable(false);
            txtEmail.setDisable(false);
            txtIncome.setDisable(false);

            btnAccept.setDisable(false);
            mainController.listEmployee();
        } else if (event.getSource().equals(btnCancel)) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else if (event.getSource().equals(btnAccept)) {
            editEmployee(currentEmployee);
        } else if (event.getSource().equals(btnChangePass)) {
            if (tokenSecurity.recoverPassword()){
                btnChangePass.setVisible(false);
                txtPass.setVisible(true);
                cxbPass.setVisible(true);
            }else {
                btnChangePass.setDisable(true);
            }
        }
    }

    @FXML
    public void editEmployee(Employee employee){
        employee.setName(txtName.getText());
        employee.setSurname(txtSurname.getText());
        employee.setEmail(txtEmail.getText());
        employee.setIncome(Integer.parseInt(txtIncome.getText()));
        employeeService.updateEmployee(employee);

        currentRegister.setDepartamento(cxDepto.getValue());
        currentRegister.setUsername(txtUsername.getText());
        currentRegister.setPassword(cxbPass.isSelected() ? txtPassShow.getText() : txtPass.getText());
        if (rbMatriz.isSelected()) {
            currentRegister.setSucursal(rbMatriz.getText());
        } else if (rbNorte.isSelected()) {
            currentRegister.setSucursal(rbNorte.getText());
        } else if (rbSur.isSelected()) {
            currentRegister.setSucursal(rbSur.getText());
        }
        registerService.editRegister(currentRegister);
        mainController.listEmployee();

        Notifications.create()
                .title("Actualización Exitosa")
                .text("los datos del empleado ha sido actualizados")
                .darkStyle()
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(7))
                .showInformation();
    }

    private void passwordVisibility(){
        cxbPass.selectedProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue){
                txtPassShow.setVisible(true);
                txtPassShow.setManaged(true);
                txtPassShow.setText(txtPass.getText());
                txtPass.setVisible(false);
                txtPass.setManaged(false);
            }else {
                txtPass.setVisible(true);
                txtPass.setManaged(true);
                txtPassShow.setVisible(false);
                txtPassShow.setManaged(false);
                txtPass.setText(txtPassShow.getText());
            }
        });
    }
}