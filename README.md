# Todo-app testing project

### Execute all tests:

```
./gradlew clean test
 ```

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
	- imagePath: Docker-image path .<br>
	- ports.host: Port on the host for forwarding.<br>
	- ports.container: The port inside the container.<br>
	- urls.base: Base URL API.<br>
	- urls.websocket: WebSocket URL.<br>
	- credentials.username & credentials.password: Authorization data.<br>
```
### Execute only Smoke tag tests:
```
./gradlew clean test -Ptags=Smoke
```

## Generating an allure report:

```
allure serve
```