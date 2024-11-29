# Оглавление

- [Что это и зачем нужно](#чтоЭто)
- [Основные термины](#основныеТермины)
- [Swagger](#сваггер)
- [Конечные точки](#конечныеТочки)
    - [Регистрация](#регистрация)
    - [Вход пользователя](#вход)
    - [Выход пользователя](#выход)
- [Работа с токенами](#работаСТокенами)
    - [Структура токена](#структураТокена)
    - [Использование токена](#использованиеТокена)
- [Проверка токена](#проверкаТокена)
- [Как поднять приложение](#какПоднять)

# <a name="чтоЭто">Что это и зачем нужно</a>

Это сервис для аутентификации пользователей. API позволяет регистрировать пользователей, совершать вход и аутентифицировать пользователей с помощью JWT-токенов.

Аутентификация требуется для того, чтобы случайный пользователь не мог получить данные, которые требует подтверждения личность, например, получить данные не своего аккаунта.

# <a name="основныеТермины">Основные термины</a>

- **Регистрация** - создание новой записи о пользователей в базу данных.
- **Аутентификация** - определения уже существующего пользователя по предоставленным данным.
- **Авторизация** - предоставление прав пользователю, исходя из роли.
- **Роль** - то, кем является пользователь (например, админ или обычный пользователь).
- **Токен** - ключ, с помощью которого пользователя можно идентифицировать. В данном случае - JWT-токен.

# <a name="сваггер">Swagger</a>

Доступна документация с помощью OpenAPI, которая находится на ручке `/swagger-ui/index.html#/`.

# <a name="конечныеТочки">Конечные точки</a>

## <a name="регистрация">Регистрация пользователя</a>

### URL

`/api/auth/signup`

### Метод

**POST**

### Описание

Данная конечная точка принимает JSON с данными пользователя в теле запроса. Информация о пользователе заносится в базу данных и возвращается ответ.

### Параметры запроса

1. `fio`: ФИО пользователя (строка).
2. `username`: имя пользователя (строка, **не может быть пустым**).
3. `email`: адрес электронной почты (строка, **не может быть пустым**).
4. `password`: пароль пользователя (строка, **не может быть пустым**).
5. `companyName`: компания, в которой работает пользователь (строка, доступно только для роли `HR`, остальные будут `null`).
6. `role`: роль пользователя (строка из следующего перечисления `(CANDIDATE, HR, ADMIN)`, **не может быть пустым**).

### Примеры запроса

#### Удачный

```JSON
{
"fio": "Петров Алексей Евгеньевич",
"username": "Lexa",
"email": "lexa@gmail.com",
"companyName": "yandex",
"password": "secr3t",
"role": "HR"
}
```

##### Ответ

```
200 OK
{
    "success": true,
    "description": "Пользователь создан"
}
```

#### Неудачный

```JSON
{
"fio": "Петров Алексей Евгеньевич",
"username": "",
"email": "lexa@gmail.com",
"companyName": "yandex",
"password": "secr3t",
"role": "HR"
}
```

##### Ответ

```
400 BAD REQUEST
{
    "success": false,
    "description": "Обязательные поля пусты"
}
```

## <a name="вход">Вход пользователя</a>

### URL

`/api/auth/signin`

### Метод

**POST**

### Описание

Данная конечная точка принимает JSON с данными пользователя в теле запроса. Информация о пользователе проверяется и возвращается ответ.

### Параметры запроса

1. `username`: имя пользователя (строка, **не может быть пустым**).
2. `password`: пароль пользователя (строка, **не может быть пустым**).

### Примеры запроса

#### Удачный

```JSON
{
"username": "Lexa",
"password": "secr3t",
}
```

##### Ответ

```
200 OK
{
    "success": true,
    "description": "Успешный вход",
    "token": <JWT-токен>
}
```

#### Неудачный

```JSON
{
"username": "",
"password": "secr3t",

```

##### Ответ

```
400 BAD REQUEST
{
    "success": false,
    "description": "Обязательные поля пусты",
    "token": ""
}
```

## <a name="выход">Выход пользователя</a>

### URL

`/api/auth/logout`

### Метод

**POST**

### Описание

Данная конечная точка принимает строку с токеном пользователя в теле запроса. Токен будет занесён в Redis.

### Параметры запроса

1. `JWT-токен`: токен пользователя (строка, **не может быть пустым**).

### Примеры запроса

#### Удачный

```
{
    "token": <Актуальный JWT-токен>
}
```

##### Ответ

```
200 OK
Токен отменён
```

#### Неудачный

```JSON
{
    "token": <Неактуальный JWT-токен>
}
```

##### Ответ

```
400 BAD REQUEST
Токен уже истёк
```


# <a name="работаСТокенами">Работа с токенами</a>

## <a name="структураТокена">Структура токена</a>

JWT-токен состоит из 
- **заголовка**, где описаны тип токена и алгоритм шифрования, 
- **полезной нагрузки**, где содержаться данные о пользователе (имя и email),
- **секрета** - ключа для расшифровки.

Токен действителен в течении 10 минут.

## <a name="использованиеТокена">Использование токена</a>

Токен нужен для доступа к защищённым конечным точкам.

Он передаётся вместе с запросом в заголовке:

```HTTP
Authorization: Bearer <JWT-token>
```

# <a name="проверкаТокена">Проверка работы токена</a>

Существует конечная точка `/test/name`, которая защищена и возвращает строку `TEST NAME`, если пользователь аутентифицирован. Если нет - вернётся код 401 `UNATHORIZED`.
При каждом запросе на защищённую конечную точку происходит проверка токена и поиск его в Redis.

# <a name="какПоднять">Как поднять приложение</a>

В корневой директории проекта находится `docker-compose.yaml`, в котором описаны все настройки.

Он поднимает 3 контейнера: 
1. Контейнер с приложением.
2. Контейнер с базой данных (таблица создаётся автоматически).
3. Контейнер с Redis.

Все контейнеры находятся в одной сети и могут общаться друг с другом.

При первом запуске используйте команду:

```shell
docker compose up --build
```

Так проект будет собран и поднят.

Если проект уже был собран, то используйте следующую команду:

```shell
docker compose up
```

> [!NOTE] 
> Все данные сохраняются при остановке контейнеров
