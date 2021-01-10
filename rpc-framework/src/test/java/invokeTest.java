import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author 陈一锋
 * @date 2021/1/9 17:08
 **/
public class invokeTest {


    @Test
    public void t1() {
        Student student = new Student();
        Field[] declaredFields = student.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println("---------------name----------");
            System.out.println(declaredField.getName());
            System.out.println("---------------type----------");
            System.out.println(declaredField.getType());
        }
    }

    @Test
    public void t2() throws IllegalAccessException {
        Student student = new Student();
        Field[] declaredFields = Student.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println("---------------name----------");
            System.out.println(declaredField.getName());
            System.out.println("---------------type----------");
            System.out.println(declaredField.getType());
            declaredField.setAccessible(true);
            declaredField.set(student,1);
        }

    }

    static class Student {
        private int i;
        private boolean b;
        private float f;
        private double d;
        private Integer integer;
        private Boolean aBoolean;
        private Float aFloat;
        private Double aDouble;
        private String name;
        private Dog dog;
        private ipTest ipTest;
    }

    static class Dog {

    }


}
