package com.kalman03.apideploy.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.kalman03.apideploy.core.configuration.ApideployConfiguration;

/**
 * @author kalman03
 * @since 2023-08-18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ApideployConfiguration.class)
public @interface EnableApideploy {

}