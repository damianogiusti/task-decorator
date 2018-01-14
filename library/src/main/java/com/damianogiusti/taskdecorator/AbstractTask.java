package com.damianogiusti.taskdecorator;

import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.damianogiusti.taskdecorator.contracts.CancelableTask;
import com.damianogiusti.taskdecorator.contracts.Task;
import com.damianogiusti.taskdecorator.internal.Function;
import com.damianogiusti.taskdecorator.internal.UnaryFunction;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public abstract class AbstractTask<I, O> implements Task<I, O> {

  private AsyncTaskFactory asyncTaskFactory = new AsyncTaskFactory();
  private AsyncTask<I, Void, Object> asyncTask;

  UnaryFunction<O> onSuccess = null;
  UnaryFunction<Throwable> onError = null;
  Function onStarted = null;
  Function onCompleted = null;
  Task chainedTask = null;
  Task subsequentTask = null;
  Object subsequentParam = null;

  protected abstract O run(I param) throws Exception;

  ///////////////////////////////////////////////////////////////////////////
  // Task
  ///////////////////////////////////////////////////////////////////////////

  @NonNull @Override public final Task<I, O> onSuccess(@NonNull UnaryFunction<O> block) {
    if (onSuccess == null) {
      onSuccess = block;
    }
    return this;
  }

  @NonNull @Override public final Task<I, O> onError(@NonNull UnaryFunction<Throwable> block) {
    if (onError == null) {
      onError = block;
    }
    return this;
  }

  @NonNull @Override public final Task<I, O> onStarted(@NonNull Function block) {
    if (onStarted == null) {
      onStarted = block;
    }
    return this;
  }

  @NonNull @Override public final Task<I, O> onCompleted(@NonNull Function block) {
    if (onCompleted == null) {
      onCompleted = block;
    }
    return this;
  }

  ///////////////////////////////////////////////////////////////////////////
  // ExecutableTask
  ///////////////////////////////////////////////////////////////////////////


  @NonNull @Override public CancelableTask execute() {
    return execute(null);
  }

  @NonNull @Override public CancelableTask execute(@Nullable I param) {
    if (asyncTask == null) {
      asyncTask = asyncTaskFactory.createAsyncTask(this);
      //noinspection unchecked
      asyncTask.execute(param);
    }
    return this;
  }

  ///////////////////////////////////////////////////////////////////////////
  // ChainableTask
  ///////////////////////////////////////////////////////////////////////////

  @NonNull @Override public <O2, T extends Task<O, O2>> Task<I, O> chainWith(@NonNull T other) {
    if (chainedTask == null) {
      chainedTask = other;
    }
    return this;
  }

  @NonNull @Override public <O2> Task<Void, O> andThen(@NonNull Task<Void, O2> other) {
    return (Task<Void, O>) andThen(other, null);
  }

  @NonNull @Override public <I2, O2> Task<I, O> andThen(@NonNull Task<I2, O2> other, I2 param) {
    if (subsequentTask == null) {
      subsequentTask = other;
      subsequentParam = param;
    }
    return this;
  }

  ///////////////////////////////////////////////////////////////////////////
  // CancelableTask
  ///////////////////////////////////////////////////////////////////////////

  @CallSuper @Override public void cancel() {
    if (asyncTask != null) {
      asyncTask.cancel(true);
    }
    if (chainedTask != null) {
      chainedTask.cancel();
      chainedTask = null;
    }
    if (subsequentTask != null) {
      subsequentTask.cancel();
      subsequentTask = null;
    }
    onSuccess = null;
    onError = null;
    onStarted = null;
    onCompleted = null;
  }

  @Override public void addTo(@NonNull TaskBag bag) {
    bag.add(this);
  }
}
