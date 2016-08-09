DROP INDEX IF EXISTS i_requisition_programs;
DROP INDEX IF EXISTS i_requisition_periods;
DROP INDEX IF EXISTS i_requisition_status;
DROP INDEX IF EXISTS i_requisition_line_item_productcodes;
DROP INDEX IF EXISTS i_product_codes;
DROP INDEX IF EXISTS i_districts;
DROP INDEX IF EXISTS i_requisition_program_period_status_index;


CREATE INDEX i_requisition_programs
  ON requisitions (programid);

CREATE INDEX i_requisition_periods
  ON requisitions (periodid);


CREATE INDEX  i_requisition_status
  ON requisitions (status);

CREATE INDEX i_requisition_line_item_productcodes
  ON requisition_line_items(productcode);

CREATE INDEX i_product_codes
  ON products (code);


CREATE INDEX i_districts
on facilities(geographiczoneid);

CREATE INDEX i_requisition_program_period_status_index
ON requisitions (programid, periodid, status);



