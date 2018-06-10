package controller;

import com.google.gson.*;
import lombok.extern.java.Log;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import utils.HTTPClient;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Log
@Path("/case")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CaseController extends HttpServlet {
    @Context
    private HttpHeaders httpHeaders;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();


    @POST
    @Path("/register")
    public Response register() {
        try {
            Map<String,Object> myMap=new HashMap<String,Object>();
            myMap.put("case_id","");
            myMap.put("external_id","main-case-"+System.currentTimeMillis());
            String resp=HTTPClient.sendPost("https://chain-service-dot-ntm-dev-202213.appspot.com/createChain",gson.toJson(myMap));
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
            String content_ = new String(base64.encode(o.get("content").getAsString().getBytes()));
            Map<String,Object> myMap=new HashMap<String,Object>();
            myMap.put("content",content_);
            myMap.put("external_ids",list);
            String resp=HTTPClient.sendPost("https://apiplus-api-sandbox-testnet.factom.com/v1/chains/" + chain_id + "/entries",gson.toJson(myMap));
            return Response.status(HttpStatus.SC_OK).entity(resp).build();

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

    @POST
    @Path("/callback")
    public Response callback(@Context UriInfo uriInfo) {
        try {
            System.out.println("Inside");
            String query = uriInfo.getRequestUri().getQuery();
            System.out.println(query);
            log.info("query: "+query);
            return Response.status(HttpStatus.SC_OK).build();

        } catch (Exception ex) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }


}
