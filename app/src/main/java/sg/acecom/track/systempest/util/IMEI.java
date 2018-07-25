package sg.acecom.track.systempest.util;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;


public class IMEI {

	private Activity activity;
	public String device_id;
	
	public IMEI() {
	}

	public IMEI(Activity act) {
		this.activity = act;
	}


	public String getDeviceID() {
		return this.device_id;
	}
	
	public void setDeviceID() {
		TelephonyManager manager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		String id = manager.getDeviceId();
   	 
   	 	if (id == null) {
   	 		String android_id = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
   	 		id = android_id;
   	 	}
   	 
   	 	int phoneType = manager.getPhoneType();
   	 	switch (phoneType) {
   	 		case TelephonyManager.PHONE_TYPE_NONE:
   	 			System.out.println("phone type: NONE");
   	 			device_id =  id;
   	 		case TelephonyManager.PHONE_TYPE_GSM:
   	 			System.out.println("phone type: GSM");
   	 			device_id =  id;
   	 		case TelephonyManager.PHONE_TYPE_CDMA:
   	 			System.out.println("phone type: CDMA");
   	 			device_id =  id;
   	 		/*for API Level 11 or above
   	 		case TelephonyManager.PHONE_TYPE_SIP:
   	 			System.out.println("phone type: SIP");
   	 			device_id = "SIP";*/
   	 		default:
   	 			device_id =  id;
   	 	}
    }
}
