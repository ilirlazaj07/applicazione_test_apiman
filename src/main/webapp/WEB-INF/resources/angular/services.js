var angular;
var zanardoFakt = angular.module('zanardo.factories', []);

zanardoFakt.factory('AdHocRequestInterceptor', function () {
    var TOKEN = 'non_autorizzato';
    return {
        request: function (config) {
            config.headers = {'parola_d_ordine': TOKEN};
            return config;
        },
        definisciToken: function (autorizzato) {
            TOKEN = autorizzato;
        }

    };
});


zanardoFakt.factory('CaricaDipendenti', function ($http, $q) {
    var lista = function () {

        var defer = $q.defer();
        $http.get('/api/servizi/dipendenti').then(function (response) {
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

