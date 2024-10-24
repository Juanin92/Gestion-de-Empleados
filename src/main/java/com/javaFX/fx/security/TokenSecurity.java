package com.javaFX.fx.security;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.controlsfx.control.Notifications;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TokenSecurity {

    private Instant instantGenerationToken;
    private static final Duration TIMELINE_TOKEN = Duration.ofMinutes(2);
    private final AtomicInteger attempts = new AtomicInteger();
    private static final int MAX_ATTEMPT = 2;
    private String token;

    public boolean recoverPassword() {
        token = generateRandomToken();
        instantGenerationToken = Instant.now();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Token de recuperación");
        alert.setHeaderText(null);
        alert.setContentText("Tu token de recuperación es: " + token);
        alert.showAndWait();

        return requestTokenConfirmation();
    }

    public String generateRandomToken() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .filteredBy(CharacterPredicates.DIGITS,CharacterPredicates.LETTERS)
                .build();
        return generator.generate(4);
    }

    public boolean requestTokenConfirmation() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmar token");
        dialog.setHeaderText(null);
        dialog.setContentText("Ingrese el token de recuperación:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String tokenIngresado = result.get();
            if (validateToken(tokenIngresado)) {
                // Token válido, permitir cambiar contraseña
                return true;
            } else {
                // Token inválido, mostrar mensaje de error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Token inválido o expirado.");
                alert.showAndWait();

                return false;
            }
        }
        return false;
    }

    private boolean validateToken(String tokenIngresado) {
        if (!tokenIngresado.equals(token)) {
            int actualAttempt = attempts.incrementAndGet();
            if (actualAttempt >= MAX_ATTEMPT) {
                Notifications.create()
                    .title("Error")
                    .text("Demasiados intentos. Intente de nuevo.")
                    .darkStyle()
                    .position(Pos.TOP_CENTER)
                    .hideAfter(javafx.util.Duration.seconds(4))
                    .showInformation();
                System.out.println("Demasiados intentos. Intente de nuevo.");
                return false;
            }
            return false;
        }
        Instant ahora = Instant.now();
        return Duration.between(instantGenerationToken, ahora).compareTo(TIMELINE_TOKEN) <= 0;
    }
}
