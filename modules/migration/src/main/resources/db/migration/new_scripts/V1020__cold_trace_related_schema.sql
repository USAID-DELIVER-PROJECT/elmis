DROP TABLE IF EXISTS equipment_daily_cold_trace_status;

CREATE TABLE equipment_daily_cold_trace_status
(
  id                   SERIAL PRIMARY KEY,
  serialNumber         VARCHAR(200) NOT NULL,
  equipmentInventoryId INT          NULL REFERENCES equipment_inventories (id),
  date                 DATE         NOT NULL,
  operationalStatusId  INT REFERENCES equipment_operational_status (id),
  mintemp              NUMERIC      NOT NULL,
  maxtemp              NUMERIC      NOT NULL,
  lowtempEpisode       NUMERIC      NOT NULL,
  hightempEpisode      NUMERIC      NOT NULL,
  remarks              VARCHAR(500) NULL,
  createdBy            INT,
  createdDate          DATE,
  modifiedBy           INT,
  modifiedDate         DATE
);