package com.tx.eoms.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.dao.ReimDao;
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
public class ReimWorkFlowTask {

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
    private ReimDao reimDao;

    /**
     * 开启报销的工作流
     * @param id 报销记录id
     * @param creatorId 申请人id
     */
    @Async
    public void startReimWorkFlow(int id, int creatorId) {
        Map<String, Object> userInfo = userDao.searchUserInfo(creatorId);
        JSONObject json = new JSONObject();
        json.set("url", receiveNotify);
        json.set("creatorId", creatorId);
        json.set("creatorName", userInfo.get("name").toString());
        json.set("code", code);
        json.set("tcode", tcode);
        json.set("title", userInfo.get("deptName").toString() + userInfo.get("name").toString() + "的报销申请");
        Integer managerId = userDao.searchDeptManagerId(creatorId);
        json.set("managerId", managerId);
        Integer gmId = userDao.searchGmId();
        json.set("gmId", gmId);

        String url = workflowUrl + "/workflow/startReimProcess";
        HttpResponse result = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(json.toString()).execute();
        if (result.getStatus() == 200) {
            json = JSONUtil.parseObj(result.body());
            String instanceId = json.getStr("instanceId");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("instanceId", instanceId);
            int rows = reimDao.updateReimInstanceId(params);
            if (rows != 1) {
                throw new EomsException("更新报销申请instanceId异常");
            }
        } else {
            log.error(result.body());
            throw new EomsException("【报销申请】工作流调用异常");
        }
    }
}
