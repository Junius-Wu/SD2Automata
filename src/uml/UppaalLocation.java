package uml;

public class UppaalLocation {
	Boolean init=false; //��ʼ״̬
	Boolean fnal=false; //����״̬
	String Name="null"; //����
	String id;
	int Type=0;   //0Ϊ���� 1Ϊ����
//	String lineEAID="null";
//	String Invariant="null";
	String R1 = "0";
	String R2 = "0";
	String Energe = "0";
	String ObjId = "NULL";
	String ObjName = "";
	String timeDuration;
	
	public String getTimeDuration() {
		return timeDuration;
	}
	public void setTimeDuration(String timeDuration) {
		this.timeDuration = timeDuration;
	}
	public Boolean getInit() {
		return init;
	}
	public void setInit(Boolean init) {
		this.init = init;
	}
	public Boolean getFnal() {
		return fnal;
	}
	public void setFnal(Boolean fnal) {
		this.fnal = fnal;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
//	public String getLineEAID() {
//		return lineEAID;
//	}
//	public void setLineEAID(String lineEAID) {
//		this.lineEAID = lineEAID;
//	}
//	public String getInvariant() {
//		return Invariant;
//	}
//	public void setInvariant(String invariant) {
//		Invariant = invariant;
//	}
	public String getR1() {
		return R1;
	}
	public void setR1(String r1) {
		R1 = r1;
	}
	public String getR2() {
		return R2;
	}
	public void setR2(String r2) {
		R2 = r2;
	}
	public String getEnerge() {
		return Energe;
	}
	public void setEnerge(String energe) {
		Energe = energe;
	}
	public String getObjId() {
		return ObjId;
	}
	public void setObjId(String objId) {
		ObjId = objId;
	}
	public String getObjName() {
		return ObjName;
	}
	public void setObjName(String objName) {
		ObjName = objName;
	}
	
	

}
