package com.damianogiusti.taskdecorator.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
interface ChainableTask<I, O> {

  @NonNull <O2, T extends Task<O, O2>> Task<I, O> chainWith(@NonNull T other);

  @NonNull <I2, O2> Task<I, O> andThen(@NonNull Task<I2, O2> other, @Nullable I2 param);

  @NonNull <O2> Task<Void, O> andThen(@NonNull Task<Void, O2> other);

}
