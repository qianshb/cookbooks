import java.lang.reflect.Method;

public class TestRunner {
  public static void run(Class clazz)  throws Exception{
    Object obj = clazz.getDeclaredConstructor().newInstance();
    
    Method[] methods = clazz.getDeclaredMethods();

    for(Method method:methods){
        if(
        method.isAnnotationPresent(
        MyTest.class)
        ){
            System.out.println(
                    "发现测试方法:"
                            + method.getName()
            );
            method.invoke(obj);
        }
    }
}
}