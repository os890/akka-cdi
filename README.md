This lib shows a possible approach to integrate Akka and CDI.
Parts of it are done with Apache DeltaSpike, however, it's easy to replace (copy) the needed parts to avoid a dependency.

It's possible to inject ActorRef, ActorSystem and CDI beans in actors.

Injection of Akka artifacts:

    @Inject
    @Actor(type = MyActor.class)
    private ActorRef standardActor;

    @Inject
    @Actor(type = MyActor.class, systemName = "alternativeActorSystem")
    private ActorRef alternativeActor;

    @Inject
    @Actor(type = AdvancedActor.class)
    private ActorRef injectionAwareActor;

    @Inject
    private ActorSystem defaultSystem;

    @Inject
    @ActorSystemName(value = "mainActorSystem")
    private ActorSystem namedSystem;

Injection of ActorRef per name,... would be possible as well, however, this small lib shouldn't provide a full integration.
This lib should just show how easy it is to integrate CDI with actor frameworks like Akka.

Injection of actors:

    public class AdvancedActor extends CdiAwareUntypedActor
    {
        @Inject
        private MyService myService;

        @Override
        public void onReceive(Object o) throws Exception
        {
            myService.process(/*...*/);
        }
    }
