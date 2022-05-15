Cotenidos
=================
- [Cotenidos](#cotenidos)
    - [Diagrama EIP](#diagrama-eip)
    - [Software requerido](#software-requerido)
    - [Pasos previos](#pasos-previos)
    - [Ejecución](#ejecución)

## Diagrama EIP
![alt text](https://github.com/veronica-platform/veronica-job/blob/main/static/eip-content-based-route.png)

## Software requerido
- JDK 1.8.0_121

## Stack de desarrollo
- H2
- Apache Camel & Content-Based Router EIP
- Spring Boot
- Thymeleaf
- HTML/Bootstrap/JQuery

## Pasos previos
Crear las siguientes variables de entorno en la computadora donde se desplegará la aplicación, utilizando los valores que se le proporcionará al momento de solicitar sus credenciales de acceso a [info@veronica.ec](mailto:info@veronica.ec):
```bash
VERONICA_BASE_URL="https://api-sbox.veronica.ec/api/v1.0/%s" #Ejemplo para ambiente de Sandbox
VERONICA_OAUTH_CLIENT_CREDENTIALS=""
VERONICA_API_KEY=""
```
La variable `VERONICA_BASE_URL` puede tomar cualquiera de los siguientes valores, dependiendo del ambiente al que queremos conectarnos:

| Ambiente   | URL                                      |
|------------|------------------------------------------|
| Desarrollo | https://api-dev.veronica.ec/api/v1.0/%s  |
| Sandbox    | https://api-sbox.veronica.ec/api/v1.0/%s |
| Producción | https://api.veronica.ec/api/v1.0/%s      |

## Ejecución
- Para desplegar la aplicación, ejecutar el siguiente comando en la carpeta donde se encuentre el archivo jar:
```bash
java -jar veronica-job-1.0.0-SNAPSHOT.jar
```

## Uso

1. Ir a la siguiente dirección en un navegador Web: 
```bash
http://localhost:5000/
```

2. Ingresar sus credenciales de Verónica (usuario/password)
![alt text](https://github.com/veronica-platform/veronica-job/blob/main/static/login-screen.png)

3. Visualizará la lista de procesos que actualmente tiene en ejecución.
![alt text](https://github.com/veronica-platform/veronica-job/blob/main/static/processes-list.png)

4. Para agregar un nuevo proceso, haga clic en el botón más de la parte superior derecha, seleccione la empresa y la ruta donde serán almacenados los comprobantes a sincronizar con Verónica.
![alt text](https://github.com/veronica-platform/veronica-job/blob/main/static/new-process.png)
   
- Ejemplos de rutas
```bash
C:\\ROOT\\XML\\
```

5. Para visualizar los eventos, diríjase a la vista de logs.
![alt text](https://github.com/veronica-platform/veronica-job/blob/main/static/audit-logs-list.png)
 