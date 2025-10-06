# language: es
Característica: Login - Store

  Esquema del escenario: Validación del inicio de sesión
    Dado estoy en la página de la tienda
    Cuando me logueo con mi usuario "<usuario>" y clave "<password>"
    Entonces valido que el login sea "<resultado>"

    Ejemplos:
      | usuario                   | password     | resultado |
      | valia.tataje@gmail.com    | Analista2025 | exitoso   |
      | usuario_invalido@test.com | clave123     | fallido   |