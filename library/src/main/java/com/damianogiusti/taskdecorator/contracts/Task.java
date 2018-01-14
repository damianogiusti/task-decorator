package com.damianogiusti.taskdecorator.contracts;

import android.support.annotation.NonNull;

import com.damianogiusti.taskdecorator.internal.Function;
import com.damianogiusti.taskdecorator.internal.UnaryFunction;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public interface Task<I, O> extends ExecutableTask<I>, ChainableTask<I, O>, CancelableTask {

  @NonNull Task<I, O> onStarted(@NonNull Function block);

  @NonNull Task<I, O> onCompleted(@NonNull Function block);

  @NonNull Task<I, O> onSuccess(@NonNull UnaryFunction<O> block);

  @NonNull Task<I, O> onError(@NonNull UnaryFunction<Throwable> block);
}
