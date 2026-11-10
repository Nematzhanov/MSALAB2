-- Схема сервиса обучения. Запуск из папки lab2-spring-java: psql -d vuz_ms -f education-service/db/init.sql

DROP SCHEMA IF EXISTS education_service CASCADE;
CREATE SCHEMA education_service;
SET search_path TO education_service;

-- Персоны и группы в других сервисах, поэтому внешних ключей на них нет.
-- Связь проверяется в программе по копиям person_ref и group_ref
CREATE TABLE education (
    id          SERIAL PRIMARY KEY,
    person_id   INT         NOT NULL,
    group_id    INT         NOT NULL,
    start_date  DATE        NOT NULL,
    end_date    DATE,
    status      VARCHAR(30) NOT NULL DEFAULT 'активен'
                CHECK (status IN ('активен', 'академический отпуск', 'отчислен', 'завершил обучение'))
);

CREATE TABLE person_ref (id INT PRIMARY KEY);

-- статус группы нужен, чтобы не записывать студентов в закрытую группу
CREATE TABLE group_ref (
    id      INT PRIMARY KEY,
    status  VARCHAR(20) NOT NULL
);

INSERT INTO education (person_id, group_id, start_date, end_date, status) VALUES
    (1, 1, '2024-09-01', '2025-02-15', 'отчислен'),
    (1, 3, '2025-09-01', NULL,         'активен'),
    (2, 1, '2024-09-01', NULL,         'активен'),
    (3, 5, '2023-09-01', NULL,         'академический отпуск'),
    (4, 4, '2022-09-01', NULL,         'активен'),
    (5, 4, '2022-09-01', NULL,         'активен'),
    (6, 2, '1994-09-01', '1999-06-30', 'завершил обучение');

INSERT INTO person_ref (id) VALUES (1), (2), (3), (4), (5), (6);
INSERT INTO group_ref (id, status) VALUES
    (1, 'актуальна'), (2, 'закрыта'), (3, 'актуальна'), (4, 'актуальна'), (5, 'актуальна');
