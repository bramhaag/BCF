package me.bramhaag.bcf.annotations;

public @interface Flag {
    String name();
    boolean nullable() default true;
    boolean required() default false;
}
