# Freelance Platform System (Freelance API)

## Студентска информация
* **Студент:** Живко Хаджиев
* **Факултетен номер:** 2401321068

---

## Описание на проекта
**Freelance Platform System** е съвременно уеб приложение за управление на фрийланс обяви и услуги. Системата позволява на потребителите да публикуват своите умения и оферти, а на клиентите – лесно да откриват точните хора за своите проекти чрез разширено динамично търсене.

Проектът е изграден на модулен принцип:
1. **freelance-api** — RESTful Web API (back-end), задвижван от Spring Boot.
2. **freelance-web** — Интуитивен уеб клиент (front-end) за крайните потребители.

---

## Технологичен стек

* **Платформа:** Java 17+ / Spring Boot 3.x
* **База данни:** MySQL 8.0+
* **ORM / Свързване:** Spring Data JPA & Hibernate
* **Сигурност:** [Ако имате сигурност, напр: Spring Security / JWT Authentication]
* **Валидация:** Jakarta Validation (Hibernate Validator)
* **Построяване на проекта:** Maven (pom.xml)

---

## Функционалности

* **Управление на обяви (CRUD):** Пълни операции за създаване, преглед, редактиране и изтриване на обяви за услуги (`Offers`), потребители (`Users`) и категории.
* **Динамично филтриране:** Възможност за едновременно филтриране по няколко категории наведнъж.
* **Сортиране и Пагинация:** Пълна поддръжка на пагинация (Pagination) и динамично подреждане на резултатите за бързодействие при голям обем от обяви.
* **Автоматично управление на БД:** Hibernate автоматично генерира схемата на базата данни и таблиците при първоначално стартиране.
* **Производителност:** Използване на HikariCP за управление на връзките към базата данни и изцяло асинхронна обработка на уеб заявките.

---

##  Инсталация и локално стартиране

### Изисквания
Преди да стартирате проекта, се уверете, че имате инсталирани:
* Java Development Kit (JDK) 17 или по-нова версия
* MySQL Server & MySQL Workbench
* IntelliJ IDEA (или друго Java IDE)

###  Стъпки за стартиране

#### 1. Клониране на хранилището (Repository)
Отворете терминал и изпълнете следните команди:
```bash
git clone [https://github.com/](https://github.com/)<your-username>/course-work.git
cd course-work/implementations/FreelanceSystem
```
#### 2. Конфигурация на връзката с базата данни
Преди да стартирате приложението, се уверете, че вашият локален MySQL сървър е пуснат. Отворете файла `src/main/resources/application.properties` и конфигурирайте потребителското име и паролата за достъп:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/freelance_db?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
```
#### 3. Изграждане на проекта (Build) с Maven
Отворете терминала в коренната папка на проекта (където се намира файлът `pom.xml`) и изпълнете следната команда, за да изтеглите всички необходими библиотеки (dependencies) и да компилирате кода:
```bash
mvn clean install
```

#### 4. Стартиране на Back-end приложението (Spring Boot API)
Можете да стартирате проекта директно от терминала с командата:
```bash
mvn spring-boot:run
```
Или като отворите проекта в IntelliJ IDEA, намерите главния клас `FreelanceApiApplication.java` и натиснете зеления бутон **Run** (триъгълника).

* **Достъп до API ендпойнтите:** Приложението ще се стартира по подразбиране на [http://localhost:8080](http://localhost:8080)
* **Примерен тест на обявите:** [http://localhost:8080/api/offers](http://localhost:8080/api/offers)