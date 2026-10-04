ALTER TABLE tasks
    ADD COLUMN started_at TIMESTAMP NULL,
    ADD COLUMN completed_at TIMESTAMP NULL,
    ADD COLUMN actual_duration_hours DOUBLE PRECISION NULL;

CREATE TABLE ai_predictions (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    delay_probability DOUBLE PRECISION NOT NULL,
    completion_probability DOUBLE PRECISION NOT NULL,
    predicted_completion_at TIMESTAMP NULL,
    confidence_score DOUBLE PRECISION NOT NULL,
    model_version VARCHAR(100) NOT NULL,
    rationale VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_predictions_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_ai_predictions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NULL,
    action_type VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NULL,
    details VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);