package com.damianogiusti.taskdecorator;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class AsyncTaskFactory {

  public <I, O> BaseAsyncTask<I, O> createAsyncTask(AbstractTask<I, O> realTask) {
    return new BaseAsyncTask<>(realTask);
  }
}
