#!/bin/sh
# Демонстрация. Перед запуском:
#   tests/manual/reset.sh
#   tests/manual/services.sh start
cd "$(dirname "$0")/../.."
RABBITMQCTL=$(command -v rabbitmqctl || echo /opt/homebrew/sbin/rabbitmqctl)
P=http://localhost:8081
G=http://localhost:8082
E=http://localhost:8083
JSON='Content-Type: application/json'

step() { printf '\n\033[1m=== %s ===\033[0m\n' "$1"; }
req()  { echo "> curl $*"; curl -s -w '\n[HTTP %{http_code}]\n' "$@"; }
queues() { echo "> rabbitmqctl list_queues"; "$RABBITMQCTL" -q list_queues name messages consumers; }

step "1. Список объектов"
req $P/persons
req $G/specialties
req $G/groups
req $E/educations

step "2. Информация об объекте"
req $P/persons/1
req $G/groups/4
req $P/persons/999

step "3. Списки студентов (новые операции)"
req $G/groups/4/students
req $G/specialties/1/students
req "$P/persons/expelled"
req -G $P/persons/expelled --data-urlencode "gender=М" --data-urlencode "birth_date=2005-03-14"
req $P/persons/1/history

step "4. Добавление"
req -X POST $P/persons -H "$JSON" \
    -d '{"name":"Анна","surname":"Новикова","birth_date":"2007-04-12","gender":"Ж","snils":"999-888-777 66"}'
sleep 1
req -X POST $E/educations -H "$JSON" \
    -d '{"person_id":7,"group_id":3,"start_date":"2025-09-01"}'
sleep 1
req $G/groups/3/students

step "5. Валидация"
req -X POST $P/persons -H "$JSON" \
    -d '{"name":"","surname":"Тест","birth_date":"2090-01-01","gender":"X","snils":"12345"}'
req -X POST $G/groups -H "$JSON" \
    -d '{"name":"9999","start_year":2025,"specialty_id":3,"course":6}'
req -X POST $G/groups -H "$JSON" \
    -d '{"name":"9999","start_year":2025,"specialty_id":4,"course":1}'
req -X POST $E/educations -H "$JSON" \
    -d '{"person_id":2,"group_id":3,"start_date":"2025-09-01","end_date":"2020-01-01"}'
req -X POST $E/educations -H "$JSON" \
    -d '{"person_id":2,"group_id":2,"start_date":"2025-09-01"}'
req -X POST $G/groups -H "$JSON" \
    -d '{"name":"4091","start_year":2024,"specialty_id":1,"course":1}'
req -X POST $E/educations -H "$JSON" \
    -d '{"person_id":999,"group_id":3,"start_date":"2025-09-01"}'

step "6. Изменение"
req -X PUT $P/persons/7 -H "$JSON" \
    -d '{"name":"Анна","surname":"Новикова-Орлова","birth_date":"2007-04-12","gender":"Ж","snils":"999-888-777 66"}'
sleep 1
req $G/groups/3/students
req -X PUT $E/educations/3 -H "$JSON" \
    -d '{"person_id":2,"group_id":1,"start_date":"2024-09-01","end_date":null,"status":"академический отпуск"}'
req -X PUT $E/educations/1 -H "$JSON" \
    -d '{"person_id":1,"group_id":1,"start_date":"2024-09-01","end_date":"2025-02-15","status":"активен"}'

step "7. Каскадное удаление через брокер"
req -X DELETE $P/persons/7
sleep 1
req $E/educations

step "8. Мягкое удаление специальности и группы"
req -X DELETE $G/specialties/1
req -X DELETE $G/groups/1
req -X POST $G/specialties -H "$JSON" \
    -d '{"name":"Тестовая","institute":"ИИТ","department":"Кафедра","study_years":2}'
req -X DELETE $G/specialties/5

step "9. Сервис обучения выключен: удаляем персону, сообщение ждёт в очереди"
tests/manual/services.sh stop education-service
req -X DELETE $P/persons/2
sleep 1
queues

step "10. Сервис обучения снова запущен: сообщение обработано"
tests/manual/services.sh start education-service
sleep 2
queues
req $E/educations

step "11. Конец учебного года"
req -X POST $G/events/end-of-year
sleep 1
req $G/groups
req $E/educations
req $P/persons/4/history
