import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;

/**
 *  Database Systems - HEAP IMPLEMENTATION
 */

public class dbquery implements dbimpl
{
   // initialize
   public static void main(String args[])
   {
      dbquery load = new dbquery();
      

      // calculate query time
      long startTime = System.currentTimeMillis();
      for(int i=0;i<25;i++) {
    	  load.readArguments(args);
      }
      long endTime = System.currentTimeMillis();

      System.out.println("Query time: " + (endTime - startTime) + "ms");
   }


   // reading command line arguments
   public void readArguments(String args[])
   {
      if (args.length == 2)
      {
         if (isInteger(args[1]))
         {
            readHeap(args[0], Integer.parseInt(args[1]));
         }
      }
      else
      {
          System.out.println("Error: only pass in two arguments");
      }
   }

   // check if pagesize is a valid integer
   public boolean isInteger(String s)
   {
      boolean isValidInt = false;
      try
      {
         Integer.parseInt(s);
         isValidInt = true;
      }
      catch (NumberFormatException e)
      {
         e.printStackTrace();
      }
      return isValidInt;
   }


   
   /*Read heapfile for record matching given name
    * @param name String name of business to find record for
    * @param pagesize int size of pages in heapfile
    */
   public void readHeap(String name, int pagesize)
   {
      File heapfile = new File(HEAP_FNAME + pagesize);
      int intSize = 4;
      int rid = 0;
      boolean isNextPage = true;
      boolean isNextRecord = true;
      try
      {
    	 //Initialize input stream from heapfile
         FileInputStream fis = new FileInputStream(heapfile);
         
         //Initialize byte arrays
         byte[] bRecord = new byte[RECORD_SIZE];
         byte[] bRid = new byte[intSize];
         
         //Get offset value of desired record
         long offset = readIndex(name,pagesize);
         
         //CHeck if offset not found
         if(offset==-1) {
        	 System.out.println("Could not find record");
        	 System.exit(0);
         }
         
         System.out.println("Record found:");

         //Position channel to offset of desired record
         fis.getChannel().position(offset);
         
         //Read in record
         fis.read(bRecord,  0,RECORD_SIZE);
         
         //Extract record od
         System.arraycopy(bRecord, 0, bRid, 0, intSize);
         rid = ByteBuffer.wrap(bRid).getInt();
         
         
         //System.out.println(new String(bRecord).trim().substring(RID_SIZE+REGISTER_NAME_SIZE, RID_SIZE+REGISTER_NAME_SIZE+BN_NAME_SIZE));

         //Print record
         printRecord(bRecord, name);
         
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + HEAP_FNAME + pagesize + " not found.");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   // returns records containing the argument text from shell
   public void printRecord(byte[] rec, String input)
   {
      String record = new String(rec);
      String BN_NAME = record
                         .substring(RID_SIZE+REGISTER_NAME_SIZE,
                          RID_SIZE+REGISTER_NAME_SIZE+BN_NAME_SIZE);
      if (BN_NAME.toLowerCase().contains(input.toLowerCase()))
      {
         System.out.println(record);
      }
   }
   
   
   /*Take search key and find index for matching record
    * @param name String Search key
    * @param int pagesize size of pages in heap
    * @return Offset of record matching search key in heap
    */
   public long readIndex(String name, int pagesize)
   {
      File heapfile = new File(IDX_FNAME + pagesize);
      int intSize = 8;
      int bucketCount = 0;
      int recordLen = 0;
      int bucketsTraversed=0;
      boolean isNextBucket = true;
      boolean isNextRecord = true;
      String keyStr;
      
      
      try
      {
    	  //Initialize input stream
         FileInputStream fis = new FileInputStream(heapfile);
         
         //Set start position incase search reaches end of index
         long startPos = fis.getChannel().position();
         
         //Calculate hash
         int hashedTo = Math.abs((name.hashCode())%NUMBER_OF_BUCKETS)*BUCKET_SIZE;
         
         //Position over hash value bucket
         fis.getChannel().position(hashedTo);
         bucketCount=hashedTo/BUCKET_SIZE;
         
         // reading in by bucket
         while (isNextBucket)
         {
        	//Initialize byte array to hold bucket
            byte[] bBucket = new byte[BUCKET_SIZE];
            
            //Check if end of index reached
            if(fis.available()<BUCKET_SIZE) {
          	  fis.getChannel().position(startPos);
            }
            
            //Read in bucket
            fis.read(bBucket, 0, BUCKET_SIZE);

            //Check records inside bucket for match key
            isNextRecord = true;
            while (isNextRecord)
            {
            	//Initialize byte arrays
               byte[] bRecord = new byte[INDEX_RECORD_SIZE];
               byte[] bOff = new byte[intSize];
               byte[] bKey = new byte[BN_NAME_SIZE];
               try
               {
            	  //Extract record from bucket
                  System.arraycopy(bBucket, recordLen, bRecord, 0, INDEX_RECORD_SIZE);
                  
                  //Extract key and offset from record
                  System.arraycopy(bRecord, 0, bKey, 0, BN_NAME_SIZE);
                  System.arraycopy(bRecord, bRecord.length-intSize, bOff, 0, intSize);
                  System.out.println("-"+name+"-"+new String(bKey,ENCODING).trim()+"-");
                  
                  
                  //Check if index is empty
                  if(!((keyStr=new String(bKey).trim()).compareTo("empty")==0)) {
                	  //CHeck for match
                	  if(keyStr.equals(name)) {
                		  //Return stored offset
                		  return ByteBuffer.wrap(bOff).getLong();
                	  }
                  }
                  //Return -1 for not not if empty record reached
                  else {
                	  //System.out.println("Record not found.");
                  }
                  //Increment record read offset
                  recordLen += INDEX_RECORD_SIZE; 
                  
               }
               
               //If end of bucket reached move to next
               catch (ArrayIndexOutOfBoundsException e)
               {
                  isNextRecord = false;
                  recordLen = 0;
               }

               
            }
            bucketsTraversed++;
            //Check if all buckets traversed 
            //(would only happen if change to fill all record slots)
            if (NUMBER_OF_BUCKETS < bucketsTraversed)
            	isNextBucket = false;
            	//fis.close();
            }
         
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + IDX_FNAME + pagesize + " not found.");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return -1;
   }
   
}
