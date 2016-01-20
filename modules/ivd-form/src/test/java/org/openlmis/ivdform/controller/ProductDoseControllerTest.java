package org.openlmis.ivdform.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.ivdform.dto.ProductDoseDTO;
import org.openlmis.ivdform.dto.VaccineServiceConfigDTO;
import org.openlmis.ivdform.service.ProductDoseService;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductDoseControllerTest {

  @Mock
  ProductDoseService service;

  @InjectMocks
  ProductDoseController controller;

  @Test
  public void shouldGetProgramProtocol() throws Exception {
    VaccineServiceConfigDTO dto = new VaccineServiceConfigDTO();
    when(service.getProductDoseForProgram(1L)).thenReturn(dto);
    ResponseEntity<OpenLmisResponse> response = controller.getProgramProtocol(1L);
    assertThat(response.getBody().getData().get("protocol"), is(notNullValue()));
  }

  @Test
  public void shouldSave() throws Exception {
    VaccineServiceConfigDTO dto = new VaccineServiceConfigDTO();
    dto.setProtocols(new ArrayList<ProductDoseDTO>());
    doNothing().when(service).save(dto.getProtocols());

    ResponseEntity<OpenLmisResponse> response = controller.save(dto);

    verify(service).save(anyList());
    assertThat(dto, is(response.getBody().getData().get("protocol")));
  }
}