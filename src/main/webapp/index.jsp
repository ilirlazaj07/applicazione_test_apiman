<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         <script src="resources/angular/jquery-1.10.0.min.js" type="text/javascript"></script>
    <script src="resources/angular/angular.min.js" type="text/javascript"></script>
    <script src="resources/angular/services.js" type="text/javascript"></script>
    <script src="resources/angular/controllers.js" type="text/javascript"></script>
    <script src="resources/angular/angular-resource.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="resources/angular/angular-ui-router.js"></script>
    <script type="text/javascript" src="resources/angular/angular-animate.js"></script>
    <script src="resources/angular/app.js" type="application/javascript"></script>
    <link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <script src="resources/angular/angular-locale_it-it.js" type="text/javascript"></script>
    <script src="resources/angular/ui-bootstrap-tpls-1.3.3.min.js" type="text/javascript"></script>
    <script src="resources/angular/moment.js" type="text/javascript"></script>
    <script src="resources/angular/angular-material-icons.min.js" type="text/javascript"></script>
    <link href="resources/bootstrap/css/lomb_ver.css" rel="stylesheet" type="text/css">
        <title>Login</title>
    </head>
    <body ng-app="lispaTest" ng-controller="MainCtrl" style="">
    <nav class="navbar navbar-default area_utente"></nav>
      
    <div ui-view></div>
    
    
   <div id="footer" class="footer navbar-fixed-bottom text-center">
	<span>&copy; Copyright Regione Lombardia</span><span class="hidden-xs">
		- Tutti i diritti riservati</span>
	<!-- <nav class="voci_navigazione" role="navigation">
		<a href="#">Voce 01</a> <a href="#">Voce 02</a> <a href="#">Voce
			03</a> -->
	</nav>
</div>
    </body>
</html>
