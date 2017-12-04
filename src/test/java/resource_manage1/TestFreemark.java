package resource_manage1;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class TestFreemark {
    public static void main(String[] args) {
        Configuration cfg = initConfig();
        Map<String, Object> root = buildBusiness();
        try {
            Template temp = cfg.getTemplate("test.ftl");
            Writer out = new OutputStreamWriter(System.out);
            temp.process(root, out);
        } catch (TemplateNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedTemplateNameException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> buildBusiness() {
        // Create the root hash
            Map<String, Object> root = new HashMap<>();
            // Put string ``user'' into the root
            root.put("user", "Big Joe");
            // Create the hash for ``latestProduct''
            Map<String, Object> latest = new HashMap<>();
            // and put it into the root
            root.put("latestProduct", latest);
            // put ``url'' and ``name'' into latest
            latest.put("url", "products/greenmouse.html");
            latest.put("name", "green mouse");
            Product latestProducts = getLatestProductFromDatabaseOrSomething();
            root.put("latestProduct", latestProducts);
            return root;
    }

    private static Product getLatestProductFromDatabaseOrSomething() {
        return new Product("http://www.test.com", "dddd");
    }

    private static Configuration initConfig() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        try {
            cfg.setDirectoryForTemplateLoading(new File("E:\\sts-bundle3.9.0\\workspace\\resource_manage1\\src\\test\\java"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }
}
