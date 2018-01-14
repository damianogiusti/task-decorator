package com.damianogiusti.taskdecorator;

import android.os.AsyncTask;

import com.damianogiusti.taskdecorator.contracts.Task;
import com.damianogiusti.taskdecorator.internal.Function;
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
      onStarted.invoke();
    }
  }

  @SafeVarargs @Override protected final Object doInBackground(I... params) {
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
    UnaryFunction<Throwable> onError = realTask.onError;
    Function onCompleted = realTask.onCompleted;
    boolean success = !(result instanceof Throwable);

    if (success) {
      if (onSuccess != null) {
        onSuccess.invoke((O) result);
      }
    } else {
      if (onError != null) {
        onError.invoke((Throwable) result);
      }
    }

    if (onCompleted != null) {
      onCompleted.invoke();
    }

    if (success) {
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
}
