package com.pyg.mapper;

import com.pyg.pojo.TbSeckillOrder;
import com.pyg.pojo.TbSeckillOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbSeckillOrderMapper {
    int countByExample(TbSeckillOrderExample example);

    int deleteByExample(TbSeckillOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbSeckillOrder record);

    int insertSelective(TbSeckillOrder record);

    List<TbSeckillOrder> selectByExample(TbSeckillOrderExample example);

    TbSeckillOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbSeckillOrder record, @Param("example") TbSeckillOrderExample example);

    int updateByExample(@Param("record") TbSeckillOrder record, @Param("example") TbSeckillOrderExample example);

    int updateByPrimaryKeySelective(TbSeckillOrder record);

    int updateByPrimaryKey(TbSeckillOrder record);
}