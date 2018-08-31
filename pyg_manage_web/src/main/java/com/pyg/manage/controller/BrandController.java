package com.pyg.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    //添加
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
    //修改
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    //批量删除
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    //获取单个信息
    @RequestMapping("findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }
    //搜索分页查询
    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand tbBrand, int page, int rows){
        return brandService.findPage(tbBrand,page,rows);
    }
}
