package resource_manage1;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Testfm {

    @Autowired  
    Configuration configuration; //freeMarker configuration  
      
    @Test  
    public void sendHtmlMailUsingFreeMarker() throws Exception {  
        Map<String, Object> model = new HashMap<String, Object>();  
        model.put("time", new Date());  
        model.put("message", "这是测试的内容。。。");  
        model.put("toUserName", "张三");  
        model.put("fromUserName", "老许");  
          
        Template t = configuration.getTemplate("welcome.ftl"); // freeMarker template  
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);  
        System.out.println(content);
        //mailService.sendHtmlMail(to, "主题：html邮件", content);  
    }  
}
