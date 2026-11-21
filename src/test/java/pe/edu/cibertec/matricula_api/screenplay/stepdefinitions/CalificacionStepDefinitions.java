package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Y;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import pe.edu.cibertec.matricula_api.dto.HistorialResponse;
import pe.edu.cibertec.matricula_api.screenplay.questions.HistorialAcademico;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class CalificacionStepDefinitions {

    private Actor alumna;

    @Y("la alumna {string} inicia sesión para consultar su matrícula con la clave {string}")
    public void iniciarSesionAlumna(String codigo, String clave) {
        alumna = theActorInTheSpotlight();
        iniciarSesion(alumna, codigo, clave);
        alumna.remember("codigoAlumno", codigo);
    }

    @Dado("que la alumna obtiene su matrícula de {string} en el periodo {string}")
    public void obtenerMatricula(String codigoCurso, String periodo) {
        String token = alumna.recall("accessToken");
        alumna.attemptsTo(Get.resource("/api/matriculas")
                .with(req -> req.header("Authorization", "Bearer " + token)
                        .queryParam("curso", codigoCurso)
                        .queryParam("periodo", periodo)));

        alumna.should(seeThatResponse("se encuentra una sola matrícula de ese curso y periodo",
                res -> res.statusCode(200)
                        .body("size()", equalTo(1))
                        .body("[0].codigoCurso", equalTo(codigoCurso))
                        .body("[0].periodo", equalTo(periodo))));

        long id = SerenityRest.lastResponse().jsonPath().getLong("[0].id");
        alumna.remember("idMatricula", id);
    }

    @Y("el docente {string} inicia sesión para calificar con la clave {string}")
    public void iniciarSesionDocente(String codigo, String clave) {
        iniciarSesion(theActorInTheSpotlight(), codigo, clave);
    }

    private void iniciarSesion(Actor actor, String codigo, String clave) {
        actor.attemptsTo(IniciarSesion.con(codigo, clave));
        actor.should(seeThatResponse(res -> res.statusCode(200)
                .body("accessToken", not(isEmptyOrNullString()))));
    }

    @Cuando("el docente registra la nota {double}")
    public void registrarNota(double nota) {
        long id = alumna.recall("idMatricula");
        System.out.println("Nota interpretada por Cucumber: " + nota);
        theActorInTheSpotlight().attemptsTo(CalificarMatricula.conNota(id, nota));
    }

    @Cuando("la alumna consulta su historial")
    public void consultarHistorial() {
        String codigo = alumna.recall("codigoAlumno");
        theActorCalled(alumna.getName()).attemptsTo(ConsultarHistorial.de(codigo));
    }

    @Y("en el historial de {string} del periodo {string} figura la nota {double} con condición {string}")
    public void verificarCurso(String curso, String periodo, double nota, String condicion) {
        HistorialResponse historial = alumna.asksFor(HistorialAcademico.deLaRespuesta());
        alumna.should(seeThat("el curso calificado figura con la nota y condición esperadas",
                actor -> historial.cursos().stream().anyMatch(item ->
                        item.codigoCurso().equals(curso)
                                && item.periodo().equals(periodo)
                                && Double.compare(item.nota(), nota) == 0
                                && item.condicion().equals(condicion)),
                is(true)));
    }

    @Y("el historial muestra {int} créditos aprobados y promedio ponderado {double}")
    public void verificarTotales(int creditos, double promedio) {
        HistorialResponse historial = alumna.asksFor(HistorialAcademico.deLaRespuesta());
        alumna.should(
                seeThat("créditos aprobados", actor -> historial.creditosAprobados(), equalTo(creditos)),
                seeThat("promedio ponderado", actor -> historial.promedioPonderado(), equalTo(promedio)));
    }
}
