
public class IndexRecord {
	private int offset;
	private String indexKey;
	
	//Constructor
	public IndexRecord() {
		//Default values for empty slot checks
		setIndexKey("empty");
		setOffset(-1);
	}
	
	/*Set class variables
	 *  @param key String key of index record
	 *  @param pageNo int Page in which record to be indexed is stored
	 *  @param  recNo int position of record to be indexed in specified page
	 *  @param pagesize int Size of pages in heapfile
	 */
	public void setValues(String key, int pageNo,int recNo,int pageSize) {
		setIndexKey(key);
		setOffset((pageNo*pageSize)+(recNo*dbimpl.RECORD_SIZE));
	}
	
	//Index key accessor
	public String getIndexKey() {
		return indexKey;
	}
	
	//Index key accessor
	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}
	
	//Offset accessor
	public int getOffset() {
		return offset;
	}


	//Offset mutator
	public void setOffset(int offset) {
		this.offset = offset;
	}
}
