# Todo-app testing project
You must have Docker report: https://www.docker.com/products/docker-desktop/

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
```

```
startup parameters are optional - by default they are taken from the config
	- imagePath: Docker-image path.
	- ports.host: Port on the host for forwarding.
	- ports.container: The port inside the container.
	- urls.base: Base URL API.
	- urls.websocket: WebSocket URL.
	- credentials.username & credentials.password: Authorization data.
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

```
allure serve
```
You must have allure report: https://allurereport.org/docs/install/

# Load testing
#### Build project for load testing:
```
./gradlew clean build
```