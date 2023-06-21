package com.cskaoyan.mall.web.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.search.param.SearchParam;
import com.cskaoyan.mall.web.client.SearchApiClient;
import com.cskaoyan.mall.web.vo.BreadCrumbVO;
import com.cskaoyan.mall.web.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品列表接口
 * </p>
 *
 */
@Controller
@Slf4j
public class SearchController {

    @Autowired
    private SearchApiClient searchApiClient;

    /**
     * 列表搜索
     * @param searchParam
     * @return
     */
    @GetMapping("list.html")
    public String search(SearchParam searchParam, Model model) {
        log.info("enter {} for {}", SearchController.class.getSimpleName(), "search");
        // 调用商品服务获取查询结果
        Result<Map> result = searchApiClient.list(searchParam);

        // 记录拼接url；
        String urlParam = makeUrlParam(searchParam);

        //处理品牌条件回显
        String trademarkParam = this.makeTrademark(searchParam.getTrademark());
        //处理平台属性条件回显
        List<BreadCrumbVO> propsParamList = this.makeProps(searchParam.getProps());
        //处理排序
        OrderVO orderVO = this.dealOrder(searchParam.getOrder());

        // 将查询结果
        model.addAllAttributes(result.getData());
        // 查询参数
        model.addAttribute("searchParam",searchParam);
        // 拼接的请求路径字符串
        model.addAttribute("urlParam",urlParam);
        // 品牌面包屑
        model.addAttribute("trademarkParam",trademarkParam);
        // 规格参数面包屑
        model.addAttribute("propsParamList",propsParamList);
        // 排序参数
        model.addAttribute("order",orderVO);
        log.info("before render template {} for {}", "list/index", "search");
        return "list/index";
    }

    /**
     * 处理排序
     * @param order
     * @return
     */
    private OrderVO dealOrder(String order) {
        OrderVO orderVO = new OrderVO();
        // 格式：  1:desc  2:desc
        if(!StringUtils.isEmpty(order)) {
            //
            String[] split = StringUtils.split(order, ":");

            if (split != null && split.length == 2) {
                // 传递的哪个字段
                orderVO.setType(split[0]);
                // 升序降序
                orderVO.setSort(split[1]);
            }
        }else {
            orderVO.setType("1");
            orderVO.setSort("asc");
        }
        return orderVO;
    }

    /**
     * 处理平台属性条件回显
     * @param props
     * @return
     */
// 处理平台属性
    private List<BreadCrumbVO> makeProps(String[] props) {
        List<BreadCrumbVO> list = new ArrayList<>();
        // attrId:attrValue:attrName
        if (props!=null && props.length!=0){
            for (String prop : props) {
                String[] split = StringUtils.split(prop, ":");
                if (split!=null && split.length==3){
                    // 声明一个map
                    BreadCrumbVO breadCrumb = new BreadCrumbVO();
                    breadCrumb.setAttrId(split[0]);
                    breadCrumb.setAttrValue(split[1]);
                    breadCrumb.setAttrName(split[2]);
                    list.add(breadCrumb);
                }
            }
        }
        return list;
    }

    private String makeTrademark(String trademark) {
        if (!StringUtils.isEmpty(trademark)) {
            String[] split = StringUtils.split(trademark, ":");
            if (split != null && split.length == 2) {
                return "品牌：" + split[1];
            }
        }
        return "";
    }

    /*
        该方法根据当前的请求参数，生成请求url
     */
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder();
        // 判断关键字
        if (searchParam.getKeyword()!=null){
            urlParam.append("keyword=").append(searchParam.getKeyword());
        }
        // 判断一级分类
        if (searchParam.getFirstLevelCategoryId()!=null){
            urlParam.append("firstLevelCategoryId=").append(searchParam.getFirstLevelCategoryId());
        }
        // 判断二级分类
        if (searchParam.getSecondLevelCategoryId()!=null){
            urlParam.append("secondLevelCategoryId=").append(searchParam.getSecondLevelCategoryId());
        }
        // 判断三级分类
        if (searchParam.getThirdLevelCategoryId()!=null){
            urlParam.append("thirdLevelCategoryId=").append(searchParam.getThirdLevelCategoryId());
        }
        // 处理品牌
        if(searchParam.getTrademark()!=null){
            if (urlParam.length()>0){
                urlParam.append("&trademark=").append(searchParam.getTrademark());
            }
        }
        // 判断平台属性值
        if (null != searchParam.getProps()){
            for (String prop : searchParam.getProps()) {
                if (urlParam.length() > 0){
                    urlParam.append("&props=").append(prop);
                }
            }
        }
        return "list.html?" + urlParam.toString();
    }
}
