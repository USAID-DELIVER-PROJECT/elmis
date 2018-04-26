
DROP TABLE IF EXISTS atomfeed.chunking_history;

CREATE TABLE atomfeed.chunking_history (
  id integer NOT NULL,
  chunk_length bigint,
  start bigint NOT NULL
);


ALTER TABLE atomfeed.chunking_history OWNER TO postgres;

CREATE SEQUENCE atomfeed.chunking_history_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE atomfeed.chunking_history_id_seq OWNER TO postgres;

ALTER SEQUENCE atomfeed.chunking_history_id_seq OWNED BY atomfeed.chunking_history.id;

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
  id integer NOT NULL,
  uuid character varying(40),
  title character varying(255),
  "timestamp" timestamp with time zone DEFAULT CURRENT_TIMESTAMP(6),
  uri character varying(255),
  object character varying(1000),
  category character varying(255),
  date_created timestamp with time zone,
  tags character varying(255)
);


ALTER TABLE atomfeed.event_records OWNER TO postgres;

CREATE SEQUENCE atomfeed.event_records_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE atomfeed.event_records_id_seq OWNER TO postgres;

ALTER SEQUENCE atomfeed.event_records_id_seq OWNED BY atomfeed.event_records.id;

DROP TABLE IF EXISTS atomfeed.event_records_offset_marker;

CREATE TABLE atomfeed.event_records_offset_marker (
  id integer NOT NULL,
  event_id integer,
  event_count integer,
  category character varying(255)
);


ALTER TABLE atomfeed.event_records_offset_marker OWNER TO postgres;

CREATE SEQUENCE atomfeed.event_records_offset_marker_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE atomfeed.event_records_offset_marker_id_seq OWNER TO postgres;

ALTER SEQUENCE atomfeed.event_records_offset_marker_id_seq OWNED BY atomfeed.event_records_offset_marker.id;


DROP TABLE IF EXISTS atomfeed.event_records_queue ;

CREATE TABLE atomfeed.event_records_queue (
  id integer NOT NULL,
  uuid character varying(40),
  title character varying(255),
  "timestamp" timestamp with time zone DEFAULT CURRENT_TIMESTAMP(6),
  uri character varying(255),
  object character varying(1000),
  category character varying(255),
  tags character varying(255)
);


ALTER TABLE atomfeed.event_records_queue OWNER TO postgres;

CREATE SEQUENCE atomfeed.event_records_queue_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE atomfeed.event_records_queue_id_seq OWNER TO postgres;

ALTER SEQUENCE atomfeed.event_records_queue_id_seq OWNED BY atomfeed.event_records_queue.id;

ALTER TABLE ONLY atomfeed.chunking_history ALTER COLUMN id SET DEFAULT nextval('atomfeed.chunking_history_id_seq'::regclass);


ALTER TABLE ONLY atomfeed.event_records ALTER COLUMN id SET DEFAULT nextval('atomfeed.event_records_id_seq'::regclass);


ALTER TABLE ONLY atomfeed.event_records_offset_marker ALTER COLUMN id SET DEFAULT nextval('atomfeed.event_records_offset_marker_id_seq'::regclass);

ALTER TABLE ONLY atomfeed.event_records_queue ALTER COLUMN id SET DEFAULT nextval('atomfeed.event_records_queue_id_seq'::regclass);

ALTER TABLE ONLY atomfeed.chunking_history
  ADD CONSTRAINT pk_chunking_history PRIMARY KEY (id);

ALTER TABLE ONLY atomfeed.databasechangelog
  ADD CONSTRAINT pk_databasechangelog PRIMARY KEY (id, author, filename);

ALTER TABLE ONLY atomfeed.databasechangeloglock
  ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);

ALTER TABLE ONLY atomfeed.event_records
  ADD CONSTRAINT pk_event_records PRIMARY KEY (id);

ALTER TABLE ONLY atomfeed.event_records_offset_marker
  ADD CONSTRAINT pk_event_records_offset_marker PRIMARY KEY (id);

ALTER TABLE ONLY atomfeed.event_records_queue
  ADD CONSTRAINT pk_event_records_queue PRIMARY KEY (id);

CREATE INDEX event_records_category_idx ON atomfeed.event_records USING btree (category);


