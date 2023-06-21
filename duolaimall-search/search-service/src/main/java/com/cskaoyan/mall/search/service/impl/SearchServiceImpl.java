package com.cskaoyan.mall.search.service.impl;

import com.cskaoyan.mall.product.dto.CategoryHierarchyDTO;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.search.client.ProductApiClient;
import com.cskaoyan.mall.search.converter.GoodsConverter;
import com.cskaoyan.mall.search.dto.GoodsDTO;
import com.cskaoyan.mall.search.dto.SearchResponseDTO;
import com.cskaoyan.mall.search.model.Goods;
import com.cskaoyan.mall.search.model.SearchAttr;
import com.cskaoyan.mall.search.param.SearchParam;
import com.cskaoyan.mall.search.repository.GoodsRepository;
import com.cskaoyan.mall.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    ProductApiClient productApiClient;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void upperGoods(Long skuId) {
        Goods goods = new Goods();

        // 调用商品服务，获取skuId对应sku商品信息

        // 根据skuId获取sku基本信息(id, 默认图片，价格等)
        SkuInfoDTO skuInfo = productApiClient.getSkuInfo(skuId);
        // 设置skuId，作为文档的唯一表示
        goods.setId(skuInfo.getId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());

        // 根据skuId获取sku商品的平台属性集合
        List<PlatformAttributeInfoDTO> attrList = productApiClient.getAttrList(skuId);
        List<SearchAttr> searchAttrs = attrList.stream().map(platformAttributeInfoDTO -> {
            SearchAttr searchAttr = new SearchAttr();
            // 设置平台属性id
            searchAttr.setAttrId(platformAttributeInfoDTO.getId());

            // 设置平台属性的名称
            searchAttr.setAttrName(platformAttributeInfoDTO.getAttrName());

            // 设置平台属性值
            String valueName = platformAttributeInfoDTO.getAttrValueList().get(0).getValueName();
            searchAttr.setAttrValue(valueName);

            return searchAttr;
        }).collect(Collectors.toList());
        // 设置平台属性集合
        goods.setAttrs(searchAttrs);

        // 根据sku基本信息中的tmId，获取品牌信息
        TrademarkDTO trademark = productApiClient.getTrademark(skuInfo.getTmId());
        goods.setTmId(skuInfo.getTmId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());

        // 根据sku基本信息中的thirdLevelCategoryId获取三级类目信息
        CategoryHierarchyDTO categoryView = productApiClient.getCategoryView(skuInfo.getThirdLevelCategoryId());
        // 设置三级类目信息
        goods.setFirstLevelCategoryId(categoryView.getFirstLevelCategoryId());
        goods.setFirstLevelCategoryName(categoryView.getFirstLevelCategoryName());
        goods.setSecondLevelCategoryId(categoryView.getSecondLevelCategoryId());
        goods.setSecondLevelCategoryName(categoryView.getSecondLevelCategoryName());
        goods.setThirdLevelCategoryId(categoryView.getThirdLevelCategoryId());
        goods.setThirdLevelCategoryName(categoryView.getThirdLevelCategoryName());

        // 保存sku商品对应文档对象
        goodsRepository.save(goods);
    }

    @Override
    public void lowerGoods(Long skuId) {
        // 删除skuId对应的文档
        goodsRepository.deleteById(skuId);

        // 删除redis中商品对应的热度
        redissonClient.getScoredSortedSet("hotScore").remove("skuId:" + skuId);

    }


    @Override
    public void incrHotScore(Long skuId) {
        // 增加商品热度，每访问一次，sku商品文档的hotScore属性值就+1,
        // 注意商品的热度数据存储在redis中，从而实现更新的优化，比如每增加50，更新一次es里的文档热度
        String key = "hotScore";
        Double hotScore = redissonClient.getScoredSortedSet(key).addScore("skuId:" + skuId, 1);

        Optional<Goods> goodsOptional = goodsRepository.findById(skuId);
        if (hotScore.longValue() % 10 == 0) {
            goodsOptional.ifPresent(goods -> {
                // 修改热度
                goods.setHotScore(hotScore.longValue());
                // 更新热度
                goodsRepository.save(goods);
            });
        }

    }

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    GoodsConverter goodsConverter;

    @Override
    public SearchResponseDTO search(SearchParam searchParam) throws IOException {
        // 构建dsl语句
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildQueryDsl(searchParam);
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        SearchHits<Goods> searchResults = restTemplate.search(searchQuery, Goods.class);

        //解析查询结果
        SearchResponseDTO responseDTO = parseSearchResult(searchResults);


        //设置满足条件的总记录数
        responseDTO.setTotal(searchResults.getTotalHits());
        // 响应中设置一页的文档数量
        responseDTO.setPageSize(searchParam.getPageSize());
        // 响应中设置当前页数
        responseDTO.setPageNo(searchParam.getPageNo());
        // 计算总页数
        long totalPages = (responseDTO.getTotal() + searchParam.getPageSize() - 1) / searchParam.getPageSize();
        if (totalPages == 0) {
            // 前端的页数是从1开始的
            totalPages = 1;
        }
        responseDTO.setTotalPages(totalPages);
        return responseDTO;
    }

    // 制作dsl 语句
    private NativeSearchQueryBuilder buildQueryDsl(SearchParam searchParam) {
        // 构建查询器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // 构建boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 构造关键字查询参数
        buildKeyQuery(searchParam, boolQueryBuilder);
        // 构建品牌查询 trademark=2:华为
        buildTrademarkQuery(searchParam, boolQueryBuilder);

        // 构造类目查询
        buildCategoryQuery(searchParam, boolQueryBuilder);

        // 构建平台属性查询 23:4G:运行内存(可能有多个查询，这个例子只是一个平台属性或者叫规格参数)
        BuildSpecificQuery(searchParam, boolQueryBuilder);
        // 设置整个复合查询
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // 构建分页(注意，spring data elasticsearch的页从0开始，而前端的页从1开始，所以这里要减1)
        PageRequest pageRequest = PageRequest.of(searchParam.getPageNo() - 1, searchParam.getPageSize());
        nativeSearchQueryBuilder.withPageable(pageRequest);

        // 构造排序参数 order=1:desc  1为按热度排序，2为按照价格排序
        buildSort(searchParam, nativeSearchQueryBuilder);

        // 构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").postTags("</span>").preTags("<span style=color:red>");
        nativeSearchQueryBuilder.withHighlightBuilder(highlightBuilder);

        // 构造聚合参数
        buildAggregation(nativeSearchQueryBuilder);


        // 设置结果集过滤
        nativeSearchQueryBuilder.withFields("id", "defaultImg", "title", "price");

        return nativeSearchQueryBuilder;
    }

    private void buildAggregation(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        //  构造品牌Id父聚合
        // 构造品牌Id父聚合的子聚合: 品牌名称聚合
        // 构造品牌Id父聚合的子聚合: 品牌LogoUrl聚合

        // 添加品牌聚合

        //  构造平台属性嵌套聚合
        // 构造平台属性
        // 添加平台属性聚合

        // 构造平台属性Id子聚合：平台属性名称聚合

        //构造平台属性Id子聚合：平台属性值聚合
        // 添加平台属性嵌套聚合
    }

    private void buildSort(SearchParam searchParam, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 排序参数： order=1:desc 1为按照热度排序，2为按照价格排序，如果该属性没有值，默认按热度排序
        //           order=2:desc
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)) {
            // 判断排序规则
            String[] split = StringUtils.split(order, ":");
            if (split != null && split.length == 2) {
                String fieldName;
                if ("1".equals(split[0])) {
                    // 按热度排序
                    fieldName = "hotScore";

                } else {
                    //  按照价格排序
                    fieldName = "price";
                }
                FieldSortBuilder hotScoreSortBuilder
                        = SortBuilders.fieldSort(fieldName).order("desc".endsWith(split[1]) ? SortOrder.DESC : SortOrder.ASC);
                nativeSearchQueryBuilder.withSort(hotScoreSortBuilder);
            }
            return;
        }

        // 没有指定排序参数的值, 指定默认的排序参数
        FieldSortBuilder hotScoreSortBuilder
                = SortBuilders.fieldSort("hotScore").order(SortOrder.DESC );
        nativeSearchQueryBuilder.withSort(hotScoreSortBuilder);
    }

    private void BuildSpecificQuery(SearchParam searchParam, BoolQueryBuilder boolQueryBuilder) {
        // 23:4G:运行内存
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0) {
            // 循环遍历
            for (String prop : props) {
                // 针对每一个平台属性查询条件 23:4G:运行内存，构造一个nested查询，放入总的复合查询的filter条件中
                String[] split = StringUtils.split(prop, ":");
                if (split != null && split.length == 3) {

                    // 构造nested查询中，真正要执行的查询
                    BoolQueryBuilder queryOfNested = QueryBuilders.boolQuery();
                    TermQueryBuilder attrIdQuery = QueryBuilders.termQuery("attrs.attrId", split[0]);
                    queryOfNested.filter(attrIdQuery);
                    TermQueryBuilder attrValueQuery = QueryBuilders.termQuery("attrs.attrValue", split[1]);
                    queryOfNested.filter(attrValueQuery);

                    // 构造嵌套查询
                    NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", queryOfNested, ScoreMode.None);
                    // 添加到最终的查询中
                    boolQueryBuilder.filter(nestedQueryBuilder);
                }
            }
        }
    }

    private void buildCategoryQuery(SearchParam searchParam, BoolQueryBuilder boolQueryBuilder) {
        // thirdLevelCategoryId=61
        // 构建类目过滤 用户在点击的时候，只能点击一个值，所以此处使用term
        if (null != searchParam.getFirstLevelCategoryId()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("firstLevelCategoryId", searchParam.getFirstLevelCategoryId()));
        }
        // 构建分类过滤
        if (null != searchParam.getSecondLevelCategoryId()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("secondLevelCategoryId", searchParam.getSecondLevelCategoryId()));
        }
        // 构建分类过滤
        if (null != searchParam.getThirdLevelCategoryId()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("thirdLevelCategoryId", searchParam.getThirdLevelCategoryId()));
        }
    }

    private void buildTrademarkQuery(SearchParam searchParam, BoolQueryBuilder boolQueryBuilder) {
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)) {
            // 针对品牌查询参数 trademark=2:华为，构造filter过滤条件
            String[] split = StringUtils.split(trademark, ":");
            if (split != null && split.length == 2) {
                // 根据品牌Id构造查询条件，添加到总的boolQueryBuilder
                TermQueryBuilder tmId = QueryBuilders.termQuery("tmId", split[0]);
                // 放入最终的复合查询
                boolQueryBuilder.filter(tmId);


            }
        }
    }

    private void buildKeyQuery(SearchParam searchParam, BoolQueryBuilder boolQueryBuilder) {
        // 判断查询条件是否为空 关键字
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            // 小米手机 或者  小米and手机 添加到总的复合查询中的must或filter
            MatchQueryBuilder keywordQuery = QueryBuilders.matchQuery("title", searchParam.getKeyword());
            // 放入到最终的复合查询中
            boolQueryBuilder.must(keywordQuery);
        }
    }

    // 制作返回结果集
    private SearchResponseDTO parseSearchResult(SearchHits<Goods> hits) {

        //声明对象
        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();

        List<Goods> goodsList = new ArrayList<>();
        //赋值商品列表
        List<SearchHit<Goods>> searchHits = hits.getSearchHits();
        if (searchHits != null && searchHits.size() > 0) {
            //循环遍历
            for (SearchHit<Goods> subHit : searchHits) {
                Goods goods = subHit.getContent();

                //获取高亮
                if (subHit.getHighlightFields().get("title") != null) {
                    String title = subHit.getHighlightField("title").get(0);
                    // 设置高亮字符串
                    goods.setTitle(title);
                }
                goodsList.add(goods);
            }
        }
        List<GoodsDTO> goodsDTOs = goodsConverter.goodsPOs2DTOs(goodsList);
        // 设置查询到的目标页中的SKU商品数据
        searchResponseDTO.setGoodsList(goodsDTOs);

        // 获取品牌Id聚合

        // 根据品牌id聚合的每个桶，以及其中的两个子聚合的，获取每个品牌id，品牌名称，品牌logo，得到trademarkList
        // ...
        //searchResponseDTO.setTrademarkList(trademarkList);


        //获取嵌套聚合
        // 获取平台属性id聚合
        // 根据attrId聚合的每个桶，以及其中的两个子聚合的，获取每个平台属性id，平台属性名称，平台属性值，得到attrList
        // ....
        //searchResponseDTO.setAttrsList(attrList);


        return searchResponseDTO;
    }

}
