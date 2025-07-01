 Краткое описание архитектуры и принятых решений
Приложение представляет собой систему мониторинга публичного API с периодическим опросом, сохранением результатов, отправкой событий в Kafka и REST-интерфейсом с авторизацией. Архитектура построена по слоям и придерживается принципов SRP и IoC.

🔧 Основные компоненты:
Scheduler (ApiScheduler)
Каждую минуту выполняет fetchAndSaveData() с помощью @Scheduled. При ошибке используется @Retryable с экспоненциальной задержкой. При исчерпании попыток работает @Recover.

Сервис (ServiceImpl)
Выполняет HTTP-запрос через RestTemplate, обрабатывает успешный/ошибочный ответ, сохраняет сущность ApiDataEntity в базу, отправляет данные в Kafka (KafkaProducer).

Kafka Producer
Использует KafkaTemplate<String, String> для отправки сообщений:

- в топик api-data при успехе,

- в api-errors при исключении.

Сущность ApiDataEntity
Содержит поля id, createdAt, success, payload. Сохраняется в PostgreSQL. Время создания выставляется через @PrePersist.

REST-контроллер (ApiController)
Реализует эндпоинты:

GET /status — проверка работоспособности (роль USER и ADMIN),

GET /data — получение 10 последних записей (роль ADMIN),

GET /fetch-now — ручной запуск опроса API (роль ADMIN).

Spring Security (SecurityConfig)
Используется Basic Auth. Конфигурация ролей USER и ADMIN через InMemoryUserDetailsManager.

Глобальный обработчик ошибок (GlobalExceptionHandler)
Обрабатывает исключения, возвращает унифицированный JSON-ответ и логирует ошибки. Примеры: NoSuchElementException, EntityNotFoundException, Exception.

Docker Compose
Используется для развёртывания Kafka и PostgreSQL в dev-среде.

📌 Принятые решения
Spring Retry применяется для надёжности при нестабильном API.

Kafka используется как транспорт для передачи как успешных, так и неудачных событий, что позволяет масштабировать систему или подключать дополнительные консюмеры.

Встроенная авторизация через Spring Security позволяет быстро проверить уровни доступа.

Выделенные слои и автоконфигурация (@Configuration, @Component) обеспечивают удобную поддержку и расширение проекта.
