package simpledb.index.hash;

//CS4432 What a single tuple looks like in the extensible hash index
public class ExtensibleHashIndexTuple {
    public final int hash;
    public final String bucket;

    //CS4432 Constructor
    ExtensibleHashIndexTuple(int hash, String bucket){
        this.hash = hash;
        this.bucket = bucket;
    }

}