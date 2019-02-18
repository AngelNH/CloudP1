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
      
  public void run(String imageFileName, String titleFileName) throws IOException
  {
    // TODO: This method should load all images and titles 
    //       into the two key-value stores.

    //TODO: check again if this is correct
    //Load the labels into titleStore
    parser = new ParseTriples(titleFileName);
    Triple triple;
    do {
      triple = parser.getNextTriple();
      if(triple != null && triple.get(2).startsWith(Config.filter)){
        titleStore.addToSet(triple.get(0),triple.get(2));
      }
    }while (triple != null);
    //Load Images into the imageStore


  }
  
  public void close() {
	  //TODO: close the databases;
  }
  
  public static void main(String args[])
  {
    System.out.println("*** Alumno:Miguel Angel Nuño  (Exp:is704713 )");
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

