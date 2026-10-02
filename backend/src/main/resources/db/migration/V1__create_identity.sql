-- Identidad local. external_subject guarda el sub del IdP para poder reemplazar WorkOS más adelante.

CREATE TABLE organizations (
    id uuid PRIMARY KEY,
    external_id varchar(255) NOT NULL UNIQUE,
    name varchar(255) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE users (
    id uuid PRIMARY KEY,
    external_subject varchar(255) NOT NULL UNIQUE,
    email varchar(320),
    display_name varchar(255),
    created_at timestamptz NOT NULL
);

CREATE TABLE memberships (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users (id),
    organization_id uuid NOT NULL REFERENCES organizations (id),
    role varchar(100) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_memberships_user_org UNIQUE (user_id, organization_id)
);
