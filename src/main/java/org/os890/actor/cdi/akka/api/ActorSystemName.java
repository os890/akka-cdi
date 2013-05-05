package org.os890.actor.cdi.akka.api;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, PARAMETER, METHOD, FIELD })
public @interface ActorSystemName
{
    @Nonbinding
    String value();

    String AKKA_DEFAULT = "default";
}
