package org.scada_lts.serorepl.utils;

public abstract class ProgresiveTask implements Runnable {


    private boolean cancelled = false;
    protected boolean completed = false;
    private IProgresiveTaskListener listener;

    public ProgresiveTask() {
    }

    public ProgresiveTask(IProgresiveTaskListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true){
            if (isCancelled()){
                declareFinished(true);
            }else{
                runImplementation();
                if(!isCompleted()){
                    continue;
                }
                this.declareFinished(false);
            }
            completed = true;
            return;
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    private void declareFinished(boolean cancelled){
        if (listener != null){
            if (cancelled) {
                listener.taskCancelled();
            } else {
                listener.taskCompleted();
            }
        }
    }
    protected abstract void runImplementation();

}
