ALTER TABLE shipment_file_info
    ADD skippedShipmentLineItems JSON NOT NULL DEFAULT '[]',
    ADD orderProcessingExceptions TEXT NULL,
    ADD hasSkippedLineItems BOOLEAN NOT NULL DEFAULT FALSE,
    ADD orderNumber VARCHAR(50);

