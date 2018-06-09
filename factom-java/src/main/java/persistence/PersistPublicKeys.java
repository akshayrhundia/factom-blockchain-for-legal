package persistence;

import com.google.cloud.datastore.*;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PersistPublicKeys {
    Datastore datastore;

    public PersistPublicKeys() {
        datastore = DatastoreOptions.newBuilder().setProjectId("ntm-dev-202213").setNamespace("hack").build().getService();
    }

    public void addPublicKey(byte[] publicKey,String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("users").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("pub", Blob.copyFrom(publicKey)).build();
        datastore.put(task);
    }

    public void addPublicKey(PublicKey publicKey,String tenant) {
        Key taskKey = datastore.newKeyFactory().setKind("users").newKey(tenant);
        Entity task = Entity.newBuilder(taskKey).set("pub", Blob.copyFrom(publicKey.getEncoded())).build();
        datastore.put(task);
    }

    public PublicKey getPublicKey(String tenant) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("users").newKey(tenant);
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("pub").toByteArray();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public byte[] getPublicKeyBytes(String tenant) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("users").newKey(tenant);
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("pub").toByteArray();
        return keyBytes;
    }
}
