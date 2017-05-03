var tenant = angular.module('tenant', [ 'ngRoute']);

var tenant_1_url = "http://localhost:9090";
var tenant_2_url = "http://localhost:9091";
var tenant_3_url = "http://localhost:9092";


tenant.config(function($routeProvider) {
	$routeProvider.when("/tenant1", {
		templateUrl : "/templates/tenant1.html",
		controller : "tenant1_controller"
	}).when("/tenant2", {
		templateUrl : "/templates/tenant2.html",
		controller : "tenant2_controller"
	}).when("/tenant3", {
		templateUrl : "/templates/tenant3.html",
		controller : "tenant3_controller"
	});
});

/*tenant.controller("tenant", function($scope, $http) {
	
});*/

tenant.directive('fileModel', [ '$parse', function($parse) {

	console.log("control caught in directive");

	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			var model = $parse(attrs.fileModel);
			var modelSetter = model.assign;

			element.bind('change', function() {
				scope.$apply(function() {
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};
} ]);

tenant.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function(file, uploadUrl, cb) {
        var fd = new FormData();
        fd.append('file', file);
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
        .success(function(data){
        	cb(data);
        })
        .error(function(err){
        	console.log(err);
        	cb(err);
        });
    }
}]);

tenant.controller('myCtrl', [ '$scope', 'fileUpload',
		function($scope, fileUpload) {

			$scope.uploadFile = function(dest) {
				console.log("control caught in myCtrl controller");
				var file = $scope.myFile;
				var uploadUrl;
				if ( dest ==='t1') {
					uploadUrl = tenant_1_url;
				} else if ( dest === 't2' ) {
					uploadUrl = tenant_2_url;
				} else {
					uploadUrl = tenant_3_url;
				}
				 console.log(uploadUrl);
				fileUpload.uploadFileToUrl(file, uploadUrl + '/upload', function(data) {
					$scope.output = uploadUrl+data.path.substring(1);
				});
		};
} ]);


tenant.controller("tenant1_controller", function($scope, $http) {
	
	$scope.submit = function() {
		var TenantDetails = {
				"tenant" : "TenantOne",
				"grade" : $scope.grade,
				"comments" : $scope.comments
		};

		if (!($scope.grade)) {
			console.log("cant enter empty credentials");
		} else {
			$http({
				method : "POST",
				url : tenant_1_url + '/grade_submission',
				headers: {'Content-Type': 'application/json'},
				data : TenantDetails
			}).success(function(data) {
				if (data.statusCode === 200) {
					console.log("invalid entry received");
				} else {
					console.log("record has been inserted");
				}
			});
		}
	}
});

tenant.controller("tenant2_controller", function($scope, $http) {
	$scope.submit = function() {
		var TenantDetails = {
				"tenant" : "TenantTwo",
				"pointer_received" : $scope.pointer_received,
				"pointer_total" : $scope.pointer_total,
				"comments" : $scope.comments
		};
		if (!($scope.pointer_received) || !($scope.pointer_total)) {
			console.log("cant enter empty credentials");
		} else {
			$http({
				method : "POST",
				url : tenant_2_url + '/grade_submission',
				headers: {'Content-Type': 'application/json'},
				data : TenantDetails
			}).success(function(data) {
				if (data.statusCode === 200) {
					console.log("invalid entry received");
				} else {
					console.log("record has been inserted");
				}
			});
		}
	}
});

tenant.controller("tenant3_controller", function($scope, $http) {
	$scope.submit = function() {
		var TenantDetails = {
				"tenant" : "TenantThree",
				"completion_status" : $scope.status,
				"comments" : $scope.comments
		};
		if (!($scope.status)) {
			console.log("cant enter empty credentials");
		} else {
			$http({
				method : "POST",
				url : tenant_3_url + '/grade_submission',
				headers: {'Content-Type': 'application/json'},
				data : TenantDetails
			}).success(function(data) {
				if (data.statusCode === 200) {
					console.log("invalid entry received");
				} else {
					console.log("record has been inserted");
				}
			});
		}
	}
});
