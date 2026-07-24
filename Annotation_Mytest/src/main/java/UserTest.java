public class UserTest {
    public static void main(String[] args)
            throws Exception {
        // 启动注解解释器
        TestRunner.run(UserTest.class);
    }
    @MyTest
    public void test1(){
        System.out.println(
                "执行测试方法test1"
        );
    }
    @MyTest
    public void test2(){
        System.out.println(
                "执行测试方法test2"
        );
    }
    public void normalMethod(){
        System.out.println(
                "普通方法，不执行"
        );

    }
}
