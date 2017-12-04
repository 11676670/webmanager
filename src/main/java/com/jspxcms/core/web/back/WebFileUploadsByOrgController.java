package com.jspxcms.core.web.back;

import static com.jspxcms.core.constant.Constants.EDIT;
import static com.jspxcms.core.constant.Constants.OPRT;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jspxcms.common.file.CommonFile;
import com.jspxcms.common.file.CommonFileFilter;
import com.jspxcms.common.file.FileHandler;
import com.jspxcms.common.web.Servlets;
import com.jspxcms.common.web.Validations;
import com.jspxcms.core.domain.PublishPoint;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.service.OrgService;
import com.jspxcms.core.support.CmsException;
import com.jspxcms.core.support.Context;

/**
 * 根据用户所属组织体系展示对应的文件信息
 * @author HuangCheng
 *
 */
@Controller
@RequestMapping("/core/web_file_4")
public class WebFileUploadsByOrgController extends WebFileControllerAbstractor {
    @Autowired
    private OrgService orgService;

    @Override
    protected int getType() {
        return UPLOAD_BYORG;
    }

    @Override
    protected String getBase(Site site) {
        return site.getSiteBase("");
    }

    @Override
    protected String getDefPath(Site site) {
        return getBase(site);
    }

    @Override
    protected String getUrlPrefix(Site site) {
        PublishPoint point = site.getGlobal().getUploadsPublishPoint();
        return point.getUrlPrefix();
    }

    @Override
    protected FileHandler getFileHandler(Site site) {
        PublishPoint point = site.getGlobal().getUploadsPublishPoint();
        return point.getFileHandler(pathResolver);
    }

    @Override
    protected boolean isFtp(Site site) {
        PublishPoint point = site.getGlobal().getUploadsPublishPoint();
        return point.isFtpMethod();
    }

/*    @RequiresPermissions("core:web_file_4:left")
    @RequestMapping("left.do")
    public String left(HttpServletRequest request, HttpServletResponse response, org.springframework.ui.Model modelMap)
            throws IOException {
        return super.left(request, response, modelMap);
    }*/

    @RequiresPermissions("core:web_file_4:left")
    @RequestMapping("left_tree.do")
    public String leftTree(HttpServletRequest request, HttpServletResponse response,
            org.springframework.ui.Model modelMap) throws IOException {
        Site site = Context.getCurrentSite();
        String parentId = Servlets.getParam(request, "parentId");
        parentId = parentId == null ? "" : parentId;
        String base = getBase(site);
        String urlPrefix = getUrlPrefix(site);
        if (StringUtils.isBlank(parentId)) {
            parentId = base;
        }
        if (!Validations.uri(parentId, base)) {
            throw new CmsException("invalidURI");
        }
        FileHandler fileHandler = getFileHandler(site);
        List<CommonFile> list = fileHandler.listFiles(dirFilter, parentId, urlPrefix);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("type", getType());
        modelMap.addAttribute("isFtp", isFtp(site));
        return "core/web_file/web_file_left_tree";
    }

/*    @RequiresPermissions("core:web_file_4:list")
    @RequestMapping("list.do")
    public String list(HttpServletRequest request, HttpServletResponse response, org.springframework.ui.Model modelMap)
            throws IOException {
        return super.list(request, response, modelMap);
    }*/

    /**
     * 构造登录用户所属组织下对应的文件结果，安装组织结构创建的文件夹
     * @param parentId
     * @param base
     * @return
     */
/*    private String buildParentId(String parentId, String base) {
        if (StringUtils.isNotBlank(parentId)) {
            return parentId;
        }
        parentId = buildOrgDir(base);
        parentId = base+"/" + parentId;
        return parentId;
    }*/

    private String buildOrgDir(String parentId,String base) {
        if (StringUtils.isNotBlank(parentId)) {
            return parentId;
        }
      //取得当前用户的组织
        User user = Context.getCurrentUser();
        /*String orgNumber = "";
        if(user != null && user.getOrg() != null) {
            orgNumber = user.getOrg().getNumber();
        }
        return orgNumber;*/
        if(user != null && user.getOrg() != null && user.getOrg().getParent() != null) {
            return orgService.findAllParent(base, user.getOrg().getParent().getId()) + "/"+user.getOrg().getNumber();
        }else {
            return base;
        }
    }

