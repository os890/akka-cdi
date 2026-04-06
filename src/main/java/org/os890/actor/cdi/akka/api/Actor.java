/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.os890.actor.cdi.akka.api;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;
import org.apache.pekko.actor.AbstractActor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier annotation for injecting Pekko {@link ActorRef} instances.
 *
 * <p>Annotate an injection point of type {@link org.apache.pekko.actor.ActorRef}
 * with this qualifier to specify which actor class to instantiate and,
 * optionally, which actor system to use.
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD})
public @interface Actor
{
    /**
     * Returns the concrete {@link AbstractActor} subclass that this actor
     * reference should wrap.
     *
     * @return the actor implementation class
     */
    @Nonbinding
    Class<? extends AbstractActor> type();

    /**
     * Returns the name of the actor system used to create this actor.
     *
     * @return the actor system name, defaults to {@link ActorSystemName#PEKKO_DEFAULT}
     */
    @Nonbinding
    String systemName() default ActorSystemName.PEKKO_DEFAULT;
}
