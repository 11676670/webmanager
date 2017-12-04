================
后台管理地址：
================

http://192.168.100.88:8080/cmscp/login.do


================
1.菜单操作
================
后台首页
com.jspxcms.core.web.back.IndexController

@RequestMapping({ "/", "/index.do" })
public String index(...
WEB-INF\views\index.jsp模板文件

================
权限信息
================
com.jspxcms.core.web.back.f7.PermF7Controller
f7NodeTree
com.jspxcms.core.holder.MenuHolder#getMenus();

左边导航栏为：
com.jspxcms.core.holder.MenuHolder类由下面的
com.jspxcms.core.MenuConfig注册的menuProperties()方法初始化
具体配置文件为:\resources\conf\menu.yml
menu.yml解释
0-0:
  name: homepage.welcome    国际化资源文件对应的名称resources\messages\core\core.properties
  url: core/homepage/welcome.do 控制器url
  perms: core:homepage:welcome   访问url需具有的权限代码
ops:
  - webFile.createText@core:web_file_1:create     代表着功能按钮权限
  - webFile.createDir@core:web_file_1:create_dir

================
组织管理
================
列表类
com.jspxcms.core.web.back.OrgController
@RequiresPermissions("core:org:list")
@RequestMapping("list.do")
组织类
com.jspxcms.core.domain.Org
组织表
cms_org

================
文件管理
================
com.jspxcms.core.web.back.WebFileUploadsController
@RequestMapping("list.do")
public String list(

com.jspxcms.core.web.back.WebFileControllerAbstractor
107行的protected String list(

新建文件夹
com.jspxcms.core.web.back.WebFileControllerAbstractor
protected String mkdir(

FileHandler fileHandler = getFileHandler(site);
boolean success = fileHandler.mkdir("bgs_1_2_32", "/1/000/bgs1/bgs1_2");

================
获取当前用户信息
================
com.jspxcms.core.support.Context
User getCurrentUser(


================
前端-新闻页的-文档页模板
查询路径为
================
com.jspxcms.core.web.fore.InfoController
@RequestMapping("/info/{id:[0-9]+}")
public String info(

请求的url指定的模板String template = Servlets.getParam(request, "template");
com.jspxcms.core.domain.Info.getTemplate()
对应数据表 cms_info   它一对一的  cms_info_detail
cms_info_detail#f_info_template字段

如果为null再找文章对应的Node(栏目表)
cms_node表对应的cms_node_detail  为f_node_id外键关联
cms_node_detail#f_info_template

如果为null再找模型
cms_node#f_info_model_id
cms_model的id在通过   cms_model_custom#f_model_id外键找个集合出来
通过关键字key = template找到对应的模板

/1/default/info_news.html

================
前端-新闻页的--栏目页模板
查询路径为
================
com.jspxcms.core.web.fore.NodeController
@RequestMapping("/node/{id:[0-9]+}")
public String node

请求的url指定的模板String template = Servlets.getParam(request, "template");
com.jspxcms.core.domain.Node.getNodeTemplate()
对应数据表 cms_node   它一对一的  cms_node_detail
cms_node_detail#f_node_template字段

如果为null再找模型
通过cms_node#f_info_model_id 找到Model类(cms_model)
cms_model的id在通过   cms_model_custom#f_model_id  外键找个集合出来
找到key=coverTemplate类型的模板

/1/default/cover_news.html

================
栏目修改
================
com.jspxcms.core.web.back.NodeController
@RequiresPermissions("core:node:edit")
@RequestMapping("edit.do")
public String edit(
对应的模板文件jsp
core/node/node_form.jsp

================
栏目页模板-修改
================
进入对应的栏目下，找到栏目页模板，选择拟喜欢的模板
默认的新闻页模板是/1/default/cover_news.html

文档页模板修改类似的方法
默认新闻文档页模板是/1/default/info_news.html

================
后台-新增文章
================
http://192.168.100.88:8080/cmscp/core/info/create.do?queryNodeId=130&queryNodeType=0&queryInfoPermType=&queryStatus=&

com.jspxcms.core.web.back.InfoController
@RequestMapping("create.do")
public String create(
参数说明：
栏目id    queryNodeId    对应  cms_node#f_node_id
前端展示模板
core/info/info_form.jsp

================
后台-新增文章--工作流程
================
core/info/info_form.jsp
<input type="hidden" id="draft" name="draft" value="false"/>
draft=false  文章状态为 status = null
draft=true  文章状态为 status = Info.DRAFT草稿

工作流逻辑判断
        Workflow workflow = null;
        if (Info.DRAFT.equals(status) || Info.CONTRIBUTION.equals(status) || Info.COLLECTED.equals(status)) {
            // 草稿、投稿、采集
            bean.setStatus(status);
        } else {
            workflow = node.getWorkflow();
            if (workflow != null) {
                bean.setStatus(Info.AUDITING);
            } else {
                bean.setStatus(Info.NORMAL);
            }
        }
     
根据项目 cms_node是否具有工作流
1.查找 WorkflowProcess 工作流过程表   cms_workflow_process
f_data_type=1  数据类型(1:文档) 
f_data_id=244  数据ID  文档id


================
删除栏目-判断内容
================

select count(*) as col_0_0_ from cms_info info0_ where (info0_.f_node_id in (42)) and info0_.f_status<>'X';

采集表判断
select count(*) as col_0_0_ from cms_collect collect0_ where collect0_.f_node_id in (42)
到采集管理
删除对应的记录

================
采集任务
================
采集任务
com.jspxcms.ext.quartz.CollectJob

采集请求url  /cmscp/ext/collect/start.do?ids=1&

com.jspxcms.ext.web.back.CollectController
@RequiresPermissions("ext:collect:start")
@RequestMapping("start.do")
public String start(

================
审核流程--新增
栏目管理的审核流程
================

/cmscp/core/node/create.do?parentId=36&infoModelId=2&queryParentId=111&showDescendants=true&
com.jspxcms.core.web.back.NodeController

@RequiresPermissions("core:node:create")
@RequestMapping("create.do")
public String create(

WEB-INF\views\core\node\node_form.jsp
node.workflow=审核流程  对应的I18N属性

对应的cms_model
SELECT * from cms_model where f_model_id = 3;
Model.getNormalFields()  对应    ModelField  cms_model_field外键f_model_id取集合
<c:forEach var="field" items="${model.normalFields}">
......
<c:when test="${field.name eq 'workflow'}">
    <select class="form-control" name="workflowId">
      <option value=""><s:message code="noneSelect"/></option>
      <f:options items="${workflowList}" itemLabel="name" itemValue="id" selected="${bean.workflow.id}"/>
    </select>
</c:when>


工作流
Workflow cms_workflow  WorkflowServiceImpl
 
        List<Workflow> workflowList = workflowService.findList(siteId);
        modelMap.addAttribute("workflowList", workflowList);
        
SELECT * from cms_workflow; #1

SELECT * from  cms_workflow_step;

SELECT * from cms_workflow_group

Model.normalFields    =   ModelField model
cms_model

================
审核流程--工作流组
================
  name: workflowGroup.management
  url: ../support_genuine.jsp
  perms: core:workflow_group:list
  ops:
    - create@core:workflow_group:create
    - copy@core:workflow_group:copy
    - edit@core:workflow_group:edit
    - save@core:workflow_group:save
    - update@core:workflow_group:update
    - delete@core:workflow_group:delete

================
搜索引擎模块
sys_search.html模板文件
================
com.jspxcms.core.web.fore.SearchController
查询数据没有使用controller
sys_search.html

而是通过freemark标签语言查询。
[@InfoFulltextPage q=Param.q title=Param.title keyword=Param.keyword description=Param.description text=Param.text nodeId=Param.nodeId beginDate=Param.beginDate endDate=Param.endDate sort=Param.sort pageSize=10;pagedList]
对应resources\conf\conf.properties
freemarkerVariables.InfoFulltextPage=CmsInfoFulltextPage

查找对应的bean
resources\conf\core\context-directive.xml
<bean id="CmsInfoFulltextPage" class="com.jspxcms.core.web.directive.InfoFulltextPageDirective" />

AbstractInfoFulltextListPageDirective通过下面的方法调用搜索引擎
@SuppressWarnings({ "unchecked", "rawtypes" })
public void doExecute(

com.jspxcms.core.fulltext.InfoFulltextServiceImpl
public Page<Info> page(

com.jspxcms.core.fulltext.FInfo  构建org.apache.lucene.search.Query对象
public static Query query(
Lucene搜索引擎语句如下：
q = QueryParser.escape(q);
Query qy = MultiFieldQueryParser.parse(LUCENE_36, q,
        new String[] { TITLE, KEYWORD, DESCRIPTION, TEXT },
        new Occur[] { SHOULD, SHOULD, SHOULD, SHOULD },
        analyzer);
query.add(qy, MUST);

通过com.jspxcms.common.fulltext.DefaultLuceneIndexTemplate

实际交由org.apache.lucene.search.IndexSearcher执行查询
