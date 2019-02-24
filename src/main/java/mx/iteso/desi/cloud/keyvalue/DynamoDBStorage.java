package mx.iteso.desi.cloud.keyvalue;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import mx.iteso.desi.cloud.lp1.Config;

import java.util.*;


public class DynamoDBStorage extends BasicKeyValueStore {

    String dbName;

    // Simple autoincrement counter to make sure we have unique entries
    int inx;

    Set<String> attributesToGet = new HashSet<String>();

    public DynamoDBStorage(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public Set<String> get(String search) {
        AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(ddb);

        HashMap<String,AttributeValue> key_to_get = new HashMap<String, AttributeValue>();
        key_to_get.put("keyword", new AttributeValue(search));

        GetItemRequest request = null;

            request = new GetItemRequest()
                    .withKey(key_to_get)
                    .withTableName(dbName);


        //final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            Map<String,AttributeValue> returned_item =
                    ddb.getItem(request).getItem();
            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                for (String key : keys) {
                    System.out.format("%s: %s\n",
                            key, returned_item.get(key).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", search);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean exists(String search) {//TODO: Implement this method
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getPrefix(String search) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addToSet(String keyword, String value) {//TODO: Implement this method
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void put(String keyword, String value) {//TODO: Implement this method
        HashMap<String,AttributeValue> item_values =
                new HashMap<String,AttributeValue>();

        item_values.put("keyword", new AttributeValue(keyword));

        /*for (String[] field : extra_fields) {
            item_values.put(field[0], new AttributeValue(field[1]));
        }*/

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            ddb.putItem(dbName, item_values);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", dbName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {//TODO: Implement this method
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean supportsPrefixes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sync() {
    }

    @Override
    public boolean isCompressible() {
        return false;
    }

    @Override
    public boolean supportsMoreThan256Attributes() {
        return true;
    }

}
