package com.permis.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习参考类
 * Created by tangjun on 2016/11/9.
 */

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();

    private static final String dialogTitle     = "提醒：";
    private static final String dialogOk        = "确定";
    private static final String dialogCancle    = "取消";
    private static final String dialogStrHeader = "无法开启这个功能。"+"\n"+"请开启权限：";
    private static final String dialogStrBottom = "\n"+ "进入应用信息-->权限，开启权限";
    private static final String[] dialogStr={
            dialogStrHeader+"获取联系人权限",
            dialogStrHeader+"获取拨号权限",
            dialogStrHeader+"获取查看日历权限",
            dialogStrHeader+"获取相机权限",
            dialogStrHeader+"获取传感器权限",
            dialogStrHeader+"获取位置权限",
            dialogStrHeader+"获取读存储卡权限",
            dialogStrHeader+"获取麦克风权限",
            dialogStrHeader+"获取SMS卡权限",
    };

    //相应权限：这里只给出每组的代表权限
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;

    private static final String[] requestPermissions = {
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_CALL_PHONE,
            PERMISSION_READ_CALENDAR,
            PERMISSION_CAMERA,
            PERMISSION_BODY_SENSORS,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_RECORD_AUDIO,
            PERMISSION_READ_SMS
    };

    //对应的权限
    public static final int CODE_GET_ACCOUNTS           = 0;
    public static final int CODE_CALL_PHONE             = 1;
    public static final int CODE_READ_CALLENDAR         = 2;
    public static final int CODE_CAMERA                 = 3;
    public static final int CODE_BOYD_SENSORS           = 4;
    public static final int CODE_ACCESS_FINE_LOCATION   = 5;
    public static final int CODE_READ_EXTERNAL_STORAGE  = 6;
    public static final int CODE_RECORD_AUDIO           = 7;
    public static final int CODE_READ_SMS               = 8;

    public static final int CODE_MULTI_PERMISSION       = 100;

    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 判断是否授权
     * @param context   上下文
     * @param permission  获取权限
     * @return    授权与否
     */
    private static boolean selfPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getTargetVersion(context) >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }


    /**
     *  获取目标版本号
     */
    private static int getTargetVersion(Context context){
        int targetSdkVersion = -1;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
    }


    /**
     * activity下请求授权
     * @param activity
     * @param requestCode
     * @param permissionGrant
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }

        Log.i(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            throw  new IllegalAccessError("requestCode is error");
        }

        if(permissionGrant == null){
            throw  new IllegalAccessError("PermissionGrant == null ？");
        }

        final String requestPermission = requestPermissions[requestCode];

        if(selfPermissionGranted(activity,requestPermission)){
            Log.d(TAG, "ActivityCompat.checkSelfPermission == PackageManager.PERMISSION_GRANTED");
            permissionGrant.onPermissionGranted(requestCode);
        }else {
            Log.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                Log.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                shouldShowRationale(activity, requestCode, requestPermission);
            } else {
                Log.d(TAG, "requestCameraPermission else");
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
        }
    }

    /**
     * fragment下请求授权
     * @param fragment
     * @param requestCode
     * @param permissionGrant
     */
    public static void requestPermission(final Fragment fragment, final int requestCode, PermissionGrant permissionGrant){
        if(fragment == null){
            return;
        }

        Log.i(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            throw  new IllegalAccessError("requestCode is error");
        }

        if(permissionGrant == null){
            throw  new IllegalAccessError("PermissionGrant == null ？");
        }

        final String requestPermission = requestPermissions[requestCode];

        if(selfPermissionGranted(fragment.getActivity(),requestPermission)){
            Log.d(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
            permissionGrant.onPermissionGranted(requestCode);
        }else {
            Log.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");
            if (fragment.shouldShowRequestPermissionRationale(requestPermission)) {
                Log.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                shouldShowRationale(fragment, requestCode, requestPermission);
            } else {
                Log.d(TAG, "requestCameraPermission else");
                fragment.requestPermissions(new String[]{requestPermission}, requestCode);
            }
        }

    }

    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {

        final List<String> permissionsList = getNoGrantedPermission(activity, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);

        //TODO checkSelfPermission
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        Log.d(TAG, "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
            Log.d(TAG, "showMessageOKCancel requestPermissions");

        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, "should open those permission",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                                    CODE_MULTI_PERMISSION);
                            Log.d(TAG, "showMessageOKCancel requestPermissions");
                        }
                    });
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }

    }

    /**
     * @param activity
     * @param isShouldRationale true: return no granted and shouldShowRequestPermissionRationale permissions, false:return no granted and !shouldShowRequestPermissionRationale
     * @return
     */
    public static ArrayList<String> getNoGrantedPermission(Activity activity, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];

            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    Log.d(TAG, "shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {

                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    Log.d(TAG, "shouldShowRequestPermissionRationale else");
                }

            }
        }

        return permissions;
    }


    /**
     * activity下
     * 系统允许询问是否开启权限
     * @param activity
     * @param requestCode
     * @param requestPermission
     */
    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
        showMessageOKCancel(activity,dialogStr[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{requestPermission},
                        requestCode);
                Log.d(TAG, "showMessageOKCancel requestPermissions:" + requestPermission);
            }
        });
    }

    /**
     * fragment下
     * 系统允许询问是否开启权限
     * @param fragment
     * @param requestCode
     * @param requestPermission
     */
    private static void shouldShowRationale(final Fragment fragment, final int requestCode, final String requestPermission){
        showMessageOKCancel(fragment.getActivity(),dialogStr[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fragment.requestPermissions(new String[]{requestPermission}, requestCode);
                Log.d(TAG, "showMessageOKCancel requestPermissions:" + requestPermission);
            }
        });
    }

    /**
     * 自定义的Dialog
     * 提示：申请权限
     * @param activity
     * @param message
     * @param okListener
     */
    private static void showMessageOKCancel(final Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(message)
                .setPositiveButton(dialogOk, okListener)
                .setNegativeButton(dialogCancle, null)
                .create()
                .show();
    }


    /*************************************回调处理**************************************************/

    /** 回调处理结果，在activity中的onRequestPermissionsResult（）中调用
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        Log.d(TAG, "requestPermissionsResult requestCode:" + requestCode);

        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant);
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.w(TAG, "requestPermissionsResult illegal requestCode:" + requestCode);
//            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + ",permissions:" + permissions.toString()
                + ",grantResults:" + grantResults.toString() + ",length:" + grantResults.length);

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onRequestPermissionsResult PERMISSION_GRANTED");
            //TODO success, do something, can use callback
            permissionGrant.onPermissionGranted(requestCode);

        } else {
            //TODO hint user this permission function
            Log.i(TAG, "onRequestPermissionsResult PERMISSION NOT GRANTED");
            //TODO
            String[] permissionsHint = dialogStr;
            openSettingActivity(activity,permissionsHint[requestCode]);
        }

    }

    /**
     * 回调处理结果，在fragment中的onRequestPermissionsResult（）方法内调用。
     * @param fragment
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param permissionGrant
     */
    public static void requestPermissionsResult(final Fragment fragment, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {
        // 如果有子fragments可以传递至子fragment中
        if (fragment != null) {
            List<Fragment> fragments = fragment.getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment frag : fragments) {
                    if (frag != null) {
                        frag.onRequestPermissionsResult(requestCode,permissions,grantResults);
                    }
                }
            }
        }else {
            return;
        }

        requestPermissionsResult(fragment.getActivity(),requestCode,permissions,grantResults,permissionGrant);
    }


    /**
     * 处理同时申请多个权限
     * @param activity
     * @param permissions
     * @param grantResults
     * @param permissionGrant
     */
    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }

        //TODO
        Log.d(TAG, "onRequestPermissionsResult permissions length:" + permissions.length);
        Map<String, Integer> perms = new HashMap<>();

        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }

        if (notGranted.size() == 0) {
            Toast.makeText(activity, "all permission success" + notGranted, Toast.LENGTH_SHORT)
                    .show();
            permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
        } else {
            openSettingActivity(activity, "those permission need granted!");
        }

    }


    /**
     * 自定义的dialog
     * 提示框：跳转进入设置界面
     * @param activity
     * @param message
     */
    private static void openSettingActivity(final Activity activity, String message) {

        showGuideMessage(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    private static void showGuideMessage(final Activity activity, String message, DialogInterface.OnClickListener okListener) {
        message = message +dialogStrBottom;

        new AlertDialog.Builder(activity)
                .setTitle(dialogTitle)
                .setMessage(message)
                .setPositiveButton(dialogOk, okListener)
                .setNegativeButton(dialogCancle, null)
                .create()
                .show();
    }


}
