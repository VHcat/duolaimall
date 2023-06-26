package com.cskaoyan.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.converter.dto.TrademarkConverter;
import com.cskaoyan.mall.product.converter.dto.TrademarkPageConverter;
import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.product.dto.TrademarkPageDTO;
import com.cskaoyan.mall.product.mapper.TrademarkMapper;
import com.cskaoyan.mall.product.model.Trademark;
import com.cskaoyan.mall.product.query.TrademarkParam;
import com.cskaoyan.mall.product.service.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrademarkServiceImpl implements TrademarkService {

    @Autowired
    private TrademarkMapper trademarkMapper;

    @Autowired
    TrademarkPageConverter pageConverter;

    @Autowired
    TrademarkConverter trademarkConverter;

    @Override
    public TrademarkDTO getTrademarkByTmId(Long tmId) {
        Trademark trademark = trademarkMapper.selectById(tmId);
        TrademarkDTO trademarkDTO = trademarkConverter.trademarkPO2DTO(trademark);
        return trademarkDTO;
    }



    @Override
    public TrademarkPageDTO getPage(Page<Trademark> pageParam) {
        // 根据商标id查询
        LambdaQueryWrapper<Trademark> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Trademark::getId);
        IPage<Trademark> page = trademarkMapper.selectPage(pageParam, queryWrapper);

        // PO -> DTO
        TrademarkPageDTO trademarkPageDTO = pageConverter.tradeMarkPagePO2PageDTO(page);

        return trademarkPageDTO;
    }

    @Override
    public void save(TrademarkParam trademarkParam) {
        Trademark trademark = trademarkConverter.trademarkParam2Trademark(trademarkParam);
        trademarkMapper.insert(trademark);
    }

    @Override
    public void updateById(TrademarkParam trademarkParam) {
        Trademark trademark = trademarkConverter.trademarkParam2Trademark(trademarkParam);
        trademarkMapper.updateById(trademark);
    }

    @Override
    public void removeById(Long id) {
        trademarkMapper.deleteById(id);
    }
}
