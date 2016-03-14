/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.service.lookup;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.repository.DonorRepository;
import org.openlmis.report.mapper.ReportRequisitionMapper;
import org.openlmis.report.mapper.lookup.*;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.model.params.UserSummaryParams;
import org.openlmis.report.model.report.OrderFillRateSummaryReport;
import org.openlmis.report.model.report.TimelinessReport;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Service
@NoArgsConstructor
public class ReportLookupService {
  @Autowired
  private TimelinessStatusReportMapper timelinessStatusReportMapper;
  @Autowired
  private OrderFillRateSummaryListMapper orderFillRateSummaryListMapper;
  @Autowired
  private UserSummaryExReportMapper userSummaryExReportMapper;

  @Autowired
  private RegimenReportMapper regimenReportMapper;

  @Autowired
  private ProductReportMapper productMapper;

  @Autowired
  private RequisitionGroupReportMapper rgMapper;

  @Autowired
  private ProductCategoryReportMapper productCategoryMapper;

  @Autowired
  private AdjustmentTypeReportMapper adjustmentTypeReportMapper;

  @Autowired
  private ConfigurationSettingService configurationService;

  @Autowired
  private ScheduleReportMapper scheduleMapper;

  @Autowired
  private ProgramReportMapper programMapper;

  @Autowired
  private FacilityTypeReportMapper facilityTypeMapper;

  @Autowired
  private GeographicZoneReportMapper geographicZoneMapper;

  @Autowired
  private FacilityLookupReportMapper facilityReportMapper;

  @Autowired
  private ProcessingPeriodReportMapper processingPeriodMapper;

  @Autowired
  private ProductGroupReportMapper productGroupReportMapper;

  @Autowired
  private RegimenCategoryReportMapper regimenCategoryReportMapper;

  @Autowired
  private ReportRequisitionMapper requisitionMapper;

  @Autowired
  private SupervisoryNodeReportMapper supervisoryNodeReportMapper;

  @Autowired
  private EquipmentTypeReportMapper equipmentTypeReportMapper;

  @Autowired
  private EquipmentReportMapper equipmentReportMapper;

  @Autowired
  private DonorRepository donorRepository;

  private UserSummaryParams userSummaryParam = null;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  @Autowired
  private FacilityLevelMapper levelMapper;

  @Autowired
  private CommaSeparator commaSeparator;

  @Autowired
  ConfigurationSettingService configurationSettingService;

  private static final String VACCINE_DATE_FORMAT = "yyyy-MM-dd";
  private static final String VACCINE_DATE_FORMAT_FOR_RANGE = "MMM-dd-yyyy";

  public List<Product> getAllProducts() {
    return productMapper.getAll();
  }


  public List<RegimenCategory> getAllRegimenCategory() {
    return regimenCategoryReportMapper.getAll();
  }

  public List<Regimen> getAllRegimens() {
    return regimenReportMapper.getAll();
  }

  public List<GeographicZone> getGeographicLevelById(Long geographicLevelId) {
    return geographicZoneMapper.getGeographicZoneByLevel(geographicLevelId);
  }

  public List<FlatGeographicZone> getFlatGeographicZoneList() {
    return geographicZoneMapper.getFlatGeographicZoneList();
  }

  public List<Regimen> getRegimenByCategory(Long regimenCategoryId) {
    return regimenReportMapper.getRegimenByCategory(regimenCategoryId);
  }

  public List<Product> getProductsActiveUnderProgram(Long programId) {
    if (configurationService.getBoolValue("ALLOW_PRODUCT_CATEGORY_PER_PROGRAM")) {
      return productMapper.getProductsForProgramPickCategoryFromProgramProduct(programId);
    }
    return productMapper.getProductsForProgram(programId);
  }

  public List<Product> getProductListByCategory(Integer programId, Integer categoryId) {
    if (categoryId == null || categoryId <= 0) {
      return productMapper.getAll();
    }
    return productMapper.getProductListByCategory(programId, categoryId);
  }

  public List<Product> getPushProgramProducts() {
    return productMapper.getPushProgramProducts();
  }

  public List<FacilityType> getFacilityTypes() {
    return facilityTypeMapper.getAll();
  }


