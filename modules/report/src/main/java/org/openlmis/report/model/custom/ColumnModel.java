package org.openlmis.report.model.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColumnModel {
  private String name;
  private String displayName;
  private String width;
  private String formatting;
  private String classification;
  private String pivotType = "month";
  private Boolean pivotRow = false;
  private Boolean pivotColumn = false;
  private Boolean visible = true;
  private Boolean pivotValue = false;
}
