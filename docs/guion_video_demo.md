# Guion Para Video Demostrativo

## Objetivo

Mostrar que Travel Journal cumple los requisitos principales del proyecto: dos actividades, SQLite, galería, compartir, teléfono/tableta, orientación y conservación de estado.

## Flujo sugerido

1. Abrir la app y mostrar la lista vacía.
2. Pulsar "Nuevo viaje".
3. Completar un viaje de ejemplo:
   - Lugar: Kioto
   - Fecha: seleccionar con el calendario
   - Descripción: escribir una experiencia breve
   - Calificación: 5 estrellas
4. Seleccionar una imagen desde la galería.
5. Girar el dispositivo antes de guardar y mostrar que los campos se conservan.
6. Pulsar "Guardar".
7. Mostrar que el viaje aparece en la lista principal.
8. Abrir el viaje guardado.
9. Editar la descripción o calificación.
10. Guardar cambios y mostrar la lista actualizada.
11. Abrir el viaje de nuevo y pulsar "Compartir".
12. Mostrar el selector de aplicaciones de Android.
13. Mostrar la confirmación de eliminación sin borrar, o borrar un viaje de prueba si se desea.
14. Cerrar y abrir la app para mostrar que los datos siguen guardados.
15. Ejecutar la app en un emulador/tableta o configuración grande para mostrar la vista lista-detalle.

## Puntos a mencionar

- La app está desarrollada en Java con Android Studio.
- La información se guarda localmente en SQLite.
- La imagen se selecciona usando la galería del dispositivo.
- El compartir se realiza con intents de Android, sin APIs externas.
- La interfaz se adapta a teléfono, tableta, vertical y horizontal.
