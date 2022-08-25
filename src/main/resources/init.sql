CREATE TABLE IF NOT EXISTS message
(
    id        SERIAL PRIMARY KEY,
    text      TEXT NOT NULL,
    ordering  INT  NOT NULL
);

CREATE TABLE IF NOT EXISTS country
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS city
(
    id         SERIAL PRIMARY KEY,
    name       TEXT NOT NULL,
    population BIGINT NULL,
    country_id INT REFERENCES city (id)
);

INSERT INTO country(id, name)
VALUES (1, 'Poland'),
       (2, 'Czechia'),
       (3, 'Slovakia'),
       (4, 'Ukraine'),
       (5, 'Lithuania'),
       (6, 'Germany')
ON CONFLICT DO NOTHING;

INSERT INTO city(id, name, country_id, population)
VALUES (1, 'Warsaw', 1, 7000000),
       (2, 'Krakow', 1, NULL),
       (3, 'Wroclaw', 1, NULL),
       (4, 'Prague', 2, NULL),
       (5, 'Bratislava', 3, NULL),
       (6, 'Kyiv', 4, NULL),
       (7, 'Vilnus', 5, NULL),
       (8, 'Berlin', 6, 10000000),
       (9, 'Hamburg', 6, NULL)
ON CONFLICT DO NOTHING;