  public List<FacilityType> getFacilityTypesForProgram(Long programId) {
    return facilityTypeMapper.getForProgram(programId);
  }

  public List<FacilityType> getFacilityLevels(Long programId, Long userId) {
    List<org.openlmis.core.domain.Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    facilities.add(facilityService.getHomeFacility(userId));

    String facilityIdString = StringHelper.getStringFromListIds(facilities);

    return facilityTypeMapper.getLevels(programId, facilityIdString);
  }

  public List<RequisitionGroup> getAllRequisitionGroups() {
    return this.rgMapper.getAll();
  }

  public List<RequisitionGroup> getRequisitionGroupsByProgramAndSchedule(int program, int schedule) {
    return this.rgMapper.getByProgramAndSchedule(program, schedule);
  }

  public List<RequisitionGroup> getRequisitionGroupsByProgram(int program) {
    return this.rgMapper.getByProgram(program);
  }

  public List<ProductCategory> getAllProductCategories() {
    return this.productCategoryMapper.getAll();
  }

  public List<ProductCategory> getCategoriesForProgram(int programId) {
    if (configurationService.getBoolValue("ALLOW_PRODUCT_CATEGORY_PER_PROGRAM")) {
      return this.productCategoryMapper.getForProgramUsingProgramProductCategory(programId);
    }
    return this.productCategoryMapper.getForProgram(programId);
  }

  public List<AdjustmentType> getAllAdjustmentTypes() {
    return adjustmentTypeReportMapper.getAll();
  }

  public List<Integer> getOperationYears() {
    int startYear = configurationService.getConfigurationIntValue(Constants.START_YEAR);
    Calendar calendar = Calendar.getInstance();
    int now = calendar.get(Calendar.YEAR);
    List<Integer> years = new ArrayList<>();

    if (startYear == 0 || startYear > now) {
      years.add(now);
      return years;
    }
    for (int year = startYear; year <= now; year++) {
      years.add(year);
    }
    return years;
  }

  public List<Object> getAllMonths() {

    return configurationService.getConfigurationListValue(Constants.MONTHS, ",");
  }

  public List<Program> getAllPrograms() {
    return programMapper.getAll();
  }

  public List<Program> getAllPrograms(Long userId) {
    return programMapper.getAllForUser(userId);
  }

  //It return all programs only with regimen
  public List<Program> getAllRegimenPrograms() {
    return programMapper.getAllRegimenPrograms();
  }

  public List<Program> getAllProgramsWithBudgeting() {
    return programMapper.getAllProgramsWithBudgeting();
  }

  public List<Schedule> getAllSchedules() {
    return scheduleMapper.getAll();
  }

  public List<org.openlmis.report.model.dto.GeographicZone> getAllZones() {
    return geographicZoneMapper.getAll();
  }


  public List<Facility> getFacilities(Long program, Long schedule, Long type, Long requisitionGroup, Long zone, Long userId) {
    // this method does not work if no program is specified
    if (program == 0L) {
      return null;
    }

    if (schedule == 0 && type == 0) {
      return facilityReportMapper.getFacilitiesByProgram(program, zone, userId);
    }

    if (type == 0 && requisitionGroup == 0) {
      return facilityReportMapper.getFacilitiesByProgramSchedule(program, schedule, zone, userId);
    }

    if (type == 0 && requisitionGroup != 0) {
      return facilityReportMapper.getFacilitiesByProgramScheduleAndRG(program, schedule, requisitionGroup, zone, userId);
    }

    if (requisitionGroup == 0 && type != 0) {
      return facilityReportMapper.getFacilitiesByProgramZoneFacilityType(program, zone, userId, type);
    }

    if (requisitionGroup == 0) {
      return facilityReportMapper.getFacilitiesByPrgraomScheduleType(program, schedule, type, zone, userId);
    }

    return facilityReportMapper.getFacilitiesByPrgraomScheduleTypeAndRG(program, schedule, type, requisitionGroup, zone);
  }


  public List<Facility> getFacilityByGeographicZoneTree(Long userId, Long zoneId, Long programId) {
    return facilityReportMapper.getFacilitiesByGeographicZoneTree(userId, zoneId, programId);
  }

