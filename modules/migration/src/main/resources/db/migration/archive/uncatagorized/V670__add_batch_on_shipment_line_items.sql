ALTER TABLE shipment_line_items
ADD batch varchar(500) NULL;

-- clean up the schema
ALTER TABLE shipment_line_items
DROP COLUMN concatenatedOrderId;

ALTER TABLE shipment_line_items
DROP COLUMN replacedProductCode;

-- clean up data

DELETE FROM shipment_file_columns
WHERE name = 'concatenatedOrderId';

DELETE FROM shipment_file_columns
WHERE name = 'replacedProductCode';

