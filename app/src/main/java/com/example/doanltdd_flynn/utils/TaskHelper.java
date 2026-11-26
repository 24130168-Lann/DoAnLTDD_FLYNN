package com.example.doanltdd_flynn.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.atomic.AtomicBoolean;


public final class TaskHelper {
    private static final long OFFLINE_TIMEOUT_MS = 1500;

    private TaskHelper() {
    }

    public static void callbackOnce(Task<Void> task,
                                    OnSuccessListener<Void> successListener,
                                    OnFailureListener failureListener) {
        final AtomicBoolean done = new AtomicBoolean(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable timeout = () -> {
            if (done.compareAndSet(false, true)) successListener.onSuccess(null);
        };
        handler.postDelayed(timeout, OFFLINE_TIMEOUT_MS);

        task.addOnSuccessListener(result -> {
            if (done.compareAndSet(false, true)) {
                handler.removeCallbacks(timeout);
                successListener.onSuccess(null);
            }
        });
        task.addOnFailureListener(e -> {
            if (done.compareAndSet(false, true)) {
                handler.removeCallbacks(timeout);
                failureListener.onFailure(e);
            }
        });
    }
}
