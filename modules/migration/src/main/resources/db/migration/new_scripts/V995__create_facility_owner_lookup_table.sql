
DROP TABLE if exists owners;
DROP SEQUENCE if exists owners_id_seq;

-- Sequence: public.downers_id_seq

CREATE SEQUENCE owners_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE owners_id_seq
  OWNER TO postgres;
  --Index: public.uc_owners_lower_code



-- Table: public.owners
CREATE TABLE owners
(
  id integer NOT NULL DEFAULT nextval('owners_id_seq'::regclass),
  code character varying NOT NULL,
  text character varying(50),
  displayorder integer,
  createddate timestamp without time zone DEFAULT now(),
  CONSTRAINT owners_pkey PRIMARY KEY (id),
  CONSTRAINT owners_code_key UNIQUE (code)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE owners
  OWNER TO postgres;
DROP INDEX if exists uc_owners_lower_code;

CREATE UNIQUE INDEX uc_owners_lower_code
  ON owners
  USING btree
  (lower(code::text) COLLATE pg_catalog."default");
