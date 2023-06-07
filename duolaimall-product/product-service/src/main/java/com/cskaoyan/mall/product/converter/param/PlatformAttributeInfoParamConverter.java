package com.cskaoyan.mall.product.converter.param;

import com.cskaoyan.mall.product.model.PlatformAttributeInfo;
import com.cskaoyan.mall.product.model.PlatformAttributeValue;
import com.cskaoyan.mall.product.query.PlatformAttributeParam;
import com.cskaoyan.mall.product.query.PlatformAttributeValueParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatformAttributeInfoParamConverter {

    PlatformAttributeInfo attributeInfoParam2Info(PlatformAttributeParam platformAttributeParam);

    PlatformAttributeValue attributeValueParam2AttributeValue(PlatformAttributeValueParam platformAttributeValueParam);

}
