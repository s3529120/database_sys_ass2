/**
 *  Database Systems - HEAP IMPLEMENTATION
 */

public interface dbimpl
{

   public static final String HEAP_FNAME = "heap.";
   public static final String IDX_FNAME = "index.";
   public static final String ENCODING = "utf-8";

   // fixed/variable lengths
   public static final int TOTAL_RECORDS = 2542374;
   public static final int NUMBER_RECORDS = (int) (TOTAL_RECORDS/0.6);
   public static final int RECORDS_PER_BUCKET = 2;
   public static final int NUMBER_OF_BUCKETS=NUMBER_RECORDS/RECORDS_PER_BUCKET;
   public static final int INDEX_RECORD_SIZE=208;
   public static final int BUCKET_SIZE=INDEX_RECORD_SIZE*RECORDS_PER_BUCKET;
   
   
   
   public static final int RECORD_SIZE = 297;
   public static final int RID_SIZE = 4;
   public static final int REGISTER_NAME_SIZE = 14;
   public static final int BN_NAME_SIZE = 200;
   public static final int BN_STATUS_SIZE = 12;
   public static final int BN_REG_DT_SIZE = 10;
   public static final int BN_CANCEL_DT_SIZE = 10;
   public static final int BN_RENEW_DT_SIZE = 10;
   public static final int BN_STATE_NUM_SIZE = 10;
   public static final int BN_STATE_OF_REG_SIZE = 3;
   public static final int BN_ABN_SIZE = 20;
   public static final int EOF_PAGENUM_SIZE = 4;

   public static final int BN_NAME_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE;

   public static final int BN_STATUS_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE;

   public static final int BN_REG_DT_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE;

   public static final int BN_CANCEL_DT_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE;

   public static final int BN_RENEW_DT_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE;

   public static final int BN_STATE_NUM_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE
                           + BN_RENEW_DT_SIZE;

   public static final int BN_STATE_OF_REG_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE
                           + BN_RENEW_DT_SIZE
                           + BN_STATE_NUM_SIZE;

   public static final int BN_ABN_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE
                           + BN_RENEW_DT_SIZE
                           + BN_STATE_NUM_SIZE
                           + BN_STATE_OF_REG_SIZE;

   //ME
   //public static final int INDEX_OFFSET_OFFSET = BN_NAME_SIZE;
   //public static final int INDEX_RECORD_OFFSET = INDEX_OFFSET_OFFSET+8;
   
   public void readArguments(String args[]);

   public boolean isInteger(String s);

}
