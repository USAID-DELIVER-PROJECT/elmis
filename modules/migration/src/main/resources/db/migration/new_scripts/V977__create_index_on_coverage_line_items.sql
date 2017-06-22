CREATE INDEX i_vaccine_report_coverage_line_item_product_dose
  ON vaccine_report_coverage_line_items USING btree (doseid, productid, reportid);
/*
ALTER TABLE vaccine_reports
 ADD CONSTRAINT vaccine_report_period_facility UNIQUE (facilityId, periodId, programId);*/