    private boolean isNotOrgRoot(String base, String parentId) {
        return !parentId.equalsIgnoreCase(buildOrgDir(null, base));
    }

    /**
     * 用户所属组织下的文件列表
     */
    @RequiresPermissions("core:web_file_4:list")
    @RequestMapping("list.do")
    public String list(HttpServletRequest request, HttpServletResponse response, org.springframework.ui.Model modelMap)
            throws IOException {
        Site site = Context.getCurrentSite();
        String searchName = Servlets.getParam(request, "search_name");
        String base = getBase(site);
        String parentId = Servlets.getParam(request, "parentId");
        parentId = parentId == null ? "" : parentId;
        parentId = buildOrgDir(parentId, base);
        String defPath = getDefPath(site);
        String urlPrefix = getUrlPrefix(site);
        if (StringUtils.isBlank(parentId)) {
            parentId = defPath;
        }
        if (!Validations.uri(parentId, base)) {
            throw new CmsException("invalidURI");
        }
        FileHandler fileHandler = getFileHandler(site);
        CommonFile pp = null;
        if (parentId.length() > base.length() && isNotOrgRoot(base, parentId)) {
            pp = new CommonFile(CommonFile.getParent(parentId), true);
        }
        List<CommonFile> list = fileHandler.listFiles(searchName, parentId, urlPrefix);
        String sort = request.getParameter("page_sort");
        String directionection = request.getParameter("page_sort_dir");
        CommonFile.sort(list, sort, directionection);
        if ( pp != null) {
            pp.setParent(true);
            list.add(0, pp);
            modelMap.addAttribute("ppId", pp.getId());
        }
        modelMap.addAttribute("parentId", parentId);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("type", getType());
        modelMap.addAttribute("isFtp", isFtp(site));
        return "core/web_file/web_file_listByOrg";
    }

    /**
     * 通过组织过滤，树状结构
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws IOException
     */
    @RequiresPermissions("core:web_file_4:left")
    @RequestMapping("left.do")
    public String leftByOrg(HttpServletRequest request, HttpServletResponse response, org.springframework.ui.Model modelMap)
            throws IOException {
        Site site = Context.getCurrentSite();
        String base = getBase(site);
        String urlPrefix = getUrlPrefix(site);
        FileHandler fileHandler = getFileHandler(site);
        base = buildOrgDir(null, base);
        CommonFile parent = new CommonFile(base, true);
        List<CommonFile> list = fileHandler.listFiles(dirFilter, base, urlPrefix);
        modelMap.addAttribute("parent", parent);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("type", getType());
        modelMap.addAttribute("isFtp", isFtp(site));
        return "core/web_file/web_file_leftByOrg";
    }

    @RequiresPermissions("core:web_file_4:create")
    @RequestMapping("create.do")
    public String create(HttpServletRequest request, HttpServletResponse response,
            org.springframework.ui.Model modelMap) throws IOException {
        return super.create(request, response, modelMap);
    }

    @RequiresPermissions("core:web_file_4:edit")
    @RequestMapping("edit.do")
    public String edit(HttpServletRequest request, HttpServletResponse response, org.springframework.ui.Model modelMap)
            throws IOException {
        Site site = Context.getCurrentSite();
        String id = Servlets.getParam(request, "id");
        String base = getBase(site);

        String urlPrefix = getUrlPrefix(site);
        if (!Validations.uri(id, base)) {
            throw new CmsException("invalidURI");
        }
        FileHandler fileHandler = getFileHandler(site);
        CommonFile bean = fileHandler.get(id, urlPrefix);

        String parentId = Servlets.getParam(request, "parentId");
        modelMap.addAttribute("parentId", parentId);
        modelMap.addAttribute("bean", bean);
        modelMap.addAttribute(OPRT, EDIT);
        modelMap.addAttribute("type", getType());
        modelMap.addAttribute("isFtp", isFtp(site));
        return "core/web_file/web_file_form";
    }

/*    @RequiresPermissions("core:web_file_4:mkdir")
    @RequestMapping(value = "mkdir.do", method = RequestMethod.POST)
    public String mkdir(String parentId, String dir, HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes ra) throws IOException {
        return super.mkdir(parentId, dir, request, response, ra);
    }
*/
    @RequiresPermissions("core:web_file_4:mkdir")
    @RequestMapping(value = "mkdir.do", method = RequestMethod.POST)
    public String mkdirByOrg(String parentId, String dir, HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes ra) throws IOException {
        super.mkdir(parentId, dir, request, response, ra);
        return "redirect:list.do";
    }

