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
    private static final String TAG = "TottemsResource";
    private static final String bar2 = "4fd90e3567e37add69fe8f20891e63a24fd90e3567e37add69fe8f20891e63a24fd90e3567e37add69fe8f20891e63a2";
    private static final String bar3 = "avBfb5kkZsmzC2IWy/8BcWaVdFjUPvBD00vmfRGyk+U0VtgBacUdy7DbQKVEVaV0qS/hrNQTgM1HoNdvBFVhIA== ";
    private static final String nativek = "EoENktcdw6vzFa1kP75cGr9u8ooYKDfz/29a0";
    private static final String nativek2 = "EoEdsaw12Nktcdw6vzFa1kP75cGr9u8ooYKDfz";
    private static final String nativek3 = "EoENds32133111300dssdaktcdw6vzFa1kP75cGr9u8ooYKDfzd";
    private static final String nativek4 = "ee2dEoEdsaDSAANktcdw6vzFa1kP75cGr9u8oo";
    private static final String caseor2 = "fd90e3567e37add69fe8f20891e63a24fd90e3567e37";
    private static final String var1 = "";
    private static final String const1 = "";
    private static final String vcaseor2 = " 3029394848029393923883OeLk7WJuBJh5OMZVW8hHpGilqG/gFiy0DyzpLjNN0Y2S0piCc/3923900";
    private static final String kcaseor = " sA6+aZDpWW9jWPnaFnqjqhOeLk7WJuBJh5OMZVW8hHpGilqG/gFiy0DyzpLjNN0Y2S0piCc/hec5GjN";
    private static final String dcaseor = "ac2a724c8b73c8452455d8c7a220a9a6";
    private static final String caseor = "A3321od9j3??/kekwli39kdjkwjlwefejfiewjfjf32jiwejeiiwieiejr329rifhjdskiewiueuuehuwihewioqieiehhe";
    private static final String[] INCLUDE_FILES = new String[] { };
    private static final String[] EXCLUDE_FILES = new String[] { };
    @Override
    public Uri remapUri(Uri uri) {
        if (uri.toString().indexOf("/+++/") > -1) {
            return this.toPluginUri(uri);
        } else {
            return uri;
        }
    }
    @Override
    public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri) throws IOException {
        Uri oriUri = this.fromPluginUri(uri);
        String uriStr = oriUri.toString().replace("/+++/", "/").split("\\?")[0];
        CordovaResourceApi.OpenForReadResult readResult =  this.webView.getResourceApi().openForRead(Uri.parse(uriStr), true);
        if (!isCryptFiles(uriStr)) {
            return readResult;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(readResult.inputStream));
        StringBuilder strb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            strb.append(line);
        }
        br.close();
        byte[] bytes = Base64.decode(strb.toString(), Base64.DEFAULT);
        LOG.d(TAG, "decrypt: " + uriStr);
        ByteArrayInputStream byteInputStream = null;
        try {
            SecretKey sy = new SecretKeySpec(var1.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sy, new IvParameterSpec(const1.getBytes("UTF-8")));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(cipher.doFinal(bytes));
            byteInputStream = new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception ex) {
            LOG.e(TAG, ex.getMessage());
        }
        return new CordovaResourceApi.OpenForReadResult(
                readResult.uri, byteInputStream, readResult.mimeType, readResult.length, readResult.assetFd);
    }
    private boolean isCryptFiles(String uri) {
        String checkPath = uri.replace("file:///android_asset/www/", "");
        if (!this.hasMatch(checkPath, INCLUDE_FILES)) {
            return false;
        }
        if (this.hasMatch(checkPath, EXCLUDE_FILES)) {
            return false;
        }
        return true;
    }
    private boolean hasMatch(String text, String[] regexArr) {
        for (String regex : regexArr) {
            if (Pattern.compile(regex).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}