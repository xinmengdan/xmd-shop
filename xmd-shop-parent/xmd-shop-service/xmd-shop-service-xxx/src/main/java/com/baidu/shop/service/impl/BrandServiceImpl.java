package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

//        PageHelper.startPage(page,rows);//分页
//
//        //排序
//        Example example = new Example(BrandEntity.class);
//        if(ObjectUtil.isNotNull(sort)){
//            example.setOrderByClause(sort + " " + (desc? "desc":""));
//        }

        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //排序 条件查询
        Example example = new Example(BrandEntity.class);
        if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());

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

        //新增品牌且可以返回主键
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //获取品牌名称
        //获取品牌名称第一个字符
        //将第一个字符串转换为pinyin
        //获取拼音的首字母
        //统一转为大写
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),
             PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);

        // 拆分后
//      String name = brandEntity.getName();
//      char c = name.charAt(0);
//      String upperCase = PinyinUtil.getUpperCase(String.valueOf(c), PinyinUtil.TO_FIRST_CHAR_PINYIN);
//      brandEntity.setLetter(upperCase.charAt(0));
//      brandMapper.insertSelective(brandEntity);


        if(brandDTO.getCategory().contains(",")){


            //通过split方法分割字符串的Array
            //Arrays.asList将Array转换为List
            //使用JDK1,8的stream
            //使用map函数返回一个新的数据
            //collect 转换集合类型Stream<T>
            //Collectors.toList())将集合转换为List类型
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(",")).stream().map(cid -> {

                CategoryBrandEntity entity = new CategoryBrandEntity();

                entity.setCategoryId(StringUtil.toInteger(cid));
                entity.setBrandId(brandEntity.getId());

                return entity;

            }).collect(Collectors.toList());
            //批量新增
            categoryBrandMapper.insertList(categoryBrandEntities);

            //     拆分后
//          String[] cidArr = brandDTO.getCategory().split(",");
//
//          List<String> list = Arrays.asList(cidArr);
//
//          List<CategoryBrandEntity> categoryBrandEntities  = new ArrayList<>();
//
//          list.stream().forEach(cid -> {
//             CategoryBrandEntity entity = new CategoryBrandEntity();
//             entity.setCategoryId(StringUtil.toInteger(cid));
//             entity.setBrandId(brandEntity.getId());
//             categoryBrandEntities.add(entity);
//          });
            //批量新增
//          categoryBrandMapper.insertList(categoryBrandEntities);


        }else{

            //新增
            CategoryBrandEntity entity = new CategoryBrandEntity();
            entity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            entity.setBrandId(brandEntity.getId());

            categoryBrandMapper.insertSelective(entity);

        }

        return this.setResultSuccess();
    }

    

}
