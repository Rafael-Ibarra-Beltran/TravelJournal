# Guía de Contribución

Gracias por contribuir a Travel Journal. Este proyecto busca mantenerse simple, funcional y fácil de revisar.

## Reglas Generales

- Mantener el desarrollo en Java.
- Usar Android Studio como entorno principal.
- No convertir el proyecto a Kotlin.
- No agregar dependencias innecesarias.
- Mantener la persistencia local con SQLite.
- No agregar inicio de sesión, sincronización en la nube, mapas, redes sociales ni APIs externas.
- Preferir cambios pequeños, claros y verificables.
- Conservar compatibilidad con teléfono, tableta, vertical y horizontal.

## Estilo de Codigo

- Usar nombres descriptivos para clases, métodos y variables.
- Mantener la lógica simple dentro de las actividades y clases existentes.
- Evitar abstracciones que no aporten valor inmediato.
- Validar entradas del usuario antes de guardar datos.
- Mantener los textos visibles en archivos de recursos cuando sea práctico.

## Flujo Recomendado

1. Crear una rama para el cambio.
2. Implementar una mejora o correccion concreta.
3. Probar manualmente el flujo afectado en un emulador o dispositivo.
4. Ejecutar la compilacion del proyecto.
5. Revisar los archivos modificados antes de entregar el cambio.

## Verificación

Antes de entregar cambios, ejecutar:

```bash
./gradlew build
```

También es recomendable probar los siguientes flujos manualmente:

- Crear un viaje.
- Editar un viaje existente.
- Eliminar un viaje.
- Seleccionar una imagen.
- Compartir un viaje.
- Rotar el dispositivo mientras se edita un formulario.
- Revisar la interfaz en teléfono y tableta.
