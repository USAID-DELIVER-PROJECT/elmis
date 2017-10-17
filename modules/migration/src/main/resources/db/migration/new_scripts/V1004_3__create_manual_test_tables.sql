CREATE TABLE manual_test_types (
  id SERIAL PRIMARY KEY,
  code CHARACTER VARYING(60) NOT NULL,
  name TEXT,
  displayorder INTEGER,
  createddate TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);
CREATE UNIQUE INDEX bio_chemistry_equipment_test_types_code_uindex ON manual_test_types USING BTREE (code);
CREATE UNIQUE INDEX manual_test_types_code_uindex ON manual_test_types USING BTREE (code);
COMMENT ON TABLE manual_test_types IS 'Manual tests lookup not linked to equipment tests';



CREATE TABLE manual_test_line_item
(
  id SERIAL PRIMARY KEY,
  rnrid INTEGER,
  testtypeid INTEGER,
  testcount INTEGER,
  remark TEXT,
  createdby INTEGER,
  modifiedby INTEGER,
  createddate TIMESTAMP DEFAULT now(),
  modifieddate TIMESTAMP DEFAULT now(),
  CONSTRAINT manual_test_line_item_manual_test_types_id_fk FOREIGN KEY (testtypeid) REFERENCES manual_test_types (id),
  CONSTRAINT manual_test_line_item_requisitions_id_fk FOREIGN KEY (rnrid) REFERENCES requisitions (id)
);


INSERT INTO manual_test_types (code, name, DisplayOrder)
VALUES
  ('SYPHILIS_RPR', 'Syphilis (RPR)', 1),
  ('PREGNANCY_TEST', 'Pregnancy Test',2),
  ('HEPATITIS_B','Hepatitis B',3),
  ('CRYPTOCOCCAL_ANTIGEN','Cryptococcal Antigen',4),
  ('HIV_TEST', 'HIV Test',5),
  ('WIDAL_TEST', 'Widal Test',6),
  ('SICKLING_TEST', 'Sickling Test',7),
  ('BLOOD_GROUPING','Blood grouping',8);