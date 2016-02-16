DO $$
BEGIN
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
BEGIN
DROP VIEW IF EXISTS vw_cce_repair_management_not_functional;
CREATE OR REPLACE VIEW vw_cce_repair_management_not_functional AS
 SELECT notfunctional.facilityid AS facilityId,
    notfunctional.facilityid AS fid,
	notfunctional.pid AS programId,
	notfunctional.model AS model,
	notfunctional.name AS operationalstatus,
	notfunctional.modifieddate AS modifieddate,
	notfunctional.modifiedby AS modifiedby,
	notfunctional.fname AS facilityname,
	notfunctional.isbad AS isbad,
    count(1) OVER (PARTITION BY notfunctional.notfunctionalstatusid, notfunctional.facilityid) AS notfunctionalstatuscount
   FROM ( SELECT DISTINCT ON (eis.inventoryid) eis.inventoryid,
            eis.notfunctionalstatusid,
            eos.name AS status,
            eosnf.name,
            eosnf.isbad,
            ei.facilityid,
            f.name as fname,
            ei.programid as pid,
            e.model as model,
            ei.modifieddate as modifieddate,
            ei.modifiedby as modifiedby
           FROM equipment_inventory_statuses eis
             LEFT JOIN equipment_operational_status eosnf ON eosnf.id = eis.notfunctionalstatusid
             LEFT JOIN equipment_operational_status eos ON eos.id = eis.statusid
             LEFT JOIN equipment_inventories ei ON ei.id = eis.inventoryid
             JOIN equipments e ON ei.equipmentid = e.id
             JOIN equipment_types et ON e.equipmenttypeid = et.id
             JOIN facilities f ON f.id = ei.facilityid
          WHERE et.iscoldchain IS TRUE
          ORDER BY eis.inventoryid, eis.createddate DESC) notfunctional WHERE name IS NOT NULL;

ALTER TABLE vw_cce_repair_management_not_functional
  OWNER TO postgres;
END;
BEGIN
DROP FUNCTION IF EXISTS fn_populate_alert_equipment_nonfunctional(integer);

CREATE OR REPLACE FUNCTION fn_populate_alert_equipment_nonfunctional(in_flag integer)
  RETURNS character varying AS

$BODY$
DECLARE
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;

BEGIN
msg := 'Success!!! fn_populate_equipment_nonfunctional - Data saved successfully' ;
DELETE FROM alert_equipment_nonfunctional;
FOR rec_detail IN
SELECT nf.facilityId, nf.programId, nf.model, nf.modifieddate,nf.modifiedby, nf.operationalstatus, nf.facilityname
FROM vw_cce_repair_management_not_functional nf
WHERE nf.isbad = FALSE
LOOP
INSERT INTO alert_equipment_nonfunctional(facilityId, programId, model,modifieddate, modifiedby, facilityname, status )
VALUES (rec_detail.facilityId, rec_detail.programId, rec_detail.model, rec_detail.modifieddate::Date,(select concat(firstname,' ',lastname) as modifiedby from users where id=rec_detail.modifiedby),rec_detail.facilityname,rec_detail.operationalstatus );
END LOOP;

RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'Error!!! fn_populate_equipment_nonfunctional.' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_equipment_nonfunctional(integer)
  OWNER TO postgres;
END;
END;
$$
