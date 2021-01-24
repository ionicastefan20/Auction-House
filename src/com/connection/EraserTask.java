package com.connection;

import static java.lang.System.*;

class EraserTask implements Runnable {
    private boolean stop;

    public EraserTask() {
    }

    public void run () {
        stop = true;
        out.print(" ");
        while (stop) {
            out.print("\010*");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMasking() {
        this.stop = false;
    }
}
