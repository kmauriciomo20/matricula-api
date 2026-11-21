# language: es
@p2
Característica: Seguridad de la sesión

  Antecedentes:
    Dado que Alumno consulta la API de matricula

  Escenario: Rotación del refresh token
    Dado que el usuario "i202610002" tiene una sesión con la clave "Cibertec2026"
    Cuando solicita tokens nuevos con su refresh token
    Entonces el codigo de respuesta es 200
    Y recibe un access token y un refresh token distintos a los originales
    Cuando vuelve a usar el refresh token original
    Entonces el codigo de respuesta es 401
    Y el código de seguridad es "REFRESH_REUTILIZADO"
    Y la respuesta cumple el esquema "error.json"
    Cuando consulta su perfil con el access token guardado
    Entonces el codigo de respuesta es 401
    Y el código de seguridad es "TOKEN_INVALIDO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: Cierre de sesión
    Dado que el usuario "i202610002" tiene una sesión con la clave "Cibertec2026"
    Cuando cierra su sesión
    Entonces el codigo de respuesta es 204
    Cuando consulta su perfil con el access token guardado
    Entonces el codigo de respuesta es 401
    Y el código de seguridad es "TOKEN_INVALIDO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: Bloqueo de cuenta por intentos fallidos
    Cuando realiza estos intentos de acceso para "i202610004"
      | intento | clave        | http | codigo                  |
      | 1       | Incorrecta1  | 401  | CREDENCIALES_INVALIDAS  |
      | 2       | Incorrecta1  | 401  | CREDENCIALES_INVALIDAS  |
      | 3       | Incorrecta1  | 401  | CREDENCIALES_INVALIDAS  |
      | 4       | Incorrecta1  | 401  | CREDENCIALES_INVALIDAS  |
      | 5       | Incorrecta1  | 423  | CUENTA_BLOQUEADA        |
      | 6       | Cibertec2026 | 423  | CUENTA_BLOQUEADA        |

  Escenario: Alumno sin permiso de administrador
    Dado que el usuario "i202610002" tiene una sesión con la clave "Cibertec2026"
    Cuando solicita el listado de alumnos
    Entonces el codigo de respuesta es 403
    Y el código de seguridad es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"