  public List<Facility> getFacilityByGeographicZone(Long userId, Long zoneId) {
    return facilityReportMapper.getFacilitiesByGeographicZone(userId, zoneId);
  }

  public List<HashMap> getFacilitiesForNotifications(Long userId, Long zoneId) {
    return facilityReportMapper.getFacilitiesForNotifications(userId, zoneId);
  }

  public List<ProductGroup> getAllProductGroups() {
    return productGroupReportMapper.getAll();
  }


  public org.openlmis.core.domain.Facility getFacilityForRnrId(Long rnrId) {
    return requisitionMapper.getFacilityForRnrId(rnrId);
  }

  public List<Program> getAllUserSupervisedActivePrograms(Long userId) {
    return programMapper.getUserSupervisedActivePrograms(userId);
  }

  public List<Program> getUserSupervisedActiveProgramsBySupervisoryNode(Long userId, Long supervisoryNodeId) {
    return programMapper.getUserSupervisedActiveProgramsBySupervisoryNode(userId, supervisoryNodeId);
  }
  public List<Program> getUserSupervisedActiveProgramsBySupervisoryNode( Long supervisoryNodeId) {
    return programMapper.getUserSupervisedActiveAllProgramsBySupervisoryNode( supervisoryNodeId);
  }

  public List<SupervisoryNode> getAllSupervisoryNodesByUserHavingActiveProgram(Long userId) {
    return supervisoryNodeReportMapper.getAllSupervisoryNodesByUserHavingActiveProgram(userId);
  }


  public List<UserRoleAssignmentsReport> getAllRolesBySupervisoryNodeHavingProgram(Long roleId, Long programId, Long supervisoryNodeId) {
    return userSummaryExReportMapper.getUserRoleAssignments(roleId, programId, supervisoryNodeId);
  }

  public List<UserRoleAssignmentsReport> getUserRoleAssignments(Map<String, String[]> filterCriteria) {
    return userSummaryExReportMapper.getUserRoleAssignment(getReportFilterData(filterCriteria));
  }

