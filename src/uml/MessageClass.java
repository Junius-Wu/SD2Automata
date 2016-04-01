package uml;

public class MessageClass {
	
	String SequenceMsgId="null";
	String name="null";
	String sendEvent="null";
	String messageSort="null";
	
	public String getSequenceMsgId() 
	{
		return SequenceMsgId;
	}
	public void setSequenceMsgId(String SequenceMsgId)
	{
		this.SequenceMsgId = SequenceMsgId;
	}
	public String getSequenceMsgName() 
	{
		return name;
	}
	public void setSequenceMsgName(String name) 
	{
		this.name = name;
	}
	public String getSequenceMsgSendEvent() 
	{
		return sendEvent;
	}
	public void setSequenceMsgSendEvent(String sendEvent) 
	{
		this.sendEvent = sendEvent;
	}
	public String getMessageSort() 
	{
		return messageSort;
	}
	public void setMessageSort(String messageSort) 
	{
		this.messageSort = messageSort;
	}
}
