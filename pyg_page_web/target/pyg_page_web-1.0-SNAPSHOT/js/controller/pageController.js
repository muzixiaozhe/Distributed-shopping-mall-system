app.controller('pageController',function($scope,$http){
	$scope.num=1;//定义商品数量
	//修改商品数量
	$scope.addNum=function(value){
		$scope.num+=value;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	//加入购物车
	$scope.findAddOrder=function () {
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(
			function (response) {
				if (response.success){
					location.href="http://localhost:9107/cart.html";
				}else{
					alert(response.message);
				}
            }
		);
    }
	//规格
	$scope.spec_html={};
	//获取选择的规格
	$scope.addSpec=function(key,value){
		$scope.spec_html[key]=value;
		searchSku()//读取sku
		
	}
	//判断规格是否为选中
	$scope.ifSpec=function(key,value){
		if($scope.spec_html[key]==value){
			return true;
		}else{
			return false;
		}
	}
	//查询sku
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(evObject(skuList[i].spec,$scope.spec_html)){
				$scope.sku=skuList[i];
				return;
			}
			$scope.sku={'id':'0','title':'--------','price':'0'};//如果没有匹配的
		}
	}
	//判断
	evObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}
	//加载默认SKU
	$scope.loadSku=function(){
    $scope.sku=skuList[0];	
			
    $scope.spec_html= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	//$scope.spec_html=$scope.sku.spec;
	
	}
	//添加商品到购物车
	$scope.addToCart=function(){
		
	}
	
});