  public UserSummaryParams getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      userSummaryParam = new UserSummaryParams();
      userSummaryParam.setRoleId(filterCriteria.get("roleId")==null || StringUtils.isBlank(filterCriteria.get("roleId")[0]) ? 0 : Long.parseLong(filterCriteria.get("roleId")[0])); //defaults to 0
      userSummaryParam.setProgramId(filterCriteria.get("programId")==null ||StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Long.parseLong(filterCriteria.get("programId")[0]));
      userSummaryParam.setSupervisoryNodeId(filterCriteria.get("supervisoryNodeId")==null||StringUtils.isBlank(filterCriteria.get("supervisoryNodeId")[0]) ? 0 : Long.parseLong(filterCriteria.get("supervisoryNodeId")[0]));
    }

    return userSummaryParam;
  }

  public List<EquipmentType> getEquipmentTypes() {
    return equipmentTypeReportMapper.getEquipmentTypeList();
  }


  public GeoZoneTree getGeoZoneTree(Long userId) {
    List<GeoZoneTree> zones = geographicZoneMapper.getGeoZonesForUser(userId);
    GeoZoneTree tree = geographicZoneMapper.getParentZoneTree();
    populateChildren(tree, zones);
    return tree;
  }

  public GeoZoneTree getGeoZoneTreeWithOutZones(Long programId) {
    List<GeoZoneTree> allGeozones = geographicZoneMapper.getGeoZones(programId);
    GeoZoneTree tree = geographicZoneMapper.getParentZoneTree();
    List<GeoZoneTree> zoneList = this.loadZoneList(tree, allGeozones);
    List<GeoZoneTree> regions = this.loadZoneChildren(zoneList, allGeozones);
    order(regions);

    for (int i = 0; i < regions.size(); i++) {
      populateChildren(regions.get(i), allGeozones);
    }
    tree.setChildren(regions);
    return tree;
  }


  private static void order(List<GeoZoneTree> zoneList) {

    Collections.sort(zoneList, new Comparator() {
      public int compare(Object o1, Object o2) {

        String x1 = ((GeoZoneTree) o1).getName();
        String x2 = ((GeoZoneTree) o2).getName();

        return x1.compareTo(x2);
      }

    });
  }

  public List<GeoZoneTree> loadZoneChildren(List<GeoZoneTree> zoneList, List<GeoZoneTree> geoSourceList) {
    List<GeoZoneTree> children = new ArrayList<>();
    for (GeoZoneTree t : geoSourceList) {
      for (GeoZoneTree zoneTree : zoneList) {
        if (t.getParentId() == zoneTree.getId()) {
          children.add(t);
        }
      }
    }
    return children;
  }

  public List<GeoZoneTree> loadZoneList(GeoZoneTree root, List<GeoZoneTree> geoSourceList) {
    List<GeoZoneTree> children = new ArrayList<>();
    for (GeoZoneTree t : geoSourceList) {

      if (t.getParentId() == root.getId()) {
        children.add(t);

      }
    }
    return children;
  }

  public GeoZoneTree getGeoZoneTree(Long userId, Long programId) {
    List<GeoZoneTree> zones = geographicZoneMapper.getGeoZonesForUserByProgram(userId, programId);
    GeoZoneTree tree = geographicZoneMapper.getParentZoneTree();
    populateChildren(tree, zones);
    return tree;
  }

  private void populateChildren(GeoZoneTree tree, List<GeoZoneTree> source) {
    // find children from the source
    List<GeoZoneTree> children = new ArrayList<>();
    for (GeoZoneTree t : source) {
      if (t.getParentId() == tree.getId()) {
        children.add(t);
      }
    }

    tree.setChildren(children);
    order(tree.getChildren());
    for (GeoZoneTree zone : tree.getChildren()) {
      populateChildren(zone, source);
    }
  }


  public List<OrderFillRateSummaryReport> getOrderFillRateSummary(Long programId, Long periodId, Long scheduleId, Long facilityTypeId, Long userId, Long zoneId, String status) {
    return orderFillRateSummaryListMapper.getOrderFillRateSummaryReportData(programId, periodId, scheduleId, facilityTypeId, userId, zoneId, status);
  }

  public List<ProductCategoryProductTree> getProductCategoryProductByProgramId(int programId) {

    List<ProductCategory> productCategory = this.productCategoryMapper.getForProgramUsingProgramProductCategory(programId);

    List<ProductCategoryProductTree> productCategoryProducts = productCategoryMapper.getProductCategoryProductByProgramId(programId);

    List<ProductCategoryProductTree> newTreeList = new ArrayList<ProductCategoryProductTree>();

    for (ProductCategory pc : productCategory) {

      ProductCategoryProductTree object = new ProductCategoryProductTree();
      object.setCategory(pc.getName());
      object.setCategory_id(pc.getId());

      for (ProductCategoryProductTree productCategoryProduct : productCategoryProducts) {

        if (pc.getId() == productCategoryProduct.getCategory_id()) {
          object.getChildren().add(productCategoryProduct);
        }
      }

      newTreeList.add(object);
    }
    return newTreeList;
  }

  public List<YearSchedulePeriodTree> getYearSchedulePeriodTree() {

    List<YearSchedulePeriodTree> yearSchedulePeriodTree = processingPeriodMapper.getYearSchedulePeriodTree();
    List<Schedule> schedules = scheduleMapper.getAll();

    List<Integer> years = getOperationYears();

    List<YearSchedulePeriodTree> yearList = new ArrayList<YearSchedulePeriodTree>();

    //add the year layer
    for (Integer year : years) {

      YearSchedulePeriodTree yearObject = new YearSchedulePeriodTree();
      yearObject.setYear(year.toString());

      // Add the schedule layer
      for (Schedule schedule : schedules) {

        YearSchedulePeriodTree scheduleObject = new YearSchedulePeriodTree();
        scheduleObject.setGroupname(schedule.getName());

        for (YearSchedulePeriodTree period : yearSchedulePeriodTree) {

          if (schedule.getId() == period.getGroupid() && period.getYear().equals(year.toString())) {
            scheduleObject.getChildren().add(period);
          }
        }

        yearObject.getChildren().add(scheduleObject);
      }

      yearList.add(yearObject);
    }

    return yearList;
  }

  public List<Equipment> getEquipmentsByType(Long equipmentType) {

    if (equipmentType == 0)
      return equipmentReportMapper.getEquipmentAll();
    else
      return equipmentReportMapper.getEquipmentsByType(equipmentType);

  }

  public List<Donor> getAllDonors() {
    return donorRepository.getAll();
  }

  public List<Schedule> getSchedulesByProgram(long program) {
    return scheduleMapper.getSchedulesForProgram(program);
  }

  public List<Facility> getFacilities(Long type) {
    return facilityReportMapper.getFacilitiesBytype(type);
  }

  public List<Facility> getFacilities(Map<String, String[]> filterCriteria, long userId) {
    List<Facility> facilitiesList = null;
//        (@Param("program") Long program, @Param("zone") Long zone, @Param("userId") Long userId, @Param("type") Long type);
    long program = 0;
    long zone;

    long type;
    program = StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Long.parseLong(filterCriteria.get("programId")[0]);
    zone = StringUtils.isBlank(filterCriteria.get("zoneId")[0]) ? 0 : Long.parseLong(filterCriteria.get("zoneId")[0]);
    type = StringUtils.isBlank(filterCriteria.get("facilityTypeId")[0]) ? 0 : Long.parseLong(filterCriteria.get("facilityTypeId")[0]);

    facilitiesList = this.facilityReportMapper.getFacilitiesByProgramZoneFacilityType(program, zone, userId, type);

    return facilitiesList;
  }


  public List<TimelinessReport> getTimelinessStatusData(Long programId, Long periodId, Long scheduleId, Long zoneId, String status) {
    return timelinessStatusReportMapper.getTimelinessStatusData(programId, periodId, scheduleId, zoneId, status);
  }

  public List<TimelinessReport> getFacilityRnRStatusData(Long programId, Long periodId, Long scheduleId, Long zoneId, String status, String facilityIds) {
    return timelinessStatusReportMapper.getFacilityRnRStatusData(programId, periodId, scheduleId, zoneId, status, facilityIds);
  }

  public List<TimelinessReport> getTimelinessReportingDates(Long periodId) {
    return timelinessStatusReportMapper.getTimelinessReportingDates(periodId);
  }

  public List<Product> getRmnchProducts() {
    return productMapper.getRmnchProducts();
  }

  public List<ProcessingPeriod> getLastPeriods(Long programId) {
    return processingPeriodMapper.getLastPeriods(programId);
  }


  public List<YearSchedulePeriodTree> getVaccineYearSchedulePeriodTree() {
    List<YearSchedulePeriodTree> yearSchedulePeriodTree = processingPeriodMapper.getVaccineYearSchedulePeriodTree();

    Set<String> years = new HashSet<>();
    Set<Schedule> schedules = new HashSet<>();
    Set<Integer> scheduleIds = new HashSet<>();
    for (YearSchedulePeriodTree periodTree : yearSchedulePeriodTree) {
      years.add(periodTree.getYear());
      if (!scheduleIds.contains(periodTree.getGroupid())) {
        scheduleIds.add(periodTree.getGroupid());
        schedules.add(new Schedule(periodTree.getGroupid(), periodTree.getGroupname(), null, null));
      }
    }

    List<YearSchedulePeriodTree> yearList = new ArrayList<>();

    //add the year layer
    for (String year : years) {

      YearSchedulePeriodTree yearObject = new YearSchedulePeriodTree();
      yearObject.setYear(year);

      // Add the schedule layer
      for (Schedule schedule : schedules) {

        YearSchedulePeriodTree scheduleObject = new YearSchedulePeriodTree();
        scheduleObject.setGroupname(schedule.getName());

        for (YearSchedulePeriodTree period : yearSchedulePeriodTree) {

          if (schedule.getId() == period.getGroupid() && period.getYear().equals(year)) {
            scheduleObject.getChildren().add(period);
          }
        }
        if (scheduleObject.getChildren().size() > 0) {

          yearObject.getChildren().add(scheduleObject);
        }

      }

      yearList.add(yearObject);
    }

    return yearList;
  }

  public List<YearSchedulePeriodTree> getVaccineYearSchedulePeriodTreeWithoutSchedule() {
    List<YearSchedulePeriodTree> yearSchedulePeriodTree = processingPeriodMapper.getVaccineYearSchedulePeriodTree();


    return yearSchedulePeriodTree;
  }


  public Long getCurrentPeriodIdForVaccine() {
    return processingPeriodMapper.getCurrentPeriodIdForVaccine();
  }

  //New
  public List<FacilityLevelTree> getFacilityByLevel(Long programId, Long userId) {


    org.openlmis.core.domain.Facility homeFacility = facilityService.getHomeFacility(userId);

    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    List<org.openlmis.core.domain.RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);

    List<FacilityLevelTree> facilityLevels = levelMapper.getFacilitiesByLevel(programId, commaSeparator.commaSeparateIds(requisitionGroups));
    List<FacilityLevelTree> parentTree = levelMapper.getParentTree(programId, commaSeparator.commaSeparateIds(requisitionGroups));

    List<FacilityLevelTree> treeList = new ArrayList<FacilityLevelTree>();

    for (FacilityLevelTree fa : facilityLevels) {

      FacilityLevelTree facilityObject = new FacilityLevelTree();
      facilityObject.setSuperVisedFacility(fa.getSuperVisedFacility());
      facilityObject.setSuperVisedFacilityId(fa.getSuperVisedFacilityId());
      facilityObject.setParentId(fa.getParentId());
      facilityObject.setHomeFacilityName(homeFacility.getName());
      facilityObject.setFacilityId(homeFacility.getId());

      for (FacilityLevelTree tree : parentTree) {

        if ((tree.getParentId() == facilityObject.getParentId()) && (tree.getSuperVisedFacilityId() == facilityObject.getSuperVisedFacilityId())) {

          facilityObject.getChildren().add(tree);

        }

      }

      treeList.add(facilityObject);


    }


    return treeList;

  }

