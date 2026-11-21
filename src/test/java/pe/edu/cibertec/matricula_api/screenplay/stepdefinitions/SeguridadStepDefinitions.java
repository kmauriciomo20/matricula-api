package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Y;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElCodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class SeguridadStepDefinitions {

    @Dado("que el usuario {string} tiene una sesión con la clave {string}")
    public void iniciarSesion(String usuario, String clave) {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(IniciarSesion.con(usuario, clave));
        actor.should(seeThatResponse(res -> res.statusCode(200)
                .body("accessToken", not(isEmptyOrNullString()))
                .body("refreshToken", not(isEmptyOrNullString()))));
    }

    @Cuando("solicita tokens nuevos con su refresh token")
    public void renovar() {
        Actor actor = theActorInTheSpotlight();
        String accessToken = actor.recall("accessToken");
        String refreshToken = actor.recall("refreshToken");
        actor.remember("accessTokenOriginal", accessToken);
        actor.remember("refreshTokenOriginal", refreshToken);
        actor.attemptsTo(RenovarToken.conElTokenGuardadoEn("refreshToken"));
    }

    @Y("recibe un access token y un refresh token distintos a los originales")
    public void verificarRotacion() {
        Actor actor = theActorInTheSpotlight();
        actor.should(
                seeThat("el access token tiene un valor nuevo",
                        quien -> tokenCambio(quien, "accessToken", "accessTokenOriginal"), is(true)),
                seeThat("el refresh token tiene un valor nuevo",
                        quien -> tokenCambio(quien, "refreshToken", "refreshTokenOriginal"), is(true)));
    }

    private boolean tokenCambio(Actor actor, String claveActual, String claveOriginal) {
        String actual = actor.recall(claveActual);
        String original = actor.recall(claveOriginal);
        return actual != null && !actual.isBlank() && !actual.equals(original);
    }

    @Cuando("vuelve a usar el refresh token original")
    public void reutilizarRefresh() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.conElTokenGuardadoEn("refreshTokenOriginal"));
    }

    @Cuando("cierra su sesión")
    public void cerrarSesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.actual());
    }

    @Cuando("consulta su perfil con el access token guardado")
    public void consultarPerfil() {
        consultarConBearer("/api/auth/perfil");
    }

    @Cuando("solicita el listado de alumnos")
    public void listarAlumnos() {
        consultarConBearer("/api/alumnos");
    }

    private void consultarConBearer(String ruta) {
        Actor actor = theActorInTheSpotlight();
        String accessToken = actor.recall("accessToken");
        actor.attemptsTo(Get.resource(ruta)
                .with(req -> req.header("Authorization", "Bearer " + accessToken)));
    }

    @Y("el código de seguridad es {string}")
    public void verificarCodigo(String codigo) {
        theActorInTheSpotlight().should(seeThat(ElCodigoDeError.deLaRespuesta(), equalTo(codigo)));
    }

    @Cuando("realiza estos intentos de acceso para {string}")
    public void verificarIntentos(String usuario, DataTable tabla) {
        Actor actor = theActorInTheSpotlight();
        for (Map<String, String> fila : tabla.asMaps()) {
            String intento = fila.get("intento");
            int http = Integer.parseInt(fila.get("http"));
            String codigo = fila.get("codigo");

            actor.attemptsTo(IniciarSesion.con(usuario, fila.get("clave")));
            actor.should(
                    seeThatResponse("intento " + intento + ": HTTP " + http + " y esquema de error",
                            res -> res.statusCode(http)
                                    .body(matchesJsonSchemaInClasspath("schemas/error.json"))),
                    seeThat("código del intento " + intento, ElCodigoDeError.deLaRespuesta(), equalTo(codigo)));
        }
    }
}
