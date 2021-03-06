package com.jspxcms.core.web.back;

import static com.jspxcms.core.constant.Constants.CREATE;
import static com.jspxcms.core.constant.Constants.DELETE_SUCCESS;
import static com.jspxcms.core.constant.Constants.EDIT;
import static com.jspxcms.core.constant.Constants.MESSAGE;
import static com.jspxcms.core.constant.Constants.OPRT;
import static com.jspxcms.core.constant.Constants.SAVE_SUCCESS;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.jspxcms.core.domain.WorkflowGroup;
import com.jspxcms.core.service.OperationLogService;
import com.jspxcms.core.service.WorkflowGroupService;
import com.jspxcms.core.support.Backends;
import com.jspxcms.core.support.Context;

/**
 * 工作流组
 * 
 */
@Controller
@RequestMapping("/core/workflow_group")
public class WorkflowGroupController {
    private static final Logger logger = LoggerFactory
            .getLogger(WorkflowGroupController.class);

    @Autowired
    private OperationLogService logService;

    @Autowired
    private WorkflowGroupService service;

    @RequiresPermissions("core:workflow_group:list")
    @RequestMapping("list.do")
    public String list(
            @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Map<String, String[]> params = Servlets.getParamValuesMap(request,
                Constants.SEARCH_PREFIX);
        Page<WorkflowGroup> pagedList = service.findList(siteId, params, pageable);
        modelMap.addAttribute("pagedList", pagedList);
        return "core/workflow_group/workflow_group_list";
    }

    @RequiresPermissions("core:workflow_group:create")
    @RequestMapping("create.do")
    public String create(Integer id, Integer modelId, 
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        if (id != null) {
            WorkflowGroup bean = service.get(id);
            Backends.validateDataInSite(bean, siteId);
            modelMap.addAttribute("bean", bean);
        }
        modelMap.addAttribute(OPRT, CREATE);
        return "core/workflow_group/workflow_group_form";
    }

    @RequiresPermissions("core:workflow_group:edit")
    @RequestMapping("edit.do")
    public String edit(
            Integer id,
            @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        WorkflowGroup bean = service.get(id);
        Backends.validateDataInSite(bean, siteId);
        modelMap.addAttribute("bean", bean);
        modelMap.addAttribute(OPRT, EDIT);
        return "core/workflow_group/workflow_group_form";
    }

    @RequiresPermissions("core:workflow_group:save")
    @RequestMapping("save.do")
    public String save(WorkflowGroup bean,
            String redirect, HttpServletRequest request, RedirectAttributes ra) {
        Integer siteId = Context.getCurrentSiteId();
        service.save(bean, siteId);
        logService.operation("opr.special.add", bean.getName(), null,
                bean.getId(), request);
        logger.info("save WorkflowGroup, title={}.", bean.getName());
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

    @RequiresPermissions("core:workflow_group:update")
    @RequestMapping("update.do")
    public String update(@ModelAttribute("bean")WorkflowGroup bean,
            Integer position,
            String redirect, HttpServletRequest request, RedirectAttributes ra) {
        Site site = Context.getCurrentSite();
        Backends.validateDataInSite(bean, site.getId());
        service.update(bean);
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

    @RequiresPermissions("core:workflow_group:delete")
    @RequestMapping("delete.do")
    public String delete(Integer[] ids, HttpServletRequest request,
            RedirectAttributes ra) {
        WorkflowGroup[] beans = service.delete(ids);
        for (WorkflowGroup bean : beans) {
            logService.operation("opr.workflow_group.delete", bean.getName(), null,
                    bean.getId(), request);
            logger.info("delete WorkflowGroup, title={}.", bean.getName());
        }
        ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
        return "redirect:list.do";
    }

    @ModelAttribute("bean")
    public WorkflowGroup preloadBean(@RequestParam(required = false) Integer oid) {
        return oid != null ? service.get(oid) : null;
    }
}