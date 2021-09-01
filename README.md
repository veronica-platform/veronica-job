Cotenidos
=================
- [Cotenidos](#cotenidos)
    - [Software requerido](#software-requerido)
    - [Pasos previos](#pasos-previos)
    - [Ejecución](#ejecución)

## Software requerido
- JDK 1.8.0_121

## Pasos previos
Crear las siguientes variables de entorno en la computadora donde se desplegará la aplicación, utilizando los valores que se le proporcionará al momento de solicitar sus credenciales de acceso a [info@veronica.ec](mailto:info@veronica.ec):
```bash
VERONICA_BASE_URL="https://api-sbox.veronica.ec/api/v1.0/%s" #Ejemplo para ambiente de Sandbox
VERONICA_OAUTH_CLIENT_ID=""
VERONICA_OAUTH_CLIENT_SECRET=""
```

## Ejecución
- Para desplegar la aplicación, ejecutar el siguiente comando en la carpeta donde se encuentre el archivo jar:
```
java -jar veronica-job-1.0.0-SNAPSHOT.jar
```

- Para acceder a la aplicación
```
http://localhost:8080/
```

- Para acceder a la base de datos de la aplicacón a través de su consola Web, utilizar el siguiente enlace:
```
http://localhost:8080/h2-console
```