package dev.costas.minicli.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String name();
	String shortname() default "";
	String description() default "";
}