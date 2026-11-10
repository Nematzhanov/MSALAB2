#!/bin/sh
# Запуск и остановка сервисов в фоне. Логи пишутся в logs/<сервис>.log
#   tests/manual/services.sh start            - запустить все
#   tests/manual/services.sh stop             - остановить все
#   tests/manual/services.sh start <сервис>   - запустить один
#   tests/manual/services.sh stop <сервис>    - остановить один
cd "$(dirname "$0")/../.."
mkdir -p logs
ALL="person-service group-service education-service"
NAMES=${2:-$ALL}

port_of() {
    case $1 in person-service) echo 8081;; group-service) echo 8082;; education-service) echo 8083;; esac
}

case $1 in
start)
    for s in $NAMES; do
        if lsof -nP -iTCP:"$(port_of $s)" -sTCP:LISTEN >/dev/null 2>&1; then
            echo "$s уже запущен (порт $(port_of $s) занят)"
            continue
        fi
        java -jar "$s/target/$s.jar" >> "logs/$s.log" 2>&1 &
        echo $! > "logs/$s.pid"
    done
    for s in $NAMES; do
        until curl -s -o /dev/null "http://localhost:$(port_of $s)/"; do sleep 0.5; done
        echo "$s работает (порт $(port_of $s))"
    done
    ;;
stop)
    for s in $NAMES; do
        # ищем процесс по имени jar, даже если он запущен не этим скриптом
        if pkill -f "$s/target/$s.jar"; then
            echo "$s остановлен"
        fi
        rm -f "logs/$s.pid"
    done
    ;;
*)
    echo "Использование: $0 start|stop [сервис]"
    ;;
esac
