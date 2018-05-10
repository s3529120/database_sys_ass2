
public class Bucket {
	IndexRecord[] records;
	public Bucket() {
		records=new IndexRecord[dbimpl.RECORDS_PER_BUCKET];
	}
	
	public boolean addRecord(IndexRecord rec) {
		for(int i=0;i<records.length;i++) {
			if(records[i]==null) {
				records[i]=rec;
				return true;
			}
		}
		return false;
	}
}
