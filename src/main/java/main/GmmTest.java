/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class GmmTest {
    AES aes = new AES();
    String key = "Sportsbook";
    String iv = "1234567890ABCDEF";
    String user = "User1";
    SimpleDateFormat sd = new SimpleDateFormat("yyMMddHHmmss");
    
    public static void main (String[] args) {
        
        AES aes = new AES();
        String key = "Sportsbook";
        String iv = "1234567890ABCDEF";
        try {
            //For Token
            String user = "User1";
            Date d = new Date(System.currentTimeMillis() + 600000);
            SimpleDateFormat sd = new SimpleDateFormat("yyMMddHHmmss");
            String timestamp = sd.format(d);
            String enctimestamp = Token.encrypt(timestamp);
            String token = aes.encrypt("User1|" + enctimestamp, key, iv);
            
            //For json message
            String json = "[{\"SportId\":23, \"Status\":1, \"bettypeId\":23, \"eventId\":880202, \"periodid\":2, \"level\":1}]";
//            String json = "[{\"SportId\":23, \"bettypeId\":23, \"eventId\":880202, \"odds\":1, \"periodid\":2, \"level\":1, \"selectiontypeid\":2}]";
            String encjson = aes.encrypt(json, key, iv); 
//            String encjson =encrypt(json);
//            String decjson =aes.decrypt(encjson, key, iv);
            
            String stringPost = "http://spi-gmmpub.188.uat/api/GMMForTA/UpdateMarketlineStatus";
//            String stringPost = "http://spi-gmmpub.188.uat/api/GMMForTA/UpdateOdds";
            System.out.println("System Time: " + new Date(System.currentTimeMillis()));
            System.out.println("Timestamp: " + timestamp);
            System.out.println("Token: " + aes.decrypt(token, key, iv));
            System.out.println();
            System.out.println(stringPost);
            System.out.println("Authorization-UserCode: " + user);
            System.out.println("Authorization-Token: " + token);
            System.out.println("Message: " + aes.decrypt(encjson, key, iv));
            System.out.println("Encrypted message: " + encjson);
            
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(stringPost);
            httppost.setHeader(new BasicHeader("Authorization-UserCode",user));
            httppost.setHeader(new BasicHeader("Authorization-Token",token));
            httppost.setHeader(new BasicHeader("Accept", "application/json"));
            httppost.setHeader(new BasicHeader("Content-type", "application/json"));
            
            StringEntity message = new StringEntity(encjson);
            httppost.setEntity(message);
            
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            System.out.println();
            System.out.println(response.getStatusLine() + "\n" + content);
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(GmmTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void send(String json, String url) {
        try {
            String timestamp = sd.format(new Date(System.currentTimeMillis() + 600000));
            String enctimestamp = Token.encrypt(timestamp);
            String token = aes.encrypt(user + "|" + enctimestamp, key, iv);
            
            
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader(new BasicHeader("Authorization-UserCode",user));
            httppost.setHeader(new BasicHeader("Authorization-Token",token));
            httppost.setHeader(new BasicHeader("Accept", "application/json"));
            httppost.setHeader(new BasicHeader("Content-type", "application/json"));
            
            String encjson = aes.encrypt(json, key, iv); 
            httppost.setEntity(new StringEntity(encjson));
            
            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity entity = response.getEntity();
//            String content = EntityUtils.toString(entity);
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
    }
    
//    public void sendOdds(int sportid, int bettypeid, long eventid, float odds, int periodid, int level, int selectiontypeid) {
    public void sendOdds(ArrayList<HashMap<String,String>> a) {
        String url = "http://spi-gmmpub.188.uat/api/GMMForTA/UpdateOdds";
        String json = genJSONA(a).toString();
        send(json,url);
    }
    
//    public void sendStatus(int sportid, int status, int bettypeid, long eventid, int periodid, int level) {
    public void sendStatus(ArrayList<HashMap<String,String>> a) {
        String url = "http://spi-gmmpub.188.uat/api/GMMForTA/UpdateMarketlineStatus";
        String json = genJSONA(a).toString();
        send(json,url);
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
    
    
}
