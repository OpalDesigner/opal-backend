-- V2__create_scene_models_table.sql
-- Place this in src/main/resources/db/migration/

CREATE TABLE IF NOT EXISTS scene_models (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- CREATE UNIQUE INDEX IF NOT EXISTS idx_scene_models_name ON scene_models(name);
