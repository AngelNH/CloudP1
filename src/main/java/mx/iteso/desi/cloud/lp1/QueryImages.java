package mx.iteso.desi.cloud.lp1;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;

public class QueryImages {
  IKeyValueStorage imageStore;
  IKeyValueStorage titleStore;
	
  public QueryImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) 
  {
	  this.imageStore = imageStore;
	  this.titleStore = titleStore;
  }
	
  public Set<String> query(String word)
  {
    // TODO: Return the set of URLs that match the given word,
    //       or an empty set if there are no matches
    String steamWord = PorterStemmer.stem(word);
    System.out.println("My steamed word "+steamWord);
    if(titleStore.exists(steamWord)){
      HashSet<String> hashSet = new HashSet<>();
      Set<String> titleSet = titleStore.get(steamWord);
      Iterator<String> titleIterator = titleSet.iterator();
      while(titleIterator.hasNext()){
        String result = titleIterator.next();
        System.out.println(result);
      }
    }
    return new HashSet<String>();
  }
        
  public void close()
  {
    // TODO: Close the databases
  }
	
  public static void main(String args[]) 
  {
    // TODO: Add your own name here
    System.out.println("*** Alumno: _____________________ (Exp: _________ )");
    
    // TODO: get KeyValueStores
    IKeyValueStorage imageStore = null;
    IKeyValueStorage titleStore = null;
    try {

    IndexImages indexImages = new IndexImages(imageStore,titleStore);


      imageStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,
              "images");
      titleStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,
              "terms");


      IndexImages indexer = new IndexImages(imageStore, titleStore);
      indexer.run(Config.imageFileName, Config.titleFileName);
      System.out.println("Indexing completed");

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed to complete the indexing pass -- exiting");
    }

    System.out.println("Indexing completed");
    
    QueryImages myQuery = new QueryImages(imageStore, titleStore);

    for (int i=0; i<args.length; i++) {
      System.out.println(args[i]+":");
      Set<String> result = myQuery.query(args[i]);
      Iterator<String> iter = result.iterator();
      while (iter.hasNext()) 
        System.out.println("  - "+iter.next());
    }
    
    myQuery.close();
  }
}

