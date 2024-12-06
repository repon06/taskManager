# Todo-app testing project

The project is developed in java and java 16+ is required to run it: https://www.oracle.com/java/technologies/downloads/
```shell
brew install java
java -version
```
You must have Docker report: https://www.docker.com/products/docker-desktop/
The project itself deploys the docker image of the application
```shell
brew install docker
docker info
```
### Execute tests with parameters:

```shell
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

```information
	* imagePath: Docker-image path.
        startup parameters are optional - by default they are taken from the config
	* ports.host: Port on the host for forwarding. (optional)
	* ports.container: The port inside the container. (optional)
	* urls.base: Base URL API. (optional)
	* urls.websocket: WebSocket URL. (optional)
	* credentials.username & credentials.password: Authorization data. (optional)
	* cleanAllure=true: for cleaning the allure-results directory. (optional)
```

### Execute all tests:

```shell
./gradlew clean test
 ```

### Execute only Smoke tag tests:

```shell
./gradlew clean test -Ptags=Smoke
```

#### Execute only api tests:

```shell
./gradlew clean test -Ptags=api
```

#### Execute only websocket tests:

```shell
./gradlew clean test -Ptags=websocket
```

##### Execute websocket + delete tests:

```shell
./gradlew clean test -Ptags=websocket,delete,update
```

existing tags: api, websocket, get, delete, update, create

## Generating an allure report:

##### Show allure report:

```shell
./gradlew allureServe
```

##### Generate local allure report: build/reports/allure-report/allureReport

```shell
./gradlew allureReport
```