package org.openlmis.shipment.dto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ShipmentLineItemDTOTest {

  private String orderId = "1";
  private String orderNumber = "123";
  private String facilityCode = "F001";
  private String programCode = "EM";
  private String productCode = "P001";
  private String quantityOrdered = "20";
  private String quantityShipped = "10";
  private String batch = "12323";
  private String cost = "100";
  private String substitutedProductCode = "";
  private String substitutedProductName = "";
  private String substitutedProductQuantityShipped = "";
  private String packSize = "";
  private String packedDate =  "12-10-2013";
  private String shippedDate = "14-09-2013";
  private String processingError = "";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfProductCodeIsMissing() throws Exception {


    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO(
       orderNumber ,
       Long.parseLong(orderId),
       facilityCode,
       programCode,
       null,
       quantityOrdered,
       quantityShipped,
       batch,
       cost,
       substitutedProductCode,
       substitutedProductName,
       substitutedProductQuantityShipped,
       packSize,
       packedDate,
       shippedDate,
       processingError
      );

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    shipmentLineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfOrderIdIsMissing() throws Exception {
    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO(
        orderNumber ,
        null,
        facilityCode,
        programCode,
        productCode,
        quantityOrdered,
        quantityShipped,
        batch,
        cost,
        substitutedProductCode,
        substitutedProductName,
        substitutedProductQuantityShipped,
        packSize,
        packedDate,
        shippedDate,
        processingError
    );

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    shipmentLineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfQuantityIsMissing() throws Exception {
    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO(
        orderNumber ,
        Long.parseLong(orderId),
        null,
        programCode,
        productCode,
        quantityOrdered,
        quantityShipped,
        batch,
        cost,
        substitutedProductCode,
        substitutedProductName,
        substitutedProductQuantityShipped,
        packSize,
        packedDate,
        shippedDate,
        processingError
    );

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    shipmentLineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldPopulateShipmentLineItemDTO() throws Exception {
    List<String> fieldsInOneRow = asList("OYELL_FVR00000123R", "P10", "2", "45", "12-10-2013", "14-09-2013");
    Collection<EDIFileColumn> shipmentFileColumns = asList(new EDIFileColumn("orderId",
      "label.order.id",
      true,
      true,
      1,
      ""), new EDIFileColumn("productCode", "label.product.code", true, true, 2, ""));

    ShipmentLineItemDTO shipmentLineItemDTO = ShipmentLineItemDTO.populate(fieldsInOneRow, shipmentFileColumns);

    assertThat(shipmentLineItemDTO.getOrderNumber(), is("OYELL_FVR00000123R"));
    assertThat(shipmentLineItemDTO.getProductCode(), is("P10"));
    assertThat(shipmentLineItemDTO.getCost(), is(nullValue()));
    assertThat(shipmentLineItemDTO.getPackedDate(), is(nullValue()));
    assertThat(shipmentLineItemDTO.getShippedDate(), is(nullValue()));
    assertThat(shipmentLineItemDTO.getQuantityShipped(), is(nullValue()));
  }
}
