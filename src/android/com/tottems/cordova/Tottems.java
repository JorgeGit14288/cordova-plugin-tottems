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

    private static final String a = "";
    private static final String a5 = "";
    private static final String[] ifs = new String[] { };
    private static final String[] efs = new String[] { };

    @Override
    public Uri remapUri(Uri u) {
        if (u.toString().indexOf("/+++/") > -1) {
            return this.toPluginUri(u);
        } else {
            return u;
        }
    }

    @Override
    public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri u) throws IOException {
        Uri oriUri = this.fromPluginUri(u);
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

        LOG.d(TAG, "tottems: " + uriStr);
        ByteArrayInputStream byteInputStream = null;
        try {
            SecretKey skey = new SecretKeySpec(a.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(a5.getBytes("UTF-8")));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(cipher.doFinal(bytes));
            byteInputStream = new ByteArrayInputStream(bos.toByteArray());

        } catch (Exception ex) {
            LOG.e(TAG, ex.getMessage());
        }

        return new CordovaResourceApi.OpenForReadResult(
                readResult.uri, byteInputStream, readResult.mimeType, readResult.length, readResult.assetFd);
    }

    private boolean isCryptFiles(String u) {
        String checkPath = u.replace("file:///android_asset/www/", "");
        if (!this.hasMatch(checkPath, ifs)) {
            return false;
        }
        if (this.hasMatch(checkPath, efs)) {
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
