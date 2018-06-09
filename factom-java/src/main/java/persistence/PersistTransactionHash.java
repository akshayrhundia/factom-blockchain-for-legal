package persistence;

import com.google.cloud.datastore.*;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PersistTransactionHash {
    Datastore datastore;

    public PersisttransHashs() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").build().getService();
    }

    public void addtransHash(byte[] transHash, String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("hash", Blob.copyFrom(transHash)).build();
        datastore.put(task);
    }

    public void addtransHash(String transHash,String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("pub", Blob.copyFrom(transHash.getEncoded())).build();
        datastore.put(task);
    }

    public transHash gettransHash(String tenant) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("hash").toByteArray();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public byte[] gettransHashBytes(String tenant) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("TransHashes").newKey(tenant);
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("hash").toByteArray();
        return keyBytes;
    }
}