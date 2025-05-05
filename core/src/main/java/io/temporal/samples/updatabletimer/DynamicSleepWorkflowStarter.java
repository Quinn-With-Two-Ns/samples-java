

package io.temporal.samples.updatabletimer;

import static io.temporal.samples.updatabletimer.DynamicSleepWorkflowWorker.DYNAMIC_SLEEP_WORKFLOW_ID;
import static io.temporal.samples.updatabletimer.DynamicSleepWorkflowWorker.TASK_QUEUE;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicSleepWorkflowStarter {

  private static final Logger logger = LoggerFactory.getLogger(DynamicSleepWorkflowStarter.class);

  public static void main(String[] args) {
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    WorkflowClient client = WorkflowClient.newInstance(service);

    DynamicSleepWorkflow workflow =
        client.newWorkflowStub(
            DynamicSleepWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId(DYNAMIC_SLEEP_WORKFLOW_ID)
                .setWorkflowIdReusePolicy(
                    WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE)
                .build());

    try {
      // Start asynchronously
      WorkflowExecution execution =
          WorkflowClient.start(workflow::execute, System.currentTimeMillis() + 60000);
      logger.info("Workflow started: " + execution);
    } catch (WorkflowExecutionAlreadyStarted e) {
      logger.info("Workflow already running: " + e.getExecution());
    }
  }
}
