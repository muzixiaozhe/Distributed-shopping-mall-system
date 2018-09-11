package com.pyg.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.pojo.TbSpecificationOptionExample.Criteria;
import com.pyg.sellergoods.service.SpecificationOptionService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationOptionServiceImpl implements SpecificationOptionService {

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecificationOption> findAll() {
		return specificationOptionMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecificationOption> page=   (Page<TbSpecificationOption>) specificationOptionMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSpecificationOption specificationOption) {
		specificationOptionMapper.insert(specificationOption);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSpecificationOption specificationOption){
		specificationOptionMapper.updateByPrimaryKey(specificationOption);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSpecificationOption findOne(Long id){
		return specificationOptionMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationOptionMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecificationOption specificationOption, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		Criteria criteria = example.createCriteria();
		
		if(specificationOption!=null){			
						if(specificationOption.getOptionName()!=null && specificationOption.getOptionName().length()>0){
				criteria.andOptionNameLike("%"+specificationOption.getOptionName()+"%");
			}
	
		}
		
		Page<TbSpecificationOption> page= (Page<TbSpecificationOption>)specificationOptionMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
