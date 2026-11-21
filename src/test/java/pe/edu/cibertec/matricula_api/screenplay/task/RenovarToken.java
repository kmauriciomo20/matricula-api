package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RenovarToken implements Task {

    private final String claveEnMemoria;

    public RenovarToken(String claveEnMemoria) {
        this.claveEnMemoria = claveEnMemoria;
    }

    public static RenovarToken conElTokenGuardadoEn(String claveEnMemoria) {
        return instrumented(RenovarToken.class, claveEnMemoria);
    }

    @Override
    @Step("{0} solicita la renovación de sus tokens")
    public <T extends Actor> void performAs(T actor) {
        String refreshToken = actor.recall(claveEnMemoria);
        actor.attemptsTo(Post.to("/api/auth/refresh")
                .with(req -> req.contentType(ContentType.JSON)
                        .body(Map.of("refreshToken", refreshToken))));

        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 200) {
            actor.remember("accessToken", respuesta.jsonPath().getString("accessToken"));
            actor.remember("refreshToken", respuesta.jsonPath().getString("refreshToken"));
        }
    }
}
