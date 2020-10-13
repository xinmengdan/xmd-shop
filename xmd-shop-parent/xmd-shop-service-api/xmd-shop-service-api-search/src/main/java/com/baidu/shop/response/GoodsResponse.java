package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GoodsResponse
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/21
 * @Version V1.0
 **/
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<BrandEntity> brandList;

    private List<CategoryEntity> categoryList;

    private Map<String, List<String>> specParamValueMap;

    public GoodsResponse(Long total, Long totalPage, List<BrandEntity> brandList, List<CategoryEntity> categoryList, List<GoodsDoc> goodsDocs,Map<String, List<String>> specParamValueMap){

        super(HTTPStatus.OK,HTTPStatus.OK + "",goodsDocs);
        this.total = total;
        this.totalPage = totalPage;
        this.brandList = brandList;
        this.categoryList = categoryList;
        this.specParamValueMap = specParamValueMap;

    }

    public Map<String, List<String>> getSpecParamValueMap() {
        return specParamValueMap;
    }

    public void setSpecParamValueMap(Map<String, List<String>> specParamValueMap) {
        this.specParamValueMap = specParamValueMap;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public List<BrandEntity> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<BrandEntity> brandList) {
        this.brandList = brandList;
    }

    public List<CategoryEntity> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryEntity> categoryList) {
        this.categoryList = categoryList;
    }
}
