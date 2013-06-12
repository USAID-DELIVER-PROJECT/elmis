/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('program product controller', function () {

  beforeEach(module('openlmis.services'));
  var scope, ctrl, $httpBackend;
  var programProducts;

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    var program = [
      {"id": 1, "name": "program1"},
      {"id": 2, "name": "program2"}
    ];
    ctrl = $controller(ProgramProductController, {$scope: scope, programs: program});
    programProducts = [
      {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "abc", "createdDate": 1371014384494,
        "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true},
      {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "name", "createdDate": 1371014384494,
        "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true}
    ];
    scope.isaForm = {$error: { required: "" }};
  }));

  it('should get program products', function () {
    scope.programId = 1;
    $httpBackend.expectGET('/programProducts/programId/1.json').respond(200, {"PROGRAM_PRODUCT_LIST": programProducts});
    scope.loadProgramProducts();
    $httpBackend.flush();
    expect(scope.programProducts).toEqual(programProducts);
    expect(scope.filteredProducts).toEqual(programProducts);

  });

  it('should filter products', function () {
    scope.query = "abc";
    scope.programProducts = programProducts;
    scope.filterProducts();

    expect(scope.filteredProducts).toEqual([programProducts[0]]);
    expect(scope.filteredProducts.length).toEqual(1);
  });

  it('should set current program product to selected program product and enable modal', function () {
    var programProduct = {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "abc", "createdDate": 1371014384494,
      "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true}

    scope.showProductISA(programProduct);

    expect(scope.currentProgramProduct).toEqual(programProduct);
    expect(scope.programProductISAModal).toBeTruthy();
  });

  it('should set current program product to null and disable modal', function () {
    scope.clearAndCloseProgramProductISAModal();

    expect(scope.currentProgramProduct).toBeNull();
    expect(scope.programProductISAModal).toBeFalsy();
  });

  it("should highlight error when value is undefined", function () {
    scope.inputClass = true;
    var returnValue = scope.highlightRequired(undefined);

    expect(returnValue).toEqual("required-error");
  });

  it("should not highlight error when value is defined", function () {
    scope.inputClass = true;
    var returnValue = scope.highlightRequired("abc");
    expect(returnValue).toEqual(null);
  });

  it("should save program product ISA when", function () {
    var programProductISA = {"id": 1, "whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    scope.currentProgramProduct = {"id": 1, "programProductISA": programProductISA};

    $httpBackend.expectPOST('/programProducts/programProductISA/1.json', programProductISA).respond(200);

    scope.saveProductISA();
    $httpBackend.flush();
    expect(scope.message).toEqual("ISA saved successfully");
    expect(scope.programProductISAModal).toBeFalsy();
    expect(scope.error).toEqual("");
  });

  it("should not save ISA if required fields are not filled", function () {
    scope.isaForm.$error.required = true;

    scope.saveProductISA();

    expect(scope.inputClass).toBeTruthy();
    expect(scope.error).toEqual("Please fill required values");
    expect(scope.message).toEqual("");
  });

  it("should return true if all fields are entered for the formula", function () {
    var programProductISA = {"whoRatio": 2, "dosesPerYear": 23, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6};
    var returnValue = scope.isPresent(programProductISA);
    expect(returnValue).toBeTruthy();
  });

  it("should return false if atleast one field is not entered for the formula", function () {
    var programProductISA = {"whoRatio": 2, "dosesPerYear": undefined, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6};
    var returnValue = scope.isPresent(programProductISA);
    expect(returnValue).toBeFalsy();
  });


 it("should return correct formula when programProductISA and its properties are properly defined",function(){
   var programProductISA = {"whoRatio": 2, "dosesPerYear": 1, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6};
   var formula = scope.getFormula(programProductISA);
   expect(formula).toEqual("(population) * 2 * 1 * 47 / 12 * 45 + 6");
 })


  it("should return blank formula when programProductISA is not properly defined ",function(){
    spyOn(scope,"isPresent").andReturn(Boolean.false);
    var formula = scope.getFormula(undefined);
    expect(formula).toEqual(undefined);
  });

  it("should return calculated value based on formula",function(){
    var programProductISA = {"whoRatio": 2, "dosesPerYear": 1, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6};
    scope.population = 2;
    programProductISA.minimumValue = 2;

    var value = scope.population * programProductISA.whoRatio * programProductISA.dosesPerYear *
        programProductISA.wastageRate / 12 * programProductISA.bufferPercentage + programProductISA.adjustmentValue;

    scope.calculateValue(programProductISA);

    expect(scope.isaValue).toEqual(Math.ceil(value));
  });

  it("should not return calculated value if population is undefined", function () {
    var programProductISA = {"whoRatio": 2, "dosesPerYear": 1, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6};
    programProductISA.minimumValue = 2;

    scope.calculateValue(programProductISA);

    expect(scope.isaValue).toEqual(0);
  });

})