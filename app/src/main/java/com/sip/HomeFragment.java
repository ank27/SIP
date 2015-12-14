package com.sip;

import android.app.Activity;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by ankurkhandelwal on 14/12/15.
 */
public class HomeFragment extends Fragment {
    Button call_btn;
    static Activity activity;
    SipAudioCall call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        call_btn=(Button) rootView.findViewById(R.id.call_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateCall();
            }
        });
        return rootView;
    }

    public void initiateCall(){
        try {
            String sipAddress = "ankur27ur@sip.linphone.org";
            SipAudioCall.Listener listner=new SipAudioCall.Listener() {
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.setListener(this);
                    call.startAudio();
                    if (call.isMuted()) {
                        call.toggleMute();
                    }
                    Main.updateStatus(call);
                }
                @Override
                public void onCallEnded(SipAudioCall call){
                    Toast.makeText(getActivity().getApplicationContext(),"Ready",Toast.LENGTH_SHORT).show();
                }
            };
            call=Main.sipManager.makeAudioCall(Main.sipProfile.getUriString(),sipAddress,listner,30);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
