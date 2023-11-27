package com.fuHoi.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuHoi.auth.service.SysUserService;
import com.fuHoi.model.process.Process;
import com.fuHoi.model.process.ProcessRecord;
import com.fuHoi.model.process.ProcessTemplate;
import com.fuHoi.model.system.SysUser;
import com.fuHoi.process.mapper.ProcessMapper;
import com.fuHoi.process.service.ProcessRecordService;
import com.fuHoi.process.service.ProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuHoi.process.service.ProcessTemplateService;
import com.fuHoi.security.custom.LoginUserInfoHelper;
import com.fuHoi.vo.process.ApprovalVo;
import com.fuHoi.vo.process.ProcessFormVo;
import com.fuHoi.vo.process.ProcessQueryVo;
import com.fuHoi.vo.process.ProcessVo;
import com.fuHoi.wechat.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-26
 */
@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MessageService messageService;
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page=processMapper.selectPage(pageParam,processQueryVo);
        return page;
    }

    //流程部署定义
    @Override
    public void deployByZip(String deployPath) {
        //定义zip输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //流程部署
        Deployment deployment=repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    //启动流程
    @Transactional
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //1,根据当前用户id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        //2.根据审批模板id把模板信息查询
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());

        //3.保存提交审批信息到业务表，oa_process
        Process process = new Process();
        //processFormVo复制到process中
        BeanUtils.copyProperties(processFormVo,process);
        //其他值
        String workNum= String.valueOf(System.currentTimeMillis());
        process.setProcessCode(workNum);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName()+"发起"+processTemplate.getName()+"申请");
        process.setStatus(1);
        baseMapper.insert(process);

        //4.启动流程实例  RuntimeService
        //4.1流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();

        //4.2 业务key processId
        String businessKey = String.valueOf(process.getId());
        //4.3 流程参数 form表单JSON数据，转换map集合
        String formValues = processFormVo.getFormValues();
        //formData
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        //循环转换
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(),entry.getValue());
        }
        variables.put("data",map);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                processDefinitionKey, businessKey, variables);

        //5.查询下一个审批人
        //审批人可能有多个(并行审批)
        List<Task> list=this.getCurrentTaskList(processInstance.getId());
        if (!CollectionUtils.isEmpty(list)){
            List<String> assigneeList = new ArrayList<>();
            for (Task task : list) {
                SysUser user = sysUserService.getByUserName(task.getAssignee());
                assigneeList.add(user.getName());
                // 6.推送消息给下一个审批人，后续完善
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            process.setProcessInstanceId(processInstance.getId());
            process.setDescription("等待"+
                    StringUtils.join(assigneeList.toArray(),",")+"审批");
        }
        //7.业务和流程关联
        processMapper.updateById(process);

        //记录操作审批信息记录
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 根据当前人的ID查询
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();
        //调用方法分页条件查询，返回list集合，待办任务集合
        //第一个参数：开始位置  第二个参数  每页显示记录数
        List<Task> taskList = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();
        List<ProcessVo> processList = new ArrayList<>();
        for (Task task : taskList) {
            //从task获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            //根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (processInstance == null) continue;
            // 业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) continue;
            //根据业务key获取Process对象
            Process process = baseMapper.selectById(Long.parseLong(businessKey));
            //Process 对象复制到 ProcessVo对象
            ProcessVo processVo=new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());
            processList.add(processVo);
        }
        // 封装返回IPage对象
        IPage<ProcessVo> page= new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);

        return page;
    }

    @Override
    public Map<String, Object> show(Long id) {
        //根据id获取流程信息Process
        Process process = baseMapper.selectById(id);

        //根据流程id获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> processRecordList = processRecordService.list(wrapper);
        //根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        //计算当前用户是否可以审批，能够查看详情的用户不是都能审批，审批后也不能重复审批
        boolean isApprove=false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            for (Task task : taskList) {
                //判断任务审批人是否是当前用户
                if (LoginUserInfoHelper.getUsername().equals(task.getAssignee())){
                    isApprove=true;
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove",isApprove);

        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        //1.從approvalVo获取任务id，根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        //2.判断审批状态值
        if (approvalVo.getStatus()==1){
            //2.1状态值=1 审批通过
            Map<String, Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);
        } else if (approvalVo.getStatus()==-1) {
            //2.2状态值 =-1 审批不通过流程直接结束
            this.endTask(taskId);
        }

        //3.记录审批过程信息 oa_process_record
        String description = approvalVo.getStatus().intValue()==1?"已通过":"驳回";
        processRecordService.record(approvalVo.getProcessId(),
                approvalVo.getStatus(),description);

        //4.查询下一个审批人，更新流程表process记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        //查询任务
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)){
            List<String> assignList=new ArrayList<>();
            for (Task task : taskList) {
                SysUser sysUser = sysUserService.getByUserName(task.getAssignee());
                assignList.add(sysUser.getName());
                // 推送消息给下一个审批人
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
            process.setStatus(1);
        }else {
            if (approvalVo.getStatus().intValue()==1){
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            }else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }
        //推送消息给申请人
        this.updateById(process);
    }

    /**
     * 已处理
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByTaskCreateTime()
                .desc();

        //调用方法条件分页查询，返回list集合
        //开始位置 和 每页显示记录数
        int begin=(int) ((pageParam.getCurrent()-1)*pageParam.getSize());
        int size=(int)pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();

        //遍历返回list集合，封装List<ProcessVo>

        List<ProcessVo> processVoList = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            //流程实例id
            String processInstanceId = historicTaskInstance.getProcessInstanceId();
            //根据流程实例id查询获取process信息
            Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>()
                    .eq(Process::getProcessInstanceId, processInstanceId));
            //process->ProcessVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            //放到list
            processVoList.add(processVo);
        }
        //IPage封装分页查询所有数据返回
        Page<ProcessVo> page = new Page<>(pageParam.getCurrent(),
                pageParam.getSize(),totalCount);
        page.setRecords(processVoList);
        return page;
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = processMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo record : page.getRecords()) {
            record.setTaskId("0");
        }
        return page;
    }

    private void endTask(String taskId) {
        //当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if (CollectionUtils.isEmpty(endEventList))return;
        FlowNode endFlowNode = (FlowNode)endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
        //  临时保存当前活动的原始方向
        List originalSequenceFlowList=new ArrayList();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList=new ArrayList();
        newSequenceFlowList.add(newSequenceFlow);

        //当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //完成当前任务

        taskService.complete(task.getId());
    }

    /**
     * 获取当前任务列表
     * @param processInstanceId
     * @return
     */
    private List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return taskList;
    }
}
