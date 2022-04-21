<h1>Тестовое задание для x5(еще одно)</h1>
<div>Сервис накопления скидочных баллов клиентов</div>
<h3>Стэк:</h3>
<li>Spring Boot, Data, Cloud</li>
<li>Liquibase, Postgres</li>
<li>Docker, Swagger, Postman</li>

<h3>Postman</h3>
<div>В корневой папке postman/ лежит коллекция</div>

<h3>Swagger</h3>
<div>В корневой папке swagger/ лежит openapi.json</div>

<h3>Инструкция запуска</h3>
<li>Из корня проекта запустить `./gradlew build` (jdk11)</li>
<li>Затем `docker-compose up --build`</li>
<li>После применения миграций в бд будут cозданы clients 1 и 2</li>

<h3>Описание пакетов:</h3>
<li>com.sbelan.x5accumulationdiscountpoints.configuration     конфиги</li>
<li>com.sbelan.x5accumulationdiscountpoints.controller        контроллеры</li>
<li>com.sbelan.x5accumulationdiscountpoints.model             модели(включая сущности для jpa)</li>
<li>com.sbelan.x5accumulationdiscountpoints.repository        репозитории</li>
<li>com.sbelan.x5accumulationdiscountpoints.service           сервисный слой</li>