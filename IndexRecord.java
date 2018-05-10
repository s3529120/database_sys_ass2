
public class IndexRecord {
	private int pageNo;
	private int recNo;
	private String indexKey;
	public IndexRecord(String key, int pageNo,int recNo) {
		setIndexKey(key);
		setRecNo(recNo);
		setPageNo(pageNo);
	}
	public String getIndexKey() {
		return indexKey;
	}
	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getRecNo() {
		return recNo;
	}
	public void setRecNo(int recNo) {
		this.recNo = recNo;
	}
	
	public int getOffset(int pageSize) {
		return (pageNo*pageSize)+(recNo*dbimpl.RECORD_SIZE);
	}
}
