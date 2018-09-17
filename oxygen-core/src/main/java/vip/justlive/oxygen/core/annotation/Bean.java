package vip.justlive.oxygen.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动创建单例bean
 * <br>
 * 可用在类上用于自动实例化
 * <br>
 * 也可用于Configuration类的方法上创建实例
 *
 * @author wubo
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

  /**
   * bean的id，默认使用class::getName
   *
   * @return bean的id
   */
  String value() default "";
}
