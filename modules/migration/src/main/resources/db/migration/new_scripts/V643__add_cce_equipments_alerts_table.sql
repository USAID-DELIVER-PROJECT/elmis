DO $$
BEGIN
DROP TABLE IF EXISTS alert_equipment_nonfunctional;

CREATE TABLE alert_equipment_nonfunctional
(
  id serial NOT NULL,
  alertsummaryid integer,
  programid integer,
  periodid integer,
  facilityid integer,
  modifieddate date,
  model character varying(255),
  modifiedby character varying(255),
  facilityname character varying(255),
  status character varying(100),
  CONSTRAINT alert_equipment_nonfunctional_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alert_equipment_nonfunctional
  OWNER TO postgres;
END;
$$