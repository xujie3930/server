import com.szmsd.system.api.task.I18nHandler;
import org.junit.Test;

public class I18nTest {


    @Test
    public void textTest(){
            String str = "100";
            I18nHandler i18nHandler = new I18nHandler();
            String a = i18nHandler.text(str);
            System.out.println(a);
    }
}
