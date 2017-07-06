package org.openlmis.report.builder;

import org.openlmis.report.model.params.LogTagParam;

import java.util.Map;


/**
 * Created by hassan on 7/5/17.
 */
public class LogTagQueryBuilder {

    public static String getQuery(Map params){

        LogTagParam filter = (LogTagParam)params.get("filterCriteria");
        return " select * from log_tags where " +
                " createdDate >='"+filter.getStartDate()+"'::DATE AND createdDate <='"+filter.getEndDate()+"'::DATE";

    }

}
