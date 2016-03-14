/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

app.directive('divisibleByPresentation', ['messageService',function(messageService) {
    return{
            restrict: 'EA',
            require: 'ngModel',
            link: function(scope, element, attr, ctrl){

                  function divisibleByPresentationValidator(ngModelValue)
                  {
                     var presentation=attr.divisibleByPresentation;
                     if(presentation !==undefined)
                     {
                        if(ngModelValue ===undefined || (ngModelValue%presentation) ===0 )
                        {
                           ctrl.$setValidity('divisibleByPresentationValidator', true);
                        }
                        else if(ngModelValue !==undefined && (ngModelValue%attr.divisibleByPresentation) !==0){
                           ctrl.$setValidity('divisibleByPresentationValidator', false);
                           ctrl.$name=attr.name;
                        }
                        return ngModelValue;
                     }
                     else{
                        ctrl.$setValidity('divisibleByPresentationValidator', true);
                        return ngModelValue;
                     }

                  }
                  ctrl.$parsers.push(divisibleByPresentationValidator);
            }
        };
}]);
