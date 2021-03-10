package main;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.crypto.DataLengthException;
import org.json.JSONArray;
import org.json.JSONObject;

public class GMMConnector {
    AES aes = new AES();
    String key = "Sportsbook";
    String iv = "1234567890ABCDEF";
    String user = "User1";
    SimpleDateFormat sd = new SimpleDateFormat("yyMMddHHmmss");
    private String url = "http://spi-gmmpub.188.uat/";
//    String urlprod = "http://sgmm-spi-sbk-ext.sbk808.prod";
    
    private String send(String json, String u) {
        try {
            System.out.println("sending to " + u);
            System.out.println(json);
            String timestamp = sd.format(new Date(System.currentTimeMillis() + 600000));
            String enctimestamp = Token.encrypt(timestamp);
            String token = aes.encrypt(user + "|" + enctimestamp, key, iv);
            
            
            HttpPost httppost = new HttpPost(u);
            httppost.setHeader(new BasicHeader("Authorization-UserCode",user));
            httppost.setHeader(new BasicHeader("Authorization-Token",token));
            httppost.setHeader(new BasicHeader("Accept", "application/json"));
            httppost.setHeader(new BasicHeader("Content-type", "application/json"));
            
            String encjson = aes.encrypt(json, key, iv); 
            httppost.setEntity(new StringEntity(encjson));
            
            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            return response.getStatusLine() + "\n" + content;
//            System.out.println(response.getStatusLine() + "\n" + content);
        } catch (IOException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataLengthException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error sending";
    }
    
//    public void sendOdds(int sportid, int bettypeid, long eventid, float odds, int periodid, int level, int selectiontypeid) {
    public String sendOdds(ArrayList<HashMap<String,String>> a) {
        String u = getUrl() + "api/GMMForTA/UpdateOdds";
        String json = genJSONA(a).toString();
        return send(json,u);
    }
    
    public String sendOdds(String a) {
        String u = getUrl() + "api/GMMForTA/UpdateOdds";
        return send(a,u);
    }
    
//    public void sendStatus(int sportid, int status, int bettypeid, long eventid, int periodid, int level) {
    public String sendStatus(ArrayList<HashMap<String,String>> a) {
        String u = getUrl() + "api/GMMForTA/UpdateMarketlineStatus";
        String json = genJSONA(a).toString();
        return send(json,u);
    }
    
    public String sendStatus(String a) {
        String u = getUrl() + "api/GMMForTA/UpdateMarketlineStatus";
        return send(a,u);
    }
    
    private JSONArray genJSONA(ArrayList<HashMap<String,String>> a) {
        JSONArray ja = new JSONArray();
        for (HashMap<String,String> h : a) {
            ja.put(genJSON(h));
        }
        return ja;
    }
    
    private JSONObject genJSON(HashMap<String,String> h) {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, String> e : h.entrySet()) {
            
        }
        return jo;
    }

    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
