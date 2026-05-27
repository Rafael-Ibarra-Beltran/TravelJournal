# Travel Journal

Travel Journal es una aplicación Android para registrar viajes, lugares visitados y experiencias personales. La información se guarda localmente en el dispositivo mediante SQLite, por lo que la aplicación funciona sin conexión a internet y no depende de servicios externos.

## Caracteristicas

- Registro de viajes con lugar, fecha, descripción, categoría, acompañantes, gasto, calificación e imagen.
- Listado de viajes guardados con resumen visual.
- Búsqueda por nombre de lugar.
- Marcado de viajes favoritos con badge visual.
- Filtro por categoría.
- Filtro por calificación mínima.
- Filtro para mostrar solo favoritos.
- Ordenamiento por actualización, lugar, calificación o fecha.
- Panel de estadísticas desplegable con total, promedio, favoritos, gasto total, mejor viaje, último viaje y categoría más usada.
- Galería simple de hasta 3 fotos extra por viaje.
- Badges con colores diferentes según la categoría.
- Vista de detalle para consultar y editar viajes existentes.
- Eliminación de viajes con confirmación.
- Selección de imagen principal y fotos extra desde la galería del dispositivo.
- Compartir experiencias desde el ícono de cada tarjeta en la pantalla principal mediante el selector de aplicaciones de Android.
- Ícono propio de la app usando el logo de Travel Journal.
- Conservación del estado del formulario y filtros al rotar el dispositivo.
- Interfaz adaptable para teléfono, tableta, orientación vertical y orientación horizontal.

## Tecnologías Utilizadas

- Java
- Android Studio
- Android SDK
- Gradle
- SQLite
- AndroidX AppCompat
- RecyclerView
- Material Components
- ConstraintLayout

## Requisitos Tecnicos

- Lenguaje principal: Java.
- Plataforma: Android.
- Persistencia local: SQLite.
- Actividades principales: `MainActivity` y `DetailActivity`.
- Función del dispositivo: selección de imagen desde galería.
- Comunicación externa: intent de compartir de Android con texto e imágenes del viaje.
- Compatibilidad visual: teléfono, tableta, vertical y horizontal.

## Estructura del Proyecto

```text
TravelJournal/
├── app/
│   ├── src/main/java/com/example/traveljournal/
│   │   ├── MainActivity.java
│   │   ├── DetailActivity.java
│   │   ├── data/TripDatabaseHelper.java
│   │   ├── model/Trip.java
│   │   ├── ui/TripAdapter.java
│   │   └── util/
│   ├── src/main/res/layout/
│   ├── src/main/res/layout-land/
│   ├── src/main/res/layout-sw600dp/
│   └── src/main/AndroidManifest.xml
├── gradle/
├── build.gradle
├── settings.gradle
└── gradlew
```

## Requisitos de Desarrollo

- Android Studio instalado.
- JDK compatible con Gradle y Android Gradle Plugin.
- SDK de Android con API de compilacion configurada.
- Emulador o dispositivo Android fisico.

## Cómo Ejecutar el Proyecto

1. Abre Android Studio.
2. Selecciona `Open` y elige la carpeta raiz del proyecto.
3. Espera a que Gradle sincronice las dependencias.
4. Selecciona un emulador o dispositivo fisico.
5. Ejecuta la aplicación con `Run`.

## Comandos Útiles

Compilar versión de depuración:

```bash
./gradlew assembleDebug
```

Ejecutar pruebas unitarias:

```bash
./gradlew test
```

Ejecutar verificación general del proyecto:

```bash
./gradlew build
```

## Privacidad y Almacenamiento

Travel Journal guarda los viajes en una base de datos SQLite local llamada `travel_journal.db`. La aplicación no usa inicio de sesión, sincronización en la nube, mapas, redes sociales ni APIs externas.

Las imágenes seleccionadas se referencian mediante URI del sistema de Android. Si el proveedor de documentos lo permite, la aplicación conserva permiso persistente de lectura para volver a mostrar la imagen. Al compartir, la app concede permiso temporal de lectura para que otras aplicaciones puedan recibir la imagen principal y las fotos extra.

## Estado del Proyecto

Versión funcional con operaciones CRUD, favoritos, búsqueda, filtros, estadísticas, persistencia local, selección de imagen, galería extra, compartir por intent desde la pantalla principal, logo propio y diseños responsivos para diferentes tamaños y orientaciones de pantalla.

## Licencia

Este proyecto está distribuido bajo la Licencia MIT. Consulta el archivo `LICENSE` para más información.
