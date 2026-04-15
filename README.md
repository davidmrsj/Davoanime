# Davoanime

Aplicación Android para explorar anime, consultar detalles, ver episodios y reproducir contenido en streaming, desarrollada con Kotlin y Jetpack Compose.

## Descripción

Davoanime es un proyecto Android centrado en ofrecer una experiencia moderna de consumo de anime desde móvil y Android TV. La app consume una API remota para mostrar últimos episodios, animes en emisión, resultados de búsqueda, detalle de cada serie y reproducción de episodios, incorporando además persistencia local para continuar el visionado.

## Funcionalidades principales

- Inicio con últimos episodios publicados
- Sección de “Seguir viendo” con progreso persistido localmente
- Animes en emisión según el día
- Búsqueda de anime con filtros
- Pantalla de detalle con información, episodios y relacionados
- Reproductor con Media3 / ExoPlayer
- Reanudación automática desde el progreso guardado
- Autoavance al siguiente episodio
- Soporte para Android TV

## Stack tecnológico

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Hilt
- Retrofit
- OkHttp
- Coil
- Media3 / ExoPlayer
- Room
- Coroutines + Flow

## Arquitectura

El proyecto está organizado siguiendo una separación por capas:

- `presentation`: pantallas, navegación, componentes UI y viewmodels
- `domain`: modelos de dominio, repositorios y casos de uso
- `data`: acceso a API remota, persistencia local y repositorios

La gestión de estado se basa en `StateFlow`, `UiState` y `ViewModel`, con inyección de dependencias mediante Hilt.



## Reproductor

El reproductor incluye varias características orientadas a mejorar la experiencia de uso:

- reproducción HLS con Media3
- doble toque para avanzar o retroceder
- control de velocidad
- guardado automático del progreso
- diálogo para reanudar desde el punto anterior
- overlay para reproducir el siguiente episodio automáticamente

## Persistencia local

La app utiliza Room para almacenar el progreso de visionado. Esto permite:

- mostrar la sección “Seguir viendo”
- recuperar la posición de reproducción de cada episodio
- marcar episodios como vistos
- mantener historial y progreso por serie

## Android TV

El proyecto también está preparado para ejecutarse en Android TV, incluyendo soporte de lanzamiento desde entorno Leanback.

## Requisitos

- Android Studio reciente
- JDK 17
- Android SDK 35
- Conexión a la API configurada en el proyecto


## Objetivo técnico

Además de la funcionalidad de usuario, este repositorio busca servir como muestra de:

- arquitectura Android moderna
- uso de Compose en una app real
- integración de reproducción multimedia
- persistencia local orientada a producto
- adaptación a móvil y TV
