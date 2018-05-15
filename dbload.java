import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;

/**
 *  Database Systems - HEAP IMPLEMENTATION
 */

public class dbload implements dbimpl
{
    //ME
    private static Bucket[] index;
    // initialize
   public static void main(String args[])
   {
	  
      dbload load = new dbload();
      
      load.initializeIndex();

      // calculate load time
      long startTime = System.currentTimeMillis();
      load.readArguments(args);
      long endTime = System.currentTimeMillis();

      System.out.println("Load time: " + (endTime - startTime) + "ms");
      

      System.out.println("Writing index file");
      
      // Index write time
      startTime = System.currentTimeMillis();
      load.writeIndex(Integer.parseInt(args[1]));
      endTime = System.currentTimeMillis();

      System.out.println("Index write time: " + (endTime - startTime) + "ms");
   }

   // reading command line arguments
   public void readArguments(String args[])
   {
      if (args.length == 3)
      {
         if (args[0].equals("-p") && isInteger(args[1]))
         {
            readFile(args[2], Integer.parseInt(args[1]));
         }
      }
      else
      {
         System.out.println("Error: only pass in three arguments");
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

   // read .csv file using buffered reader
   public void readFile(String filename, int pagesize)
   {
      dbload load = new dbload();
      File heapfile = new File(HEAP_FNAME + pagesize);
      BufferedReader br = null;
      FileOutputStream fos = null;
      String line = "";
      String nextLine = "";
      String stringDelimeter = "\t";
      byte[] RECORD = new byte[RECORD_SIZE];
      int outCount, pageCount, recCount, bRecCount,posInPage;
      outCount = pageCount = recCount = bRecCount = posInPage = 0;
      

      try
      {
         // create stream to write bytes to according page size
         fos = new FileOutputStream(heapfile);
         br = new BufferedReader(new FileReader(filename));
         // read line by line
         while ((line = br.readLine()) != null)
         {
        	 long pos = fos.getChannel().position();
            String[] entry = line.split(stringDelimeter, -1);
            RECORD = createRecord(RECORD, entry, outCount);
            // outCount is to count record and reset everytime
            // the number of bytes has exceed the pagesize
            outCount++;
            fos.write(RECORD);
            
            /* Add search key and offset of record to index
             * @author Thomas Higgins
             */
            addIndex(index,entry[1].trim(),pageCount,posInPage,pagesize);
            
            
            if ((outCount+1)*RECORD_SIZE > pagesize)
            {
               eofByteAddOn(fos, pagesize, outCount, pageCount);
               //reset counter to start newpage
               outCount = 0;
               pageCount++;
               
               //Record position in page back to 0
               posInPage=0;
            }
            //Record position in page increment
            else {
                posInPage++;
            }
            recCount++;
         }
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + filename + " not found.");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (br != null)
         {
            try
            {
               // final add on at end of file
               if ((nextLine = br.readLine()) == null)
               {
                  eofByteAddOn(fos, pagesize, outCount, pageCount);
                  pageCount++;
               }
               fos.close();
               br.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
      System.out.println("Page total: " + pageCount);
      System.out.println("Record total: " + recCount);
   }
   
   /*Write Index file from index array
    * @param pagesize int size of heapfile pages
    */
   public void writeIndex(int pagesize)
   {
      File IDXfile = new File(IDX_FNAME + pagesize);
      FileOutputStream fos = null;
      byte[] RECORD = new byte[INDEX_RECORD_SIZE];
      int bucketCount, recCount;
      bucketCount = recCount = 0;
      

      try
      {
         // create stream to write bytes to according page size
         fos = new FileOutputStream(IDXfile);
         
         // Write each bucket
         for (int i=0;i<NUMBER_OF_BUCKETS;i++)
         {
        	 //Create and write each record
        	 for(int j=0;j<RECORDS_PER_BUCKET;j++) {
        		 
        		//Create record
        		RECORD =  createIndexRecord(RECORD, index[i].getRecord(j),pagesize);
        		
        		//Write record to index file
            	fos.write(RECORD);

                recCount++;
        	 }
             bucketCount++;
            }
         
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File not found.");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
            try
            {
               //CLose file
               fos.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
      }
      
      //Print completion message
      System.out.println("Indexing completed");
      System.out.println("Bucket total: " + bucketCount);
      System.out.println("Record total: " + recCount);
   }

   // create byte array for a field and append to record array at correct 
   // offset using array copy
   public void copy(String entry, int SIZE, int DATA_OFFSET, byte[] rec)
          throws UnsupportedEncodingException
   {
      byte[] DATA = new byte[SIZE];
      byte[] DATA_SRC = entry.trim().getBytes(ENCODING);
      if (entry != "")
      {
         System.arraycopy(DATA_SRC, 0,
                DATA, 0, DATA_SRC.length);
      }
      System.arraycopy(DATA, 0, rec, DATA_OFFSET, DATA.length);
   }

   // creates record by appending using array copy and then applying offset
   // where neccessary
   public byte[] createRecord(byte[] rec, String[] entry, int out)
          throws UnsupportedEncodingException 
   {
      byte[] RID = intToByteArray(out);
      System.arraycopy(RID, 0, rec, 0, RID.length);

      copy(entry[0], REGISTER_NAME_SIZE, RID_SIZE, rec);

      copy(entry[1], BN_NAME_SIZE, BN_NAME_OFFSET, rec);

      copy(entry[2], BN_STATUS_SIZE, BN_STATUS_OFFSET, rec);

      copy(entry[3], BN_REG_DT_SIZE, BN_REG_DT_OFFSET, rec);

      copy(entry[4], BN_CANCEL_DT_SIZE, BN_CANCEL_DT_OFFSET, rec);

      copy(entry[5], BN_RENEW_DT_SIZE, BN_RENEW_DT_OFFSET, rec);

      copy(entry[6], BN_STATE_NUM_SIZE, BN_STATE_NUM_OFFSET, rec);

      copy(entry[7], BN_STATE_OF_REG_SIZE, BN_STATE_OF_REG_OFFSET, rec);

      copy(entry[8], BN_ABN_SIZE, BN_ABN_OFFSET, rec);

      return rec;
   }
   
   /*Creates a byte array of index record information
    * @param rec byte[] array in which to store record
    * @param IndexRecord irec IndexRecord to convert
    * @param pagesize int size of heapfile pages
    * @return record as byte array
    */
   public byte[] createIndexRecord(byte[] rec, IndexRecord irec,int pageSize)
          throws UnsupportedEncodingException 
   {
	  //Convert offset to byte array
      byte[] ROFF = longToByteArray(irec.getOffset());

      //Add key to byte array
      copy(irec.getIndexKey(), BN_NAME_SIZE, 0, rec);
	
      //Add offset to byte array
      System.arraycopy(ROFF, 0, rec, BN_NAME_SIZE, ROFF.length);

      return rec;
   }
   
   
   // EOF padding to fill up remaining pagesize
   // * minus 4 bytes to add page number at end of file
   public void eofByteAddOn(FileOutputStream fos, int pSize, int out, int pCount) 
          throws IOException
   {
      byte[] fPadding = new byte[pSize-(RECORD_SIZE*out)-4];
      byte[] bPageNum = intToByteArray(pCount);
      fos.write(fPadding);
      fos.write(bPageNum);
   }

   // converts ints to a byte array of allocated size using bytebuffer
   public byte[] intToByteArray(int i)
   {
      ByteBuffer bBuffer = ByteBuffer.allocate(4);
      bBuffer.putInt(i);
      return bBuffer.array();
   }
   
   //Converts longs to byte array (copy of intToByteArray with modification)
   public byte[] longToByteArray(long i)
   {
      ByteBuffer bBuffer = ByteBuffer.allocate(8);
      bBuffer.putLong(i);
      return bBuffer.array();
   }
   

   
   /*Add record to index using linear porobing
    * @param index Bucket[] Array holding index before write
    * @param key Sting index key business name
    * @param pNum int page number of record
    * @param rNum record number within
    * @param pagesize int size of pages in heapfile
    */
   public static void addIndex(Bucket[] index,String key,int pNum,int rNum,int pagesize) {
	   int hCode = Math.abs(key.hashCode());
	   int bucketNum = hCode%NUMBER_OF_BUCKETS;
	   
	   /*Linear probing insertion checks desired bucket for space and
	    * continues through buckets until empty slot is found at which
	    * point record is inserted into slot
	    */
	   while(!index[bucketNum].addRecord(key,  pNum,  rNum, pagesize)) {
		   bucketNum=(bucketNum+1)%NUMBER_OF_BUCKETS;	   
		}
	   
   }
   
   //Initialize Bucket and IndexRecord objects for index file
   public void initializeIndex() {
	   //Create array of buckets to form index
	   index = new Bucket[NUMBER_OF_BUCKETS];
	   
	   //Initialize records within buckets
	   for (int i=0;i<NUMBER_OF_BUCKETS;i++) {
		   index[i]=new Bucket();
	   }
   }
}
