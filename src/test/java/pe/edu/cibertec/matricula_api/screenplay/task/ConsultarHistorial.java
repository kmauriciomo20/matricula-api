package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ConsultarHistorial implements Task {

    private final String codigoAlumno;

    public ConsultarHistorial(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public static ConsultarHistorial de(String codigoAlumno) {
        return instrumented(ConsultarHistorial.class, codigoAlumno);
    }

    @Override
    @Step("{0} consulta el historial de #codigoAlumno")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        actor.attemptsTo(Get.resource("/api/alumnos/{codigo}/historial")
                .with(req -> req.header("Authorization", "Bearer " + token)
                        .pathParam("codigo", codigoAlumno)));
    }
}
