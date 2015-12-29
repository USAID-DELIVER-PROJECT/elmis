/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ShipmentLineItemTransformerTest {

  public static final String SIMPLE_DATE_FORMAT = "MM/dd/yyyy";

  @Rule
  public ExpectedException expectException = ExpectedException.none();

  private ShipmentLineItemTransformer transformer = new ShipmentLineItemTransformer();

  @Test
  public void shouldTrimAndParseFieldsWithSpaces() throws Exception {
    String orderNumberWithSpaces = " 11 ";
    String productCodeWithSpaces = " P111 ";
    String replacedProductCodeWithSpaces = " P151 ";
    String quantityShippedWithSpaces = " 22 ";
    String costWithSpaces = "21 ";
    String packedDateWithSpaces = " 10/10/2013 ";
    String shippedDateWithSpaces = "10/12/2013";

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO(orderNumberWithSpaces, productCodeWithSpaces, replacedProductCodeWithSpaces,
      quantityShippedWithSpaces, costWithSpaces, packedDateWithSpaces, shippedDateWithSpaces);

    ShipmentLineItem lineItem = new ShipmentLineItemTransformer().transform(dto, "MM/dd/yyyy", "MM/dd/yyyy", new Date());

    assertThat(lineItem.getProductCode(), is("P111"));
    assertThat(lineItem.getQuantityShipped(), is(22));
    assertThat(lineItem.getOrderId(), is(11L));
    assertThat(lineItem.getPackedDate().toString(), is("Thu Oct 10 00:00:00 IST 2013"));
    assertThat(lineItem.getShippedDate().toString(), is("Sat Oct 12 00:00:00 IST 2013"));
    assertThat(lineItem.getCost().toString(), is("21"));
    assertThat(lineItem.getSubstitutedProductCode(), is("P151"));
  }

  @Test
  public void shouldThrowErrorIfCostIsNegative() {
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();
    dto.setCost("-3333.33");

    expectException.expect(DataException.class);
    expectException.expectMessage("error.cost.negative");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfQuantityShippedIsMoreThanEightCharacters() {
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();
    dto.setQuantityShipped("123456789");

    expectException.expect(DataException.class);
    expectException.expectMessage("invalid.quantity.shipped");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfPackedDateIsDifferentFromFormat() {
    String packedDate = "10/10/2013 ";

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO("11", "P111", "P151",
      "12", "34", packedDate, "10/09/2013");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");
    new ShipmentLineItemTransformer().transform(dto, "MM/dd/yy", "MM/dd/yyyy", new Date());
  }

  @Test
  public void shouldThrowErrorIfShippedDateIsDifferentFromFormat() {
    String shippedDate = "10/10/13 ";

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO("11", "P111", "P151",
      "12", "34", "10/09/2013", shippedDate);

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");
    new ShipmentLineItemTransformer().transform(dto, "MM/dd/yyyy", "MM/dd/yyyy", new Date());
  }

  @Test
  public void shouldCreateLineItemIfOnlyMandatoryFieldsArePresent() throws Exception {
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();

    ShipmentLineItem lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertNull(lineItem.getShippedDate());
    assertNull(lineItem.getCost());
  }

  @Test
  public void shouldSetPackedDateToCreationDateIfPackedDateIsNull() {
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();

    Date ftpDate = new Date();
    ShipmentLineItem lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, ftpDate);

    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertThat(lineItem.getPackedDate(), is(ftpDate));
    assertNull(lineItem.getShippedDate());
    assertNull(lineItem.getCost());
  }

 /* @Test
  public void shouldThrowErrorForWrongRnrIdDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setOrderNumber("3333.33");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }*/

  @Test
  public void shouldThrowErrorForWrongQuantityShippedDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setQuantityShipped("E333");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorForWrongCostDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setCost("EE333");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorForWrongPackedDateDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setPackedDate("AAA");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorForWrongShippedDateDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setShippedDate("AAA");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfProductCodeIsMissing() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setProductCode(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("error.mandatory.fields.missing");
    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfOrderNumberIsMissing() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setOrderNumber(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("error.mandatory.fields.missing");
    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfQuantityShippedIsMissing() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setQuantityShipped(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("error.mandatory.fields.missing");
    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldCreateLineItemWithAllMandatoryAndGivenOptionalFields() throws Exception {
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();
    dto.setCost("3333.33");

    ShipmentLineItem lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertThat(lineItem.getCost().toString(), is("3333.33"));

    dto = dtoWithMandatoryFields();
    dto.setPackedDate("01/01/2011");
    lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
    assertThat(lineItem.getPackedDate().toString(), is("Sat Jan 01 00:00:00 IST 2011"));

    dto = dtoWithMandatoryFields();
    dto.setShippedDate("01/01/2012");
    lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
    assertThat(lineItem.getShippedDate().toString(), is("Sun Jan 01 00:00:00 IST 2012"));
  }

  private ShipmentLineItemDTO dtoWithMandatoryFields() {
    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();
    dto.setProductCode("P123");
    dto.setQuantityShipped("1234");
    dto.setOrderNumber("111");

    return dto;
  }

  private ShipmentLineItemDTO dtoWithAllFields() {
    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();

    dto.setProductCode("P123");
    dto.setQuantityShipped("1234");
    dto.setOrderNumber("111");
    dto.setCost("11");
    dto.setShippedDate("03/03/2012");
    dto.setPackedDate("03/03/2012");

    return dto;
  }
}
