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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.stockmanagement.domain.*;
import org.openlmis.stockmanagement.repository.mapper.LotMapper;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineInventoryMapperIT {

    @Autowired
    private LotMapper lotMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private VaccineInventoryMapper mapper;

    private Lot defaultLot1;
    private Lot defaultLot2;
    private Product defaultProduct;

    @Before
    public void setup() {
        defaultProduct = make(a(ProductBuilder.defaultProduct));
        productMapper.insert(defaultProduct);
        Timestamp expirationDate1 = new Timestamp(DateUtil.parseDate("2022-12-12 12:12:12").getTime());
        defaultLot1 = new Lot();
        defaultLot1.setProduct(defaultProduct);
        defaultLot1.setLotCode("Code1");
        defaultLot1.setExpirationDate(expirationDate1);
        defaultLot1.setManufacturerName("Manufacturer1");
        lotMapper.insert(defaultLot1);

        Timestamp expirationDate2 = new Timestamp(DateUtil.parseDate("2025-12-12 12:12:12").getTime());
        defaultLot2 = new Lot();
        defaultLot2.setProduct(defaultProduct);
        defaultLot2.setLotCode("Code2");
        defaultLot2.setExpirationDate(expirationDate2);
        defaultLot2.setManufacturerName("Manufacturer2");

        lotMapper.insert(defaultLot2);
    }


    @Test
    public void shouldGetLotsByProductId() {

        List<Lot> lots = mapper.getLotsByProductId(defaultProduct.getId());
        assertEquals(lots.size(), 2);

    }


}
