package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.annotations.RequiredParam;
import org.openlmis.report.model.ReportParameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class VaccineStockStatusParam extends BaseParam implements ReportParameter {

    private String products;

    private String statusDate;

    private String facilityLevel;

    @RequiredParam
    private Long program;

    private String productCategory;


}