//End new

  public Map<String, Object> getCustomPeriodDates(Long period) {


    Map<String, Object> dates = new HashMap<String, Object>();

    if (period != null && period < 5 && period > 0) {
      DateTime
        sDate = periodStartDate(period),
        eDate = periodEndDate();

      String startDate = sDate.toString(VACCINE_DATE_FORMAT);
      String endDate = eDate.toString(VACCINE_DATE_FORMAT);
      String startDateString = sDate.toString(VACCINE_DATE_FORMAT_FOR_RANGE);
      String endDateString = eDate.toString(VACCINE_DATE_FORMAT_FOR_RANGE);

      if (startDate != null && endDate != null) {
        dates.put("startDate", startDate);
        dates.put("endDate", endDate);
        dates.put("startDateString", startDateString);
        dates.put("endDateString", endDateString);
      }
    }

    return dates;
  }

  public DateTime periodEndDate() {

    int currentDay = new DateTime().getDayOfMonth();

    Integer cutOffDays = configurationSettingService.getConfigurationIntValue("VACCINE_LATE_REPORTING_DAYS");

    boolean dateBeforeCutoff = cutOffDays != null && currentDay < cutOffDays;

    if (dateBeforeCutoff)
      return new DateTime().withDayOfMonth(1).minusMonths(1).minusDays(1);
    else
      return new DateTime().withDayOfMonth(1).minusDays(1);
  }

  public DateTime periodStartDate(Long range) {

    DateTime periodEndDate = periodEndDate();

    if (range == 1)
      return periodEndDate.withDayOfMonth(1);
    else if (range == 2)
      return periodEndDate.minusMonths(2).withDayOfMonth(1);
    else if (range == 3)
      return periodEndDate.minusMonths(5).withDayOfMonth(1);
    else if (range == 4)
      return periodEndDate.minusYears(1).withDayOfMonth(1);

    return null;

  }

  public List<Product> getProductsActiveUnderProgramWithoutDescriptions(Long programId) {
    if (configurationService.getBoolValue("ALLOW_PRODUCT_CATEGORY_PER_PROGRAM")) {
      return productMapper.getProductsForProgramPickCategoryFromProgramProductWDescriptions(programId);
    }
    return productMapper.getProductsForProgramWithoutDescriptions(programId);
  }
}
