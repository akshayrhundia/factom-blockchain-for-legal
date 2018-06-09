package persistence;

import com.google.cloud.datastore.*;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PersistTransactionHash {
    Datastore datastore;

    public PersistTransactionHash() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").setNamespace("hack").build().getService();
    }

    public void addtransHash(byte[] transHash, String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("hash", Blob.copyFrom(transHash)).build();
        datastore.put(task);
    }

    public void addtransHash(String transHash,String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("hash", Blob.copyFrom(transHash.getBytes())).build();
        datastore.put(task);
    }

    public byte[] gettransHash(String tenant) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("hash").toByteArray();
        return keyBytes;
    }
}