package com.fuHoi.process.service;

import com.fuHoi.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-19
 */
public interface ProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessType();

}
