var angular;
var apiCtrls = angular.module('zanardo.controllers', []);


apiCtrls.controller('MainCtrl', ['$scope', '$http', '$state', 'AdHocRequestInterceptor', 'CaricaDipendenti', function ($scope, $http, $state, AdHocRequestInterceptor, CaricaDipendenti) {



        $scope.entra = function (login) {

            if (login.nome === login.password) {
                AdHocRequestInterceptor.definisciToken('chiamata_rest_ok');
                $state.go('home.autorizzato');
            }
            else {
                alert('Inserire e Username e Password uguali per entrare');
            }
        };
    }]);

apiCtrls.controller('AuthCtrl', ['$scope', '$http', '$state', 'AdHocRequestInterceptor', 'CaricaDipendenti', function ($scope, $http, $state, AdHocRequestInterceptor, CaricaDipendenti) {
        AdHocRequestInterceptor.definisciToken('chiamata_rest_ok');
        
        var ACCESS_TOKEN_OBJ;

        generaCodici();

        $scope.generaCodici = function () {
            generaCodici();
        };


        function generaCodici() {
            AdHocRequestInterceptor.definisciAutorizzazione('Basic WDlHRjBWTEVXWTFDZG5RbHdWODVJR1FuamNZYTpZb3FrR1JlQUFlQmJiNkZRUzlZeWVwbjRoUjBh');
            AdHocRequestInterceptor.definisciContentType('application/x-www-form-urlencoded');
            $http({
                method: 'POST',
                data: 'grant_type=client_credentials',
                url: 'https://192.168.56.1:8250/token'
            }).success(function (risposta) {
                ACCESS_TOKEN_OBJ = risposta;
                console.log('Oggetto: ' + ACCESS_TOKEN_OBJ.access_token);
                AdHocRequestInterceptor.definisciAutorizzazione('Bearer ' + ACCESS_TOKEN_OBJ.access_token);
                AdHocRequestInterceptor.definisciContentType('application/json');
                CaricaDipendenti.lista().then(function (output) {
                    $scope.dipendenti = output;
                }).catch(function () {
                    $log.error('');
                });
            });
        }
        ;


        ;

    }]);
