package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by hassan on 7/5/17.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogTagParam extends BaseParam {

    private String startDate;

   private String endDate;

}
