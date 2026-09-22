# api-reservations — Primer Parcial COM470

## Descripción del trabajo

Proyecto de una API de reservas desarrollado con Spring Boot.
Se implementaron pruebas unitarias con JUnit y Mockito para comprobar
consultas, creación, actualización y eliminación de reservas.

El trabajo incluye:
- 16 pruebas de Services con casos exitosos, errores y valores nulos.
- Uso de `@Mock` y `@InjectMocks` para simular el repositorio,
  el conversor y el catálogo de ciudades.
- Corrección y aislamiento de las pruebas de Repository.
- Reportes de cobertura con JaCoCo.
- Trabajo colaborativo mediante ramas, commits e Issues en GitHub.

## Requisitos

- Java 21.
- Maven 3.9.9.
- IntelliJ IDEA u otro IDE compatible con Java.

## Instrucciones

Ejecutar los comandos desde la carpeta `api-reservations`,
donde se encuentra `pom.xml`.

### Ejecutar todas las pruebas y generar la cobertura

```bash
mvn clean verify
```

### Ejecutar únicamente las pruebas de Services

```bash
mvn "-Dtest=ReservationServiceTest" test
```

### Consultar la cobertura

Después de ejecutar `mvn clean verify`, abrir en el navegador:

```text
target/site/jacoco/index.html
```

El reporte muestra la cobertura por clase y la cobertura total del proyecto.