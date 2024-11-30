# Проект тестирования приложения todo-app
### запуск всех тестов

```
./gradlew clean test
 ```
<br>
### запуск тестов с параметрами

```
./gradlew test -DimagePath=/Users/repon/Desktop/work/repon/BHFT/tester-task/todo-app.tar -Dports.host=8080 -Dports.container=4242 -Durls.base=http://localhost:8080/todos -Durls.websocket=ws://localhost:8080/ws -Dcredentials.username=testUser -Dcredentials.password=testPass
```
<br>
<br> где:
<br>- imagePath: Путь к Docker-образу.
<br>- ports.host: Порт на хосте для проброса.
<br>- ports.container: Порт внутри контейнера.
<br>- urls.base: Базовый URL API.
<br>- urls.websocket: URL для подключения WebSocket.
<br>- credentials.username и credentials.password: Данные для авторизации.

## формирование allure отчета:
```
allure serve
```

	1. Запустите тесты через Gradle или Maven. Например, если используете Gradle:./gradlew test
```
./gradlew clean test
```
	2. После выполнения тестов выполните команду для генерации Allure отчета:
```
./gradlew allureReport
```
	3. Чтобы просмотреть отчет, выполните:
```
./gradlew allureServe
```