package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;
import pe.edu.cibertec.matricula_api.dto.HistorialResponse;

public class HistorialAcademico {

    public static Question<HistorialResponse> deLaRespuesta() {
        return Question.about("el historial académico de la alumna")
                .answeredBy(actor -> SerenityRest.lastResponse().as(HistorialResponse.class));
    }
}
