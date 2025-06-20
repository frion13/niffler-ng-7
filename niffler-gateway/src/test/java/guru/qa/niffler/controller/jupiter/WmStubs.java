package guru.qa.niffler.controller.jupiter;


import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(WiremockJsonExtension.class)
public @interface WmStubs {
    String[] value();
    int port() default 8080;
}
