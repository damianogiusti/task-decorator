package com.damianogiusti.taskdecorator;

import com.damianogiusti.taskdecorator.contracts.Task;

import java.util.ArrayList;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class TaskBag extends ArrayList<Task> {

  @Override public void clear() {
    for (Task task : this) {
      task.cancel();
    }
    super.clear();
  }
}
