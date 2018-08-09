package com.serotonin.timer.sync;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncJobSink implements Runnable {
    private final ConcurrentLinkedQueue<Event> inbox = new ConcurrentLinkedQueue<Event>();

    private Thread thread;
    private volatile boolean running;

    public synchronized boolean initialize() {
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
            return true;
        }
        return false;
    }

    public synchronized void terminate() {
        if (thread != null) {
            running = false;
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                // no op
            }
            thread = null;
        }
    }

    public boolean add(Event event) {
        if (running)
            return inbox.offer(event);
        return false;
    }

    @Override
    public void run() {
        int processed = 0;

        while (true) {
            Event event = inbox.poll();
            if (event != null) {
                System.out.println("Processed " + event.getId());
                processed++;
            }
            else if (!running)
                break;
            else {
                System.out.println("null");
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    // no op
                }
            }
        }

        System.out.println("Exiting having processed " + processed);
    }

    static class Event {
        static int nextId = 0;

        private final String id;

        public Event() {
            id = Integer.toString(nextId++);
        }

        public String getId() {
            return id;
        }
    }

    public static void main(String[] args) throws Exception {
        AsyncJobSink sink = new AsyncJobSink();
        sink.initialize();

        long start = System.currentTimeMillis();

        int failed = 0;
        for (int i = 0; i < 100; i++) {
            Event event = new Event();
            if (!sink.add(event))
                failed++;
        }

        Thread.sleep(10000);
        sink.terminate();

        System.out.println("Failed to add " + failed);
        System.out.println("Runtime: " + (System.currentTimeMillis() - start));
    }
}
