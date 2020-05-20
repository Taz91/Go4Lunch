package com.syc.go4lunch.utils;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    private static final String WORK_RESULT = "work_result";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data taskData = getInputData();
        String taskDataString = taskData.getString(WORK_RESULT);

        //get data from API NYT
        //loadData(taskDataString);

        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();
        //return Result.retry();
        return Result.success(outputData);
    }

}
