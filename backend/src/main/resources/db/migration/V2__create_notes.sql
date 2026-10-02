-- Notas de ejemplo. organization_id aísla los datos de cada tenant.

CREATE TABLE notes (
    id uuid PRIMARY KEY,
    organization_id uuid NOT NULL REFERENCES organizations (id),
    title varchar(200) NOT NULL,
    body text NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL
);

CREATE INDEX idx_notes_organization_id ON notes (organization_id);
