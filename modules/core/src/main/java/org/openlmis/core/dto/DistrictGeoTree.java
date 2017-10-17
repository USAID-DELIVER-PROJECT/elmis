package org.openlmis.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by hassan on 9/10/17.
 */
@Setter
@Getter
public class DistrictGeoTree {
    private Long id;
    private String text;
    private long facility;
    private List<DistrictGeoTree> nodes;
    private Long regionId;
}
