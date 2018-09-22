 //控制层 
app.controller('loginController' ,function($scope,  loginService){
	

	
	//搜索
	$scope.findLoginName=function(){
		loginService.findLoginName().success(
			function(response){
				$scope.loginName=response.loginName;

			}			
		);
	}
    
});	
