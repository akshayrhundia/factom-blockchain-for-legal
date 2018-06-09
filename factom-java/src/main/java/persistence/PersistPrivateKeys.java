package persistence;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class PersistPrivateKeys {
    Datastore datastore;

    public PersistPrivateKeys() {
        datastore = DatastoreOptions.newBuilder().setProjectId("slb-ingested-storage").build().getService();
    }

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Key taskKey = datastore.newKeyFactory().setKind("encryption").newKey("mykeys");
        Entity esEntity = datastore.get(taskKey);
        byte[] keyBytes = esEntity.getBlob("private").toByteArray();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

}
