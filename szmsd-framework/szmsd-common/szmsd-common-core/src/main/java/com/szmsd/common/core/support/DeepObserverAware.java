package com.szmsd.common.core.support;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * 一个对象实现该接口，即可具备正序或者倒序循环自身及所有深度成员变量的功能，观察实现指定接口的层级对象。
 * 例如：
 * <pre>
 *     @Data
 *     public static class A implements DeepObserverAware, ClassNamePrinter{
 *         private String label = "A";
 *         private B b = new B();
 *     }
 *
 *     @Data
 *     public static class B implements DeepObserverAware, ClassNamePrinter {
 *         private String name = "B";
 *         private C c = new C();
 *         private D d = new D();
 *     }
 *
 *     @Data
 *     public static class C implements DeepObserverAware, ClassNamePrinter {
 *         private String name = "C";
 *     }
 *
 *     @Data
 *     public static class D implements DeepObserverAware, ClassNamePrinter {
 *         private String name = "D";
 *     }
 *
 *     public interface ClassNamePrinter {
 *         default void print() {
 *             System.out.println(this.getClass().getName());
 *         }
 *     }
 * </pre>
 * 那么当执行以下代码的时候:
 * <pre>
 *     A a = new A();
 *     System.out.println("自上而下 subscribeOutGoing");
 *     a.subscribeOutGoing(ClassNamePrinter.class, classNamePrinter -> {
 *         classNamePrinter.print();
 *     });
 *     System.out.println("自下而上 subscribeIncoming");
 *     a.subscribeIncoming(ClassNamePrinter.class, classNamePrinter -> {
 *         classNamePrinter.print();
 *     });
 * </pre>
 * 会输出：
 * <pre>
 *     自上而下 subscribeOutGoing
 *     com.yj2025.sample.ObserverTest$A
 *     com.yj2025.sample.ObserverTest$B
 *     com.yj2025.sample.ObserverTest$C
 *     com.yj2025.sample.ObserverTest$D
 *     自下而上 subscribeIncoming
 *     com.yj2025.sample.ObserverTest$C
 *     com.yj2025.sample.ObserverTest$D
 *     com.yj2025.sample.ObserverTest$B
 *     com.yj2025.sample.ObserverTest$A
 * </pre>
 */
public interface DeepObserverAware {

    /**
     * 自上而下，观察当前及所有成员变量，当实例为指定类对象的时候，触发观察事件
     *
     * @param interfaceType 指定接口
     * @param consumer
     * @param <T>
     */
    default <T> void subscribeOutGoing(Class<T> interfaceType, Consumer<T> consumer) {
        Assert.state(interfaceType.isInterface(), interfaceType.getName() + "不是一个接口");
        Class<?>[] interfaces = this.getClass().getInterfaces();
        boolean anyMatch = Arrays.stream(interfaces).anyMatch(aClass -> aClass.isAssignableFrom(interfaceType));
        if (anyMatch) {
            consumer.accept((T) this);
        }
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object fieldValue = ReflectionUtils.getField(declaredField, this);
            if (fieldValue == null) {
                continue;
            }
            if (fieldValue instanceof DeepObserverAware) {
                ((DeepObserverAware) fieldValue).subscribeOutGoing(interfaceType, consumer);
            }
            if (fieldValue.getClass().isArray()) {
                for (T t : ((T[]) fieldValue)) {
                    ((DeepObserverAware) t).subscribeOutGoing(interfaceType, consumer);
                }
            }
            if (fieldValue instanceof Iterable) {
                ((Iterable<?>) fieldValue).forEach(o -> {
                    if (o instanceof DeepObserverAware) {
                        ((DeepObserverAware) o).subscribeOutGoing(interfaceType, consumer);
                    }
                });
            }
        }
    }

    /**
     * 自下而上，观察所有成员变量及当前，当实例为指定类对象的时候，触发观察事件
     *
     * @param interfaceType 指定接口
     * @param consumer
     * @param <T>
     */
    default <T> void subscribeIncoming(Class<T> interfaceType, Consumer<T> consumer) {
        Assert.state(interfaceType.isInterface(), interfaceType.getName() + "不是一个接口");
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object fieldValue = ReflectionUtils.getField(declaredField, this);
            if (fieldValue == null) {
                continue;
            }
            if (fieldValue instanceof DeepObserverAware) {
                ((DeepObserverAware) fieldValue).subscribeOutGoing(interfaceType, consumer);
            }
            if (fieldValue.getClass().isArray()) {
                for (T t : ((T[]) fieldValue)) {
                    ((DeepObserverAware) t).subscribeOutGoing(interfaceType, consumer);
                }
            }
            if (fieldValue instanceof Iterable) {
                ((Iterable<?>) fieldValue).forEach(o -> {
                    if (o instanceof DeepObserverAware) {
                        ((DeepObserverAware) o).subscribeOutGoing(interfaceType, consumer);
                    }
                });
            }
        }
        Class<?>[] interfaces = this.getClass().getInterfaces();
        boolean anyMatch = Arrays.stream(interfaces).anyMatch(aClass -> aClass.isAssignableFrom(interfaceType));
        if (anyMatch) {
            consumer.accept((T) this);
        }
    }

}
