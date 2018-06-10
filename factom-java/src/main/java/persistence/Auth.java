package persistence;

import com.google.cloud.datastore.*;

import java.util.LinkedList;
import java.util.List;

public class Auth {
    Datastore datastore;

    public Auth() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").setNamespace("hack").build().getService();
    }

    public void updateUsersOnChain(String chain_id,List<String> users) {
        Key taskKey = datastore.newKeyFactory().setKind("CHAIN_STORE").newKey(chain_id);
        Entity task = Entity.newBuilder(taskKey).set("chain_id", orig.getString("chain_id"))
                .set("entry_hash",orig.getString("entry_hash"))
                .set("external_ids",orig.get(""))
                .set("",users).build();
        datastore.put(task);
    }

}
