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
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.domain.WorkflowGroup;
import com.jspxcms.core.service.OperationLogService;
import com.jspxcms.core.service.WorkflowGroupService;
import com.jspxcms.core.service.WorkflowService;
import com.jspxcms.core.support.Backends;
import com.jspxcms.core.support.Context;

/**
 * 工作流
 * 
 */
@Controller
@RequestMapping("/core/workflow")
public class WorkflowController {
    private static final Logger logger = LoggerFactory
            .getLogger(WorkflowController.class);

    @Autowired
    private OperationLogService logService;

    @Autowired
    private WorkflowService service;

    @Autowired
    private WorkflowGroupService workflowGroupservice;

    @ModelAttribute("bean")
    public Workflow preloadBean(@RequestParam(required = false) Integer oid) {
        return oid != null ? service.get(oid) : null;
    }

    @RequiresPermissions("core:workflow:list")
    @RequestMapping("list.do")
    public String list(
            @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Map<String, String[]> params = Servlets.getParamValuesMap(request, Constants.SEARCH_PREFIX);
        List<Workflow> pagedList = service.findList(siteId, params, pageable.getSort());
        modelMap.addAttribute("pagedList", pagedList);
        return "core/workflow/workflow_list";
    }

    @RequiresPermissions("core:workflow:create")
    @RequestMapping("create.do")
    public String create(Integer id, Integer modelId, 
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        if (id != null) {
            Workflow bean = service.get(id);
            Backends.validateDataInSite(bean, siteId);
            modelMap.addAttribute("bean", bean);
        }
        modelMap.addAttribute(OPRT, CREATE);
        List<WorkflowGroup> workflowGroupList = workflowGroupservice.findList(siteId);
        modelMap.addAttribute("workflowGroupList", workflowGroupList);
        return "core/workflow/workflow_form";
    }

    @RequiresPermissions("core:workflow:edit")
    @RequestMapping("edit.do")
    public String edit(
            Integer id,
            @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Workflow bean = service.get(id);
        Backends.validateDataInSite(bean, siteId);
        modelMap.addAttribute("bean", bean);
        modelMap.addAttribute(OPRT, EDIT);
        List<WorkflowGroup> workflowGroupList = workflowGroupservice.findList(siteId);
        modelMap.addAttribute("workflowGroupList", workflowGroupList);
        return "core/workflow/workflow_form";
    }

    @RequiresPermissions("core:workflow:save")
    @RequestMapping("save.do")
    public String save(Workflow bean,
            Integer groupId,
            String redirect, HttpServletRequest request, RedirectAttributes ra) {
        Integer siteId = Context.getCurrentSiteId();
        service.save(bean, groupId, siteId);
        logService.operation("opr.workflow.add", bean.getName(), null,
                bean.getId(), request);
        logger.info("save Workflow, title={}.", bean.getName());
        ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
        if (Constants.REDIRECT_LIST.equals(redirect)) {
            return "redirect:list.do";
        } else if (Constants.REDIRECT_CREATE.equals(redirect)) {
            return "redirect:create.do";
        } else {
            ra.addAttribute("id", bean.getId());
            return "redirect:edit.do";
        }
    }

    @RequiresPermissions("core:workflow:update")
    @RequestMapping("update.do")
    public String update(@ModelAttribute("bean")Workflow bean,
            Integer position,
            Integer groupId,
            String redirect, HttpServletRequest request, RedirectAttributes ra) {
        Site site = Context.getCurrentSite();
        Backends.validateDataInSite(bean, site.getId());
        service.update(bean, groupId);
        logService.operation("opr.workflowGroup.edit", bean.getName(), null,
                bean.getId(), request);
        logger.info("update workflowGroup, title={}.", bean.getName());
        ra.addFlashAttribute(MESSAGE, SAVE_SUCCESS);
        if (Constants.REDIRECT_LIST.equals(redirect)) {
            return "redirect:list.do";
        } else {
            ra.addAttribute("id", bean.getId());
            ra.addAttribute("position", position);
            return "redirect:edit.do";
        }
    }

    @RequiresPermissions("core:workflow:delete")
    @RequestMapping("delete.do")
    public String delete(Integer[] ids, HttpServletRequest request,
            RedirectAttributes ra) {
        Workflow[] beans = service.delete(ids);
        for (Workflow bean : beans) {
            logService.operation("opr.workflow_group.delete", bean.getName(), null,
                    bean.getId(), request);
            logger.info("delete Workflow, title={}.", bean.getName());
        }
        ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
        return "redirect:list.do";
    }
}