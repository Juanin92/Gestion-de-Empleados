package com.javaFX.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Optional;

@SpringBootApplication
@ComponentScan(basePackages = "com.javaFX.fx")
public class FxApplication extends Application {

	@Getter
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void init() {
		context = SpringApplication.run(FxApplication.class);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/javaFX/fx/view/Main.fxml"));
		fxmlLoader.setControllerFactory(context::getBean);

		Scene scene = new Scene(fxmlLoader.load());
		Image icon = new Image("com/javaFX/fx/img/iconView.png");
		stage.getIcons().add(icon);
		stage.setTitle("Sistema de Ingreso de Datos");
		stage.setScene(scene);
		// Configurar el tamaño al máximo de la ventana
		stage.setWidth(Screen.getPrimary().getBounds().getWidth());
		stage.setHeight(Screen.getPrimary().getBounds().getHeight());
		// para asignar a pantalla completa
	//		stage.setFullScreen(true);
		stage.show();

		stage.setOnCloseRequest(windowEvent -> {
			windowEvent.consume();

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmación para salir");
			alert.setHeaderText("¿Estas seguro que desea salir?");

			Optional<ButtonType> resultado = alert.showAndWait();
			if (resultado.isPresent() && resultado.get() == ButtonType.OK){
				context.close();
				stage.close();
			}
		});
	}

	@Override
	public void stop() throws Exception {
		context.close();
	}
}
