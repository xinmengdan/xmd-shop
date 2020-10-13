package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/3
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements  SpecificationService{

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    //规格组 getSpecGroupInfo
    @Override
    public Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO) {

        Example example = new Example(SpecGroupEntity.class);

        if(ObjectUtil.isNotNull(specGroupDTO.getCid())){
            example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());
        }

        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> save(SpecGroupDTO specGroupDTO) {

        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess("规格组新增成功");
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> edit(SpecGroupDTO specGroupDTO) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess("规格组修改成功");
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> delete(Integer id) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        if(list.size() > 0){
            return this.setResultError("规则组中包含参数无法删除");
        }

        specGroupMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess("规格组删除成功");
    }


    //规格参数 getSpecParamInfo
    @Override
    public Result<List<SpecParamEntity>> list(SpecParamDTO specParamDTO) {

//        if(ObjectUtil.isNull(specParamDTO.getGroupId())){
//            return this.setResultError("规格组id不能为空");
//        }

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(specParamDTO.getGroupId())){
            criteria.andEqualTo("groupId",specParamDTO.getGroupId());
        }

        if(ObjectUtil.isNotNull(specParamDTO.getCid())){
            criteria.andEqualTo("cid",specParamDTO.getCid());
        }

        if(ObjectUtil.isNotNull(specParamDTO.getSearching())){
            criteria.andEqualTo("searching",specParamDTO.getSearching());
        }

        if (ObjectUtil.isNotNull(specParamDTO.getGeneric())) {
            criteria.andEqualTo("generic",specParamDTO.getGeneric());
        }

        // example.createCriteria().andEqualTo("groupId",specParamDTO.getGroupId());

        List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> saveparam(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess("规格参数新增成功");
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> editparam(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess("规格参数修改成功");
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> deleteparam(Integer id) {

        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess("规格参数删除成功");

    }

}
