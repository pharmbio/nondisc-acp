'use strict';

(function() {

	var MODULE = angular.module('io.pharmb.service.ndcp.backend',
			[ 'ngRoute', 'ngResource', 'enMarkdown' ]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/io.pharmb.service.ndcp.backend/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/io.pharmb.service.ndcp.backend/main/htm/about.htm'});
		$routeProvider.otherwise('/');
	});
	
	MODULE.run( function($rootScope, $location) {
		$rootScope.alerts = [];
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
	});
	
	
	
	var mainProvider = function($scope, $http) {
		function log(d) {
			$scope.alerts.push( { type: 'danger', msg: 'Failed with ['+ d.status + '] '+ d.statusText });
		}
		$http.get('/rest/examples').then(
				function(d) {
					$scope.examples = d.data;
				}, log
		);
		$scope.execute = function(e) {
			switch(e.method) {
			case 'GET':
				$http.get(e.uri).then(
						function(d) { e.result=d.data; },log
				);
				break;
				
			case 'PUT':
				$http.put(e.uri, e.payload|| {}).then(
						function(d) { e.result=d.data; },log
				);
				break;
			case 'POST':
				console.log('Payload ' + e.payload );
				$http.post(e.uri, e.payload || {}).then(
						function(d) { e.result=d.data; },log
				);
				break;
			case 'DELETE':
				$http.delete(e.uri).then(
						function(d) { e.result=d.data; },log
				);
				break;
			}
		}
	}
	
})();