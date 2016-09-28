package com.oxbix.spanlogistics.recevied;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


/**
 * @author yzw
 * @version V1.0
 * @company 跨越速运
 * @Description
 * @date 2016/3/23
 */
public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = "SMSReceiver";
    public static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    public static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.d("SmsReceiver", "----发送短信成功---------------------------");
                Toast.makeText(context, "发送短信成功", Toast.LENGTH_SHORT).show();
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            default:
                Log.d("SmsReceiver", "----发送短信失败---------------------------");
                Toast.makeText(context, "发送短信失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
