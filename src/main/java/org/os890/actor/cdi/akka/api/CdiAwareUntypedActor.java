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
package org.os890.actor.cdi.akka.api;

import akka.actor.UntypedActor;
import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * <b>optional</b> base-class to allow dependency injection in actors
 *
 * attention:
 * actor frameworks like akka aren't fully compatible with scopes which are bound to a thread like @RequestScoped.
 * however, that isn't the suggested approach aways.
 *
 * with DeltaSpike 0.4+ you can use @TransactionScoped services, but the transaction does get propagated across actors.
 *
 * if you e.g. start and stop such a context in with ContextControl of deltaspike, you have to do it within
 * {@link #onReceive(Object)}. however, it's important to stop it at the end,
 * because the current thread might be used by a different actor.
 */
public abstract class CdiAwareUntypedActor extends UntypedActor
{
    @Override
    public void preStart()
    {
        BeanProvider.injectFields(this);
    }

    @Override
    public void postRestart(Throwable reason)
    {
        BeanProvider.injectFields(this);
    }
}
