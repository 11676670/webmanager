package resource_manage1;

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
import freemarker.template.TemplateNotFoundException;

public class TranslateBig extends FreeMakerBase{
    public static void main(String[] args) {
        Configuration cfg = initConfig();
        Map<String, Object> root = buildBusiness();
        try {
            Template temp = cfg.getTemplate("translateBig.ftl");
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
        root.put("upper", new UpperDirective());
        return root;
    }
}