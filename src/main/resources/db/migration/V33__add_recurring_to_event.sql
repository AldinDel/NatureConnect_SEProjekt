ALTER TABLE nature_connect.event
    ADD COLUMN is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN recurrence_start DATE,
    ADD COLUMN recurrence_end DATE;

CREATE TABLE nature_connect.event_recurrence_day (
                                                     event_id BIGINT NOT NULL,
                                                     day_of_week VARCHAR(10) NOT NULL,
                                                     CONSTRAINT fk_event_recurrence
                                                         FOREIGN KEY (event_id) REFERENCES nature_connect.event(id)
                                                             ON DELETE CASCADE
);
