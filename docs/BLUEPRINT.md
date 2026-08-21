# Reclama — Blueprint maestro

Reclama es un asistente inteligente de defensa del consumidor para Chile.

## Principio de arquitectura

La IA interpreta y redacta. El software determinista valida reglas, rutas, estados y plazos.

## Tres pilares

1. **Reclama AI** — comprensión del problema, extracción estructurada y redacción.
2. **Rights Engine** — reglas jurídicas versionadas y fuentes oficiales.
3. **Expediente inteligente** — caso persistente con hechos, evidencia, cronología y seguimiento.

## Roadmap inmediato

### v0.1.0-alpha — Foundation
- Android + Kotlin + Jetpack Compose
- Clean Architecture / MVVM como dirección arquitectónica
- Modelo ConsumerCase
- Home, Casos y Detalle
- persistencia local
- tests
- GitHub Actions y APK

### v0.2.0-alpha — Reclama AI Intake
- relato por texto
- AIProvider
- structured outputs
- extracción de empresa, fechas, producto, problema, monto y pretensión
- preguntas dinámicas
- revisión del resumen

## Regla de producto

Toda función debe ayudar al usuario a pasar de «tengo un problema» a «sé exactamente qué hacer».
