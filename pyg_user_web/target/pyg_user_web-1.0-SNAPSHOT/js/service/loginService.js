//服务层
app.service('loginService',function($http){
	    	

	//获取用户姓名
	this.findLoginName=function () {
		return $http.get('../findLoginName.do?');
    }
});
