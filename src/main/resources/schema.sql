CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(50)         NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  VARCHAR(1000),
    requestor_id BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    description  VARCHAR(1000),
    is_available BOOLEAN     NOT NULL,
    owner_id     BIGINT      NOT NULL REFERENCES users (id),
    request_id   BIGINT UNIQUE REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL REFERENCES items (id),
    booker_id  BIGINT                      NOT NULL REFERENCES users (id),
    status     VARCHAR(50)                 NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      VARCHAR(1000),
    item_id   BIGINT                      NOT NULL REFERENCES items (id),
    author_id BIGINT                      NOT NULL REFERENCES users (id),
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);