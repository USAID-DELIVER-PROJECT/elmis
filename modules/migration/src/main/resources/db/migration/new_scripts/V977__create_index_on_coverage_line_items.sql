CREATE INDEX i_vaccine_report_coverage_line_item_product_dose
  ON vaccine_report_coverage_line_items USING btree (doseid, productid, reportid);