    @RequiresPermissions("core:web_file_4:save")
    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    public String save(String parentId, String name, String text, String redirect, HttpServletRequest request,
            HttpServletResponse response, RedirectAttributes ra) throws IOException {
        return super.save(parentId, name, text, redirect, request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:update")
    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    public void update(String parentId, String origName, String name, String text, Integer position, String redirect,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.update(parentId, origName, name, text, position, redirect, request, response);
    }

    @RequiresPermissions("core:web_file_4:delete")
    @RequestMapping("delete.do")
    public String delete(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra)
            throws IOException {
        return super.delete(request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:rename")
    @RequestMapping("rename.do")
    public String rename(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra)
            throws IOException {
        return super.rename(request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:move")
    @RequestMapping("move.do")
    public String move(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra)
            throws IOException {
        return super.move(request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:zip")
    @RequestMapping("zip.do")
    public String zip(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra)
            throws IOException {
        return super.zip(request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:zip_download")
    @RequestMapping("zip_download.do")
    public void zipDownload(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra)
            throws IOException {
        super.zipDownload(request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:unzip")
    @RequestMapping("unzip.do")
    public String unzip(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra)
            throws IOException {
        return super.unzip(request, response, ra);
    }

    @RequiresPermissions("core:web_file_4:upload")
    @RequestMapping("upload.do")
    public void upload(@RequestParam(value = "file", required = false) MultipartFile file, String parentId,
            HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException {
        super.upload(file, parentId, request, response);
    }

    @RequiresPermissions("core:web_file_4:zip_upload")
    @RequestMapping("zip_upload.do")
    public void zipUpload(@RequestParam(value = "file", required = false) MultipartFile file, String parentId,
            HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) throws IOException {
        super.zipUpload(file, parentId, request, response, ra);
    }

    @RequestMapping("choose_dir.do")
    protected String dir(HttpServletRequest request, HttpServletResponse response,
            org.springframework.ui.Model modelMap) throws IOException {
        Site site = Context.getCurrentSite();
        String parentId = Servlets.getParam(request, "parentId");
        parentId = parentId == null ? "" : parentId;
        String base = getBase(site);
        String urlPrefix = getUrlPrefix(site);
        if (StringUtils.isBlank(parentId)) {
            parentId = base;
        }
        if (!Validations.uri(parentId, base)) {
            throw new CmsException("invalidURI");
        }

        // 需排除的文件夹
        final String[] ids = Servlets.getParamValues(request, "ids");
        CommonFileFilter filter = new CommonFileFilter() {
            public boolean accept(CommonFile file) {
                // 只显示文件夹，不显示文件
                if (file.isDirectory()) {
                    String id = file.getId();
                    for (int i = 0, len = ids.length; i < len; i++) {
                        if (id.equals(ids[i]) || id.startsWith(ids[i] + "/")) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        FileHandler fileHandler = getFileHandler(site);
        List<CommonFile> list = fileHandler.listFiles(filter, parentId, urlPrefix);
        // 设置当前目录
        CommonFile parent = new CommonFile(parentId, true);
        parent.setCurrent(true);
        list.add(0, parent);
        // 设置上级目录
        if (parentId.length() > base.length() && isNotOrgRoot(base, parentId)) {
            CommonFile pp = new CommonFile(CommonFile.getParent(parentId), true);
            pp.setParent(true);
            list.add(0, pp);
            modelMap.addAttribute("ppId", pp.getId());
        }
        modelMap.addAttribute("ids", ids);
        modelMap.addAttribute("parentId", parentId);
        modelMap.addAttribute("list", list);
        Servlets.setNoCacheHeader(response);
        return "core/web_file/choose_dir";
    }

    @RequestMapping("choose_dir_list.do")
    public String dirList(HttpServletRequest request, HttpServletResponse response,
            org.springframework.ui.Model modelMap) throws IOException {
        return super.dirList(request, response, modelMap);
    }
}
