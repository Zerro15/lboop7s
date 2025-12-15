# Node.js Authentication Backend

Полнофункциональный backend на Express и MongoDB с регистрацией, авторизацией по JWT и защищённым маршрутом для получения данных текущего пользователя.

## Быстрый старт
1. Установите зависимости:
   ```bash
   npm install
   ```
2. Создайте файл `.env` на основе примера ниже.
3. Запустите сервер в разработке:
   ```bash
   npm run dev
   ```
   или в продакшене:
   ```bash
   npm start
   ```

## Переменные окружения
Создайте `.env` с параметрами подключения:
```
PORT=5000
MONGODB_URI=mongodb://127.0.0.1:27017/auth_app
JWT_SECRET=change_me_to_a_strong_secret
JWT_EXPIRES_IN=7d
CLIENT_ORIGIN=http://localhost:3000
```

## Доступные маршруты
- `POST /api/auth/register` — регистрация нового пользователя (email, password). Хеширует пароль и возвращает JWT.
- `POST /api/auth/login` — авторизация. Возвращает JWT при верных данных, общую ошибку при неверных.
- `GET /api/users/me` — защищённый маршрут, доступен только с валидным JWT в заголовке `Authorization: Bearer <token>`.
- `GET /health` — проверка работоспособности сервера.

Пароль хранится только в виде bcrypt-хеша. JWT по умолчанию также устанавливается в httpOnly-cookie.
