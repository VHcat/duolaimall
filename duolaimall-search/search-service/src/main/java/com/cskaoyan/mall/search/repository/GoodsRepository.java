package com.cskaoyan.mall.search.repository;

import com.cskaoyan.mall.search.model.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
