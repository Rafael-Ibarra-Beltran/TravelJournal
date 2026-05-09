# Manual de Usuario - Travel Journal

Travel Journal permite guardar viajes y lugares visitados en el dispositivo. La app funciona sin internet y conserva los datos en una base de datos SQLite local.

## Abrir la aplicación

1. Abre Travel Journal desde el lanzador de Android.
2. La pantalla principal muestra la lista de viajes guardados.
3. Si no hay viajes, aparece el mensaje "Aún no hay viajes guardados".

## Registrar un viaje

1. Pulsa "Nuevo viaje".
2. Escribe el nombre del lugar.
3. Pulsa el campo de fecha y selecciona la fecha del viaje.
4. Escribe una descripción de la experiencia.
5. Selecciona una calificación entre 1 y 5 estrellas.
6. Opcionalmente pulsa "Seleccionar imagen" para elegir una foto desde la galería.
7. Pulsa "Guardar".
8. La app muestra una confirmación y regresa a la lista principal.

## Consultar un viaje

1. En la pantalla principal, pulsa la tarjeta del viaje.
2. La pantalla de edición muestra el nombre, fecha, descripción, calificación e imagen guardada.
3. Para regresar sin cambios, usa el botón de volver del dispositivo.

## Editar un viaje

1. Abre un viaje desde la lista principal.
2. Modifica los campos que necesites.
3. Pulsa "Guardar".
4. La app muestra una confirmación y actualiza la lista principal.

## Eliminar un viaje

1. Abre un viaje existente.
2. Pulsa "Eliminar".
3. Confirma la eliminación en el diálogo.
4. La app borra el registro y regresa a la lista principal.

## Compartir una experiencia

1. Abre un viaje existente.
2. Pulsa "Compartir".
3. Android muestra las aplicaciones compatibles instaladas.
4. Elige una app como correo, mensajes o WhatsApp.
5. La app comparte un texto con lugar, fecha, calificación y descripción.

## Uso en teléfono y tableta

En teléfono, la app usa navegación normal: lista principal y pantalla de formulario.

En tableta, la pantalla principal aprovecha el espacio con una distribución lista-detalle: la lista aparece a la izquierda y el detalle del viaje seleccionado a la derecha.

## Orientación vertical y horizontal

La app funciona en vertical y horizontal. Si giras el dispositivo mientras escribes un viaje, se conservan temporalmente el nombre, fecha, descripción, calificación e imagen seleccionada.

## Validaciones

Antes de guardar, la app exige:

- Nombre del lugar.
- Fecha del viaje.
- Descripción.
- Calificación entre 1 y 5.

Si falta información, se muestra un mensaje claro y el viaje no se guarda.
