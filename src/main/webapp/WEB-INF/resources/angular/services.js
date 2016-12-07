var angular;
var servizi = angular.module('zanardo.factories', []);

servizi.factory('AdHocRequestInterceptor', function () {
    var TOKEN = 'non_autorizzato';
    var AUTHORIZATION = '';
    var CONTENT_TYPE = '';
    return {
        request: function (config) {
            config.headers = {'authorization': AUTHORIZATION, 'parola_d_ordine': TOKEN, 'Content-Type': CONTENT_TYPE};
            return config;
        },
        definisciToken: function (token) {
            TOKEN = token;
        },
        definisciAutorizzazione: function (autorizzazione) {
            AUTHORIZATION = autorizzazione;
        },
        definisciContentType: function (content_type) {
            CONTENT_TYPE = content_type;
        }
    };
});


servizi.factory('CaricaDipendenti', function ($http, $q) {
    var lista = function () {

        var defer = $q.defer();
        $http.get('https://192.168.56.1:8250/dipendenti/1.0.0/').then(function (response) {
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

