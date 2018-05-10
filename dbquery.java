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
         // reading page by page
         while (isNextPage)
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
         }
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
   
   // read heapfile by page
   public void readIndex(String name, int pagesize)
   {
      File heapfile = new File(IDX_FNAME + pagesize);
      //ME
      
      int intSize = 4;
      int bucketCount = 0;
      //int recCount = 0;
      int recordLen = 0;
      int rOffset=0;
      int bucketsTraversed=0;
      boolean isNextBucket = true;
      boolean isNextRecord = true;
      try
      {
         FileInputStream fis = new FileInputStream(heapfile);
         // reading page by page
         while (isNextBucket)
         {
            byte[] bBucket = new byte[BUCKET_SIZE];
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
                  System.arraycopy(bBucket, recordLen, bRecord, 0, RECORD_SIZE);
                  
                  System.arraycopy(bRecord, 0, bKey, 0, BN_NAME_SIZE);
                  System.arraycopy(bRecord, bRecord.length-intSize, bOff, 0, intSize);
                  
                  rOffset = ByteBuffer.wrap(bOff).getInt();
                  /*if (rid != recCount)
                  {
                     isNextRecord = false;
                  }
                  else
                  {
                     printRecord(bRecord, name);
                     recordLen += INDEX_RECORD_SIZE;
                  }*/
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
            // check to complete all pages
            if (NUMBER_OF_BUCKETS == bucketsTraversed)
            {
               isNextBucket = false;
            }
            bucketCount++;
            if(bucketCount>=NUMBER_OF_BUCKETS) {
            	bucketCount=bucketCount%NUMBER_OF_BUCKETS;
            }
         }
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
}
