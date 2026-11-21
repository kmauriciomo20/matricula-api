package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CalificarMatricula implements Task {

    private final long idMatricula;
    private final double nota;

    public CalificarMatricula(long idMatricula, double nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula conNota(long idMatricula, double nota) {
        return instrumented(CalificarMatricula.class, idMatricula, nota);
    }

    @Override
    @Step("{0} registra la nota #nota")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        actor.attemptsTo(Patch.to("/api/matriculas/{id}/nota")
                .with(req -> req.header("Authorization", "Bearer " + token)
                        .pathParam("id", idMatricula)
                        .contentType(ContentType.JSON)
                        .body(Map.of("nota", nota))));
    }
}
