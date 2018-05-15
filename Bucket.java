
public class Bucket {
	private IndexRecord[] records;
	
	//Constructor
	public Bucket() {
		records=new IndexRecord[dbimpl.RECORDS_PER_BUCKET];
		
		//Initialize records in bucket
		for(int i=0;i<dbimpl.RECORDS_PER_BUCKET;i++) {
			records[i]=new IndexRecord();
		}
	}
	
	/*Add record to bucket if slot available
	 * @param key String key for specified record
	 * @param pageNo int The number page record is located in heapfile
	 * @param receNo int The number record record is located in heapfile
	 * @param pagesize int Size of the pages in heapfile
	 * @return Boolean indicator if record is available
	 */
	public boolean addRecord(String key, int pageNo,int recNo,int pagesize) {
		//Iterate through records in bucket
		for(int i=0;i<dbimpl.RECORDS_PER_BUCKET;i++) {
			//Check if filled
			if(records[i].getOffset()==-1) {
				//Wriye if empty
				records[i].setValues(key,pageNo,recNo,pagesize);
				return true;
			}
		}
		return false;
	}
	
	/*Record accessor
	 * @param position int Position of record in bucket
	 * @return Index record at specified position
	 */
	public IndexRecord getRecord(int pos) {
		return records[pos];
	}
}
