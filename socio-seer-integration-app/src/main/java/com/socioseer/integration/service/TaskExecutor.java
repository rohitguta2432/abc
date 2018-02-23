package com.socioseer.integration.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public class TaskExecutor {

  private static final ExecutorService executorService = new ThreadPoolExecutor(50, 200, 5,
      TimeUnit.SECONDS, new LinkedBlockingQueue<>(500), new ThreadPoolExecutor.CallerRunsPolicy());

  /**
   * 
   * @param task
   */
  public static void execute(Runnable task) {
    executorService.execute(task);
  }

}
