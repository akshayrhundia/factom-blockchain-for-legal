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

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            gk = new GenerateKeys(1024);
            gk.createKeys();
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            JsonElement org = o.get("org");
            PersistPublicKeys persistPublicKeys = new PersistPublicKeys();
            persistPublicKeys.addPublicKey(gk.getPublicKey().getEncoded(), org.getAsString());
            Map<String, String> res = new HashMap<String, String>();
            res.put("key", new String(Base64.encodeBase64(gk.getPrivateKey().getEncoded())));
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
