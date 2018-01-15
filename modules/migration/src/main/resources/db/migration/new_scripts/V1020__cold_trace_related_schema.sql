CREATE TABLE equipment_daily_cold_trace_status
(
  id                   SERIAL PRIMARY KEY,
  serialNumber         VARCHAR(200) NOT NULL,
  equipmentInventoryId INT          NULL REFERENCES equipment_inventories (id),
  date                 DATE         NOT NULL,
  operationalStatusId  INT REFERENCES equipment_operational_status (id),
  minepisodetemp       NUMERIC      NOT NULL,
  maxepisodetemp       NUMERIC      NOT NULL,
  lowtemp              NUMERIC      NOT NULL,
  hightemp             NUMERIC      NOT NULL,
  remarks              VARCHAR(500) NULL,
  createdBy            INT,
  createdDate          DATE,
  modifiedBy           INT,
  modifiedDate         DATE
);