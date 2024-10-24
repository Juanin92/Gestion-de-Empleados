package com.javaFX.fx.controller;
import com.javaFX.fx.Entity.DTO.DataDto;
import com.javaFX.fx.Entity.Departamentos;
import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.Entity.Register;

import com.javaFX.fx.service.interfaces.IEmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class ListViewController implements Initializable {

    @FXML private TableColumn<DataDto, String> colBranch;
    @FXML private TableColumn<DataDto, Departamentos> colDepto;
    @FXML private TableColumn<DataDto, LocalDate> colEntry;
    @FXML private TableColumn<DataDto, Integer> colIncome;
    @FXML private TableColumn<DataDto, String> colName;
    @FXML private TableColumn<DataDto, LocalDate> colExit;
    @FXML private TableColumn<DataDto, String> colSurname;
    @FXML private TableColumn<DataDto, String> colTime;
    @FXML private TableView<DataDto> tvListComplete;

    private ObservableList<DataDto> list;
    @Setter private Employee employee;
    @Autowired private IEmployeeService employeeService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url.toString().contains("listView.fxml")){
            listComplete();
            //Asignar un CSS a la tableview
            URL stylesheet = getClass().getResource("/com/javaFX/fx/CSS/style.css");
            tvListComplete.getStylesheets().add(stylesheet.toExternalForm());
        }
    }

    @FXML
    public void listComplete(){
        List<DataDto> dataDtos = transferDataToDto();
        list = FXCollections.observableArrayList(dataDtos);
        tvListComplete.setItems(list);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        colDepto.setCellValueFactory(new PropertyValueFactory<>("depto"));
        colIncome.setCellValueFactory(new PropertyValueFactory<>("income"));
        colEntry.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        colExit.setCellValueFactory(new PropertyValueFactory<>("exitDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("calculateTime"));

        configurationColumnTable();
    }

    public List<DataDto> transferDataToDto(){
        List<Employee> employeeList = employeeService.getAll();
        List<DataDto> data = new ArrayList<>();
        for (Employee employeePrincipal : employeeList){
            for (Register registerPrincipal : employeePrincipal.getRegisterList()){
                DataDto dto = new DataDto();
                dto.setIdEmployee(employeePrincipal.getId());
                dto.setIdRegister(registerPrincipal.getId());
                dto.setName(employeePrincipal.getName());
                dto.setSurname(employeePrincipal.getSurname());
                dto.setStatus(employeePrincipal.isStatus());
                dto.setBranch(registerPrincipal.getSucursal());
                dto.setDepto(registerPrincipal.getDepartamento());
                dto.setIncome(employeePrincipal.getIncome());
                dto.setEntryDate(employeePrincipal.getEntryDate());
                dto.setExitDate(employeePrincipal.getExitDate());
                dto.setCalculateTime(calculateTime(employeePrincipal.getEntryDate(),employeePrincipal.getExitDate()));
                data.add(dto);
            }
        }
        return data;
    }

    private String calculateTime(LocalDate entryDate, LocalDate exitDate){
        LocalDate now = LocalDate.now();
        Period period = entryDate.until(exitDate != null ? exitDate:now);
        String labelTime = String.format("%d años %d meses %d Dias", period.getYears(),period.getMonths(),period.getDays());

        return labelTime;
    }

    private void configurationColumnTable(){
        // Establece que las columnas se redimensionen de forma proporcional al tamaño de la tabla.
        tvListComplete.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tvListComplete.setRowFactory(tv -> new TableRow<>(){
            @Override
            protected void updateItem(DataDto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.isStatus()) {
                        setStyle("");
                    } else {
                        setTextFill(Color.WHITE);
                        setStyle("-fx-background-color: red;");
                    }
                }
            }
        });

        colTime.setCellFactory(column -> new TableCell<>(){

            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    DataDto data = getTableView().getItems().get(getIndex());
                    if (data.isStatus()) {
                        setTextFill(Color.WHITE);
                        setStyle("-fx-background-color: green");
                    }
                }
            }
        });

        colIncome.setCellFactory(column -> new TableCell<DataDto,Integer>(){

            @Override
            protected void updateItem(Integer item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
                    setText(currencyFormat.format(item));
                }
            }
        });
    }
}
