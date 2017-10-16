/*
 * This code was generated by AWS Flow Framework Annotation Processor.
 * Refer to Amazon Simple Workflow Service documentation at http://aws.amazon.com/documentation/swf 
 *
 * Any changes made directly to this file will be lost when 
 * the code is regenerated.
 */
 package com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.StartWorkflowOptions;
import com.amazonaws.services.simpleworkflow.flow.WorkflowSelfClient;

/**
 * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow}. 
 * Used to continue a workflow execution as a new run.
 * Must be used from a worklfow scope. 
 */
public interface CronWithRetryWorkflowSelfClient extends WorkflowSelfClient
{

    /**
     * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow#startCron}
     */
    void startCron(com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowOptions options);

    /**
     * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow#startCron}
     */
    void startCron(com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowOptions options, Promise<?>... waitFor);

    /**
     * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow#startCron}
     */
    void startCron(com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowOptions options, StartWorkflowOptions optionsOverride, Promise<?>... waitFor);

    /**
     * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow#startCron}
     */
    void startCron(Promise<com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowOptions> options);

    /**
     * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow#startCron}
     */
    void startCron(Promise<com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowOptions> options, Promise<?>... waitFor);

    /**
     * Generated from {@link com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflow#startCron}
     */
    void startCron(Promise<com.amazonaws.services.simpleworkflow.flow.examples.cronwithretry.CronWithRetryWorkflowOptions> options, StartWorkflowOptions optionsOverride, Promise<?>... waitFor);
}