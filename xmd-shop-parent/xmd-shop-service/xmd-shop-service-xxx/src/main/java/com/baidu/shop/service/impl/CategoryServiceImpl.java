package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.CategoryService;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/8/27
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;


    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> addCategory(CategoryEntity categoryEntity) {

        CategoryEntity parentCateEntity = new CategoryEntity();
        parentCateEntity.setId(categoryEntity.getParentId());
        parentCateEntity.setIsParent(1);

        categoryMapper.updateByPrimaryKeySelective(parentCateEntity);

        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess("新增成功");

    }

    @Transactional
    @Override
    public Result<JsonObject> editCategory(CategoryEntity categoryEntity) {

        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        return this.setResultSuccess("修改成功");

    }

    @Transactional
    @Override
    public Result<JsonObject> deleteCategory(Integer id) {

        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

        if (categoryEntity == null) {
            return this.setResultError("当前id不存在");
        }

        if(categoryEntity.getParentId() == 1){
            return this.setResultSuccess("当前节点为父节点 不能被删除");
        }



        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> list =  categoryMapper.selectByExample(example);


        //分类绑定商品
        Example example3 = new Example(SpuEntity.class);
        example3.createCriteria().andEqualTo("cid3",id);
        List<SpuEntity> list3 = spuMapper.selectByExample(example3);
        if(list3.size() > 0) return this.setResultError("分类绑定商品不能被删除");


        //分类绑定规格组 不能删除
        Example example1 = new Example(SpecGroupEntity.class);
        example1.createCriteria().andEqualTo("cid",id);
        List<SpecGroupEntity> list1 = specGroupMapper.selectByExample(example1);
        if(list1.size() == 1){
            return this.setResultError("分类绑定规格组不能删除");
        }

        //分类绑定品牌
        Example example2 = new Example(CategoryBrandEntity.class);
        example2.createCriteria().andEqualTo("categoryId",id);
        List<CategoryBrandEntity> list2 = categoryBrandMapper.selectByExample(example2);
        if(list2.size() == 1){
            return this.setResultError("分类绑定品牌不能删除");
        }


        if(list.size() == 1){
            CategoryEntity parentCateEntity = new CategoryEntity();
            parentCateEntity.setId(categoryEntity.getParentId());
            parentCateEntity.setIsParent(0);

            categoryMapper.updateByPrimaryKeySelective(parentCateEntity);
        }

        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess("删除成功");
    }


    //数据回显
    @Transactional
    @Override
    public Result<List<CategoryEntity>> getByBrand(Integer brandId) {

        List<CategoryEntity> byBrandId = categoryMapper.getByBrandId(brandId);
        
        return this.setResultSuccess(byBrandId);
    }


    @Override
    public Result<List<CategoryEntity>> getCategoryByIdList(String cateIds) {

        List<Integer> cateIdsArr = Arrays.asList(cateIds.split(",")).stream().map(cateIdStr ->  Integer.parseInt(cateIdStr)).collect(Collectors.toList());

        List<CategoryEntity> list = categoryMapper.selectByIdList(cateIdsArr);

        return this.setResultSuccess(list);
    }


}
