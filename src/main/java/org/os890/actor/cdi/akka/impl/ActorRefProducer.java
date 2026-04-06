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

package org.os890.actor.cdi.akka.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.Creator;
import org.os890.actor.cdi.akka.api.Actor;

/**
 * CDI producer that creates Pekko {@link ActorRef} instances for injection
 * points qualified with {@link Actor}.
 *
 * <p>Each produced actor is looked up through CDI so that the actor instance
 * itself can receive CDI injections.
 */
@ApplicationScoped
public class ActorRefProducer
{
    /**
     * Creates an {@link ActorRef} for the actor class specified by the
     * {@link Actor} qualifier on the injection point.
     *
     * @param injectionPoint      the CDI injection point requesting the actor
     * @param actorSystemProducer the producer that manages actor systems
     * @return a new {@code ActorRef} backed by a CDI-managed actor instance
     */
    @Produces
    @Dependent
    @Actor(type = AbstractActor.class)
    protected ActorRef createActorRef(InjectionPoint injectionPoint,
                                     ActorSystemProducer actorSystemProducer)
    {
        Actor actorQualifier = injectionPoint.getAnnotated().getAnnotation(Actor.class);
        ActorSystem actorSystem = actorSystemProducer.getActorSystem(actorQualifier.systemName());

        Class<? extends AbstractActor> actorType = actorQualifier.type();

        Creator<AbstractActor> creator = () ->
            CDI.current().select(actorType).get();

        return actorSystem.actorOf(Props.create(AbstractActor.class, creator));
    }
}
