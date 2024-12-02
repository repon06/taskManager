# Todo-app testing project

You must have Docker report: https://www.docker.com/products/docker-desktop/

The project itself deploys the docker image of the application

### Execute tests with parameters:

```
./gradlew clean test 
-DimagePath=/Users/repon/Desktop/work/repon/BHFT/tester-task/todo-app.tar 
-Dports.host=8080 
-Dports.container=4242 
-Durls.base=http://localhost:8080/todos 
-Durls.websocket=ws://localhost:8080/ws 
-Dcredentials.username=admin 
-Dcredentials.password=admin
-PcleanAllure=true 
```

```
	- imagePath: Docker-image path.
        startup parameters are optional - by default they are taken from the config
	- ports.host: Port on the host for forwarding. (optional)
	- ports.container: The port inside the container. (optional)
	- urls.base: Base URL API. (optional)
	- urls.websocket: WebSocket URL. (optional)
	- credentials.username & credentials.password: Authorization data. (optional)
	- cleanAllure=true: for cleaning the allure-results directory. (optional)
```

### Execute all tests:

```
./gradlew clean test
 ```

### Execute only Smoke tag tests:

```
./gradlew clean test -Ptags=Smoke
```

#### Execute only api tests:

```
./gradlew clean test -Ptags=api
```

#### Execute only websocket tests:

```
./gradlew clean test -Ptags=websocket
```

##### Execute websocket + delete tests:

```
./gradlew clean test -Ptags=websocket,delete,update
```

existing tags: api, websocket, get, delete, update, create

## Generating an allure report:

##### Show allure report:

```
./gradlew allureServe
```

##### Generate local allure report: build/reports/allure-report/allureReport

```
./gradlew allureReport
```

```
allure serve
```

If you must have allure report: https://allurereport.org/docs/install/

~~# Load testing~~

~~#### Build project for load testing:~~

```
./gradlew clean build
./gradlew clean jarWithTests
./gradlew clean jar
```