package io.temporal.samples.nexuscancellation.caller;

import static io.temporal.samples.nexus.service.NexusService.Language.*;

import io.temporal.failure.CanceledFailure;
import io.temporal.failure.NexusOperationFailure;
import io.temporal.samples.nexus.service.NexusService;
import io.temporal.workflow.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class HelloCallerWorkflowImpl implements HelloCallerWorkflow {
  public static final Logger log = Workflow.getLogger(HelloCallerWorkflowImpl.class);
  private static final NexusService.Language[] languages =
      new NexusService.Language[] {EN, FR, DE, ES, TR};
  NexusService nexusService =
      Workflow.newNexusServiceStub(
          NexusService.class,
          NexusServiceOptions.newBuilder()
              .setOperationOptions(
                  NexusOperationOptions.newBuilder()
                      .setScheduleToCloseTimeout(Duration.ofSeconds(10))
                      // Set the cancellation type to WAIT_REQUESTED. This means that the caller
                      // will wait for the cancellation request to be received by the handler before
                      // proceeding with the cancellation.
                      //
                      // By default, the caller would wait until the operation is completed.
                      .setCancellationType(NexusOperationCancellationType.WAIT_REQUESTED)
                      .build())
              .build());

  @Override
  public String hello(String message) {
    List<Promise<NexusService.HelloOutput>> results = new ArrayList<>(languages.length);

    /*
     * Create our CancellationScope. Within this scope we call the nexus operation asynchronously
     * hello method asynchronously for each of our defined languages.
     */
    CancellationScope scope =
        Workflow.newCancellationScope(
            () -> {
              for (NexusService.Language language : languages) {
                results.add(
                    Async.function(
                        nexusService::hello, new NexusService.HelloInput(message, language)));
              }
            });

    /*
     * Execute all nexus operations within the CancellationScope. Note that this execution is
     * non-blocking as the code inside our cancellation scope is also non-blocking.
     */
    scope.run();

    // We use "anyOf" here to wait for one of the nexus operation invocations to return
    NexusService.HelloOutput result = Promise.anyOf(results).get();

    // Trigger cancellation of all uncompleted nexus operations invocations within the cancellation
    // scope
    scope.cancel();
    // Wait for all nexus operations to receive a cancellation request before
    // proceeding.
    //
    // Note: Once the workflow completes any pending cancellation requests are dropped by the
    // server. In general, it is a good practice to wait for all cancellation requests to be
    // processed before completing the workflow.
    for (Promise<NexusService.HelloOutput> promise : results) {
      try {
        promise.get();
      } catch (NexusOperationFailure e) {
        // If the operation was cancelled, we can ignore the failure
        if (e.getCause() instanceof CanceledFailure) {
          log.info("Operation was cancelled");
          continue;
        }
        throw e;
      }
    }
    return result.getMessage();
  }
}
