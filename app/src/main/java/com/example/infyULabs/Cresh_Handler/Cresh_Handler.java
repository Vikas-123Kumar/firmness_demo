//Crash_Handler.java
//
//        Type
//        Java
//        Size
//        5 KB (5,459 bytes)
//        Storage used
//        5 KB (5,459 bytes)
//        Location
//        crash_handler
//        Owner
//        me
//        Modified
//        Jan 9, 2020 by me
//        Opened
//        11:23 PM by me
//        Created
//        Jan 9, 2020 with Google Drive Web
//        Add a description
//        Viewers can download
//        package org.bifwms.crash_handler;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.os.Build;
//import android.util.Log;
//
//import org.bifwms.SplashActivity;
//import org.bifwms._application.B_App;
//import org.bifwms.constant.Constants;
//import org.bifwms.log.Log;
//import org.bifwms.receivers.ConnectionDetector;
//import org.bifwms.util.C_Lite_utillity;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//
///**
// * Created by Crazy Programmer ツ on 12/23/2016.
// */
//
//public class Crash_Handler implements java.lang.Thread.UncaughtExceptionHandler {
//
//    private final String LINE_SEPARATOR = "\n";
//    private final Context myContext;
//    private final Class<?> myActivityClass;
//    private SharedPreferences shared;
//
//    public Crash_Handler(Context context, Class<?> c) {
//        myContext = context;
//        shared = B_App.getAppPref();
//        myActivityClass = c;
//    }
//
//    @Override
//    public void uncaughtException(Thread thread, Throwable exception) {
//        try {
//            StringWriter stackTrace = new StringWriter();
//            exception.printStackTrace(new PrintWriter(stackTrace));
//            //System.err.println(stackTrace);// You can use LogCat too
//            Intent intent = new Intent(myContext, SplashActivity.class);
//            String crash_logs = stackTrace.toString();
//
//            StringBuilder errorReport = new StringBuilder();
//            errorReport.append("************ CAUSE OF ERROR ************\n\n");
//            errorReport.append(crash_logs);
//            errorReport.append("\n************ DEVICE INFORMATION ***********\n");
//            //errorReport.append("User ID : " + shared.getString(Constants.USER_ID, ""));
//            errorReport.append("Brand: ");
//            errorReport.append(Build.BRAND);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("Device: ");
//            errorReport.append(Build.DEVICE);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("Model: ");
//            errorReport.append(Build.MODEL);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("Id: ");
//            errorReport.append(Build.ID);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("Product: ");
//            errorReport.append(Build.PRODUCT);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("\n************ FIRMWARE ************\n");
//            errorReport.append("SDK: ");
//            errorReport.append(Build.VERSION.SDK);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("Release: ");
//            errorReport.append(Build.VERSION.RELEASE);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("Incremental: ");
//            errorReport.append(Build.VERSION.INCREMENTAL);
//            errorReport.append(LINE_SEPARATOR);
//            errorReport.append("\n************ APP ************\n");
//            PackageInfo pInfo = null;
//            try {
//                pInfo = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0);
//                String version = pInfo.versionName;
//                int code = pInfo.versionCode;
//                errorReport.append("\nVersion name-");
//                errorReport.append(version);
//                errorReport.append("\nVersion code-");
//                errorReport.append(code);
//                errorReport.append("\nTime-");
//                errorReport.append(C_Lite_utillity.getDateTime(System.currentTimeMillis()));
//            } catch (Exception ex) {
//                // ignore
//            }
//            Log.e("Crash_Handler", " " + errorReport);
//
////            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
////                //crash_logs send to mail
////                intent.putExtra("error", errorReport.toString());
////            }
//
//
//
//            //  you can use this String to know what caused the exception and in which Activity
////          intent.putExtra("uncaughtException","Exception is: " + stackTrace.toString());
////          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //intent.setAction ("SplashActivity"); // see step 5.
//            //myContext.startActivity(intent);
//            //for restarting the Activity
//            //Process.killProcess(Process.myPid());
//            System.exit(0);
//
//        } catch (Exception ex) {
//            Log.e("Crash_Handler excpt", " " + ex.getMessage());
//        }
//    }
//
////    private void write_logs(String logs) {
////        try {
////            File logdir = new File(Constants.SD_CARD_LOGS);
////            File logfile = new File(Constants.SD_CARD_LOGS, "nfwms_logs.txt");
////            if (!logdir.exists()) {
////                logdir.mkdirs();
////            }
////            if (!logfile.exists())
////                logfile.createNewFile();
////            FileWriter writer = new FileWriter(logfile, true);
////            writer.append(logs);
////            writer.flush();
////            writer.close();
////        } catch (Exception ex) {
////            android.util.Log.e("write_logs excpt", "" + ex.getMessage());
////        }
////    }
//
//
//}