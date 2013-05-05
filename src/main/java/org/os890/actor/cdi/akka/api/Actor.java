package org.os890.actor.cdi.akka.api;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD })
public @interface Actor
{
    @Nonbinding
    Class<? extends akka.actor.Actor> type();

    @Nonbinding
    String systemName() default ActorSystemName.AKKA_DEFAULT;
}
