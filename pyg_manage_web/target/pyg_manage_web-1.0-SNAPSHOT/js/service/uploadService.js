/*
app.service('uploadService',function ($http) {
    this.uploadFile=function () {
        var formData=new formData();
        formData.append("file",file.files[0])
        return $http({
                method:'post',
                url:'../upluad.do',
                data: formData,
                headers: {'Content-Type':undefined},
                transformRequest: angular.identity

        }
        )
    }
})*/
//文件上传服务层
app.service("uploadService",function($http){
    this.uploadFile=function(){
        var formData=new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }
});