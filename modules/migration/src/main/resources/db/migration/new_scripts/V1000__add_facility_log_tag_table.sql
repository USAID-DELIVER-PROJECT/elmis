
DROP TABLE IF EXISTS log_tag_facility_mappings;
CREATE TABLE log_tag_facility_mappings
(
  id serial NOT NULL,
  facilityId integer,
  serialnumber character varying(100),
  description character varying(100),
  createddate timestamp without time zone,
  CONSTRAINT log_tag_facility_mappings_pkey PRIMARY KEY (id),
    CONSTRAINT log_tag_facility_mappings_facilityId_fkey FOREIGN KEY (facilityId)
      REFERENCES facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)