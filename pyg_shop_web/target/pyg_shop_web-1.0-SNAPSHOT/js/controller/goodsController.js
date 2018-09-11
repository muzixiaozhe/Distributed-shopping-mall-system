 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,typeTemplateService,itemCatService,uploadService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
    $scope.marketable=['上架','下架']//上下架状态
	$scope.updateMarketable=function (id,marketable) {
		goodsService.updateMarketable(id,marketable).success(
			function (response) {
				if(response.success){
                    $scope.reloadList();
				}else {
					alert(response.message);
				}
            }
		);
    }
	$scope.itemCatList=[]
	$scope.findItemCate1List=function () {
		itemCatService.findAll().success(
			function (response) {
                for (var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
				}
            }
		);
    }

    //读取一级分类
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCate1List=response;
            }
		);
    }
    //读取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCate2List=response;
            }
        );
    });
	//读取三级分类
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCate3List=response;
            }
        );
    });
    //三级分类选择后  读取模板ID
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId=response.typeId;
            }
        );
    });
    //模板ID选择后  更新品牌列表
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate=response;//获取类型模板
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表
            }
        );
        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList=response;
            }
        );
    });
    //从集合中按照key查询对象
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for(var i=0;i<list.length;i++){
            if (list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }

	//定义商品实体
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]} //页面实体结构
	
	$scope.updateSpecAttribute=function ($event,name,value) {
		var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName',name);
		if (object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{//取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(object),1);
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
    }

    //创建SKU列表
	$scope.createItemList=function () {
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'1',isDefault:'0' } ];//初始
		var items=$scope.entity.goodsDesc.specificationItems;
		for (var i=0;i<items.length;i++){
            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
    }
    addColumn=function (list,name,value) {
		var newList=[]//新的集合
		for (var i=0;i<list.length;i++){
			var oldRow=list[i];
			for (var j=0;j<value.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[name]=value[j];
				newList.push(newRow);
			}
		}
		return newList;
    }
	
	//添加图片列表
	$scope.add_image_entity=function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片列表
	$scope.remove_image_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
    $scope.image_entity={}
	//上传图片
	$scope.uploadFile=function () {
        uploadService.uploadFile().success(
        	function (response) {
				if(response.success){
                    $scope.image_entity.url=response.message;//设置文件地址
				}else{
					alert(response.message)
				}
            }).error(function() {
            alert("上传发生错误");
        });
    }
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//查询实体 
	$scope.findOne=function(){
		var id=$location.search()['id'];//获取参数值
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){

				$scope.entity= response;
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                //规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //SKU列表规格列转换
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
    //根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function (name,value) {
       var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(items ,'attributeName',name);
        if (object!=null){
			if(object.attributeValue.indexOf(value)>=0){
				return true;
			}else{
				return false;
			}
		}else{
        	return false;
		}
    }
	
	//保存 
	$scope.save=function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
                alert(response.message);
				if(response.success){
		        	//跳转到商品管理页面
					location.href="goods.html";
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
