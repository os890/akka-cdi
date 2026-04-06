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

package org.os890.test.actor.cdi.akka;

import jakarta.inject.Inject;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.junit.jupiter.api.Test;
import org.os890.actor.cdi.akka.api.Actor;
import org.os890.actor.cdi.akka.api.ActorSystemName;
import org.os890.cdi.addon.dynamictestbean.EnableTestBeans;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration test verifying CDI injection of Pekko actors and actor systems.
 *
 * <p>Uses the dynamic-cdi-test-bean-addon with full classpath scan to boot
 * a CDI SE container that discovers all beans automatically.
 */
@EnableTestBeans
public class InjectionTest
{
    @Inject
    @Actor(type = StandardActor1.class)
    private ActorRef standardActor;

    @Inject
    @Actor(type = StandardActor2.class, systemName = "alternativeActorSystem")
    private ActorRef alternativeActor;

    @Inject
    @Actor(type = InjectionAwareActor.class)
    private ActorRef injectionAwareActor;

    @Inject
    //uses the name of the field as system-name (because there is no  @ActorSystemName)
    private ActorSystem defaultSystem;

    @Inject
    @ActorSystemName(value = "mainActorSystem")
    private ActorSystem namedSystem;

    @Inject
    private TestService testService;

    /**
     * Verifies that a standard actor receives messages via the default actor system.
     *
     * @throws AssertionError if the actor does not receive the expected message
     */
    @Test
    public void testStandardActor()
    {
        assertNotNull(standardActor);

        standardActor.tell("hello akka with cdi", standardActor);
        try
        {
            StandardActor1.TEST_LATCH.await(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            fail();
        }

        assertEquals("hello akka with cdi", StandardActor1.message);
        assertTrue(standardActor.toString().contains("pekko://default"));
    }

    /**
     * Verifies that an actor on a named alternative actor system receives messages.
     *
     * @throws AssertionError if the actor does not receive the expected message
     */
    @Test
    public void testAlternativeActor()
    {
        assertNotNull(alternativeActor);

        alternativeActor.tell("hello akka with cdi", alternativeActor);
        try
        {
            StandardActor2.TEST_LATCH.await(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            fail();
        }

        assertEquals("hello akka with cdi", StandardActor2.message);
        assertTrue(alternativeActor.toString().contains("pekko://alternativeActorSystem"));
    }

    /**
     * Verifies that CDI beans are injected into actor instances and invoked correctly.
     *
     * @throws AssertionError if the injected service was not called by the actor
     */
    @Test
    public void testInjectionInActor()
    {
        assertNotNull(injectionAwareActor);

        injectionAwareActor.tell("hello injection aware actor", injectionAwareActor);
        try
        {
            InjectionAwareActor.TEST_LATCH.await(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            fail();
        }

        assertTrue(testService.isCalled());
        assertTrue(injectionAwareActor.toString().contains("pekko://default"));
    }

    /**
     * Verifies that a default {@link ActorSystem} is injected when no qualifier is present.
     */
    @Test
    public void testInjectionOfActorSystem()
    {
        assertNotNull(defaultSystem);
        assertEquals(ActorSystem.create().name(), defaultSystem.name());
    }

    /**
     * Verifies that a named {@link ActorSystem} is injected via {@link ActorSystemName}.
     */
    @Test
    public void testInjectionOfNamedActorSystem()
    {
        assertNotNull(namedSystem);
        assertEquals("mainActorSystem", namedSystem.name());
    }
}
