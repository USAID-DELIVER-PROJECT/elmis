/*
 *
 *  * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *  *
 *  * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

function RegimentTemplateModalInstanceController($scope, $modalInstance, items) {
    $scope.selectedDose = {};
    $scope.productCombination = items.productCombination;
    $scope.isDosage = items.isDosage;

    $scope.save = function (combinationConstituent) {
        var constituent = {};
        var constituentDosage = {dosageUnit: combinationConstituent.selectedDose,dosageFrequency:combinationConstituent.selectedFrequency,quantity:combinationConstituent.quantity};

        if($scope.isDosage===false) {
            constituent.constituentDosageList = [];
            constituent.constituentDosageList.push(constituentDosage);
            constituent.defaultDosage = constituentDosage;
            constituent.product = combinationConstituent.selectedProgramProduct.product;
            $scope.productCombination.combinationConstituentList.push(constituent);
        }else{
            $scope.productCombination.constituentDosageList.push(constituentDosage);
        }
        $modalInstance.close();
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}
