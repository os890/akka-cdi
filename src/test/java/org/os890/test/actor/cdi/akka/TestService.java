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

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Simple application-scoped service used to verify CDI injection inside actors.
 */
@ApplicationScoped
public class TestService
{
    private boolean called;

    /**
     * Marks this service as having been called.
     */
    public void call()
    {
        this.called = true;
    }

    /**
     * Returns whether {@link #call()} has been invoked.
     *
     * @return {@code true} if {@link #call()} was invoked, {@code false} otherwise
     */
    public boolean isCalled()
    {
        return called;
    }
}
