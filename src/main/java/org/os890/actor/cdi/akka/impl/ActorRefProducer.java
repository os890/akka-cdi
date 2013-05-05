/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.os890.actor.cdi.akka.impl;

import akka.actor.*;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.os890.actor.cdi.akka.api.Actor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class ActorRefProducer
{
    @Produces
    @Dependent
    @Actor(type = akka.actor.Actor.class /*just used as placeholder*/)
    protected ActorRef createActorRef(InjectionPoint injectionPoint, ActorSystemProducer actorSystemProducer)
    {
        final Actor actorQualifier = injectionPoint.getAnnotated().getAnnotation(Actor.class);

        ActorSystem actorSystem = actorSystemProducer.getActorSystem(actorQualifier.systemName());

        if (!UntypedActor.class.isAssignableFrom(actorQualifier.type()))
        {
            actorSystem.actorOf(new Props(actorQualifier.type()));
        }

        return actorSystem.actorOf(new Props(new UntypedActorFactory()
        {
            private static final long serialVersionUID = 8739310463390426896L;

            public UntypedActor create()
            {
                return (UntypedActor) BeanProvider.getContextualReference(actorQualifier.type());
            }
        }));
    }
}
