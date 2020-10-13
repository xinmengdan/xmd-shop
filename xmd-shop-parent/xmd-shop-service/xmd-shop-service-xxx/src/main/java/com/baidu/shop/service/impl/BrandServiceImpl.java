package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;


    @Override
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid) {

        List<BrandEntity> list =  brandMapper.getBrandByCategoryId(cid);

        return this.setResultSuccess(list);
    }


    @Override
    public Result<List<BrandEntity>> getBrandByIdList(String brandIds) {

        List<Integer> brandIdsArr = Arrays.asList(brandIds.split(",")).stream().map(brandIdStr -> Integer.parseInt(brandIdStr)).collect(Collectors.toList());

        List<BrandEntity> list = brandMapper.selectByIdList(brandIdsArr);
        
        return this.setResultSuccess(list);
    }



    //查询 (获取品牌信息)
    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

          //   代码拆分
//        PageHelper.startPage(page,rows);//分页
//
//        //排序
//        Example example = new Example(BrandEntity.class);

          //条件查询
//        if(ObjectUtil.isNotNull(sort)){
//            example.setOrderByClause(sort + " " + (desc? "desc":""));
//        }

        //分页   代码优化
        //商品列表分页
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows())){
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        }

        //排序 条件查询
        Example example = new Example(BrandEntity.class);
        if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());

        //商品列表(分页 总条数)
        Example.Criteria criteria = example.createCriteria();
        if(ObjectUtil.isNotNull(brandDTO.getId())){
            criteria.andEqualTo("id",brandDTO.getId());
        }


        //条件查询
        if (StringUtil.isNotEmpty(brandDTO.getName())){
            example.createCriteria().andLike("name","%" + brandDTO.getName() + "%");
        }

        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        return this.setResultSuccess(pageInfo);//返回

    }

    @Transactional
    @Override
    public Result<JsonObject> save(BrandDTO brandDTO) {

        //brandMapper.insertSelective(BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class));

        //新增品牌 且 可以返回主键
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //  代码拆分
//      String name = brandEntity.getName();   //获取品牌名称
//      char c = name.charAt(0);   //获取品牌名称第一个字符
//      String upperCase = PinyinUtil.getUpperCase(String.valueOf(c), PinyinUtil.TO_FIRST_CHAR_PINYIN);
//      brandEntity.setLetter(upperCase.charAt(0));

        //  代码优化
        //获取品牌名称  获取品牌名称第一个字符  将第一个字符串转换为pinyin  获取拼音的首字母  统一转为大写
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);


        //代码优化 将公共的代码 抽取出来
        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess("新增成功");

    }

    @Transactional
    @Override
    public Result<JsonObject> editBrand(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //获取品牌名称  获取品牌名称第一个字符  将第一个字符串转换为pinyin  获取拼音的首字母  统一转为大写
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //修改
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //删除关系 将公共代码抽取出来
        this.deleteCategoryAndBrand(brandEntity.getId());

        //新增 新的数据
        //代码优化 将公共的代码 抽取出来
        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess("修改成功");

    }

    @Transactional
    @Override
    public Result<JsonObject> deleteBrand(Integer id) {

        //品牌绑定商品
        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        List<SpuEntity> list = spuMapper.selectByExample(example);
        if(list.size() > 0) return this.setResultError("品牌绑定商品不能被删除");

        //删除品牌
        brandMapper.deleteByPrimaryKey(id);

        //删除关系 将公共代码抽取出来
        this.deleteCategoryAndBrand(id);

        return this.setResultSuccess("删除成功");
    }


    //新增 关系数据
    //代码优化(封装) 将公共的代码结合
    public void insertCategoryAndBrand(BrandDTO brandDTO,BrandEntity brandEntity){

        if(brandDTO.getCategory().contains(",")){

            //通过split方法分割字符串的Array  Arrays.asList将Array转换为List  使用JDK1,8的stream 使用map函数返回一个新的数据
            //collect 转换集合类型Stream<T>  Collectors.toList())将集合转换为List类型
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(",")).stream().map(cid -> {

                CategoryBrandEntity entity = new CategoryBrandEntity();
                entity.setCategoryId(StringUtil.toInteger(cid));
                entity.setBrandId(brandEntity.getId());

                return entity;

            }).collect(Collectors.toList());

            //批量新增
            categoryBrandMapper.insertList(categoryBrandEntities);

            //     代码拆分
//          String[] cidArr = brandDTO.getCategory().split(","); //通过split方法分割字符串的Array
//
//          List<String> list = Arrays.asList(cidArr); //Arrays.asList将Array转换为List
//
//          List<CategoryBrandEntity> categoryBrandEntities  = new ArrayList<>();
//
//          list.stream().forEach(cid -> {
//             CategoryBrandEntity entity = new CategoryBrandEntity();
//             entity.setCategoryId(StringUtil.toInteger(cid));
//             entity.setBrandId(brandEntity.getId());
//             categoryBrandEntities.add(entity);
//          });

        }else{

            //新增
            CategoryBrandEntity entity = new CategoryBrandEntity();
            entity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            entity.setBrandId(brandEntity.getId());

            categoryBrandMapper.insertSelective(entity);

        }

    }


    //代码优化  删除关系
    private void deleteCategoryAndBrand(Integer id){

        //通过brandId 删除中间表的关系
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);

    }


}
