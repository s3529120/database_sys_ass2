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
      
      for(String s : args) {
    	  System.out.println(s);
      }

      // calculate query time
      long startTime = System.currentTimeMillis();
      load.readArguments(args);
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

   // read heapfile by page
   public void readHeap(String name, int pagesize)
   {
      File heapfile = new File(HEAP_FNAME + pagesize);
      int intSize = 4;
      int pageCount = 0;
      int recCount = 0;
      int recordLen = 0;
      int rid = 0;
      boolean isNextPage = true;
      boolean isNextRecord = true;
      try
      {
         FileInputStream fis = new FileInputStream(heapfile);
         

         byte[] bRecord = new byte[RECORD_SIZE];
         byte[] bRid = new byte[intSize];
         
         int offset = readIndex(name,pagesize);
         
         if(offset==-1) {
        	 System.out.println("Could not find record");
        	 System.exit(0);
         }
         
         System.out.println("avail: "+fis.available()+"off: "+offset);

         fis.getChannel().position(offset);
         
         fis.read(bRecord,  0,RECORD_SIZE);
         System.arraycopy(bRecord, 0, bRid, 0, intSize);
         rid = ByteBuffer.wrap(bRid).getInt();
         
         
         //System.out.println(new String(bRecord).trim().substring(RID_SIZE+REGISTER_NAME_SIZE, RID_SIZE+REGISTER_NAME_SIZE+BN_NAME_SIZE));

         printRecord(bRecord, name);
         
         // reading page by page
         /*while (isNextPage)
         {
            byte[] bPage = new byte[pagesize];
            byte[] bPageNum = new byte[intSize];
            fis.read(bPage, 0, pagesize);
            System.arraycopy(bPage, bPage.length-intSize, bPageNum, 0, intSize);

            // reading by record, return true to read the next record
            isNextRecord = true;
            while (isNextRecord)
            {
               byte[] bRecord = new byte[RECORD_SIZE];
               byte[] bRid = new byte[intSize];
               try
               {
                  System.arraycopy(bPage, recordLen, bRecord, 0, RECORD_SIZE);
                  System.arraycopy(bRecord, 0, bRid, 0, intSize);
                  rid = ByteBuffer.wrap(bRid).getInt();
                  if (rid != recCount)
                  {
                     isNextRecord = false;
                  }
                  else
                  {
                     printRecord(bRecord, name);
                     recordLen += RECORD_SIZE;
                  }
                  recCount++;
                  // if recordLen exceeds pagesize, catch this to reset to next page
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                  isNextRecord = false;
                  recordLen = 0;
                  recCount = 0;
                  rid = 0;
               }
            }
            // check to complete all pages
            if (ByteBuffer.wrap(bPageNum).getInt() != pageCount)
            {
               isNextPage = false;
            }
            pageCount++;
         }*/
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
      System.out.println(BN_NAME+"was found using index!");
      if (BN_NAME.toLowerCase().contains(input.toLowerCase()))
      {
         System.out.println(BN_NAME+"was found using index!");
      }
   }
   
   // read heapfile by page
   public int readIndex(String name, int pagesize)
   {
      File heapfile = new File(IDX_FNAME + pagesize);
      //ME
      
      int intSize = 4;
      int bucketCount = 0;
      int recordLen = 0;
      int bucketsTraversed=0;
      boolean isNextBucket = true;
      boolean isNextRecord = true;
      String keyStr;
      
      
      try
      {
         FileInputStream fis = new FileInputStream(heapfile);
         long startPos = fis.getChannel().position();
         
         int hashedTo = Math.abs((name.hashCode())%NUMBER_OF_BUCKETS)*BUCKET_SIZE;
         
         fis.getChannel().position(hashedTo);
         bucketCount=hashedTo/BUCKET_SIZE;
         
         // reading page by page
         while (isNextBucket)
         {
        	 //System.out.println(bucketCount);
            byte[] bBucket = new byte[BUCKET_SIZE];
            
            if(fis.available()<BUCKET_SIZE) {
          	  fis.getChannel().position(startPos);
            }
            
            fis.read(bBucket, 0, BUCKET_SIZE);

            // reading by record, return true to read the next record
            isNextRecord = true;
            while (isNextRecord)
            {
               byte[] bRecord = new byte[INDEX_RECORD_SIZE];
               byte[] bOff = new byte[intSize];
               byte[] bKey = new byte[BN_NAME_SIZE];
               try
               {
                  System.arraycopy(bBucket, recordLen, bRecord, 0, INDEX_RECORD_SIZE);
                  
                  System.arraycopy(bRecord, 0, bKey, 0, BN_NAME_SIZE);
                  System.arraycopy(bRecord, bRecord.length-intSize, bOff, 0, intSize);
                  System.out.println("-"+name+"-"+new String(bKey,ENCODING).trim()+"-");
                  System.out.println("-"+name+"-"+new String(bKey,ENCODING).trim()+"-");
                  //System.exit(1);
                  
                  
                  
                  if(!((keyStr=new String(bKey).trim()).compareTo("empty")==0)) {
                	  if(keyStr.equals(name)) {
                		  System.out.println("Found");
                		  return ByteBuffer.wrap(bOff).getInt();
                	  }
                  }
                  /*if (rid != recCount)
                  {
                     isNextRecord = false;
                  }
                  else
                  {*/
                     //printRecord(bRecord, name);
                     recordLen += INDEX_RECORD_SIZE;
                  //}
                  //recCount++;
                  // if recordLen exceeds bucketsize, catch this to reset to next bucket
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                  isNextRecord = false;
                  recordLen = 0;
                  //recCount = 0;
               }

               
            }
            bucketsTraversed++;
            // check to complete all pages
               if (NUMBER_OF_BUCKETS < bucketsTraversed)
               {
                  isNextBucket = false;
                  fis.close();
               }
               System.out.println("traversed: "+bucketsTraversed);
               //bucketCount++;
               //if(bucketCount==NUMBER_OF_BUCKETS) {
               	//bucketCount=0;
                   //fis.getChannel().position(startPos);
               //}
            
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
