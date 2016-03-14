/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.inventory.repositoty.mapper;

import org.apache.lucene.search.similarities.Distribution;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.repository.mapper.LotMapper;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryDistributionMapper;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineInventoryDistributionMapperIT {

    @Autowired
    private LotMapper lotMapper;

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private VaccineInventoryDistributionMapper mapper;

    @Autowired
    private ProcessingScheduleMapper scheduleMapper;

    @Autowired
    private ProcessingPeriodMapper periodMapper;

    private Lot defaultLot;
    private Facility defaultFacility;
    private Product defaultProduct;
    private VaccineDistribution defaultVaccineDistribution;
    private VaccineDistributionLineItem defaultDistributionLineItem;
    private VaccineDistributionLineItemLot defaultDistributionLineItemLot;
    private ProcessingSchedule defaultSchedule;
    private ProcessingPeriod defaultPeriod;

    @Before
    public void setup() {
        defaultFacility = make(a(FacilityBuilder.defaultFacility));
        defaultProduct = make(a(ProductBuilder.defaultProduct));
        defaultSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
        defaultPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod));

        facilityMapper.insert(defaultFacility);
        productMapper.insert(defaultProduct);
        defaultLot = new Lot();
        defaultLot.setProduct(defaultProduct);
        defaultLot.setLotCode("CODE");
        Timestamp date = new Timestamp(DateUtil.parseDate("2022-12-12 12:12:12").getTime());
        defaultLot.setExpirationDate(date);
        defaultLot.setManufacturerName("MANUFACTURER");
        lotMapper.insert(defaultLot);
        scheduleMapper.insert(defaultSchedule);
        defaultPeriod.setScheduleId(defaultSchedule.getId());
        periodMapper.insert(defaultPeriod);

    }


    private VaccineDistribution getDefaultDistribution() {
        defaultVaccineDistribution = new VaccineDistribution();
        defaultVaccineDistribution.setToFacilityId(defaultFacility.getId());
        defaultVaccineDistribution.setFromFacilityId(defaultFacility.getId());
        defaultVaccineDistribution.setVoucherNumber("VOUCHER NUMBER");
        Timestamp date = new Timestamp(DateUtil.parseDate("2022-12-12 12:12:12").getTime());
        defaultVaccineDistribution.setDistributionDate(date);
        defaultVaccineDistribution.setDistributionType("ROUTINE");
        defaultVaccineDistribution.setPeriodId(defaultPeriod.getId());
        defaultVaccineDistribution.setStatus("STATUS");
        return defaultVaccineDistribution;

    }

    private VaccineDistributionLineItem getDefaultLineItem() {
        defaultDistributionLineItem = new VaccineDistributionLineItem();
        VaccineDistribution distribution = getDefaultDistribution();
        mapper.saveDistribution(distribution);
        defaultDistributionLineItem.setDistributionId(distribution.getId());
        defaultDistributionLineItem.setProductId(defaultProduct.getId());
        defaultDistributionLineItem.setQuantity(1000L);
        return defaultDistributionLineItem;
    }

    private VaccineDistributionLineItemLot getDefaultLineItemLots() {
        defaultDistributionLineItemLot = new VaccineDistributionLineItemLot();
        VaccineDistributionLineItem lineItem = getDefaultLineItem();
        mapper.saveDistributionLineItem(lineItem);
        defaultDistributionLineItemLot.setDistributionLineItemId(lineItem.getId());
        defaultDistributionLineItemLot.setQuantity(1000L);
        defaultDistributionLineItemLot.setLotId(defaultLot.getId());
        defaultDistributionLineItemLot.setVvmStatus(1);

        return defaultDistributionLineItemLot;
    }


    @Test
    public void shouldSaveDistribution() {
        VaccineDistribution vaccineDistribution = getDefaultDistribution();
        Integer savedRow = mapper.saveDistribution(vaccineDistribution);
        assertEquals(savedRow.intValue(), 1L);
    }

    @Test
    public void shouldUpdateDistribution() {
        VaccineDistribution vaccineDistribution = getDefaultDistribution();
        Integer savedRow = mapper.saveDistribution(vaccineDistribution);
        vaccineDistribution.setStatus("NEW STATUS");
        Integer updatedRow = mapper.updateDistribution(vaccineDistribution);
        VaccineDistribution updatedDistribution = mapper.getById(vaccineDistribution.getId());
        assertEquals(savedRow.intValue(), 1L);
        assertEquals(updatedRow.intValue(), 1L);
        assertEquals(updatedDistribution.getStatus(), "NEW STATUS");
    }

    @Test
    public void shouldSaveDistributionLineItem() {
        VaccineDistributionLineItem distributionLineItem = getDefaultLineItem();
        Integer saveItems = mapper.saveDistributionLineItem(distributionLineItem);
        assertEquals(saveItems.intValue(), 1L);
    }

    @Test
    public void shouldUpdateDistributionLineItem() {
        VaccineDistributionLineItem distributionLineItem = getDefaultLineItem();
        Integer savedItems = mapper.saveDistributionLineItem(distributionLineItem);
        distributionLineItem.setQuantity(2000L);
        Integer updatedItem = mapper.updateDistributionLineItem(distributionLineItem);
        assertEquals(savedItems.intValue(), 1L);
        assertEquals(updatedItem.intValue(), 1L);
    }

    @Test
    public void shouldSaveDistributionLineItemLot() {
        VaccineDistributionLineItemLot distributionLineItemLot = getDefaultLineItemLots();
        Integer saveItems = mapper.saveDistributionLineItemLot(distributionLineItemLot);
        assertEquals(saveItems.intValue(), 1L);
    }

    @Test
    public void shouldUpdateDistributionLineItemLot() {
        VaccineDistributionLineItemLot distributionLineItemLot = getDefaultLineItemLots();
        Integer saveItemLot = mapper.saveDistributionLineItemLot(distributionLineItemLot);
        distributionLineItemLot.setQuantity(2000L);
        Integer updatedItemLot = mapper.updateDistributionLineItemLot(distributionLineItemLot);
        assertEquals(saveItemLot.intValue(), 1L);
        assertEquals(updatedItemLot.intValue(), 1L);
    }

    @Test
    public void shouldGetDistributionForFacilityByPeriod() {
        VaccineDistribution distribution = getDefaultDistribution();
        mapper.saveDistribution(distribution);
        VaccineDistributionLineItem lineItem = getDefaultLineItem();
        lineItem.setDistributionId(distribution.getId());
        mapper.saveDistributionLineItem(lineItem);
        VaccineDistributionLineItemLot lineItemLot = getDefaultLineItemLots();
        lineItemLot.setDistributionLineItemId(lineItem.getId());
        mapper.saveDistributionLineItemLot(lineItemLot);

        VaccineDistribution newDistribution = mapper.getDistributionForFacilityByPeriod(defaultFacility.getId(), defaultPeriod.getId());
        assertNotNull(newDistribution);
        assertNotNull(newDistribution.getLineItems());
        assertNotNull(newDistribution.getLineItems().get(0).getLots());

    }

}
