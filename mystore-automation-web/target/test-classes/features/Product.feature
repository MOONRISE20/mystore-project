# language: es
Característica: Product - Store

  Esquema del escenario: Validación del precio de un producto
    Dado estoy en la página de la tienda
    Y me logueo con mi usuario "<usuario>" y clave "<password>"
    Cuando navego a la categoria "<categoria>" y subcategoria "<subcategoria>"
    Y agrego <cantidad> unidades del primer producto al carrito
    Entonces valido en el popup la confirmación del producto agregado
    Y valido en el popup que el monto total sea calculado correctamente
    Cuando finalizo la compra
    Entonces valido el titulo de la pagina del carrito
    Y vuelvo a validar el calculo de precios en el carrito

    Ejemplos:
      | usuario                   | password     | categoria | subcategoria | cantidad |
      | valia.tataje@gmail.com    | Analista2025 | Clothes   | Men          | 2        |
      | usuario_invalido@test.com | clave123     | Clothes   | Men          | 2        |
      | valia.tataje@gmail.com    | Analista2025 | Autos     | Deportivos   | 1        |