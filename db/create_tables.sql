create table users
(
    id       UUID PRIMARY KEY,
    username varchar(1024)        NOT NULL,
    email    varchar(1024) UNIQUE NOT NULL
);

create table user_credentials
(
    id         varchar(2048) PRIMARY KEY,
    public_key TEXT NOT NULL,
    user_id    UUID NOT NULL,
    type       text NOT NULL
);

CREATE TABLE webauthn_login_flow
(
    id               UUID PRIMARY KEY,
    start_request    TEXT,
    start_response   TEXT,
    assertion_request TEXT,
    assertion_result  TEXT,
    successful_login BOOLEAN
);
