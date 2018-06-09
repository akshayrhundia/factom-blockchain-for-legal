package controller;

import com.google.api.client.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpStatus;
import persistence.PersistPublicKeys;
import utils.GenerateKeys;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/query")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MSPController  extends HttpServlet {
    @Context
    private HttpHeaders httpHeaders;

    private Gson gson = new Gson();

    @POST
    @Path("/externalid")
    public Response register(String params) {
        try {

            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(params).getAsJsonObject();
            String chain_id = o.get("chain_id");
            String [] external_id = o.get("external_id");
            String content_ = Base64.encodeBase64(o.get("content"));
            
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            Map<String,String> myMap=new HashMap<String,String>();
            myMap.put("external_ids",external_id);
            String json=gson.toJson(myMap);

            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
              .url("https://apiplus-api-sandbox-testnet.factom.com/v1/chains/chain/entries/search")
              .post(body)
              .build();

            Response response = client.newCall(request).execute();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
}
