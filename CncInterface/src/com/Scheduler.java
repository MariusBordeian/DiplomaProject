package com;
/**
 * Created by Andrei on 11/8/2015.
 */
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler
{
    static ScheduledExecutorService executor;
    public static Coordinates coordinates;
    /**
     * Job that we need to run on scheduler
     */

    public synchronized void startThread() {
        executor = Executors.newSingleThreadScheduledExecutor();
        final Random randomGen=new Random();
        Runnable watchingTask = new Runnable() {
            public void run() {
                coordinates=new Coordinates(randomGen.nextFloat(),randomGen.nextFloat(),randomGen.nextFloat());
            }
        };

        executor.scheduleAtFixedRate(watchingTask, 0, 100, TimeUnit.MILLISECONDS);
    }

    public synchronized void stopThread() {
        executor.shutdown();
    }

}
