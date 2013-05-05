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

import akka.actor.ActorSystem;
import org.os890.actor.cdi.akka.api.ActorSystemName;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ActorSystemProducer
{
    private Map<String, ActorSystem> actorSystemMap = new ConcurrentHashMap<String, ActorSystem>();

    @Produces
    @Dependent
    @ActorSystemName(value = "placeholder")
    protected ActorSystem createNamedActorSystem(InjectionPoint injectionPoint)
    {
        return getActorSystem(injectionPoint.getAnnotated().getAnnotation(ActorSystemName.class).value());
    }

    @Produces
    @Dependent
    protected ActorSystem createDefaultActorSystem(InjectionPoint injectionPoint)
    {
        return getActorSystem(ActorSystemName.AKKA_DEFAULT);
    }

    public ActorSystem getActorSystem(String actorSystemName)
    {
        ActorSystem actorSystem = actorSystemMap.get(actorSystemName);
        if (actorSystem == null || actorSystem.isTerminated())
        {
            actorSystem = bootActorSystem(actorSystemName);
        }

        return actorSystem;
    }

    private synchronized ActorSystem bootActorSystem(String actorSystemName)
    {
        ActorSystem actorSystem = actorSystemMap.get(actorSystemName);
        if (actorSystem != null && !actorSystem.isTerminated())
        {
            return actorSystem;
        }

        actorSystem = ActorSystem.create(actorSystemName);
        actorSystemMap.put(actorSystemName, actorSystem);
        return actorSystem;
    }

    @PreDestroy
    protected void cleanup()
    {
        for (ActorSystem actorSystem : actorSystemMap.values())
        {
            if (!actorSystem.isTerminated())
            {
                actorSystem.shutdown();
            }
        }
    }
}
