package com.jspxcms.core.web.back;

import static com.jspxcms.core.constant.Constants.CREATE;
import static com.jspxcms.core.constant.Constants.DELETE_SUCCESS;
import static com.jspxcms.core.constant.Constants.EDIT;
import static com.jspxcms.core.constant.Constants.MESSAGE;
import static com.jspxcms.core.constant.Constants.OPRT;
import static com.jspxcms.core.constant.Constants.SAVE_SUCCESS;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jspxcms.common.web.Servlets;
import com.jspxcms.core.constant.Constants;
import com.jspxcms.core.domain.Role;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.domain.WorkflowStep;
import com.jspxcms.core.service.OperationLogService;
import com.jspxcms.core.service.RoleService;
import com.jspxcms.core.service.WorkflowService;
import com.jspxcms.core.service.WorkflowStepService;
import com.jspxcms.core.support.Context;

/**
 * 工作流-步骤
 * 
 */
@Controller
@RequestMapping("/core/workflow_step")
public class WorkflowStepController {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowStepController.class);

    @Autowired
    private OperationLogService logService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowStepService service;

    @Autowired
    private RoleService roleService;

    @ModelAttribute("bean")
    public WorkflowStep preloadBean(@RequestParam(required = false) Integer oid) {
        return oid != null ? service.get(oid) : null;
    }

    @RequiresPermissions("core:workflow_step:list")
    @RequestMapping("list.do")
    public String list(
            Integer workflowId,
            @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Map<String, String[]> params = Servlets.getParamValuesMap(request, Constants.SEARCH_PREFIX);
        List<WorkflowStep> pagedList = service.findList(workflowId, params, pageable.getSort());
        modelMap.addAttribute("pagedList", pagedList);
        if(workflowId != null) {
            Workflow dbWorkflow = workflowService.get(workflowId);
            modelMap.addAttribute("workflow", dbWorkflow);
        }
        modelMap.addAttribute("workflowId", workflowId);
        return "core/workflow_step/workflow_step_list";
    }

    @RequiresPermissions("core:workflow_step:create")
    @RequestMapping("create.do")
    public String create(Integer id, Integer workflowId,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        if (id != null) {
            WorkflowStep bean = service.get(id);
            modelMap.addAttribute("bean", bean);
        }
        if(workflowId != null) {
            Workflow dbWorkflow = workflowService.get(workflowId);
            modelMap.addAttribute("workflow", dbWorkflow);
        }
        Site site = Context.getCurrentSite();
        //角色
        List<Role> dbRoleList = roleService.findList(site.getId());
        modelMap.addAttribute("roleList", dbRoleList);
        modelMap.addAttribute("workflowId", workflowId);
        modelMap.addAttribute(OPRT, CREATE);
        return "core/workflow_step/workflow_step_form";
    }

    @RequiresPermissions("core:workflow_step:edit")
    @RequestMapping("edit.do")
    public String edit(
            Integer id,
            Integer workflowId,
            @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        WorkflowStep bean = service.get(id);
        modelMap.addAttribute("bean", bean);
        modelMap.addAttribute("workflowId", workflowId);
        modelMap.addAttribute(OPRT, EDIT);
        Site site = Context.getCurrentSite();
        //角色
        List<Role> dbRoleList = roleService.findList(site.getId());
        modelMap.addAttribute("roleList", dbRoleList);
        return "core/workflow_step/workflow_step_form";
    }

    @RequiresPermissions("core:workflow_step:save")
    @RequestMapping("save.do")
    public String save(WorkflowStep bean,
            Integer[] roleIds,
            Integer workflowId,
            String redirect, HttpServletRequest request, RedirectAttributes ra) {
        service.save(bean, roleIds, workflowId);
        logService.operation("opr.workflow.add", bean.getName(), null,
                bean.getId(), request);
        logger.info("save Workflow, title={}.", bean.getName());
        ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
        if (Constants.REDIRECT_LIST.equals(redirect)) {
            return "redirect:list.do?workflowId="+workflowId;
        } else if (Constants.REDIRECT_CREATE.equals(redirect)) {
            return "redirect:create.do?workflowId="+workflowId;
        } else {
            ra.addAttribute("id", bean.getId());
            return "redirect:edit.do?workflowId="+workflowId;
        }
    }

    @RequiresPermissions("core:workflow_step:update")
    @RequestMapping("update.do")
    public String update(@ModelAttribute("bean")WorkflowStep bean,
            Integer[] roleIds,
            Integer workflowId,
            String redirect, HttpServletRequest request, RedirectAttributes ra) {
        service.update(bean, roleIds);
        logService.operation("opr.workflow.edit", bean.getName(), null,
                bean.getId(), request);
        logger.info("update workflowGroup, title={}.", bean.getName());
        ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
        if (Constants.REDIRECT_LIST.equals(redirect)) {
            return "redirect:list.do?workflowId="+workflowId;
        } else {
            ra.addAttribute("id", bean.getId());
            return "redirect:edit.do?workflowId="+workflowId;
        }
    }

    @RequiresPermissions("core:workflow_step:delete")
    @RequestMapping("delete.do")
    public String delete(Integer[] ids, HttpServletRequest request,
            RedirectAttributes ra) {
        WorkflowStep[] beans = service.delete(ids);
        for (WorkflowStep bean : beans) {
            logService.operation("opr.workflow_group.delete", bean.getName(), null,
                    bean.getId(), request);
            logger.info("delete Workflow, title={}.", bean.getName());
        }
        ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
        return "redirect:list.do";
    }
}