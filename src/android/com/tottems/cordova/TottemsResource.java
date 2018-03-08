package com.tottems.cordova;
import android.net.Uri;
import android.util.Base64;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class TottemsResource extends CordovaPlugin {
    private String asfhlasjdfjaldfja = "asdlkSPOQKJFLKASU-"; 
    private static final String TAG = "TottemsResource", bar2 = "4fd90e3567e37add69fe8f20891e63a24fd90e3567e37add69fe8f20891e63a24fd90e3567e37add69fe8f20891e63a2", bar3 = "avBfb5kkZsmzC2IWy/8BcWaVdFjUPvBD00vmfRGyk+U0VtgBacUdy7DbQKVEVaV0qS/hrNQTgM1HoNdvBFVhIA== ",nativek = "EoENktcdw6vzFa1kP75cGr9u8ooYKDfz/29a0",nativek2 = "EoEdsaw12Nktcdw6vzFa1kP75cGr9u8ooYKDfz",nativek3 = "EoENds32133111300dssdaktcdw6vzFa1kP75cGr9u8ooYKDfzd",nativek4 = "ee2dEoEdsaDSAANktcdw6vzFa1kP75cGr9u8oo",caseor2 = "fd90e3567e37add69fe8f20891e63a24fd90e3567e37",vcaseor2 = " 3029394848029393923883OeLk7WJuBJh5OMZVW8hHpGilqG/gFiy0DyzpLjNN0Y2S0piCc/3923900",kcaseor = " sA6+aZDpWW9jWPnaFnqjqhOeLk7WJuBJh5OMZVW8hHpGilqG/gFiy0DyzpLjNN0Y2S0piCc/hec5GjN",dcaseor = "ac2a724c8b73c8452455d8c7a220a9a6",caseor = "A3321od9j3??/kekwli39kdjkwjlwefejfiewjfjf32jiwejeiiwieiejr329rifhjdskiewiueuuehuwihewioqieiehhe";
    private static final String[] INCLUDE_FILES = new String[] { };
    private static final String[] EXCLUDE_FILES = new String[] { };
    private static final String o1_ = "";
    private static final String o2_ = "";
    private static final String o3_ = "";

    private static final String f1_ = "";
    private static final String f2_ = "";
    private static final String f3_ = "";

    @Override
    public Uri remapUri(Uri u) {
        if (u.toString().indexOf("/+++/") > -1) {return this.toPluginUri(u);} else {return u;}
    }
    @Override
    public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri u) throws IOException {
        String basdfasdfhjadflajdhfladf = "wkSAWWEoQdElikdjADEaF98"; 
                                                                      
        Uri a = this.fromPluginUri(u);
        String b = a.toString().replace("/+++/", "/").split("\\?")[0];
        CordovaResourceApi.OpenForReadResult r =  this.webView.getResourceApi().openForRead(Uri.parse(b), true);
        if (!mjolnir(b)) {
            return r;
        }
        String ac2a724c8b73c8452455d8c7a220a9a6 = "aASASDAtAiekdjfurhtl6T-"; 
        
        BufferedReader br = new BufferedReader(new InputStreamReader(r.inputStream));
        StringBuilder strb = new StringBuilder();
        String n = null;
        while ((n = br.readLine()) != null) {
            strb.append(n);
        }
        br.close();
        byte[] p = Base64.decode(strb.toString(), Base64.DEFAULT);
        LOG.d(TAG, "tottems: " + b);
        ByteArrayInputStream byteInputStream = null;
        try {
            
            SecretKey sy = new SecretKeySpec(jg().getBytes(asfhlasjdfjaldfja.substring(16,17)+ac2a724c8b73c8452455d8c7a220a9a6.substring(21,22)+basdfasdfhjadflajdhfladf.substring(20,21)+ asfhlasjdfjaldfja.substring(17,18)+basdfasdfhjadflajdhfladf.substring(22,23)), ac2a724c8b73c8452455d8c7a220a9a6.substring(8, 9)+basdfasdfhjadflajdhfladf.substring(10, 11)+asfhlasjdfjaldfja.substring(5,6));
            Cipher h = Cipher.getInstance(ac2a724c8b73c8452455d8c7a220a9a6.substring(8, 9)+basdfasdfhjadflajdhfladf.substring(10, 11)+asfhlasjdfjaldfja.substring(5,6)+"/CBC/PKCS5Padding");
            h.init(Cipher.DECRYPT_MODE, sy, new IvParameterSpec(gh().getBytes(asfhlasjdfjaldfja.substring(16,17)+ac2a724c8b73c8452455d8c7a220a9a6.substring(21,22)+basdfasdfhjadflajdhfladf.substring(20,21)+ asfhlasjdfjaldfja.substring(17,18)+basdfasdfhjadflajdhfladf.substring(22,23))));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(h.doFinal(p));
            byteInputStream = new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception ex) {
            LOG.e(TAG, ex.getMessage());
        }
        return new CordovaResourceApi.OpenForReadResult(
                r.uri, byteInputStream, r.mimeType, r.length, r.assetFd);
    }

private String jg (){
    return o1_.substring(0, 9)+o2_.substring(10, 22)+o3_.substring(22);
}

private String gh (){
    return f1_.substring(0, 3)+f2_.substring(4, 11)+f3_.substring(10);
}

    private boolean mjolnir(String u) {
        String c = u.replace("file:///android_asset/www/", "");
        if (!this.batman(c, INCLUDE_FILES)) {
            return false;
        }
        if (this.batman(c, EXCLUDE_FILES)) {
            return false;
        }
        return true;
    }
    private boolean batman(String text, String[] regexArr) {
        for (String regex : regexArr) {
            if (Pattern.compile(regex).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}