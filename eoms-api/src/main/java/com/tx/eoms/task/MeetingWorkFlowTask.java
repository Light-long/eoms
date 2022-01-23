package com.tx.eoms.task;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.dao.MeetingDao;
import com.tx.eoms.dao.UserDao;
import com.tx.eoms.exception.EomsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MeetingWorkFlowTask {

    @Resource
    private UserDao userDao;

    @Resource
    private MeetingDao meetingDao;

    @Value("${eoms.receiveNotify}")
    private String receiveNotify;

    @Value("${eoms.code}")
    private String code;

    @Value("${eoms.tcode}")
    private String tcode;

    @Value("${workflow.url}")
    private String workflowUrl;

    /**
     * 开启会议工作流
     * @param uuid 会议uuid，用于定时任务的分组
     * @param creatorId 会议创建人的id
     * @param title 会议名称
     * @param date 会议日期
     * @param start 会议开始时间
     * @param meetingType 会议类型
     */
    @Async
    public void startMeetingWorkFlow(String uuid, int creatorId, String title, String date, String start, String meetingType) {
        // 查询会议申请人的基本信息
        Map<String, Object> userInfo = userDao.searchUserInfo(creatorId);
        JSONObject json = new JSONObject();
        json.set("url", receiveNotify);
        json.set("uuid", uuid);
        json.set("creatorId",creatorId);
        json.set("creatorName", userInfo.get("name").toString());
        json.set("code", code);
        json.set("tcode", tcode);
        json.set("title",title);
        json.set("date", date);
        json.set("start", start);
        json.set("meetingType",meetingType);
        String[] roles = userInfo.get("roles").toString().split(",");
        // 如果是总经理，不用审批
        if (!ArrayUtil.contains(roles, "总经理")) {
            // 查询部门经理id
            Integer managerId = userDao.searchDeptManagerId(creatorId);
            json.set("managerId", managerId);
            // 查询总经理id
            Integer gmId = userDao.searchGmId();
            json.set("gmId", gmId);
            // 查询参会人是否是同一部门
            boolean sameDept = meetingDao.searchMeetingMembersInSameDept(uuid);
            json.set("sameDept", sameDept);
        }
        // 开启工作流
        String url = workflowUrl + "/workflow/startMeetingProcess";
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(json.toString()).execute();
        if (response.getStatus() == 200) {
            json = JSONUtil.parseObj(response.body());
            String instanceId = json.getStr("instanceId");
            Map<String, Object> params = new HashMap<>();
            params.put("instanceId", instanceId);
            params.put("uuid", uuid);
            // 更新会议记录的instance_id字段
            int rows = meetingDao.updateMeetingInstanceId(params);
            if (rows != 1) {
                throw new EomsException("保存会议工作流实例ID失败");
            }
        } else {
            log.error(response.body());
        }
    }

    /**
     * 删除会议申请工作流
     * @param uuid 会议uuid
     * @param instanceId 会议的工作流id
     * @param reason 删除原因
     */
    @Async
    public void deleteMeetingApplication(String uuid, String instanceId, String reason) {
        JSONObject json = new JSONObject();
        json.set("uuid", uuid);
        json.set("instanceId", instanceId);
        json.set("code", code);
        json.set("tcode", tcode);
        json.set("type", "会议申请");
        json.set("reason", reason);
        String url = workflowUrl + "/workflow/deleteProcessById";
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(json.toString()).execute();
        if (response.getStatus() == 200) {
            log.info("删除了会议申请");
        } else {
            log.error(response.body());
        }
    }

}
