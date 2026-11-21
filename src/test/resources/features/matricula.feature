# language: es
@p1
Característica: Matrícula del alumno

  Antecedentes:
    Dado que Luis consulta la API de matricula
    Y el alumno "i202610002" inicia sesión con la clave "Cibertec2026"

  Escenario: Cotizar Desarrollo de Aplicaciones Móviles
    Cuando cotiza el curso "DM5512"
    Entonces el codigo de respuesta es 200
    Y el total de la cotización es 432

  Escenario: Matricularse y pagar Desarrollo de Aplicaciones Móviles
    Cuando cotiza el curso "DM5512"
    Entonces el codigo de respuesta es 200
    Cuando se matricula en el curso "DM5512"
    Entonces el codigo de respuesta es 201
    Y la matrícula queda "REGISTRADA"
    Y el monto de la matrícula coincide con la cotización
    Y la respuesta cumple el esquema "matricula.json"
    Cuando paga la matrícula con el método "TRANSFERENCIA"
    Entonces el codigo de respuesta es 200
    Y la matrícula queda "PAGADA"
    Y la fecha de pago tiene valor
    Cuando consulta sus pagos
    Entonces el codigo de respuesta es 200
    Y su matrícula tiene un pago con método "TRANSFERENCIA" y operación con formato "OP-[0-9]{6}"

  Escenario: Rechazar la matrícula duplicada en Pruebas de Software
    Cuando se matricula en el curso "PS5511"
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "MATRICULA_DUPLICADA"
    Y la respuesta cumple el esquema "error.json"
