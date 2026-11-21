package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import net.serenitybdd.screenplay.rest.interactions.Post;
import pe.edu.cibertec.matricula_api.screenplay.task.Matricularse;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class MatriculaStepDefinitions {

    @Y("el alumno {string} inicia sesión con la clave {string}")
    public void iniciarSesion(String alumno, String clave) {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(Post.to("/api/auth/login")
                .with(req -> req.contentType(ContentType.JSON)
                        .body(Map.of("username", alumno, "password", clave))));

        actor.should(seeThatResponse(res -> res.statusCode(200)
                .body("accessToken", not(isEmptyOrNullString()))));
        actor.remember("alumno", alumno);
        actor.remember("token", SerenityRest.lastResponse().jsonPath().getString("accessToken"));
    }

    @Cuando("cotiza el curso {string}")
    public void cotizar(String curso) {
        Actor actor = theActorInTheSpotlight();
        String alumno = actor.recall("alumno");
        String token = actor.recall("token");

        actor.attemptsTo(Post.to("/api/matriculas/cotizacion")
                .with(req -> req.header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
                        .body(Map.of("codigoAlumno", alumno, "cursos", List.of(curso)))));

        if (SerenityRest.lastResponse().statusCode() == 200) {
            actor.remember("totalCotizado", SerenityRest.lastResponse().jsonPath().getDouble("total"));
        }
    }

    @Entonces("el total de la cotización es {double}")
    public void verificarTotal(double esperado) {
        Actor actor = theActorInTheSpotlight();
        Double total = actor.recall("totalCotizado");
        actor.should(seeThat(
                "el total cotizado", ignored -> total, equalTo(esperado)));
    }

    @Cuando("se matricula en el curso {string}")
    public void matricular(String curso) {
        Actor actor = theActorInTheSpotlight();
        String alumno = actor.recall("alumno");
        actor.attemptsTo(Matricularse.en(alumno, curso));
    }

    @Y("la matrícula queda {string}")
    public void verificarEstado(String estado) {
        theActorInTheSpotlight().should(
                seeThatResponse(res -> res.body("estado", equalTo(estado))));
    }

    @Y("el monto de la matrícula coincide con la cotización")
    public void verificarMontoMatricula() {
        Actor actor = theActorInTheSpotlight();
        Double total = actor.recall("totalCotizado");
        actor.should(seeThat(
                "el monto registrado",
                ignored -> SerenityRest.lastResponse().jsonPath().getDouble("monto"),
                equalTo(total)));
    }

    @Cuando("paga la matrícula con el método {string}")
    public void pagar(String metodo) {
        Actor actor = theActorInTheSpotlight();
        long id = SerenityRest.lastResponse().jsonPath().getLong("id");
        actor.remember("idMatricula", id);
        String token = actor.recall("token");

        actor.attemptsTo(Patch.to("/api/matriculas/{id}/pagar")
                .with(req -> req.header("Authorization", "Bearer " + token)
                        .pathParam("id", id)
                        .contentType(ContentType.JSON)
                        .body(Map.of("metodo", metodo))));
    }

    @Y("la fecha de pago tiene valor")
    public void verificarFechaPago() {
        theActorInTheSpotlight().should(
                seeThatResponse(res -> res.body("fechaPago", not(isEmptyOrNullString()))));
    }

    @Cuando("consulta sus pagos")
    public void consultarPagos() {
        Actor actor = theActorInTheSpotlight();
        String token = actor.recall("token");
        actor.attemptsTo(Get.resource("/api/pagos").with(req -> req.header("Authorization", "Bearer " + token)));
    }

    @Y("su matrícula tiene un pago con método {string} y operación con formato {string}")
    public void verificarPago(String metodo, String formato) {
        Actor actor = theActorInTheSpotlight();
        long id = actor.recall("idMatricula");
        String pago = "find { it.idMatricula == " + id + " }";
        actor.should(seeThatResponse(res -> res
                .body(pago, notNullValue())
                .body(pago + ".metodo", equalTo(metodo))
                .body(pago + ".numeroOperacion", matchesPattern(formato))));
    }
}
