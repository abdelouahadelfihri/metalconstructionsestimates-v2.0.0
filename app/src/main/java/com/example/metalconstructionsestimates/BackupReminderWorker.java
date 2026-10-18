package com.example.metalconstructionsestimates;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.metalconstructionsestimates.dbbackuprestore.BackUpRestore; // adjust to your actual backup activity

import java.util.concurrent.TimeUnit;

public class BackupReminderWorker extends Worker {

    private static final String CHANNEL_ID = "backup_reminder_channel";
    private static final String WORK_NAME = "backup_reminder";
    private static final String KEY_LAST_BACKUP_TIME = "last_backup_time";
    private static final long REMINDER_INTERVAL_DAYS = 7;

    /**
     * Schedules the weekly reminder. The first run is delayed by a full week:
     * without the delay WorkManager runs a new periodic request immediately.
     */
    public static void schedule(Context context, ExistingPeriodicWorkPolicy policy) {
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(BackupReminderWorker.class, REMINDER_INTERVAL_DAYS, TimeUnit.DAYS)
                        .setInitialDelay(REMINDER_INTERVAL_DAYS, TimeUnit.DAYS)
                        .build();
        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(WORK_NAME, policy, reminderRequest);
    }

    public static void cancel(Context context) {
        WorkManager.getInstance(context.getApplicationContext()).cancelUniqueWork(WORK_NAME);
    }

    /** Records a successful backup and restarts the one-week countdown from now. */
    public static void onBackupCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_LAST_BACKUP_TIME, System.currentTimeMillis()).apply();
        if (prefs.getBoolean(SettingsActivity.KEY_BACKUP_REMINDER, true)) {
            schedule(context, ExistingPeriodicWorkPolicy.REPLACE);
        }
    }

    public BackupReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(SettingsActivity.PREFS_SETTINGS, Context.MODE_PRIVATE);
        long lastBackup = prefs.getLong(KEY_LAST_BACKUP_TIME, 0L);
        boolean backedUpThisWeek = System.currentTimeMillis() - lastBackup
                < TimeUnit.DAYS.toMillis(REMINDER_INTERVAL_DAYS);
        if (prefs.getBoolean(SettingsActivity.KEY_BACKUP_REMINDER, true) && !backedUpThisWeek) {
            showNotification();
        }
        return Result.success();
    }

    private void showNotification() {
        Context context = getApplicationContext();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Backup Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Weekly reminder to back up your data");
            manager.createNotificationChannel(channel);
        }

        // Check POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Tap notification → open backup screen
        Intent intent = new Intent(context, BackUpRestore.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.database)
                .setContentTitle("Backup Reminder")
                .setContentText("Don't forget to back up your data this week.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("It's been a week since your last backup. Tap to open the backup screen and keep your data safe."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(1001, builder.build());
    }
}