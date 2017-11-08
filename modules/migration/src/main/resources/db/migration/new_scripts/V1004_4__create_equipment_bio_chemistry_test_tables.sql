CREATE TABLE equipment_bio_chemistry_test_types
(
  id SERIAL PRIMARY KEY,
  code VARCHAR(60) NOT NULL,
  name TEXT,
  DisplayOrder INTEGER,
  CreatedDate TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);
CREATE UNIQUE INDEX equipment_bio_chemistry_test_types_code_uindex ON equipment_bio_chemistry_test_types (code);
COMMENT ON TABLE equipment_bio_chemistry_test_types IS 'Bio chemistry equipment test types';


INSERT INTO equipment_bio_chemistry_test_types (code, name, DisplayOrder)
    VALUES
      ('ELECTROLYTES', 'Electrolytes', 1),
      ('ANALYTES', 'Analytes', 2);



CREATE TABLE equipment_bio_chemistry_products
(
  id SERIAL PRIMARY KEY NOT NULL,
  name TEXT,
  displayorder INTEGER,
  createddate TIMESTAMP DEFAULT now(),
  code VARCHAR(60),
  testtypeid INTEGER,
  CONSTRAINT equipment_bio_chemistry_products_equipment_bio_chemistry_test_t
  FOREIGN KEY (testtypeid) REFERENCES equipment_bio_chemistry_test_types (id)
);
CREATE UNIQUE INDEX equipment_bio_chemistry_products_code_uindex
  ON equipment_bio_chemistry_products (code);



INSERT INTO equipment_bio_chemistry_products(testtypeid, name, code, displayorder)
    VALUES
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'AST', 'AST', 1),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'ALT', 'ALT', 2),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'CREATININE', 'CREATININE', 3),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'UREA', 'UREA', 4),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'URIC ACID', 'URIC_ACID', 5),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'GGT', 'GGT', 6),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'TRIGLYCERIDES', 'TRIGLYCERIDES', 7),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'GLUCOSE', 'GLUCOSE', 8),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'ALBUMIN', 'ALBUMIN', 9),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'TOTAL PROTEIN', 'TOTAL_PROTEIN', 10),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'TOTAL BILIRUBIN', 'TOTAL_BILIRUBIN', 11),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'DIRECT BILIRUBIN', 'DIRECT_BILIRUBIN', 12),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'CHOLESTEROL', 'CHOLESTEROL', 13),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'AMYLASE', 'AMYLASE', 14),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'HDL', 'HDL', 15),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'LDL', 'LDL', 16),
      ((select id from equipment_bio_chemistry_test_types where code = 'ANALYTES' LIMIT 1), 'CREATININE KINASE (CK)', 'CREATININE_KINASE_CK', 17),
      ((select id from equipment_bio_chemistry_test_types where code = 'ELECTROLYTES' LIMIT 1), 'SODIUM (Na)', 'SODIUM', 18),
      ((select id from equipment_bio_chemistry_test_types where code = 'ELECTROLYTES' LIMIT 1), 'POTASSIUM(K)', 'POTASSIUM', 19),
      ((select id from equipment_bio_chemistry_test_types where code = 'ELECTROLYTES' LIMIT 1), 'CHLORIDE (Cl)', 'CHLORIDE', 20),
      ((select id from equipment_bio_chemistry_test_types where code = 'ELECTROLYTES' LIMIT 1), 'Calcium (CA)', 'Calcium', 21);


CREATE TABLE equipment_bio_chemistry_tests (
  id SERIAL PRIMARY KEY,
  productid INTEGER,
  numberoftestes INTEGER,
  equipmentlineitemid INTEGER,
  createdby INTEGER,
  createddate TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modifiedby INTEGER,
  modifieddate TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  FOREIGN KEY (equipmentlineitemid) REFERENCES equipment_status_line_items (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);