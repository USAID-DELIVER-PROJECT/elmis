ALTER TABLE shipment_file_info
    ADD skippedShipmentLineItems JSON NOT NULL DEFAULT '[]',
    ADD orderProcessingExceptions JSON NULL,
    ADD hasSkippedLineItems BOOLEAN NOT NULL DEFAULT FALSE,
    ADD orderNumber VARCHAR(50);

