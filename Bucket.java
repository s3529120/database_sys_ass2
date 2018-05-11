
public class Bucket {
	private IndexRecord[] records;
	public Bucket() {
		records=new IndexRecord[dbimpl.RECORDS_PER_BUCKET];
		for(int i=0;i<records.length;i++) {
			records[i]=new IndexRecord();
		}
	}
	
	public boolean addRecord(String key, int pageNo,int recNo,int pagesize) {
		for(int i=0;i<records.length;i++) {
			if(records[i].getOffset()==-1) {
				records[i].setValues(key,pageNo,recNo,pagesize);
				return true;
			}
		}
		return false;
	}
	
	public IndexRecord getRecord(int pos) {
		return records[pos];
	}
}
