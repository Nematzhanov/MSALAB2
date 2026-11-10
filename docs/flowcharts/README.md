# Блок-схемы ЛР2

Оформлены по ГОСТ 19.701-90. Для каждой схемы есть PNG и SVG.

## Рисунок 1 – Общий алгоритм работы сервиса

![Рисунок 1](01_startup.png)

## Рисунок 2 – Обработка HTTP-запроса

![Рисунок 2](02_http_request.png)

## Рисунок 3 – Отправка события в RabbitMQ (EventPublisher)

![Рисунок 3](03_publish_event.png)

## Рисунок 4 – Приём сообщения из очереди (@RabbitListener)

![Рисунок 4](04_consume_event.png)

## Рисунок 5 – Добавление персоны (person-service)

![Рисунок 5](05_person_create.png)

## Рисунок 6 – Удаление персоны (person-service)

![Рисунок 6](06_person_delete.png)

## Рисунок 7 – Обработка событий в education-service (каскады)

![Рисунок 7](07_education_cascade.png)

## Рисунок 8 – Добавление записи обучения (education-service)

![Рисунок 8](08_education_create.png)

## Рисунок 9 – Изменение записи обучения (education-service)

![Рисунок 9](09_education_update.png)

## Рисунок 10 – Добавление группы (group-service)

![Рисунок 10](10_group_create.png)

## Рисунок 11 – Удаление группы (group-service)

![Рисунок 11](11_group_delete.png)

## Рисунок 12 – Удаление специальности (group-service)

![Рисунок 12](12_specialty_delete.png)

## Рисунок 13 – Событие «Конец учебного года» (group-service)

![Рисунок 13](13_end_of_year.png)

## Рисунок 14 – Обновление копий чужих данных (person-service, group-service)

![Рисунок 14](14_update_copies.png)

## Рисунок 15 – Список студентов группы или специальности (group-service)

![Рисунок 15](15_students.png)

## Рисунок 16 – Поиск отчисленных (person-service)

![Рисунок 16](16_expelled.png)

## Рисунок 17 – Информация о студенте: история обучения (person-service)

![Рисунок 17](17_history.png)

