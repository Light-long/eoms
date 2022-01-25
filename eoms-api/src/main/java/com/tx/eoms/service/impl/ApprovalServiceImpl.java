package com.tx.eoms.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.service.ApprovalService;
import com.tx.eoms.util.PageUtils;
import io.micrometer.core.instrument.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {

    @Value("${eoms.code}")
    private String code;

    @Value("${eoms.tcode}")
    private String tcode;

    @Value("${workflow.url}")
    private String workflowUrl;

    /**
     * 调用工作流项目查询 该用户任务列表
     */
    @Override
    public PageUtils searchTaskByPage(Map<String, Object> params) {
        params.put("code", code);
        params.put("tcode", tcode);
        String url = workflowUrl + "/workflow/searchTaskByPage";
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(params)).execute();
        if (response.getStatus() == 200) {
            JSONObject json = JSONUtil.parseObj(response.body());
            JSONObject page = json.getJSONObject("page");
            List<Map<String, Object>> taskList = page.get("list", List.class);
            Long totalCount = page.getLong("totalCount");
            Integer pageIndex = page.getInt("pageIndex");
            Integer pageSize = page.getInt("pageSize");
            return new PageUtils(taskList, totalCount, pageIndex, pageSize);
        } else {
            log.error(response.body());
            throw new EomsException("获取工作流数据异常");
        }
    }

    /**
     * 根据instanceId查询任务信息
     */
    @Override
    public Map<String, Object> searchApprovalContent(Map<String, Object> params) {
        params.put("code", code);
        params.put("tcode", tcode);
        String url = workflowUrl + "/workflow/searchApprovalContent";
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(params)).execute();
        if (response.getStatus() == 200) {
            JSONObject json = JSONUtil.parseObj(response.body());
            return json.get("content", Map.class);
        } else {
            log.error(response.body());
            throw new EomsException("获取工作流数据异常");
        }
    }

    /**
     * 审批任务
     */
    @Override
    public void approvalTask(Map<String, Object> params) {
        params.put("code", code);
        params.put("tcode", tcode);
        String url = workflowUrl + "/workflow/approvalTask";
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(params)).execute();
        if (response.getStatus() != 200) {
            log.error(response.body());
            throw new EomsException("调用工作流异常");
        }
    }
}
