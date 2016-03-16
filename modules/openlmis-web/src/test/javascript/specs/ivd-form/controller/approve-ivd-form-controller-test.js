
describe('ApproveIvdFormController', function(){

  var scope, controller;
  var programs = [{id: 1, name: 'Program Name'}];
  beforeEach(module('openlmis'));
  beforeEach(inject(function($rootScope, $controller, ApprovalPendingIvds){
    scope = $rootScope.$new();
    $controller(ApproveIvdFormController, {$scope: scope, programs: programs });
  }));

  it('should work', function(){
    //expect(scope).isNot(null);
  });

  describe('Should select the first program if programs list is 1', function(){
  //  expect(scope).toEqual(1);
  });

  it('Should load pending orders', function (){
    //console.log('I am here');
  });
});
