var angular;
var zanardoCtrls = angular.module('zanardo.controllers', []);


zanardoCtrls.controller('MainCtrl', ['$scope', '$http', '$state', 'AdHocRequestInterceptor', 'CaricaDipendenti', function ($scope, $http, $state, AdHocRequestInterceptor, CaricaDipendenti) {

        $scope.entra = function (login) {

            if (login.nome === login.password) {
                AdHocRequestInterceptor.definisciToken('chiamata_rest_ok');
                $state.go('home.autorizzato');
            }
            else {
                alert('Inserire e username e password uguali per entrare');
            }
        };
    }]);

zanardoCtrls.controller('AuthCtrl', ['$scope', '$http', '$state', 'AdHocRequestInterceptor', 'CaricaDipendenti', function ($scope, $http, $state, AdHocRequestInterceptor, CaricaDipendenti) {

        AdHocRequestInterceptor.definisciToken('chiamata_rest_ok');
        CaricaDipendenti.lista().then(function (output) {
            $scope.dipendenti = output;
        }).catch(function () {
            $log.error('');
        });
    }]);
