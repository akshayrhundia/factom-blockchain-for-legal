package controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import persistence.Auth;
import persistence.PersistPrivateKeys;
import persistence.PersistPublicKeys;
import utils.GenerateKeys;
import utils.HTTPClient;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Path("/chain")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChainController extends HttpServlet {
    @Context
    private HttpHeaders httpHeaders;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();


    @POST
    @Path("/auth_user")
    public Response auth_user(String params) {
        try {
            Base64 base64 = new Base64();
            Auth auth=new Auth();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            String chain_id = o.get("chainid").getAsString();
            JsonArray jsonArray = o.get("users").getAsJsonArray();
            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.size();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).getAsString());
                }
            }
            for(String user:list){
                auth.updateUsersOnChain(chain_id,user);
            }

            return Response.status(HttpStatus.SC_OK).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
    @POST
    @Path("/create_case")
    public Response create_case(String params) {
        try {
            Base64 base64 = new Base64();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            String chain_id = o.get("parentid").getAsString();
            JsonArray jsonArray = o.get("childid").getAsJsonArray();
            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.size();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).getAsString());
                }
            }
            String content_ = new String(base64.encode(gson.toJson(list).getBytes()));
            ArrayList<String> ext = new ArrayList<String>();
            String s="case-chain";
            ext.add(new String(base64.encode(s.getBytes())));
            Map<String,Object> myMap=new HashMap<String,Object>();
            myMap.put("content",content_);
            myMap.put("external_ids",ext);
            String resp=HTTPClient.sendPost("https://apiplus-api-sandbox-testnet.factom.com/v1/chains/" + chain_id + "/entries",gson.toJson(myMap));
            return Response.status(HttpStatus.SC_OK).entity(resp).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/addentry")
    public Response register(String params) {
        try {
            Base64 base64 = new Base64();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            String chain_id = o.get("chain_id").getAsString();
            JsonArray jsonArray = o.get("external_ids").getAsJsonArray();
            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.size();
                for (int i=0;i<len;i++){
                    list.add(new String(base64.encode(jsonArray.get(i).getAsString().getBytes())));
                }
            }
            System.out.println("-------------------");
            HashMap<String, Object> map = new Gson().fromJson(o.get("content"), new TypeToken<HashMap<String, Object>>() {
                }.getType());
            String content_ = new String(base64.encode(gson.toJson(map).getBytes()));
            System.out.println(content_);
            Map<String,Object> myMap=new HashMap<String,Object>();
            myMap.put("content",content_);
            myMap.put("external_ids",list);
            myMap.put("callback_url","https://factom-java-dot-ntm-dev-202213.appspot.com/case/callback");
            String resp=HTTPClient.sendPost("https://apiplus-api-sandbox-testnet.factom.com/v1/chains/" + chain_id + "/entries",gson.toJson(myMap));
            return Response.status(HttpStatus.SC_OK).entity(resp).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/getsignedpayload")
    public Response getsignedpayload(String params) {
        try {
            Base64 base64 = new Base64();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            String user = o.get("user").getAsString();
            PersistPrivateKeys persistPrivateKeys=new PersistPrivateKeys();
            PrivateKey privateKey=persistPrivateKeys.getPrivateKey(user);
            String payload = o.get("payload").getAsString();
            String signed=signedPayload(privateKey,payload,user);
            return Response.status(HttpStatus.SC_OK).entity(signed).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/getall")
    public Response getAll() {
        try {
            String resp=HTTPClient.sendGet("https://apiplus-api-sandbox-testnet.factom.com/v1/chains");
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(resp).getAsJsonObject();
            JsonArray jsonArray=o.getAsJsonArray("items");
            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.size();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).getAsJsonObject().get("chain_id").getAsString());
                }
            }
            return Response.status(HttpStatus.SC_OK).entity(list).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
    @POST
    @Path("/getall")
    public Response getAllChainsByExtId(String params) {
        try {
            Base64 base64 = new Base64();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            JsonArray jsonArray = o.get("external_ids").getAsJsonArray();
            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.size();
                for (int i=0;i<len;i++){
                    list.add(new String(base64.encode(jsonArray.get(i).getAsString().getBytes())));
                }
            }
            Map<String,Object> myMap=new HashMap<String,Object>();
            myMap.put("external_ids",list);
            String resp=HTTPClient.sendPost("https://apiplus-api-sandbox-testnet.factom.com/v1/chains/search",gson.toJson(myMap));
            JsonObject or = parser.parse(resp).getAsJsonObject();
            JsonArray jsonArrayr=or.getAsJsonArray("items");
            ArrayList<String> listr = new ArrayList<String>();
            if (jsonArrayr != null) {
                int len = jsonArrayr.size();
                for (int i=0;i<len;i++){
                    listr.add(jsonArrayr.get(i).getAsJsonObject().get("chain_id").getAsString());
                }
            }
            return Response.status(HttpStatus.SC_OK).entity(listr).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    public  String signedPayload(PrivateKey privateKey,String payload,String user){
        try {
            //Map<String,Object> map=new HashMap<String, Object>();
            System.out.println("Payload: "+payload);
            //map.put("payload",payload);
            byte[] signature=sign(payload,privateKey);
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("payload", payload);
            body.put("user", user);
            System.out.println("Payload: "+user);
            body.put("signature", new String(com.google.api.client.util.Base64.encodeBase64(signature)));
            return new Gson().toJson(body);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public static byte[] sign(String data, PrivateKey privateKey) throws InvalidKeyException, Exception{
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(privateKey);
        rsa.update(data.getBytes());
        return rsa.sign();
    }

}
