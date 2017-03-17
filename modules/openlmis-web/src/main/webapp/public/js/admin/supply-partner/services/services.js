var post = {post: {method: 'POST'}};

services.factory('SupplyPartners', function ($resource) {
    return $resource('/supply-partners.json', {}, {});
});

services.factory('CreateSupplyPartner', function ($resource) {
    return $resource('/supply-partners.json', {}, post);
});

services.factory('UpdateSupplyPartner', function ($resource) {
    return $resource('/supply-partners/:id.json', {id: '@id'}, update);
});

services.factory('SupplyPartner', function ($resource) {
  return $resource('/supply-partners/:id.json', {id: '@id'});
});

services.factory('FacilityListByCode', function($resource){
  return $resource('/supply-partners/facility-list.json', {}, post);
});

services.factory('ProductListByCode', function($resource){
  return $resource('/supply-partners/product-list.json', {}, post);
});