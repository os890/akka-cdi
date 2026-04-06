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

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.apache.pekko.actor.ActorSystem;
import org.os890.actor.cdi.akka.api.ActorSystemName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CDI producer that manages the lifecycle of Pekko {@link ActorSystem} instances.
 *
 * <p>Actor systems are cached by name and reused across injection points.
 * On CDI shutdown the producer terminates every active actor system.
 */
@ApplicationScoped
public class ActorSystemProducer
{
    private final Map<String, ActorSystem> actorSystemMap = new ConcurrentHashMap<>();

    /**
     * Produces a named {@link ActorSystem} for injection points qualified
     * with {@link ActorSystemName}.
     *
     * @param injectionPoint the CDI injection point requesting the actor system
     * @return the actor system whose name matches the qualifier value
     */
    @Produces
    @Dependent
    @ActorSystemName(value = "placeholder")
    protected ActorSystem createNamedActorSystem(InjectionPoint injectionPoint)
    {
        return getActorSystem(injectionPoint.getAnnotated().getAnnotation(ActorSystemName.class).value());
    }

    /**
     * Produces the default {@link ActorSystem} for injection points that do
     * not carry an {@link ActorSystemName} qualifier.
     *
     * @param injectionPoint the CDI injection point requesting the actor system
     * @return the default actor system
     */
    @Produces
    @Dependent
    protected ActorSystem createDefaultActorSystem(InjectionPoint injectionPoint)
    {
        return getActorSystem(ActorSystemName.PEKKO_DEFAULT);
    }

    /**
     * Returns the {@link ActorSystem} for the given name, creating it on first
     * access or if the previous instance has been terminated.
     *
     * @param actorSystemName the name of the actor system
     * @return the running actor system for that name
     */
    public ActorSystem getActorSystem(String actorSystemName)
    {
        ActorSystem actorSystem = actorSystemMap.get(actorSystemName);
        if (actorSystem == null || actorSystem.whenTerminated().isCompleted())
        {
            actorSystem = bootActorSystem(actorSystemName);
        }
        return actorSystem;
    }

    private synchronized ActorSystem bootActorSystem(String actorSystemName)
    {
        ActorSystem actorSystem = actorSystemMap.get(actorSystemName);
        if (actorSystem != null && !actorSystem.whenTerminated().isCompleted())
        {
            return actorSystem;
        }

        actorSystem = ActorSystem.create(actorSystemName);
        actorSystemMap.put(actorSystemName, actorSystem);
        return actorSystem;
    }

    /**
     * Terminates all managed actor systems during CDI container shutdown.
     */
    @PreDestroy
    protected void cleanup()
    {
        for (ActorSystem actorSystem : actorSystemMap.values())
        {
            if (!actorSystem.whenTerminated().isCompleted())
            {
                actorSystem.terminate();
            }
        }
    }
}
