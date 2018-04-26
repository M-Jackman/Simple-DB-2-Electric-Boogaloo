package simpledb.index.hash;

import simpledb.index.Index;
import simpledb.query.Constant;
import simpledb.query.TableScan;
import simpledb.record.RID;
import simpledb.record.Schema;
import simpledb.record.TableInfo;
import simpledb.tx.Transaction;

import java.util.ArrayList;
import java.util.List;

 //CS4432 The code for the operations of the extensible hash index
 //Code found and adapted from the HashIndex.java file
/**
 * A extensible hash implementation of the Index interface.
 * A fixed number of buckets is allocated (currently, 100),
 * and each bucket is implemented as a file of index records.
 */
public class ExtensibleHashIndex implements Index {
    public static int NUM_BUCKETS = 101; //an extra 1 bucket because whats life without a little whimsy?
    private String idxname;
    private Schema sch;
    private Transaction tx;
    private Constant searchkey = null;
    private TableInfo indTi;
    //CS4432 Information for the buckets of the extensible hash
    private ExtensibleHashBucket bucket = null;
    private String bucketPostfix = null;
    //CS4432 Create the depth values to be used later
    private int depth;
    private int localDepth;

    /**
     * Opens a hash index for the specified index.
     * @param idxname the name of the index
     * @param bucketSchema the schema of the index records
     * @param tx the calling transaction
     */
    public ExtensibleHashIndex(String idxname, Schema bucketSchema, Transaction tx){
        this.idxname = idxname;
        this.sch = bucketSchema;

		/*Deals with the 2nd level index*/
        Schema indexSchema = new Schema();
        indexSchema.addIntField("hash");
        indexSchema.addIntField("depth");
        indexSchema.addStringField("bucket", 35); //CS4432
        String indextbl = idxname + "ind";
        indTi = new TableInfo(indextbl, indexSchema);
        if(tx.size(indTi.fileName()) <= 0){  //CS4432 If empty, initialize values
            this.depth = 1;
            //create all the index entries for the table scan
            TableScan ts = new TableScan(indTi, tx);
            ts.insert();
            ts.setInt("hash", 1);
            ts.setInt("depth", 1);
            ts.setString("bucket", "1");
            ts.insert();
            ts.setInt("hash", 0);
            ts.setInt("depth", 1);
            ts.setString("bucket", "0");
            ts.close();

        } else { //
            TableScan ts = new TableScan(indTi, tx);
            ts.next();
            this.depth = ts.getInt("depth");
            ts.close();
        }
        this.tx = tx;
    }

    /**
     * Positions the index before the first index record
     * having the specified search key.
     * The method hashes the search key to determine the bucket,
     * and then opens a table scan on the file
     * corresponding to the bucket.
     * The table scan for the previous bucket (if any) is closed.
     * @see simpledb.index.Index#beforeFirst(simpledb.query.Constant)
     */
    public void beforeFirst(Constant searchKey) {
        close();
        this.searchkey = searchKey;
        TableScan ts = new TableScan(indTi, tx);
        int hash = searchkey.hashCode() & genBitmask();
        String bucketName = idxname;
        while(ts.next()){
            if(ts.getInt("hash") == hash){
                bucketName = bucketName + ts.getString("bucket");

                break;
            }
        }
        //CS4432 set the local depth for the bucket
        localDepth = ts.getInt("bucket");
        bucketPostfix = ts.getString("bucket");

        //CS4432 close the table scan
        ts.close();

        bucket = new ExtensibleHashBucket(bucketName, sch, tx);
        bucket.beforeFirst(searchkey);
    }

    // CS4432 Calls the bucket's next() function
    public boolean next() {
        return bucket.next();
    }

    // CS4432 Calls the bucket's getDataRid() function
    public RID getDataRid() {
        return bucket.getDataRid();
    }

    // CS4432 Inserts a new record into the table scan for the bucket
    // Depending on the size of the bucket, it may need to be split up
    public void insert(Constant val, RID rid) {
        beforeFirst(val);
        if(localDepth < 32){ //CS4432 Check the local depth first to see if a split is possible
            while(bucket.isFull() && localDepth < 32){ //CS4432 split the bucket
                split();
                beforeFirst(val);
            }
            beforeFirst(val);
        }

        bucket.insert(val, rid);
    }

