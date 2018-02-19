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
    private static final String a = "";
    private static final String a2 = "pgGtqOfpaP3YVTm4WlJUytaz28sQGyC0TKrmMB6+WG1IinsGJ1S3FUx8EHmguGXyTCxg/eLuSLmTNZdN9QaAUGNcQ7J+Y5fFzUk0P6y629g";
    private static final String a3= "P7gBe4iIMsIav91Vs/Gs+uIsR0iU9pAzGFeewkO3BkbmbXGH5WHDDrJTrEMYpMbG3kVm/OZKlcZZqenZ9F0CMxSASgElOFbShbBE6653WQE4SVs1663A6Skewmk3r6BwOrhtck6TMG793UxikT1bmooVkj+uYD+K21piXvlEjjAGD7Xb0lD1Adr6lPXommJD7+Po1rvMRJIxX0GoatAIQ9fx6jccswiWNweUYbd3jl3SWP+ZFOfydkscUzDnR17AGMBNX7qCD7rviWZhlUeuCeKWy6CpKeTsbO1/JT2OrZRW7B3LzZ+qGf1+Qfk8LAwreEw+0RilSJGY+0N524X2HZyDE3gBnkL25Nmm2sCgi/t2HaO0cfr1EaJNjAIGxyhNtIwSTkG7eEJo1oheOlTxgzjRpx6kx+OLThw0BLn/IzE9PT0y0GIHEf4JTwTa4eSs";
    private static final String a4 = "TST/HdZGy0oi11sfphgF9one/YJjy0karpsbnAJMgSQpIh1mXTZzxD/UYkgG6DhzDVaxA+++LxLXnd/4guUHkRkF8nEpklPBuCjfy5fxaVHFE4zbpK5yYScN3AkJrgBaMnOjUzjoErjRz4Y3k7bBkN0QQc+JdG8Cr+KvseE+vbE1Op5sl/r0Yg6AiUyWlTuAofQGS4M7lwMCjSxiUNfOTf5KjvbsTV7mbpAmqIvGPuu3JdC0poRH+MExFeCv8rlgNA+YT+O/wlBRwgLoPkpOWwp1UrtH5avCMu7WoKe3Es+46hjWaCqteM0JxPWN+r6cxey/pFHBMmPWMFu+jnUVufqeXQyHrOs6TsM1P33LKOVRy5zMKt+CB+rWzNJ4FO8EWHqMJqRzbgWyr1tE9gj8vLlNq2+Iftkk4tnPcHIADpffqkWR0axFy1aMvtB0JjPnt7nJZkcUwpUtdljITadfkaWgA7fEkcTRrmseBsVVxIkXx9R3vJKwt6rt9CDbO3O7hjerl3AyAA8dudWGX4eHOQ6DJIGAKTsbEuxcV4Ebo1BIKzPB9B0V3xsvKAOMz7pYr+LmPRAoB70OKFx1lpatRdPUmEJhRnhd1KioK2Gw43+1JpZyk3059X28XciiC53O";
    
    private static final String a5 = "";
    private static final String a6 = "";
    private static final String[] ifs = new String[] { };
    private static final String[] efs = new String[] { };

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

        if (!isCFiles(uriStr)) {
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

        LOG.d(TAG, "dc: " + uriStr);
        ByteArrayInputStream byteInputStream = null;
        try {
            SecretKey sy = new SecretKeySpec(a.getBytes("UTF-8"), "AES");
            SecretKey sy2 = new SecretKeySpec(a2.getBytes("UTF-8"), "AES");
            SecretKey sy3= new SecretKeySpec(a3.getBytes("UTF-8"), "AES");
            SecretKey sy4 = new SecretKeySpec(a4.getBytes("UTF-8"), "AES");
            Cipher cr = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cr.init(Cipher.DECRYPT_MODE, sy, new IvParameterSpec(a5.getBytes("UTF-8")));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(cipher.doFinal(bytes));
            byteInputStream = new ByteArrayInputStream(bos.toByteArray());

        } catch (Exception ex) {
            LOG.e(TAG, ex.getMessage());
        }

        return new CordovaResourceApi.OpenForReadResult(
                readResult.uri, byteInputStream, readResult.mimeType, readResult.length, readResult.assetFd);
    }

    private boolean isCFiles(String uri) {
        String checkPath = uri.replace("file:///android_asset/www/", "");
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
