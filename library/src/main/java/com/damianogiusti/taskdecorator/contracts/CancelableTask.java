package com.damianogiusti.taskdecorator.contracts;

import android.support.annotation.NonNull;

import com.damianogiusti.taskdecorator.TaskBag;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public interface CancelableTask {

  void cancel();

  void addTo(@NonNull TaskBag bag);
}
