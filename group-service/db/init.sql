-- Схема сервиса групп и специальностей. Запуск из папки lab2-spring-java: psql -d vuz_ms -f group-service/db/init.sql

DROP SCHEMA IF EXISTS group_service CASCADE;
CREATE SCHEMA group_service;
SET search_path TO group_service;

CREATE TABLE specialty (
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    institute    VARCHAR(200) NOT NULL,
    department   VARCHAR(200) NOT NULL,
    study_years  INT          NOT NULL CHECK (study_years BETWEEN 1 AND 6),
    status       VARCHAR(20)  NOT NULL DEFAULT 'актуально'
                 CHECK (status IN ('актуально', 'неактуально'))
);

CREATE TABLE study_group (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    start_year    INT         NOT NULL,
    specialty_id  INT         NOT NULL REFERENCES specialty (id) ON DELETE RESTRICT,
    course        INT         NOT NULL CHECK (course BETWEEN 1 AND 6),
    status        VARCHAR(20) NOT NULL DEFAULT 'актуальна'
                  CHECK (status IN ('актуальна', 'закрыта')),
    UNIQUE (start_year, name)
);

-- Копии чужих данных (заполняются событиями из RabbitMQ).
-- Нужны для списков студентов группы и специальности
CREATE TABLE education_ref (
    education_id  INT PRIMARY KEY,
    person_id     INT         NOT NULL,
    group_id      INT         NOT NULL,
    status        VARCHAR(30) NOT NULL
);

CREATE TABLE person_ref (
    id          INT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    surname     VARCHAR(100) NOT NULL,
    birth_date  DATE         NOT NULL,
    gender      VARCHAR(1)   NOT NULL,
    snils       VARCHAR(14)
);

INSERT INTO specialty (name, institute, department, study_years, status) VALUES
    ('Программная инженерия',        'Институт информационных технологий', 'Кафедра программной инженерии', 4, 'актуально'),
    ('Информационная безопасность',  'Институт информационных технологий', 'Кафедра защиты информации',     5, 'актуально'),
    ('Прикладная математика',        'Институт математики',                'Кафедра прикладной математики', 4, 'актуально'),
    ('Вычислительные машины (старая)','Институт информационных технологий','Кафедра ЭВМ',                   5, 'неактуально');

INSERT INTO study_group (name, start_year, specialty_id, course, status) VALUES
    ('4091',    2024, 1, 2, 'актуальна'),
    ('4091',    1994, 4, 5, 'закрыта'),
    ('5012',    2025, 2, 1, 'актуальна'),
    ('3011',    2022, 3, 4, 'актуальна'),
    ('1984-до', 2023, 1, 3, 'актуальна');

INSERT INTO education_ref (education_id, person_id, group_id, status) VALUES
    (1, 1, 1, 'отчислен'),
    (2, 1, 3, 'активен'),
    (3, 2, 1, 'активен'),
    (4, 3, 5, 'академический отпуск'),
    (5, 4, 4, 'активен'),
    (6, 5, 4, 'активен'),
    (7, 6, 2, 'завершил обучение');

INSERT INTO person_ref (id, name, surname, birth_date, gender, snils) VALUES
    (1, 'Иван',     'Иванов',   '2005-03-14', 'М', '123-456-789 01'),
    (2, 'Мария',    'Петрова',  '2006-07-02', 'Ж', '234-567-890 12'),
    (3, 'Алексей',  'Сидоров',  '2004-11-23', 'М', '345-678-901 23'),
    (4, 'Екатерина','Смирнова', '2003-01-30', 'Ж', NULL),
    (5, 'Дмитрий',  'Кузнецов', '2004-05-18', 'М', '456-789-012 34'),
    (6, 'Ольга',    'Волкова',  '1976-09-09', 'Ж', '567-890-123 45');
