package com.damianogiusti.taskdecorator;

import android.os.AsyncTask;

import com.damianogiusti.taskdecorator.contracts.Task;
import com.damianogiusti.taskdecorator.internal.Function;
import com.damianogiusti.taskdecorator.internal.OnErrorNotImplementedException;
import com.damianogiusti.taskdecorator.internal.TaskUncaughtException;
import com.damianogiusti.taskdecorator.internal.UnaryFunction;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
class BaseAsyncTask<I, O> extends AsyncTask<I, Void, Object> {

  private AbstractTask<I, O> realTask;

  BaseAsyncTask(AbstractTask<I, O> realTask) {
    this.realTask = realTask;
  }

  @Override protected final void onPreExecute() {
    Function onStarted = realTask.onStarted;
    if (onStarted != null) {
      try {
        // Try to perform the onStarted block.
        onStarted.invoke();
      } catch (Exception e) {
        // If something goes wrong, dispatch the error.
        dispatchError(e);
      }
    }
  }

  @Override protected Object doInBackground(I... params) {
    try {
      I param = params[0];
      return realTask.run(param);
    } catch (Exception e) {
      return e;
    }
  }

  @SuppressWarnings("unchecked")
  @Override protected final void onPostExecute(Object result) {
    AbstractTask<I, O> realTask = this.realTask;
    if (realTask == null) return;

    UnaryFunction<O> onSuccess = realTask.onSuccess;
    Function onCompleted = realTask.onCompleted;
    boolean success = !(result instanceof Throwable);
    boolean executionFailed = false;

    if (success) {
      // We got a successful background execution.
      if (onSuccess != null) {
        try {
          // Try to perform the onSuccess block.
          onSuccess.invoke((O) result);
        } catch (Exception e) {
          // If something goes wrong, dispatch the error.
          dispatchError(e);
          executionFailed = true;
        }
      }
    } else {
      // The background execution failed with an error, so dispatch it.
      dispatchError((Throwable) result);
      executionFailed = true;
    }

    if (onCompleted != null && !executionFailed) {
      // If the onCompleted block was implemented and the execution was successful...
      try {
        // Try to perform the onCompleted block.
        onCompleted.invoke();
      } catch (Exception e) {
        // If something goes wrong, dispatch the error.
        dispatchError(e);
        executionFailed = true;
      }
    }

    // If the background execution succeeded and the execution was successful,
    // run the chained task and the subsequent task.
    if (success && !executionFailed) {
      Task chainedTask = realTask.chainedTask;
      if (chainedTask != null) {
        chainedTask.execute(result);
      }
      Task subsequentTask = realTask.subsequentTask;
      if (subsequentTask != null) {
        subsequentTask.execute(realTask.subsequentParam);
      }
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void dispatchError(Throwable error) {
    UnaryFunction<Throwable> block = realTask.onError;
    if (block != null) {
      try {
        // Try to perform the onError block.
        block.invoke(error);
      } catch (Exception e) {
        // If something goes wrong, throw an exception
        // since the onError block is faulty.
        throw new TaskUncaughtException(e);
      }
    } else {
      throw new OnErrorNotImplementedException();
    }
  }
}
