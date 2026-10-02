-- Registro de publicación de eventos de Spring Modulith.

CREATE TABLE event_publication (
    id uuid PRIMARY KEY,
    listener_id text NOT NULL,
    event_type text NOT NULL,
    serialized_event text NOT NULL,
    publication_date timestamptz NOT NULL,
    completion_date timestamptz,
    status varchar(32),
    completion_attempts integer NOT NULL DEFAULT 0,
    last_resubmission_date timestamptz
);

CREATE INDEX event_publication_by_completion_date_idx ON event_publication (completion_date);

CREATE TABLE event_publication_archive (
    id uuid PRIMARY KEY,
    listener_id text NOT NULL,
    event_type text NOT NULL,
    serialized_event text NOT NULL,
    publication_date timestamptz NOT NULL,
    completion_date timestamptz,
    status varchar(32),
    completion_attempts integer NOT NULL DEFAULT 0,
    last_resubmission_date timestamptz
);
