package persistence;

import com.google.cloud.datastore.*;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.LinkedList;
import java.util.List;

public class Users {
    Datastore datastore;

    public Users() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").setNamespace("hack").build().getService();
    }

    public List<String> getAllUsers() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("users")
                .build();
        QueryResults<Entity> tasks = datastore.run(query);
        List<String> ret=new LinkedList<String>();
        while(tasks.hasNext()){
            ret.add(tasks.next().getKey().getName());
        }
        return ret;
    }

}
