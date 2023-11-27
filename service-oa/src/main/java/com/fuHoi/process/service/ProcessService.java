package com.fuHoi.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuHoi.model.process.Process;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fuHoi.vo.process.ApprovalVo;
import com.fuHoi.vo.process.ProcessFormVo;
import com.fuHoi.vo.process.ProcessQueryVo;
import com.fuHoi.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-26
 */
public interface ProcessService extends IService<Process> {

    //审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    //流程定义部署
    void deployByZip(String deployPath);

    void startUp(ProcessFormVo processFormVo);

    //查询待处理的任务列表
    IPage<ProcessVo> findPending(Page<Process> pageParam);

    //查看审批详情信息
    Map<String, Object> show(Long id);

    //审批
    void approve(ApprovalVo approvalVo);

    /**
     * 已处理接口
     */
    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    /**
     * 已发起接口
     * @param pageParam
     * @return
     */
    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