    /**
     * Deletes the specified record from the table scan for
     * the bucket. Uses RID to determine correct value
     */
    public void delete(Constant val, RID rid) {
        beforeFirst(val);
        bucket.delete(val, rid);
    }

    /**
     * Closes the index by closing the current table scan.
     * @see simpledb.index.Index#close()
     */
    public void close() {
        if (bucket != null){
            bucket.close();
        }
        bucket = null;
    }

    /**
     * Returns the cost of searching an index file having the
     * specified number of blocks.
     * The method assumes that all buckets are about the
     * same size, and so the cost is simply the size of
     * the bucket.
     * @param numblocks the number of blocks of index records
     * @param rpb the number of records per block (not used here)
     * @return the cost of traversing the index
     */
    public static int searchCost(int numblocks, int rpb){
        System.out.print(numblocks/HashIndex.NUM_BUCKETS);
        return numblocks / HashIndex.NUM_BUCKETS;
    }

    // CS4432 Generates the bitmask for the current depth
    private int genBitmask(){
        return genBitmask(depth);
    }

    // CS4432 uses bitwise operations to get the depth
    // of the bucket make sure elements are sorted correctly
    private int genBitmask(int depth){
        return (~0x0) >>> (32 - depth);
    }

    // CS4432 Used when there are too many elements in a single
    // index, we have to split the bucket.
    private void split(){
        if(localDepth == depth){
            splitIndex();
        }

        TableScan ts = new TableScan(indTi, tx);

        String bucketName = bucket.name;
        bucketName = bucketPostfix;
        int bucketHash = Integer.parseInt(bucketName, 2);
        int bucketHash1 = bucketHash | (1 << localDepth);

        while(ts.next()){
            int currentHash = ts.getInt("hash");

            if((currentHash & genBitmask(localDepth + 1)) == bucketHash){
                String newName = genBucketName(bucketHash, localDepth + 1);
                ts.setString("bucket", newName);
                ExtensibleHashBucket b = new ExtensibleHashBucket(newName, sch, tx);
                b.copyFrom(bucket, bucketHash, genBitmask(localDepth + 1));
            }

            if((currentHash & genBitmask(localDepth + 1)) == bucketHash1){
                String newName = genBucketName(bucketHash1, localDepth + 1);
                ts.setString("bucket", newName);
                ExtensibleHashBucket b = new ExtensibleHashBucket(newName, sch, tx);
                b.copyFrom(bucket, bucketHash1, genBitmask(localDepth + 1));
            }

        }

        ts.close();

    }

    //CS4432 Creates the name of the bucket
    private String genBucketName(int hash, int depth){
        String result = Integer.toString(hash, 2);
        while(result.length() < depth){
            result = "0" + result;
        }
        return result;
    }

    // CS4432 If a bucket has the same local depth as the global depth,
    // then we need to increase the global depth and have that
    // be reflected in the rest of the buckets
    private void splitIndex(){
        TableScan ts = new TableScan(indTi, tx);
        List<ExtensibleHashIndexTuple> hashes = new ArrayList<ExtensibleHashIndexTuple>();
        while(ts.next()){
            hashes.add(new ExtensibleHashIndexTuple(ts.getInt("hash"),
                    ts.getString("bucket")));
        }

        ts.beforeFirst();
        for (ExtensibleHashIndexTuple hash : hashes){
            ts.insert();
            ts.setInt("hash", hash.hash);
            ts.setInt("depth", depth + 1);
            ts.setString("bucket", hash.bucket);
            ts.insert();
            ts.setInt("hash", hash.hash | (1 << depth));
            ts.setInt("depth", depth + 1);
            ts.setString("bucket", hash.bucket);
        }

        ts.beforeFirst();

        while(next()){
            if(ts.getInt("depth") == depth){
                ts.delete();
            }
        }
        depth++;
    }
}