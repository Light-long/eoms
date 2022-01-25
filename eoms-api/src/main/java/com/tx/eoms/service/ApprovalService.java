package com.tx.eoms.service;

import com.tx.eoms.util.PageUtils;

import java.util.Map;

public interface ApprovalService {

    /**
     * 查询分页任务列表
     */
    PageUtils searchTaskByPage(Map<String, Object> params);

    /**
     * 根据instanceId查询任务信息
     */
    Map<String, Object> searchApprovalContent(Map<String, Object> params);

    /**
     * 审批任务
     */
    void approvalTask(Map<String, Object> params);
}
