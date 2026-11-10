-- Схема сервиса персон. Запуск из папки lab2-spring-java: psql -d vuz_ms -f person-service/db/init.sql

DROP SCHEMA IF EXISTS person_service CASCADE;
CREATE SCHEMA person_service;
SET search_path TO person_service;

CREATE TABLE person (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    surname     VARCHAR(100) NOT NULL,
    birth_date  DATE         NOT NULL,
    gender      VARCHAR(1)   NOT NULL CHECK (gender IN ('М', 'Ж')),
    snils       VARCHAR(14)  UNIQUE
);

-- Копии чужих данных (заполняются событиями из RabbitMQ).
-- Нужны для поиска отчисленных и истории обучения студента
CREATE TABLE education_ref (
    education_id  INT PRIMARY KEY,
    person_id     INT         NOT NULL,
    group_id      INT         NOT NULL,
    start_date    DATE        NOT NULL,
    end_date      DATE,
    status        VARCHAR(30) NOT NULL
);

CREATE TABLE group_ref (
    id              INT PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    start_year      INT         NOT NULL,
    course          INT         NOT NULL,
    status          VARCHAR(20) NOT NULL,
    specialty_name  VARCHAR(200)
);

INSERT INTO person (name, surname, birth_date, gender, snils) VALUES
    ('Иван',     'Иванов',    '2005-03-14', 'М', '123-456-789 01'),
    ('Мария',    'Петрова',   '2006-07-02', 'Ж', '234-567-890 12'),
    ('Алексей',  'Сидоров',   '2004-11-23', 'М', '345-678-901 23'),
    ('Екатерина','Смирнова',  '2003-01-30', 'Ж', NULL),
    ('Дмитрий',  'Кузнецов',  '2004-05-18', 'М', '456-789-012 34'),
    ('Ольга',    'Волкова',   '1976-09-09', 'Ж', '567-890-123 45');

INSERT INTO education_ref (education_id, person_id, group_id, start_date, end_date, status) VALUES
    (1, 1, 1, '2024-09-01', '2025-02-15', 'отчислен'),
    (2, 1, 3, '2025-09-01', NULL,         'активен'),
    (3, 2, 1, '2024-09-01', NULL,         'активен'),
    (4, 3, 5, '2023-09-01', NULL,         'академический отпуск'),
    (5, 4, 4, '2022-09-01', NULL,         'активен'),
    (6, 5, 4, '2022-09-01', NULL,         'активен'),
    (7, 6, 2, '1994-09-01', '1999-06-30', 'завершил обучение');

INSERT INTO group_ref (id, name, start_year, course, status, specialty_name) VALUES
    (1, '4091',    2024, 2, 'актуальна', 'Программная инженерия'),
    (2, '4091',    1994, 5, 'закрыта',   'Вычислительные машины (старая)'),
    (3, '5012',    2025, 1, 'актуальна', 'Информационная безопасность'),
    (4, '3011',    2022, 4, 'актуальна', 'Прикладная математика'),
    (5, '1984-до', 2023, 3, 'актуальна', 'Программная инженерия');
