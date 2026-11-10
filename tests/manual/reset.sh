#!/bin/sh
# Возвращает всё в исходное состояние: пересоздаёт схемы трёх сервисов и очищает очереди RabbitMQ
cd "$(dirname "$0")/../.."
RABBITMQCTL=$(command -v rabbitmqctl || echo /opt/homebrew/sbin/rabbitmqctl)

createdb vuz_ms 2>/dev/null || true
for s in person-service group-service education-service; do
    PGOPTIONS="--client-min-messages=warning" psql -q -d vuz_ms -f "$s/db/init.sql"
    "$RABBITMQCTL" purge_queue "$s" >/dev/null 2>&1 || true
done
echo "База vuz_ms и очереди сброшены"
