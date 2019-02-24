package mx.iteso.desi.cloud.lp1;

import java.io.IOException;
import java.util.HashSet;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.ParseTriples;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;
import mx.iteso.desi.cloud.keyvalue.Triple;

public class IndexImages {
    ParseTriples parser;
    IKeyValueStorage imageStore, titleStore;

    public IndexImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) {
        this.imageStore = imageStore;
        this.titleStore = titleStore;
    }

    public void run(String imageFileName, String titleFileName) throws IOException {
        // TODO: This method should load all images and titles
        //       into the two key-value stores.
        //Load the labels into titleStore
        parser = new ParseTriples(imageFileName);
        Triple triple;
        do {
            triple = parser.getNextTriple();
            if(triple != null && triple.get(2).contains("/"+Config.filter)){
                imageStore.addToSet(triple.get(0),triple.get(2));
            }
        }while (triple != null);
        //Load labels into the titleStore
        parser = new ParseTriples(titleFileName);
        do {
            triple = parser.getNextTriple();
            if( triple != null && imageStore.exists(triple.get(0))){
                String [] labels = triple.get(2).split(" |, ");
                for (String s: labels
                     ) {
                    if(PorterStemmer.stem(s)!= "Invalid term") {
                        //System.out.println(PorterStemmer.stem(s));
                        titleStore.addToSet(PorterStemmer.stem(s), triple.get(0));
                    }else {
                        //System.out.println(s);
                        titleStore.addToSet(s,triple.get(0));
                    }
                }
                //System.out.println("TitleToStorage: "+triple.get(0)+" --- "+triple.get(2));
            }
        }while(triple != null);
    }

    public void close() {
        //TODO: close the databases;
        imageStore.close();
        titleStore.close();
    }

    public static void main(String args[])
    {
        System.out.println("*** Alumno:_______________ (Exp:___________ )");
        try {

            IKeyValueStorage imageStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,
                    "images");
            IKeyValueStorage titleStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,
                    "terms");


            IndexImages indexer = new IndexImages(imageStore, titleStore);
            indexer.run(Config.imageFileName, Config.titleFileName);
            System.out.println("Indexing completed");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to complete the indexing pass -- exiting");
        }
    }
}

