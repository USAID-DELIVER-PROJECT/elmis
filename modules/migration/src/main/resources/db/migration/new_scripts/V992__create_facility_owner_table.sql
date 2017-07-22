DROP    TABLE  if EXISTS facility_owners;
DROP   SEQUENCE if EXISTS facility_owners_id_seq;

CREATE SEQUENCE facility_owners_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE facility_owners_id_seq
  OWNER TO postgres;


CREATE TABLE facility_owners
(
  id integer NOT NULL DEFAULT nextval('facility_owners_id_seq'::regclass),
  facilityid integer,
  ownerid integer,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  description character varying(1000),
  active boolean,
  CONSTRAINT facility_owners_pkey PRIMARY KEY (id),
  CONSTRAINT facility_owners_id_fkey FOREIGN KEY (id)
      REFERENCES facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT facility_owners_ownerid_fkey FOREIGN KEY (ownerid)
      REFERENCES facility_operators (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE facility_owners
  OWNER TO postgres;
