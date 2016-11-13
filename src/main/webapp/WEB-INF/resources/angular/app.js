angular.module('lispaTest', ['ngResource', 'ngAnimate', 'ngMdIcons', 'ui.bootstrap', 'ui.router', 'zanardo.factories', 'zanardo.controllers'])
        .config(['$urlRouterProvider', '$stateProvider', '$provide', '$httpProvider',
            function ($urlRouterProvider, $stateProvider, $provide, $httpProvider) {
                $urlRouterProvider.otherwise('/');
                $httpProvider.interceptors.push('AdHocRequestInterceptor');
                $stateProvider.state('home', {
                    abstract: true,
                    template: '<div ui-view></div>'
                }).state('home.login', {
                    url: '/',
                    templateUrl: 'resources/temp/login.html',
                    params: {
                    }
                }).state('home.autorizzato', {
                    url: '/utente_autenticato',
                    templateUrl: 'resources/temp/utente_autenticato.html',
                    params: {
                    }
                });

                $provide.decorator('$log', ['$delegate', function $logDecorator($delegate) {
                        var erroreOriginale = $delegate.error;
                        $delegate.error = function decoratedError(msg) {
                            var adesso = new Date();
                            msg = 'Connessione al API non riuscita | Data:  ' + adesso + ' . ' + msg;
                            erroreOriginale.apply($delegate, arguments);
                        };
                        return $delegate;
                    }]);

            }]);