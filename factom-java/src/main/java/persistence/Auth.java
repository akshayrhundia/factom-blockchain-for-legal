package persistence;

import com.google.cloud.datastore.*;

import java.util.LinkedList;
import java.util.List;

public class Auth {
    Datastore datastore;

    public Auth() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").build().getService();
    }

    public void updateUsersOnChain(String chain_id,String user) {
        System.out.println(user);
        Key taskKey = datastore.newKeyFactory().setKind("CHAIN_STORE").newKey(System.currentTimeMillis());
        Entity task = Entity.newBuilder(taskKey).set("chain_id", chain_id)
                .set("user",user).build();
        datastore.put(task);
    }


}
