package controller;

import com.google.api.client.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpStatus;
import persistence.PersistPublicKeys;
import persistence.Users;
import utils.GenerateKeys;
import utils.HTTPClient;
import utils.StringUtil;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/member")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MSPController  extends HttpServlet {
    @Context
    private HttpHeaders httpHeaders;

    private Gson gson = new Gson();

    @POST
    @Path("/register")
    public Response register(String params) {
        GenerateKeys gk;
        try {
            org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();

            gk = new GenerateKeys(1024);
            gk.createKeys();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            JsonElement org = o.get("org");
            PersistPublicKeys persistPublicKeys = new PersistPublicKeys();
            persistPublicKeys.addPublicKey(gk.getPublicKey().getEncoded(), org.getAsString());
            Map<String, String> res = new HashMap<String, String>();
            res.put("key", new String(Base64.encodeBase64(gk.getPrivateKey().getEncoded())));
            System.out.println("------------------");
            List<String> lst=persistPublicKeys.getAllPublicKeys();
            List<String> hashes=new LinkedList<String>();
            for(String key:lst)
                hashes.add(StringUtil.applySha256(key));
            String content_ = new String(base64.encode(gson.toJson(hashes).getBytes()));
            ArrayList<String> ext = new ArrayList<String>();
            String s="key-hashed-record";
            ext.add(new String(base64.encode(s.getBytes())));
            Map<String,Object> myMap=new HashMap<String,Object>();
            myMap.put("content",content_);
            myMap.put("external_ids",ext);
            String resp= HTTPClient.sendPost("https://apiplus-api-sandbox-testnet.factom.com/v1/chains/f5b0e8b21cade3c0b1971ba83a382c60be0f1337681fc6694a7af549c5c95dd3/entries",gson.toJson(myMap));

            return Response.status(HttpStatus.SC_OK).entity(gson.toJson(res)).build();
        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/registerwithmykey")
    public Response registerwithmykey(String params) {
        System.out.println(params);
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(params).getAsJsonObject();
        JsonElement jkey = o.get("key");
        JsonElement jOrg = o.get("org");
        System.out.println(jOrg.getAsString());
        byte[] keyBytes = Base64.decodeBase64(jkey.getAsString());
        PersistPublicKeys persistPublicKeys = new PersistPublicKeys();
        persistPublicKeys.addPublicKey(keyBytes, jOrg.getAsString());
        return Response.status(HttpStatus.SC_OK).build();
    }

    @GET
    @Path("/getall")
    public Response getAll() {
        Users users=new Users();
        List<String> resp=users.getAllUsers();
        return Response.status(HttpStatus.SC_OK).entity(gson.toJson(resp)).build();
    }
}
