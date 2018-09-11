package com.pyg.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.apache.zookeeper.data.Id;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    /**
     * 获取所有品牌
     * @return
     */
    @RequestMapping("findAll")
    public List<TbBrand> findAll(){
       return brandService.findAll();
    }
    /**
     * 分页
     */
    @RequestMapping("findPage")
    public PageResult findPage(int page, int rows){
        return brandService.findPage(page,rows);
    }
    /**
     * 添加数据
     */
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
    /**
     * 根据id获取单个数据
     */
    @RequestMapping("findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }
    /**
     * 删除数据
     */
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
    /**
     * 修改数据
     */
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
    /**
     * 查询分页
     */
    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand tbBrand,int page, int rows){
        //System.out.println(brandService.findPage(tbBrand,page,rows));
        return brandService.findPage(tbBrand,page,rows);
    }
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
