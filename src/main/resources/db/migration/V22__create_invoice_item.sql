CREATE TABLE nature_connect.invoice_item (
                                             id BIGSERIAL PRIMARY KEY,
                                             invoice_id BIGINT NOT NULL,
                                             equipment_id BIGINT NOT NULL,
                                             quantity INT NOT NULL,
                                             unit_price NUMERIC(12,2) NOT NULL,
                                             total_price NUMERIC(12,2) NOT NULL,
                                             CONSTRAINT fk_invoice_item_invoice
                                                 FOREIGN KEY (invoice_id)
                                                     REFERENCES nature_connect.invoice(id)
);
