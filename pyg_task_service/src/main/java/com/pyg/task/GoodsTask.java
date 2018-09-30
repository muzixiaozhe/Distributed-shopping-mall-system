package com.pyg.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class GoodsTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 刷新秒杀商品
     */
    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoods(){
        try {
            Set ids=redisTemplate.boundHashOps("seckillGoods").keys();
            List idList=new ArrayList(ids);
            TbSeckillGoodsExample example=new TbSeckillGoodsExample();
            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//已审核
            criteria.andStockCountGreaterThan(0);//剩余库存大于0
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            criteria.andEndTimeGreaterThan(new Date());
            criteria.andIdNotIn(idList);//排除缓存中已经有的商品
            List<TbSeckillGoods> tbSeckillGoods = seckillGoodsMapper.selectByExample(example);
            for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
                redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGood.getId(),tbSeckillGood);
            }
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
            System.out.println("已同步"+tbSeckillGoods.size()+"条数据------"+simpleDateFormat.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除缓存中过期的秒杀商品
     */
    @Scheduled(cron = "10 * * * * ?")
    public void removeSeckillGoods(){
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
            if (tbSeckillGoods.getEndTime().getTime()<System.currentTimeMillis()){
                seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);//保存到数据库中
                redisTemplate.boundHashOps("seckillGoods").delete(tbSeckillGoods.getId());//从缓存中移除
                System.out.println("移除"+tbSeckillGoods.getId());
            }
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

        System.out.println("已同步"+simpleDateFormat.format(new Date()));
    }
}
