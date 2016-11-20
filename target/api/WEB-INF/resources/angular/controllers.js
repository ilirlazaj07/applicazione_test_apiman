var angular;
var apiCtrls = angular.module('zanardo.controllers', []);


apiCtrls.controller('MainCtrl', ['$scope', '$http', '$state', 'AdHocRequestInterceptor', 'CaricaDipendenti', function ($scope, $http, $state, AdHocRequestInterceptor, CaricaDipendenti) {



        $scope.entra = function (login) {
            if (login.nome === login.password) {

                AdHocRequestInterceptor.definisciToken('chiamata_rest_ok');
              
            }
            else {
                alert('Inserire e Username e Password uguali per entrare');
            }
        };
    }]);

apiCtrls.controller('AuthCtrl', ['$scope', '$http', 'AdHocRequestInterceptor', 'CaricaDipendenti', function ($scope, $http, AdHocRequestInterceptor, CaricaDipendenti) {

        AdHocRequestInterceptor.definisciToken('chiamata_rest_ok');

        $scope.show = false;

        var ACCESS_TOKEN_OBJ;

        generaCodici();

        $scope.generaCodici = function () {
            generaCodici();
            $scope.show = true;
        };

        $scope.refreshToken = function () {
            refreshToken();
            $scope.show = true;
        };


        function generaCodici() {
            AdHocRequestInterceptor.definisciAutorizzazione('Basic WDlHRjBWTEVXWTFDZG5RbHdWODVJR1FuamNZYTpZb3FrR1JlQUFlQmJiNkZRUzlZeWVwbjRoUjBh');
            AdHocRequestInterceptor.definisciContentType('application/x-www-form-urlencoded');
            $http({
                method: 'POST',
                data: 'grant_type=password&username=admin&password=admin', //grant_type=client_credentials
                url: 'https://192.168.56.1:8250/token'
            }).success(function (risposta) {
                aggiorna(risposta);
            });
        }
        ;


        function refreshToken() {
            AdHocRequestInterceptor.definisciAutorizzazione('Basic WDlHRjBWTEVXWTFDZG5RbHdWODVJR1FuamNZYTpZb3FrR1JlQUFlQmJiNkZRUzlZeWVwbjRoUjBh');
            AdHocRequestInterceptor.definisciContentType('application/x-www-form-urlencoded');
            $http({
                method: 'POST',
                data: 'grant_type=refresh_token&refresh_token=' + $scope.rt + '&scope=PRODUCTION',
                url: 'https://192.168.56.1:8250/token'
            }).success(function (risposta) {
                aggiorna(risposta);
            });
        }
        ;

        function aggiorna(risposta) {
            $scope.at = risposta.access_token;
            $scope.rt = risposta.refresh_token;
            $scope.sc = risposta.scope;
            $scope.tt = risposta.token_type;
            $scope.exin = risposta.expires_in;
            ACCESS_TOKEN_OBJ = risposta;
            AdHocRequestInterceptor.definisciAutorizzazione('Bearer ' + ACCESS_TOKEN_OBJ.access_token);
            AdHocRequestInterceptor.definisciContentType('application/json');
            CaricaDipendenti.lista().then(function (output) {
                $scope.dipendenti = output;
            }).catch(function () {
                $log.error('');
            });
        }
        ;

        ;

    }]);
