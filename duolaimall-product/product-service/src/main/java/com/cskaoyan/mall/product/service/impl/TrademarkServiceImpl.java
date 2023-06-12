package com.cskaoyan.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.converter.dto.TrademarkConverter;
import com.cskaoyan.mall.product.converter.dto.TrademarkPageConverter;
import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.product.dto.TrademarkPageDTO;
import com.cskaoyan.mall.product.mapper.TrademarkMapper;
import com.cskaoyan.mall.product.model.Trademark;
import com.cskaoyan.mall.product.query.TrademarkParam;
import com.cskaoyan.mall.product.service.TrademarkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/08 16:36
 */
@Service
public class TrademarkServiceImpl implements TrademarkService {
    @Resource
    TrademarkMapper trademarkMapper;
    @Resource
    TrademarkPageConverter trademarkPageConverter;
    @Resource
    TrademarkConverter trademarkConverter;


    @Override
    public TrademarkDTO getTrademarkByTmId(Long tmId) {
        Trademark trademark = trademarkMapper.selectById(tmId);
        return trademarkConverter.trademarkPO2DTO(trademark);
    }


    @Override
    public TrademarkPageDTO getPage(Page<Trademark> pageParam) {
        QueryWrapper<Trademark> queryWrapper = new QueryWrapper<>();
        Page<Trademark> trademarkPage = trademarkMapper.selectPage(pageParam, queryWrapper);
        return trademarkPageConverter.tradeMarkPagePO2PageDTO(trademarkPage);
    }

    @Override
    public void save(TrademarkParam trademarkParam) {
        // 将TrademarkParam对象转化为Trademark对象
        Trademark trademark = trademarkConverter.trademarkParam2Trademark(trademarkParam);
        trademarkMapper.insert(trademark);
//        trademarkMapper.updateById(trademark);

    }

    @Override
    public void updateById(TrademarkParam trademarkParam) {
        // 将TrademarkParam对象转化为Trademark对象
        Trademark trademark = trademarkConverter.trademarkParam2Trademark(trademarkParam);
        trademarkMapper.updateById(trademark);
    }

    @Override
    public void removeById(Long id) {
        trademarkMapper.deleteById(id);
    }
}
