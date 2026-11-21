package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {
    @Before
    public void beforeScenario() {
        OnStage.setTheStage(new OnlineCast());

        String fechaHora = ZonedDateTime.now(ZoneId.of("America/Lima"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        System.out.println("Estudiante: Karla Mauricio Morrillo");
        System.out.println("Código Cibertec: i202503042");
        System.out.println("Fecha y hora: " + fechaHora);
    }
}
