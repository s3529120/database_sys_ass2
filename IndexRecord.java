
public class IndexRecord {
	private int offset;
	private String indexKey;
	public IndexRecord() {
		setIndexKey("empty");
		setOffset(-1);
	}
	
	public void setValues(String key, int pageNo,int recNo,int pageSize) {
		setOffset((pageNo*pageSize)+(recNo*dbimpl.RECORD_SIZE));
	}
	public String getIndexKey() {
		return indexKey;
	}
	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}
	
	public long getOffset() {
		return offset;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}
}
