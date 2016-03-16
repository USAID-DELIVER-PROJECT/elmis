/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Services", function () {
  var httpMock, successStub, failureStub;


  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend) {
    httpMock = $httpBackend;
    successStub = jasmine.createSpy();
    failureStub = jasmine.createSpy();

  }));

  afterEach(function () {
    httpMock.verifyNoOutstandingExpectation();
    httpMock.verifyNoOutstandingRequest();
  });

  describe("ApproveRnrService", function () {

    var requisitionForApprovalService;

    beforeEach(inject(function (RequisitionForApproval) {
      requisitionForApprovalService = RequisitionForApproval;
    }));

    it('should GET R&Rs pending for approval', function () {
      var requisitions = {"rnr_list": []};
      httpMock.expect('GET', "/requisitions-for-approval.json").respond(requisitions);
      requisitionForApprovalService.get({}, function (data) {
        expect(data.rnr_list).toEqual(requisitions.rnr_list);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("SupplyLineSearchService", function () {

    var supplyLineSearchService;

    beforeEach(inject(function (SupplyLinesSearch) {
      supplyLineSearchService = SupplyLinesSearch;
    }));

    it('should GET searched supplyLines', function () {
      var supplyLinesResponse = {"supplyLines": [], "pagination": {}};
      httpMock.expect('GET', "/supplyLines/search.json").respond(supplyLinesResponse);
      supplyLineSearchService.get({}, function (data) {
        expect(data.supplyLines).toEqual(supplyLinesResponse.supplyLines);
        expect(data.pagination).toEqual(supplyLinesResponse.pagination);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("programProductsFilter", function () {

    var programProductsFilter;

    beforeEach(inject(function (ProgramProductsFilter) {
      programProductsFilter = ProgramProductsFilter;
    }));

    it('should filter program products', function () {
      var programProductsFilterResponse = {"programProducts": []};
      var programId = 1, facilityTypeId = 2;
      httpMock.expectGET('/programProducts/filter/programId/' + programId + '/facilityTypeId/' + facilityTypeId + '.json')
          .respond(200, {programProductList: programProductsFilterResponse});

      programProductsFilter.get({'programId': programId, 'facilityTypeId': facilityTypeId},
          function (data) {
            successStub();
            expect(data.programProductList).toEqual(programProductsFilterResponse);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      var programId = 1, facilityTypeId = 2;

      httpMock.expectGET('/programProducts/filter/programId/' + programId + '/facilityTypeId/' + facilityTypeId + '.json')
          .respond(404);

      programProductsFilter.get({'programId': programId, 'facilityTypeId': facilityTypeId},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("Facility type approved products", function () {

    var facilityTypeApprovedProducts;

    beforeEach(inject(function (FacilityTypeApprovedProducts) {
      facilityTypeApprovedProducts = FacilityTypeApprovedProducts;
    }));

    it('should GET searched FacilityTypeApprovedProducts', function () {
      var FacilityApprovedProductsResponse = {"facilityApprovedProducts": [], "pagination": {}};
      httpMock.expectGET("/facilityApprovedProducts.json").respond(200, FacilityApprovedProductsResponse);
      facilityTypeApprovedProducts.get({}, function (data) {
        expect(data.facilityApprovedProducts).toEqual(FacilityApprovedProductsResponse.facilityApprovedProducts);
        expect(data.pagination).toEqual(FacilityApprovedProductsResponse.pagination);
        successStub();
      }, function () {
        failureStub();
      });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while get', function () {
      httpMock.expectGET("/facilityApprovedProducts.json").respond(400);

      facilityTypeApprovedProducts.get({}, function () {
        successStub();
      }, function () {
        failureStub();
      });

      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });

    it('should add facility type approved product', function () {
      var successMessage = "Saved successfully";
      httpMock.expectPOST('/facilityApprovedProducts.json').respond(200, {"success": successMessage});

      facilityTypeApprovedProducts.save({}, {},
          function (data) {
            successStub();
            expect(data.success).toEqual(successMessage);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while post', function () {
      httpMock.expectPOST('/facilityApprovedProducts.json').respond(404);

      facilityTypeApprovedProducts.save({}, {},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });

    it('should update facility type approved product', function () {
      var successMessage = "Updated successfully";
      httpMock.expectPUT('/facilityApprovedProducts.json').respond(200, {"success": successMessage});

      facilityTypeApprovedProducts.update({}, {},
          function (data) {
            successStub();
            expect(data.success).toEqual(successMessage);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while updating', function () {
      httpMock.expectPUT('/facilityApprovedProducts.json').respond(404);

      facilityTypeApprovedProducts.update({}, {},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("Reports", function () {

    var reports;

    beforeEach(inject(function (Reports) {
      reports = Reports;
    }));

    it('should get report parameters', function () {
      var templateResponse = {"template": {}};
      var templateId = 1;
      httpMock.expectGET('/reports/' + templateId + '.json')
          .respond(200, {template: templateResponse});

      reports.get({'id': templateId},
          function (data) {
            successStub();
            expect(data.template).toEqual(templateResponse);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      var templateId = 1;

      httpMock.expectGET('/reports/' + templateId + '.json')
          .respond(404);

      reports.get({'id': templateId},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("ProductGroupService", function () {

    var productGroupService;

    beforeEach(inject(function (ProductGroups) {
      productGroupService = ProductGroups;
    }));

    it('should GET product groups', function () {
      var productGroups = {"groups": []};
      httpMock.expectGET("/products/groups.json").respond(200, productGroups);
      productGroupService.get({}, function (data) {
        expect(data.groups).toEqual(productGroups.groups);
        successStub();
      }, function () {
        failureStub();
      });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while get', function () {
      httpMock.expectGET("/products/groups.json").respond(400);

      productGroupService.get({}, function () {
        successStub();
      }, function () {
        failureStub();
      });

      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("ProductFormService", function () {

    var productFormService;

    beforeEach(inject(function (ProductForms) {
      productFormService = ProductForms;
    }));

    it('should GET product forms', function () {
      var productForms = {"forms": []};
      httpMock.expectGET("/products/forms.json").respond(200, productForms);
      productFormService.get({}, function (data) {
        expect(data.forms).toEqual(productForms.forms);
        successStub();
      }, function () {
        failureStub();
      });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while get', function () {
      httpMock.expectGET("/products/forms.json").respond(400);

      productFormService.get({}, function () {
        successStub();
      }, function () {
        failureStub();
      });

      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("DosageUnitService", function () {

    var dosageUnitService;

    beforeEach(inject(function (DosageUnits) {
      dosageUnitService = DosageUnits;
    }));

    it('should GET dosage Units', function () {
      var dosageUnits = {"units": []};
      httpMock.expectGET("/products/dosageUnits.json").respond(200, dosageUnits);
      dosageUnitService.get({}, function (data) {
        expect(data.forms).toEqual(dosageUnits.forms);
        successStub();
      }, function () {
        failureStub();
      });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while get', function () {
      httpMock.expectGET("/products/dosageUnits.json").respond(400);

      dosageUnitService.get({}, function () {
        successStub();
      }, function () {
        failureStub();
      });

      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("Products", function () {

    var products;

    beforeEach(inject(function (Products) {
      products = Products;
    }));

    it('should get product', function () {
      var productId = 1;
      httpMock.expectGET('/products/' + productId + '.json')
          .respond(200, {productDTO: {}});

      products.get({'id': productId},
          function (data) {
            successStub();
            expect(data.productDTO).toEqual({});
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      var productId = 1;

      httpMock.expectGET('/products/' + productId + '.json')
          .respond(404);

      products.get({'id': productId},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });

    it('should add Product', function () {
      var successMessage = "Saved successfully";
      httpMock.expectPOST('/products.json').respond(200, {"success": successMessage});

      products.save({}, {},
          function (data) {
            successStub();
            expect(data.success).toEqual(successMessage);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while post', function () {
      httpMock.expectPOST('/products.json').respond(404);

      products.save({}, {},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });

    it('should update Product', function () {
      var successMessage = "Saved successfully";
      httpMock.expectPUT('/products.json').respond(200, {"success": successMessage});

      products.update({}, {},
          function (data) {
            successStub();
            expect(data.success).toEqual(successMessage);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while update', function () {
      httpMock.expectPUT('/products.json').respond(404);

      products.update({}, {},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("Program", function () {

    var program;

    beforeEach(inject(function (Program) {
      program = Program;
    }));

    it('should get program', function () {
      httpMock.expectGET('/programs.json')
          .respond(200, {programs: []});

      program.get({},
          function (data) {
            successStub();
            expect(data.programs).toEqual([]);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      httpMock.expectGET('/programs.json')
          .respond(400, {});

      program.get({},
          function (data) {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("ProductCategoryService", function () {

    var productCategoryService;

    beforeEach(inject(function (ProductCategories) {
      productCategoryService = ProductCategories;
    }));

    it('should GET product categories', function () {
      var productCategories = {"categories": []};
      httpMock.expectGET("/products/categories.json").respond(200, productCategories);
      productCategoryService.get({}, function (data) {
        expect(data.categories).toEqual(productCategories.categories);
        successStub();
      }, function () {
        failureStub();
      });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while get', function () {
      httpMock.expectGET("/products/categories.json").respond(400);

      productCategoryService.get({}, function () {
        successStub();
      }, function () {
        failureStub();
      });

      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });
});