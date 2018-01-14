package com.damianogiusti.taskdecorator.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.damianogiusti.taskdecorator.internal.Function;
import com.damianogiusti.taskdecorator.internal.UnaryFunction;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
interface ExecutableTask<I> {

  @NonNull CancelableTask execute();

  @NonNull CancelableTask execute(@Nullable I param);
}
