package com.sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;

import com.sip.Main;

/**
 * Created by ankurkhandelwal on 14/12/15.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall sipAudioCall=null;
        try {
            SipAudioCall.Listener listener= new SipAudioCall.Listener(){
                @Override
                public void onRinging(SipAudioCall call, SipProfile profile){
                    try {
                        call.answerCall(30);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            Main mainActivity=(Main) context;
            sipAudioCall.answerCall(30);
            sipAudioCall.startAudio();
            sipAudioCall.setSpeakerMode(true);
            if(sipAudioCall.isMuted()){
                sipAudioCall.toggleMute();
            }
            mainActivity.call=sipAudioCall;
            mainActivity.updateStatus(sipAudioCall);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
