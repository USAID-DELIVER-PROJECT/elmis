package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.domain.inventory.VoucherNumberCode;
import org.openlmis.vaccine.dto.BatchExpirationNotificationDTO;
import org.openlmis.vaccine.dto.VaccineDistributionAlertDTO;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryDistributionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@NoArgsConstructor
public class VaccineInventoryDistributionRepository {

    @Autowired
    VaccineInventoryDistributionMapper mapper;

    public List<Facility> getOneLevelSupervisedFacilities(Long facilityId) {
        return mapper.getOneLevelSupervisedFacilities(facilityId);
    }
    public Integer saveDistribution(VaccineDistribution vaccineDistribution) {
        return mapper.saveDistribution(vaccineDistribution);
    }

    public Integer updateDistribution(VaccineDistribution vaccineDistribution) {
        return mapper.updateDistribution(vaccineDistribution);
    }

    public Integer saveDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem) {
        return mapper.saveDistributionLineItem(vaccineDistributionLineItem);
    }

    public Integer updateDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem) {
        return mapper.updateDistributionLineItem(vaccineDistributionLineItem);
    }

    public Integer saveDistributionLineItemLot(VaccineDistributionLineItemLot vaccineDistributionLineItemLot) {
        return mapper.saveDistributionLineItemLot(vaccineDistributionLineItemLot);
    }

    public Integer updateDistributionLineItemLot(VaccineDistributionLineItemLot vaccineDistributionLineItemLot) {
        return mapper.updateDistributionLineItemLot(vaccineDistributionLineItemLot);
    }

    public VaccineDistribution getDistributionForFacilityByMonth(Long facilityId, int month, int year) {
        return mapper.getDistributionForFacilityByMonth(facilityId, month, year);
    }

    public VaccineDistribution getDistributionForFacilityByPeriod(Long facilityId, Long periodId) {
        return mapper.getDistributionForFacilityByPeriod(facilityId, periodId);
    }

    public ProcessingPeriod getSupervisedCurrentPeriod(Long facilityId, Long programId, Date distributionDate) {
        return mapper.getSupervisedCurrentPeriod(facilityId, programId, distributionDate);
    }

    public VaccineDistribution getById(Long id) {
        return mapper.getById(id);
    }

    public List<Lot> getLotsByProductId(Long productId) {
        return mapper.getLotsByProductId(productId);
    }

    public VaccineDistribution getDistributionByVoucherNumber(Long facilityId,String voucherNumber){
        return mapper.getDistributionByVoucherNumber(facilityId,voucherNumber);
    }
    public VaccineDistribution getAllDistributionsByVoucherNumber(Long facilityId,String voucherNumber){
        return mapper.getAllDistributionsByVoucherNumber(facilityId,voucherNumber);
    }

    public String getLastVoucherNumber()
    {
        return mapper.getLastVoucherNumber();
    }

    public VoucherNumberCode getFacilityVoucherNumberCode(Long facilityId)
    {
        return mapper.getFacilityVoucherNumberCode(facilityId);
    }

    public VaccineDistribution getAllDistributionsForNotification(Long facilityId)
    {
        return mapper.getAllDistributionsForNotification(facilityId);
    }
    public Long updateNotification(Long Id){
        return mapper.updateNotification(Id);
    }

    public VaccineDistribution getDistributionByToFacility(Long facilityId) {
        return mapper.getDistributionByToFacility(facilityId);
    }

    public Long getSupervisorFacilityId(Long facilityId) {
        return mapper.getSupervisorFacilityId(facilityId);
    }

    public List<VaccineDistributionAlertDTO>getPendingDistributionAlert(Long facilityId){
        return mapper.getPendingConsignmentAlert(facilityId);
    }
    public List<VaccineDistributionAlertDTO>getPendingNotificationFoLowerLevel(Long facilityId){
        return mapper.getPendingConsignmentToLowerLevel(facilityId);
    }
    public List<Facility> getFacilitiesSameType(Long facilityId, String query) {
        return mapper.getFacilitiesSameType(facilityId, query.toLowerCase());
    }

    public List<VaccineDistribution> getDistributionsByDate(Long facilityId, String date) {
        return mapper.getDistributionsByDate(facilityId, date);
    }

    public List<VaccineDistribution> getDistributionsByDateRange(Long facilityId, String date, String endDate,String distributionType) {
        return mapper.getDistributionsByDateRange(facilityId, date, endDate,distributionType);
    }

    public VaccineDistribution getDistributionByVoucherNumberIfExist(Long facilityId, String voucherNumber) {
        return mapper.getDistributionByVoucherNumberIfExist(facilityId, voucherNumber);
    }

    public List<BatchExpirationNotificationDTO> getBatchExpiryNotifications(Long facilityId){
        return mapper.getBatchExpiryNotifications(facilityId);
    }

    public VaccineDistribution getDistributionById(Long id) {
        return mapper.getDistributionById(id);
    }

    public List<VaccineDistribution> getDistributionsByDateRangeAndFacility(Long facilityId, String startDate, String endDate) {
        return mapper.getDistributionsByDateRangeAndFacility(facilityId, startDate,endDate);
    }

    public List<VaccineDistribution> getDistributionsByDateRangeForFacility(Long facilityId, String startDate, String endDate) {
        return mapper.getDistributionsByDateRangeForFacility(facilityId, startDate, endDate);
    }

    public List<VaccineDistribution> searchDistributionByDateRangeAndFacility(Long facilityId, String startDate, String endDate,String distributionType, String searchParam) {

        return mapper.searchDistributionAndFacilityByDateRange(facilityId,startDate,endDate,distributionType,searchParam);
    }
}
