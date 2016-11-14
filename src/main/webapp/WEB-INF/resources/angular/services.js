var angular;
var servizi = angular.module('zanardo.factories', []);

servizi.factory('AdHocRequestInterceptor', function () {
    var TOKEN = 'non_autorizzato';
    return {
        request: function (config) {
            config.headers = {'authorization':'Bearer 954b6519-c952-3045-a6d0-54b2b0b998eb', 'parola_d_ordine': TOKEN};
            return config;
        },
        definisciToken: function (autorizzato) {
            TOKEN = autorizzato;
        }

    };
});


servizi.factory('CaricaDipendenti', function ($http, $q) {
    var lista = function () {

        var defer = $q.defer();
        $http.get('https://192.168.1.4:8250/dipendenti/1.0.0').then(function (response) {
            defer.resolve(response.data);
        }, function (response) {
            defer.reject(response);
        });
        return defer.promise;
    };

    return {
        lista: lista
    };

});

