# Guion Para Video Demostrativo

## Objetivo

Mostrar que Travel Journal cumple los requisitos principales del proyecto: dos actividades, SQLite, galería, compartir, teléfono/tableta, orientación y conservación de estado.

## Flujo sugerido

1. Abrir la app y mostrar la lista vacía.
2. Pulsar "Nuevo viaje".
3. Completar un viaje de ejemplo:
   - Lugar: Kioto
   - Fecha: seleccionar con el calendario
   - Categoría: Cultura
   - Descripción: escribir una experiencia breve
   - Calificación: 5 estrellas
4. Intentar seleccionar una fecha futura y mostrar que no se permite.
5. Seleccionar una imagen desde la galería.
6. Girar el dispositivo antes de guardar y mostrar que los campos se conservan.
7. Pulsar "Guardar".
8. Mostrar que el viaje aparece en la lista principal y que el resumen se actualiza.
9. Abrir el viaje guardado.
10. Editar la descripción o calificación.
11. Guardar cambios y mostrar la lista actualizada.
12. Abrir el viaje de nuevo y pulsar "Compartir".
13. Mostrar el selector de aplicaciones de Android y mencionar que se comparte texto e imagen si existe.
14. Mostrar la confirmación de eliminación sin borrar, o borrar un viaje de prueba si se desea.
15. Cerrar y abrir la app para mostrar que los datos siguen guardados localmente en SQLite.
16. Ejecutar la app en un emulador/tableta o configuración grande para mostrar la vista lista-detalle.

## Puntos a mencionar

- La app está desarrollada en Java con Android Studio.
- La información se guarda localmente en SQLite.
- La imagen se selecciona usando la galería del dispositivo.
- El compartir se realiza con intents de Android, sin APIs externas, y puede adjuntar la imagen seleccionada.
- La interfaz se adapta a teléfono, tableta, vertical y horizontal.
- La fecha del viaje no puede ser futura.
- Los datos son estrictamente locales y se excluyen del backup de Android.
