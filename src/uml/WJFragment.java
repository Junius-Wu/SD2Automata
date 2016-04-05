package uml;

import java.util.ArrayList;

import org.dom4j.Element;

public class WJFragment implements Cloneable{//×éºÏÆ¬¶Î
	public Object clone() {   
		WJFragment o = null;   
        try {   
            o = (WJFragment) super.clone();   
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
        return o;   
    }   
	String fragType="null";
	String fragId="null";
	
	String fragCondition="null";
		
	String BigId="null";
	String comId="null";
	 
	public String getComId() {
		return comId;
	}

	public void setComId(String comId) {
		this.comId = comId;
	}

	public String getBigId() {
		return BigId;
	}

	public void setBigId(String bigId) {
		BigId = bigId;
	}

	String sourceId[];
	String targetId[];
	
	ArrayList<Element> frag=new ArrayList<Element>();

	
	public String getFragType() {
		return fragType;
	}

	public void setFragType(String fragType) {
		this.fragType = fragType;
	}

	public String getFragId() {
		return fragId;
	}

	public void setFragId(String fragId) {
		this.fragId = fragId;
	}

	

	public String getFragCondition() {
		return fragCondition;
	}

	public void setFragCondition(String fragCondition) {
		this.fragCondition = fragCondition;
	}

	
	public String[] getSourceId() {
		return sourceId;
	}

	public void setSourceId(String[] sourceId) {
		this.sourceId = sourceId;
	}

	public String[] getTargetId() {
		return targetId;
	}

	public void setTargetId(String[] targetId) {
		this.targetId = targetId;
	}

	public ArrayList<Element> getFrag() {
		return frag;
	}

	public void setFrag(ArrayList<Element> frag) {
		this.frag = frag;
	}
	
	
}
