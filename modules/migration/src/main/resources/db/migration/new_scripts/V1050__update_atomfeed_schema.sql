
DROP TABLE IF EXISTS atomfeed.chunking_history;

CREATE TABLE atomfeed.chunking_history (
  id SERIAL PRIMARY KEY ,
  chunk_length bigint,
  start bigint NOT NULL
);


ALTER TABLE atomfeed.chunking_history OWNER TO postgres;

DROP TABLE IF EXISTS atomfeed.databasechangelog;

CREATE TABLE atomfeed.databasechangelog (
  id character varying(63) NOT NULL,
  author character varying(63) NOT NULL,
  filename character varying(200) NOT NULL,
  dateexecuted timestamp with time zone NOT NULL,
  orderexecuted integer NOT NULL,
  exectype character varying(10) NOT NULL,
  md5sum character varying(35),
  description character varying(255),
  comments character varying(255),
  tag character varying(255),
  liquibase character varying(20)
);


ALTER TABLE atomfeed.databasechangelog OWNER TO postgres;

CREATE TABLE atomfeed.databasechangeloglock (
  id integer NOT NULL,
  locked boolean NOT NULL,
  lockgranted timestamp with time zone,
  lockedby character varying(255)
);


ALTER TABLE atomfeed.databasechangeloglock OWNER TO postgres;

DROP TABLE IF EXISTS atomfeed.event_records ;

CREATE TABLE atomfeed.event_records (
  id SERIAL PRIMARY KEY ,
  uuid character varying(40),
  title character varying(255),
  "timestamp" timestamp with time zone DEFAULT CURRENT_TIMESTAMP(6),
  uri character varying(255),
  object character varying(1000),
  category character varying(255),
  date_created timestamp with time zone,
  tags character varying(255)
);


DROP TABLE IF EXISTS atomfeed.event_records_offset_marker;

CREATE TABLE atomfeed.event_records_offset_marker (
  id SERIAL PRIMARY KEY ,
  event_id integer,
  event_count integer,
  category character varying(255)
);

DROP TABLE IF EXISTS atomfeed.event_records_queue ;

CREATE TABLE atomfeed.event_records_queue (
  id SERIAL PRIMARY KEY ,
  uuid character varying(40),
  title character varying(255),
  "timestamp" timestamp with time zone DEFAULT CURRENT_TIMESTAMP(6),
  uri character varying(255),
  object character varying(1000),
  category character varying(255),
  tags character varying(255)
);

CREATE INDEX event_records_category_idx ON atomfeed.event_records USING btree (category);


