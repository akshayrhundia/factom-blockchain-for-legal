package persistence;

import com.google.cloud.datastore.*;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class PersistPrivateKeys {
    Datastore datastore;

    public PersistPrivateKeys() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").setNamespace("hack").build().getService();
    }

    public PrivateKey getPrivateKey(String user) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("mykeys").newKey(user);
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("private").toByteArray();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
    public void addPublicKey(byte[] privateKey,String user) {
        Key taskKey = datastore.newKeyFactory().setKind("mykeys").newKey(user);
        Entity task = Entity.newBuilder(taskKey).set("private", Blob.copyFrom(privateKey)).build();
        datastore.put(task);
    }

    public void addPublicKey(PrivateKey privateKey, String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("mykeys").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("private", Blob.copyFrom(privateKey.getEncoded())).build();
        datastore.put(task);
    }
}
