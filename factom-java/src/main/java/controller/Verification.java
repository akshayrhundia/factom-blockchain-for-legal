package controller;

import com.google.api.client.util.Base64;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpStatus;
import persistence.PersistPublicKeys;
import utils.VerifySignatures;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Path("/verification")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Verification {
    @Context
    private HttpHeaders httpHeaders;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @POST
    @Path("/verifysign")
    public Response verifysign(String params) {
        try {
           // System.out.println(params);
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            JsonElement jpayload = o.get("payload");
            JsonElement user = o.get("user");
            JsonElement jSignature = o.get("signature");
            //HashMap<String, Object> map = new Gson().fromJson(jpayload, new TypeToken<HashMap<String, Object>>() {
            //}.getType());
            byte[] data = jpayload.getAsString().getBytes();
            byte[] signature = Base64.decodeBase64(jSignature.getAsString().getBytes());
            PersistPublicKeys persistPublicKeys = new PersistPublicKeys();
            PublicKey publicKey = persistPublicKeys.getPublicKey(user.getAsString());
            System.out.println(new String(data));
            Boolean flag = VerifySignatures.verifySignature(data, signature, publicKey);
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("verified", flag);
            return Response.status(HttpStatus.SC_OK).entity(gson.toJson(res)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }

    }
}
