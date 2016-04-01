package uml;

public class ConnectorsClass {
	
	String connectorId="null";
	String sourceId="null";
	String tragetId="null";
	String name="null";
	String messageSort="null";
	String styleValue;
	public String getStyleValue() {
		return styleValue;
	}
	public void setStyleValue(String styleValue) {
		this.styleValue = styleValue;
	}
	public String getConnectorId() 
	{
		return connectorId;
	}
	public void setConnectorId(String connectorId)
	{
		this.connectorId = connectorId;
	}
	public String getSourceId()
	{
		return sourceId;
	}
	public void setSourceId(String sourceId)
	{
		this.sourceId = sourceId;
	}
	public String getTragetId() 
	{
		return tragetId;
	}
	public void setTragetId(String tragetId) 
	{
		this.tragetId = tragetId;
	}
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
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
