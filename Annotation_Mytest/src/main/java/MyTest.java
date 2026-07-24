import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//注解范围是标注在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTest {
}
