package org.os890.test.actor.cdi.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.os890.actor.cdi.akka.api.Actor;
import org.os890.actor.cdi.akka.api.ActorSystemName;
import org.os890.actor.cdi.akka.impl.ActorRefProducer;
import org.os890.actor.cdi.akka.impl.ActorSystemProducer;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class InjectionTest
{
    @Deployment
    public static WebArchive createTestArchive()
    {
        return ShrinkWrap.create(WebArchive.class, "actor-cdi-akka-test.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClass(StandardActor1.class)
                .addClass(StandardActor2.class)
                .addClass(InjectionAwareActor.class)
                .addClass(TestService.class)

                .addAsLibraries(ShrinkWrap.create(JavaArchive.class, "actor-cdi-akka-lib.jar")
                        .addPackages(true, Actor.class.getPackage())
                        .addClass(ActorRefProducer.class)
                        .addClass(ActorSystemProducer.class));
    }

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
        assertTrue(standardActor.toString().contains("akka://default"));
    }

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
        assertTrue(alternativeActor.toString().contains("akka://alternativeActorSystem"));
    }

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
        assertTrue(injectionAwareActor.toString().contains("akka://default"));
    }

    @Test
    public void testInjectionOfActorSystem()
    {
        assertNotNull(defaultSystem);
        assertEquals(ActorSystem.create().name(), defaultSystem.name());
    }

    @Test
    public void testInjectionOfNamedActorSystem()
    {
        assertNotNull(namedSystem);
        assertEquals("mainActorSystem", namedSystem.name());
    }
}
