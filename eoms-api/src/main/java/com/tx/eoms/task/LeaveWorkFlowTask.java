package com.tx.eoms.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.dao.LeaveDao;
import com.tx.eoms.dao.UserDao;
import com.tx.eoms.exception.EomsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LeaveWorkFlowTask {

    @Value("${eoms.code}")
    private String code;

    @Value("${eoms.tcode}")
    private String tcode;

    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${eoms.receiveNotify}")
    private String receiveNotify;

    @Resource
    private UserDao userDao;

    @Resource
    private LeaveDao leaveDao;

    /**
     * 开启请假工作流
     * @param id leave表的id
     * @param creatorId 请假申请人id
     * @param days 请假天数
     */
    @Async
    public void startLeaveWorkFlow(int id, int creatorId, String days) {
        Map<String, Object> userInfo = userDao.searchUserInfo(creatorId);
        JSONObject json = new JSONObject();
        json.set("url", receiveNotify);
        json.set("creatorId", creatorId);
        json.set("creatorName", userInfo.get("name").toString());
        json.set("code", code);
        json.set("tcode", tcode);
        json.set("title", userInfo.get("deptName").toString() + userInfo.get("name").toString() + "的请假申请");
        Integer managerId = userDao.searchDeptManagerId(creatorId);
        json.set("managerId", managerId);
        Integer gmId = userDao.searchGmId();
        json.set("gmId", gmId);
        json.set("days", Double.parseDouble(days));
        // 调用activiti工作流
        String url = workflowUrl + "/workflow/startLeaveProcess";
        HttpResponse result = HttpRequest.post(url).header("Context-Type", "application/json")
                .body(json.toString()).execute();
        if (result.getStatus() == 200) {
            json = JSONUtil.parseObj(result.body());
            String instanceId = json.getStr("instanceId");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("instanceId", instanceId);
            int rows = leaveDao.updateLeaveInstanceId(params);
            if (rows != 1) {
                throw new EomsException("保存请假工作流instanceId失败");
            }
        } else {
            log.error(result.body());
        }
    }

    /**
     * 删除请假工作流
     * @param instanceId 请假工作流实例id
     * @param type 说明是：请假类型
     * @param reason 删除原因
     */
    @Async
    public void deleteLeaveWorkflow(String instanceId, String type, String reason) {
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("type", type);
        json.set("reason", reason);
        json.set("code", code);
        json.set("tcode", tcode);
        String url = workflowUrl + "/workflow/deleteProcessById";
        HttpResponse result = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(json.toString()).execute();
        if (result.getStatus() != 200) {
            log.error(result.body());
            throw new EomsException("请假工作流删除失败");
        }
    }
}
