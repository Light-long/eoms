package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.todo.AddTodoListForm;
import com.tx.eoms.controller.todo.DeleteTodoListForm;
import com.tx.eoms.controller.todo.FinishTaskForm;
import com.tx.eoms.controller.todo.SearchTodoListForm;
import com.tx.eoms.service.TodoService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/todo")
@Tag(name = "TodoController", description = "待办事项Web接口")
@Slf4j
public class TodoController {

    @Resource
    private TodoService todoService;

    @PostMapping("/searchTodoList")
    @Operation(summary = "查询待办事项列表")
    @SaCheckLogin
    public CommonResult searchTodoList(@Valid @RequestBody SearchTodoListForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        int start = (form.getPage() - 1) * form.getLength();
        params.put("start", start);
        params.put("userId", StpUtil.getLoginIdAsInt());
        PageUtils todoList = todoService.searchTodoList(params);
        return CommonResult.ok().put("todoList", todoList);
    }

    @PostMapping("/finishTask")
    @Operation(summary = "完成待办")
    @SaCheckLogin
    public CommonResult finishTask(@Valid @RequestBody FinishTaskForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("userId", StpUtil.getLoginIdAsInt());
        int rows = todoService.finishTask(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/addTodoList")
    @Operation(summary = "添加待办事项")
    @SaCheckLogin
    public CommonResult addTodoList(@Valid @RequestBody AddTodoListForm form) {
        // 比较起始时间和截止时间
        DateTime start = DateUtil.parse(form.getStart());
        DateTime end = DateUtil.parse(form.getEnd());
        if (start.isAfterOrEquals(end)) {
            return CommonResult.error("结束时间必须大于开始时间");
        }
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("userId", StpUtil.getLoginIdAsInt());
        int rows = todoService.addTodoList(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteTodoList")
    @Operation(summary = "删除待办事项")
    @SaCheckLogin
    public CommonResult deleteTodoList(@Valid @RequestBody DeleteTodoListForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", form.getId());
        params.put("userId", StpUtil.getLoginIdAsInt());
        int rows = todoService.deleteTodoList(params);
        return CommonResult.ok().put("rows", rows);
    }

}
