package com.home.task_algofocus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by HOME on 13-06-2018.
 */

class GeocoderHandler  extends Handler {

    @Override
    public void handleMessage(Message message) {
        String result;
        switch (message.what) {
            case 1:
                Bundle bundle = message.getData();
                result = bundle.getString("address");
                break;
            default:
                result = null;
        }
        // replace by what you need to do
        //myLabel.setText(result);
    }
}