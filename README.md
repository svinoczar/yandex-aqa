# Yandex Disk API Autotests

Пример проекта API-автотестов для сервиса [Яндекс Диск](https://yandex.ru/dev/disk/rest/).

Тесты работают с REST API `https://cloud-api.yandex.net/v1/disk` и проверяют основные операции с диском, ресурсами, файлами, корзиной, публичными ссылками и асинхронными операциями.

Проект подготовлен как тестовое задание и не ставит целью покрытие всех endpoint Яндекс Диска.

## Стек

- Kotlin;
- Java 21;
- JUnit 5;
- REST Assured;
- Jackson;
- Maven;
- Allure Report.

## Покрытые сценарии

- получение информации о диске;
- создание, чтение, изменение и удаление ресурсов;
- позитивные и негативные проверки API-ошибок;
- копирование и перемещение директорий;
- загрузка и скачивание файлов;
- публикация ресурсов и снятие публикации;
- получение публичных ресурсов и ссылок на скачивание;
- перемещение ресурсов в корзину, восстановление и окончательное удаление;
- получение статуса асинхронных операций;
- один сквозной сценарий загрузки и скачивания файла.

Тесты используют HTTP-методы `GET`, `POST`, `PUT`, `PATCH` и `DELETE`.

## Структура проекта

```text
src
├── main/kotlin/com/disk/yandex/model
│   ├── request                         # Модели запросов
│   └── response                        # Модели ответов API
└── test/kotlin/com/disk/yandex
    ├── client/DiskClient.kt            # HTTP-клиент Яндекс Диска
    ├── configuration                   # Конфигурация и базовый класс тестов
    ├── tests                           # Тестовые классы
    └── util                            # Десериализация, проверки и ожидание операций
```

## Предварительные требования

- JDK 21 или новее;
- OAuth-токен отдельного тестового аккаунта Яндекса с доступом к Диску.

> Не используйте личный аккаунт и личный OAuth-токен. Тесты создают и удаляют ресурсы, а cleanup окончательно удаляет из корзины объекты с префиксом `autotest-`.

## Получение OAuth-токена

- [полигон Яндекс Диска](https://yandex.ru/dev/disk/poligon/);
- [документация REST API](https://yandex.ru/dev/disk/api/concepts/about-docpage/).

## Настройка окружения

Создайте локальный файл `.env` на основе `.env.example`.

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Linux или macOS:

```bash
cp .env.example .env
```

Добавьте токен тестового пользователя:

```dotenv
YANDEX_DISK_TOKEN=your_test_oauth_token
YANDEX_DISK_BASE_URL=https://cloud-api.yandex.net/v1/disk
```

Вместо `.env` можно использовать переменные окружения `YANDEX_DISK_TOKEN` и `YANDEX_DISK_BASE_URL`.

## Запуск тестов

Запуск всех тестов в Windows:

```powershell
.\mvnw.cmd test
```

Запуск всех тестов в Linux или macOS:

```bash
./mvnw test
```

Запуск отдельного тестового класса в Windows PowerShell:

```powershell
.\mvnw.cmd '-Dtest=ResourceCrudTest' test
```

Запуск тестов по JUnit-тегу в Windows PowerShell:

```powershell
.\mvnw.cmd '-Dgroups=negative' test
```

В проекте используются теги:

- `negative` — негативные сценарии;
- `async` — сценарии с асинхронными операциями;
- `e2e` — end-to-end-тесты.

## Allure Report

Во время тестового запуска результаты Allure сохраняются в `target/allure-results`.

Сгенерировать статический отчёт:

```powershell
.\mvnw.cmd allure:report
```

Отчёт будет создан в `target/site/allure-maven-plugin`.

Сгенерировать отчёт и открыть его локально:

```powershell
.\mvnw.cmd allure:serve
```

Для Linux и macOS используйте `./mvnw` вместо `.\mvnw.cmd`.

## Очистка тестовых данных

Каждый тест использует уникальное имя ресурса с префиксом `autotest-`.

- `@AfterEach` удаляет ресурс текущего теста с диска или из корзины;
- `@AfterAll` дополнительно удаляет оставшиеся в корзине ресурсы с префиксом `autotest-`.

Поэтому проект следует запускать только на выделенном тестовом аккаунте.
