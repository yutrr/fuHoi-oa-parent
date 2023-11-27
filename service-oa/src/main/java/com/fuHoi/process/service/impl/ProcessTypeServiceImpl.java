package com.fuHoi.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuHoi.model.process.ProcessTemplate;
import com.fuHoi.model.process.ProcessType;
import com.fuHoi.process.mapper.ProcessTypeMapper;
import com.fuHoi.process.service.ProcessTemplateService;
import com.fuHoi.process.service.ProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-19
 */
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {

    @Autowired
    private ProcessTemplateService processTemplateService;
    @Override
    public List<ProcessType> findProcessType() {
        //1.查询所有审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        //2 遍历返回所有审批分类list集合
        for (ProcessType processType : processTypeList) {
            //3 得到每个审批分类，根据审批分类id查询对应审批模板
            //审批分类id
            Long typeId = processType.getId();
            //根据审批分类id查询对应审批模板
            List<ProcessTemplate> processTemplateList = processTemplateService
                    .list(new LambdaQueryWrapper<ProcessTemplate>().eq(ProcessTemplate::getProcessTypeId,typeId));
            //4 根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
