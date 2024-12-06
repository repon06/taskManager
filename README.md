# Todo-app testing project

## Project Description

This project contains automated tests for the Todo Application.
The tests are written in Java and verify the application's functionality, including creating/updating/deleting tasks and
retrieving the task list,
and ensuring the WebSocket connection works to receive real-time updates about new tasks.

## Project Structure

## Environment requirements:

- **Java Development Kit (JDK)** version 15+ : https://www.oracle.com/java/technologies/downloads/

```shell
brew install java
java -version
```

- **Build system**: Gradle
- **Docker**: Required to run tests in a containerized environment: https://www.docker.com/products/docker-desktop/

```shell
brew install docker
```

## Installation and Usage:

1. **Clone the repository**:

```shell
git clone https://github.com/repon06/taskManager.git
cd <project directory>
```

2. **Ensure Docker is installed and running**:

```shell
docker --version
docker info
```

3. **Run tests**:

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

4. **Report generation**:

## Generating an allure report:

##### Show allure report:

```shell
./gradlew allureServe
```

##### Generate local allure report: build/reports/allure-report/allureReport

```shell
./gradlew allureReport
```