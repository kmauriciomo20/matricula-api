# language: es
@p3
Característica: Calificación de matrículas

  Antecedentes:
    Dado que Rosa consulta la API de matricula
    Y la alumna "i202610005" inicia sesión para consultar su matrícula con la clave "Cibertec2026"

  Esquema del escenario: Actualizar el historial con la nota <nota> de Proyecto Integrador
    Dado que la alumna obtiene su matrícula de "PI5620" en el periodo "2026-2"
    Y que Docente consulta la API de matricula
    Y el docente "D0001" inicia sesión para calificar con la clave "Cibertec2026"
    Cuando el docente registra la nota <nota>
    Entonces el codigo de respuesta es 200
    Cuando la alumna consulta su historial
    Entonces el codigo de respuesta es 200
    Y la respuesta cumple el esquema "historial.json"
    Y en el historial de "PI5620" del periodo "2026-2" figura la nota <nota> con condición "<condicion>"
    Y el historial muestra <creditos> créditos aprobados y promedio ponderado <promedio>

    Ejemplos:
      | nota | condicion    | creditos | promedio |
      | 12   | DESAPROBADO  | 7        | 13,85    |
      | 12,5 | DESAPROBADO  | 7        | 14,08    |

  Escenario: Un docente ajeno al curso intenta registrar la nota
    Dado que la alumna obtiene su matrícula de "PI5620" en el periodo "2026-2"
    Y que Docente consulta la API de matricula
    Y el docente "D0002" inicia sesión para calificar con la clave "Cibertec2026"
    Cuando el docente registra la nota 12
    Entonces el codigo de respuesta es 403
    Y el codigo de error es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: El docente intenta calificar una matrícula sin pagar
    Dado que la alumna obtiene su matrícula de "PS5511" en el periodo "2026-2"
    Y que Docente consulta la API de matricula
    Y el docente "D0001" inicia sesión para calificar con la clave "Cibertec2026"
    Cuando el docente registra la nota 12
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "MATRICULA_NO_PAGADA"
    Y la respuesta cumple el esquema "error.json"
