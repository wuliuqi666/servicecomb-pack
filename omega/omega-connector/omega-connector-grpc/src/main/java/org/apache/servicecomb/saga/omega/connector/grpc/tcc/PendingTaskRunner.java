/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.servicecomb.saga.omega.connector.grpc.tcc;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PendingTaskRunner {

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  private final LoadBalanceSenderContext loadContext;

  public PendingTaskRunner(LoadBalanceSenderContext loadContext) {
    this.loadContext = loadContext;
  }

  public void start() {
    scheduler.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        try {
          loadContext.getPendingTasks().take().run();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }, 0, loadContext.getReconnectDelay(), MILLISECONDS);
  }

  public void shutdown() {
    scheduler.shutdown();
  }
}