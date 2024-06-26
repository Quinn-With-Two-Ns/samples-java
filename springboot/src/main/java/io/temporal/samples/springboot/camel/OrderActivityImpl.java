/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.samples.springboot.camel;

import io.temporal.spring.boot.ActivityImpl;
import java.util.List;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "CamelSampleTaskQueue")
public class OrderActivityImpl implements OrderActivity {

  @Autowired private ProducerTemplate producerTemplate;

  @Override
  public List<OfficeOrder> getOrders() {
    producerTemplate.start();
    List<OfficeOrder> orders =
        producerTemplate.requestBody("direct:findAllOrders", null, List.class);
    producerTemplate.stop();
    return orders;
  }
}
