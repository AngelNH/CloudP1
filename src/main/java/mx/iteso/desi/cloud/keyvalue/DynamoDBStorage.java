package mx.iteso.desi.cloud.keyvalue;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import mx.iteso.desi.cloud.lp1.Config;

import java.util.*;





public class DynamoDBStorage extends BasicKeyValueStore {

    public static final String PRIMARY_KEY = "keyword";
    public static final String SORT_KEY = "inx";
    String dbName;
    DynamoDB dynamoDB;
    Table table;
    // Simple autoincrement counter to make sure we have unique entries
    int inx;

    Set<String> attributesToGet = new HashSet<String>();

    public DynamoDBStorage(String dbName) {
        this.dbName = dbName;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(Config.amazonRegion)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        dynamoDB = new DynamoDB(client);
        ListTablesResult tables = client.listTables();

        //Table aux = dynamoDB.getTable("terms");
        //aux.delete();
        /*
        for (String t: tables.getTableNames()
             ) {
            System.out.println(t);
        }
        */
        if(!tables.getTableNames().contains(dbName)){
            try {
                System.out.println("Not table, creating new table named: "+dbName+"...");
                table = dynamoDB.createTable(dbName,
                        Arrays.asList(new KeySchemaElement(PRIMARY_KEY,KeyType.HASH),
                                new KeySchemaElement(SORT_KEY,KeyType.RANGE)),
                    Arrays.asList(new AttributeDefinition(PRIMARY_KEY,ScalarAttributeType.S),
                            new AttributeDefinition(SORT_KEY,ScalarAttributeType.N)),
                    new ProvisionedThroughput(1L,1L));
                table.waitForActive();

            }catch (Exception e){
                System.err.println(e.getMessage());
            }
        }else{
            try {
                System.out.println("Table exist, retrieving table...");
                table = dynamoDB.getTable(dbName);
                table.waitForActive();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }


        }

    }

    @Override
    public Set<String> get(String search) {
        HashSet<String> result = new HashSet<>();
        ItemCollection<QueryOutcome> items = table.query(PRIMARY_KEY,search);
        for (Item i:
             items) {
            result.add((String) i.get("value"));
        }
        return result;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean exists(String search) {//TODO: Implement this method
        if (get(search).size()>0)
            return true;
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getPrefix(String search) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addToSet(String keyword, String value) {//TODO: Implement this method
        put(keyword,value);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void put(String keyword, String value) {//TODO: Implement this method
        try {
            Item item = new Item()
                    .withPrimaryKey("keyword",keyword,"inx",inx)
                    .withString("value",value);
            table.putItem(item);
            System.out.println("Successfully added in "+ dbName+ ": "+ keyword + " - "+ value + "- inx: "+inx);
        }catch (Exception e){
            System.err.println("Unable to add "+ dbName+ ": "+ keyword + " - "+ value + "- inx: "+inx);
            System.err.println(e.getMessage());
        }
        inx++;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {//TODO: Implement this method
        dynamoDB.shutdown();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
