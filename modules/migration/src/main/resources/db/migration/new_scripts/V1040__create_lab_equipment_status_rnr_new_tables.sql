ALTER TABLE equipment_types DROP COLUMN IF EXISTS categoryid;

DROP TABLE IF EXISTS equipment_test_type_operational_status;
DROP TABLE IF EXISTS equipment_test_item_tests;
DROP TABLE IF EXISTS equipment_test_items;
DROP TABLE IF EXISTS equipment_functional_test_types;
DROP TABLE IF EXISTS equipment_category;


CREATE TABLE equipment_category
(
	id serial,
	code character varying(60) NOT NULL,
	name text,
	createdby integer,
	createddate timestamp without time zone DEFAULT now(),
	modifiedby integer,
	modifieddate timestamp without time zone DEFAULT now(),
	CONSTRAINT equipment_category_pk PRIMARY KEY (id),
	CONSTRAINT equipment_category_code UNIQUE (code)
);


CREATE TABLE  equipment_functional_test_types
(
	id serial,
	code character varying(60) NOT NULL,
	name text,
	equipmentCategoryId integer,
	createdby integer,
	createddate timestamp without time zone DEFAULT now(),
	modifiedby integer,
	modifieddate timestamp without time zone DEFAULT now(),
	CONSTRAINT equipment_functional_test_types_pk PRIMARY KEY (id),
	CONSTRAINT equipment_functional_test_types_code UNIQUE (code),
	CONSTRAINT equipment_functional_test_types_equipment_category FOREIGN KEY (equipmentCategoryId)
	REFERENCES equipment_category (id) MATCH SIMPLE
);


CREATE TABLE  equipment_test_items
(
	id serial,
	code character varying(60),
	name text,
	displayorder integer,
	functionalTestTypeId integer,
	createdby integer,
	createddate timestamp without time zone DEFAULT now(),
	modifiedby integer,
	modifieddate timestamp without time zone DEFAULT now(),
	CONSTRAINT equipment_test_items_pkey PRIMARY KEY (id),
	CONSTRAINT equipment_test_items_code_functionalTestTypeId_unique UNIQUE (code, functionalTestTypeId),
	CONSTRAINT equipment_test_items_equipment_functional_test_types FOREIGN KEY (functionalTestTypeId)
	REFERENCES equipment_functional_test_types (id) MATCH SIMPLE
);


CREATE TABLE equipment_test_item_tests
(
  id serial,
  testitemid integer,
  numberoftestes integer,
  equipmentlineitemid integer,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT equipment_test_items_tests_pkey PRIMARY KEY (id),
  CONSTRAINT equipment_test_items_tests_equipmentlineitemid_fkey FOREIGN KEY (equipmentlineitemid)
      REFERENCES equipment_status_line_items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT equipment_test_items_tests_testitemid_fkey FOREIGN KEY (testitemid)
      REFERENCES equipment_test_items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE TABLE equipment_test_type_operational_status
(
  id serial,
  functionaltestypeid integer,
  nonfunctional boolean default false,
  daysoutofuse integer,
  equipmentlineitemid integer,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT equipment_test_type_operational_status_pkey PRIMARY KEY (id),
  CONSTRAINT equipment_test_type_operational_status_equipmentlineitemid_fkey FOREIGN KEY (equipmentlineitemid)
      REFERENCES equipment_status_line_items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT equipment_test_type_operational_status_functionaltestypeid_fkey FOREIGN KEY (functionaltestypeid)
      REFERENCES equipment_functional_test_types (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


ALTER TABLE equipment_types ADD COLUMN categoryid integer;
ALTER TABLE equipment_types
  ADD CONSTRAINT equipment_types_equipment_category_fkey FOREIGN KEY (categoryid)
      REFERENCES equipment_category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
