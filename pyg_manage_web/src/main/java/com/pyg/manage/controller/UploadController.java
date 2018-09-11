package com.pyg.manage.controller;

import com.pyg.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("upload")
    public Result upload(MultipartFile file){
    //获取文件的扩展名
        String filename = file.getOriginalFilename();
        String substring = filename.substring(filename.lastIndexOf(".") + 1);
        try {
            //创建一个FastDFS客户端
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
            //执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(),substring);
            //拼接返回的url和ip地址
            String url=FILE_SERVER_URL+path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
