
insert into equipment_category (code, name) values ('BIOCHEMISTRY','Bio-Chemistry');
insert into equipment_category (code, name) values ('VIROLOGY','Virology');
insert into equipment_category (code, name) values ('HEAMATOLOGY','Heamatology');
insert into equipment_category (code, name) values ('BACTERIOLOGY','Bacteriology');
insert into equipment_category (code, name) values ('FLOWCYTOMETRY','Flowcytometry');



INSERT INTO equipment_functional_test_types (code, name, equipmentcategoryid) VALUES ('ELECTROLYTES', 'Electrolytes', (select id from equipment_category  where code = 'BIOCHEMISTRY'));
INSERT INTO equipment_functional_test_types (code, name, equipmentcategoryid) VALUES ('ANALYTES', 'Analytes', (select id from equipment_category  where code = 'BIOCHEMISTRY'));
INSERT INTO equipment_functional_test_types (code, name, equipmentcategoryid) VALUES ('VIROLOGY_TESTS', 'Virology Tests', (select id from equipment_category  where code = 'VIROLOGY'));
INSERT INTO equipment_functional_test_types (code, name, equipmentcategoryid) VALUES ('HEAMATOLOGY_TESTES', 'Heamatology Tests', (select id from equipment_category  where code = 'HEAMATOLOGY'));
INSERT INTO equipment_functional_test_types (code, name, equipmentcategoryid) VALUES ('FLOWCYTOMETRY_TESTS', 'Flowcytometry Tests', (select id from equipment_category  where code = 'FLOWCYTOMETRY'));
INSERT INTO equipment_functional_test_types (code, name, equipmentcategoryid) VALUES ('BACKTERIOLOGY_TESTS', 'Bacteriology Tests', (select id from equipment_category  where code = 'BACTERIOLOGY'));



insert into equipment_test_items (code, name, functionaltesttypeid) values ('FBC', 'FBC', (select id from equipment_functional_test_types where code ='HEAMATOLOGY_TESTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('ALT', 'ALT', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('AST', 'AST', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('CREATININE', 'CREATININE', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('GLUCOSE', 'GLUCOSE', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('UREA', 'UREA', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('CHOLESTEROL', 'CHOLESTEROL', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('GGT', 'GGT', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('TOTALBILIRUBIN', 'TOTAL BILIRUBIN', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('TRIGLYCERIDES', 'TRIGLYCERIDES', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('URICACID', 'URIC ACID', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('ALP', 'ALP', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('DIRECTBILIRUBIN', 'DIRECT BILIRUBIN', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('TOTALPROTEIN', 'TOTAL PROTEIN', (select id from equipment_functional_test_types where code ='ANALYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('SODIUM', 'Sodium(Na)', (select id from equipment_functional_test_types where code ='ELECTROLYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('POTASSIUM', 'Potassium(K)', (select id from equipment_functional_test_types where code ='ELECTROLYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('CHLORIDE', 'Chloride(Cl)', (select id from equipment_functional_test_types where code ='ELECTROLYTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('CD4', 'CD4', (select id from equipment_functional_test_types where code ='FLOWCYTOMETRY_TESTS'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('TB', 'TB', (select id from equipment_functional_test_types where code ='BACKTERIOLOGY_TESTS'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('VLEID', 'VL/EID', (select id from equipment_functional_test_types where code ='VIROLOGY_TESTS'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('HB', 'HB', (select id from equipment_functional_test_types where code ='HEAMATOLOGY_TESTES'));
insert into equipment_test_items (code, name, functionaltesttypeid) values ('ALBUMIN', 'ALBUMIN', (select id from equipment_functional_test_types where code ='ANALYTES'));




update equipment_types set categoryid = (select id from equipment_category where code ='BIOCHEMISTRY') where code = 'LAB030';
update equipment_types set categoryid = (select id from equipment_category where code ='BIOCHEMISTRY') where code = 'LAB032';
update equipment_types set categoryid = (select id from equipment_category where code ='BIOCHEMISTRY') where code = 'LAB033';
update equipment_types set categoryid = (select id from equipment_category where code ='BIOCHEMISTRY') where code = 'LAB041';
update equipment_types set categoryid = (select id from equipment_category where code ='FLOWCYTOMETRY') where code = 'EQP0101';
update equipment_types set categoryid = (select id from equipment_category where code ='FLOWCYTOMETRY') where code = 'EQP0102';
update equipment_types set categoryid = (select id from equipment_category where code ='FLOWCYTOMETRY') where code = 'EQP0103';
update equipment_types set categoryid = (select id from equipment_category where code ='FLOWCYTOMETRY') where code = 'LAB045';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB020';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB021';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB022';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB023';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB046';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB047';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB051';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB052';
update equipment_types set categoryid = (select id from equipment_category where code ='HEAMATOLOGY') where code = 'LAB053';
update equipment_types set categoryid = (select id from equipment_category where code ='VIROLOGY') where code = 'LAB04';