package uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.dom4j.Element;

public class Read
{
	ArrayList<WJLifeline> umlLifeLines=new ArrayList<WJLifeline>();
	ArrayList<MessageClass> umlMessages=new ArrayList<MessageClass>();
	ArrayList<ConnectorsClass> umlConnectors=new ArrayList<ConnectorsClass>();
	ArrayList<MessageComplete> umlMessageComplete=new ArrayList<MessageComplete>();
	ArrayList<WJFragment> umlFragment=new ArrayList<WJFragment>();
	ArrayList<WJFragment> umlFragmentInner=new ArrayList<WJFragment>();
	ArrayList<WJMessage> umlMsgFragment=new ArrayList<WJMessage>();
	ArrayList<WJDiagramsData> umlAllDiagramData = new ArrayList<WJDiagramsData>();
	
	HashMap<String , String> findAltsFather = new HashMap<String , String>();
	public boolean hasNoLifeline()
	{	
		if(umlLifeLines.isEmpty())                             	
			return true;
		else 
			return false;	
	}
	
	public  void load(Element root) throws Exception
	{
		ArrayList<Element> EAlifeLineList=new ArrayList();
		ArrayList<Element> EAconnectorList=new ArrayList();
		ArrayList<Element> EAfragmentList=new ArrayList();
		ArrayList<Element> EAmessagesList = new ArrayList();
		
		
		EAlifeLineList.addAll(root.element("Model").element("packagedElement").element("packagedElement").element("ownedBehavior").elements("lifeline"));
		EAconnectorList.addAll(root.element("Extension").element("connectors").elements("connector"));
		EAfragmentList.addAll(root.element("Model").element("packagedElement").element("packagedElement").element("ownedBehavior").elements("fragment"));
		EAmessagesList.addAll(root.element("Model").element("packagedElement").element("packagedElement").element("ownedBehavior").elements("message"));
		//读取lifeline信息
		for(Iterator<Element> lifeLineIterator=EAlifeLineList.iterator();lifeLineIterator.hasNext();)
		{
			Element elLifeLine=lifeLineIterator.next();
			WJLifeline lifeLine=new WJLifeline();
			lifeLine.setlifeLineId(elLifeLine.attribute("id").getValue());
			lifeLine.setlifeLineName(elLifeLine.attribute("name").getValue());
			umlLifeLines.add(lifeLine);
		}
		//读取message信息
		for(Iterator<Element> EAmessagesIterator=EAmessagesList.iterator();EAmessagesIterator.hasNext();)
		{
			Element elmessage=EAmessagesIterator.next();
			MessageClass message=new MessageClass();
			message.setSequenceMsgId(elmessage.attribute("id").getValue());
			message.setSequenceMsgName(elmessage.attribute("name").getValue());
			message.setSequenceMsgSendEvent(elmessage.attribute("sendEvent").getValue());
			message.setMessageSort(elmessage.attribute("messageSort").getValue());
			umlMessages.add(message);
		}
		//读取connectors信息
		for(Iterator<Element> connectorIterator=EAconnectorList.iterator();connectorIterator.hasNext();)
		{
			Element elConnector=connectorIterator.next();
			ConnectorsClass connectorsMsg=new ConnectorsClass();
			connectorsMsg.setConnectorId(elConnector.attribute("idref").getValue());
			connectorsMsg.setSourceId(elConnector.element("source").attribute("idref").getValue());
			connectorsMsg.setTragetId(elConnector.element("target").attribute("idref").getValue());
			connectorsMsg.setName(elConnector.element("properties").attribute("name").getValue());

			if (elConnector.element("style").attribute("value")!=null) {
				String styleValue=elConnector.element("style").attribute("value").getValue();
				connectorsMsg.setStyleValue(styleValue);
			} 
			umlConnectors.add(connectorsMsg);
		}
		//整合message和connectors的信息 ，把connectors中的source和targetID 读入到新类中
		for(Iterator<MessageClass> umlMessagesIterator=umlMessages.iterator();umlMessagesIterator.hasNext();)
		{
			MessageClass messageI = umlMessagesIterator.next();
			MessageComplete messageComplete=new MessageComplete();
			messageComplete.name = messageI.getSequenceMsgName();
			messageComplete.connectorId = messageI.getSequenceMsgId();
			messageComplete.sendEvent = messageI.getSequenceMsgSendEvent();
			messageComplete.messageSort = messageI.getMessageSort();
			
			for(Iterator<ConnectorsClass> umlConnectorsIterator=umlConnectors.iterator();umlConnectorsIterator.hasNext();)
			{
				ConnectorsClass connectorsI=umlConnectorsIterator.next();
				if(connectorsI.getConnectorId().equals(messageI.getSequenceMsgId()))
				{
					messageComplete.sourceId = connectorsI.getSourceId();
					messageComplete.tragetId = connectorsI.getTragetId();
					messageComplete.styleValue = connectorsI.getStyleValue();
				}
			}
			umlMessageComplete.add(messageComplete);
		}
//***************************************	 组合片段的嵌套读取			
		for(Iterator<Element> fragListIterator=EAfragmentList.iterator();fragListIterator.hasNext();)//遍历所有fragment
		{
			Element fragment=fragListIterator.next();
			if(fragment.attribute("type").getValue().equals("uml:CombinedFragment"))
			{//是组合片段
				Queue<Element> q = new LinkedList<Element>();
				Queue<String> q_AorP = new LinkedList<String>();
				Queue<String> q_BigId = new LinkedList<String>();
				q_BigId.add("null");
				q.add(fragment);
				while(!q.isEmpty())
				{
					Element parent = q.poll();
					/*if(parent.attribute("type").getValue().equals("uml:CombinedFragment"))//打印pop组合片段名字loop
						System.out.println("pop:"+parent.attribute("interactionOperator").getValue());
					else																//打印alt或者par
						System.out.println("pop:"+q_AorP.peek()+parent.attribute("id").getValue());*/
					
					ArrayList<Element> alfrags=new ArrayList<Element>();
					
					if(parent.attribute("type").getValue().equals("uml:CombinedFragment"))//是整个组合片段
					{
						
						WJFragment fragInfo = new WJFragment();
						//visit(parent);
						fragInfo.setFragId(parent.attribute("id").getValue());					
						fragInfo.setFragType(parent.attribute("interactionOperator").getValue());//loop
					
						if(fragInfo.getFragType().equals("opt") || fragInfo.getFragType().equals("loop") || fragInfo.getFragType().equals("break"))//非alt par 只有1个operand
						{
							//System.out.println(parent.element("operand").element("guard").element("specification").attribute("body").getValue());
							fragInfo.setFragCondition(parent.element("operand").element("guard").element("specification").attribute("body").getValue());
							alfrags.addAll(parent.element("operand").elements("fragment"));
								//很多fragment 代表迁移(uml:OccurrenceSpecification）和内嵌的组合片段(uml:CombinedFragment)																														
							Iterator<Element> alfragsIterator = alfrags.iterator();
							
							ArrayList<String> sID= new ArrayList<String>();
							ArrayList<String> tID= new ArrayList<String>();
							while(alfragsIterator.hasNext())//遍历这一个operand的所有fragment
							{
								Element child_fragsI = alfragsIterator.next();
								
								if(child_fragsI.attribute("type").getValue().equals("uml:OccurrenceSpecification"))
								{	
									sID.add(child_fragsI.attribute("id").getValue());
									child_fragsI = alfragsIterator.next();
									tID.add(child_fragsI.attribute("id").getValue());
									
								}
								else if(child_fragsI.attribute("type").getValue().equals("uml:CombinedFragment"))
								{
									q.add(child_fragsI);
									q_BigId.add(parent.attribute("id").getValue());
									//System.out.println("push:"+child_fragsI.attribute("interactionOperator").getValue());
									if(child_fragsI.attribute("interactionOperator").getValue().equals("alt")
											||child_fragsI.attribute("interactionOperator").getValue().equals("par"))
										findAltsFather.put(child_fragsI.attribute("id").getValue(), parent.attribute("id").getValue());
								}
							}		
							String[] s = new String[sID.size()];
							sID.toArray(s);
							String[] t = new String[tID.size()];
							tID.toArray(t);
							fragInfo.setSourceId(s);
							fragInfo.setTargetId(t);
							fragInfo.setBigId(q_BigId.poll());
							umlFragment.add(fragInfo);						
							
						}//非alt par 只有1个operand
						else if(fragInfo.getFragType().equals("alt") || fragInfo.getFragType().equals("par"))//alt ||par 多个operand
						{
							ArrayList<Element> operandList=new ArrayList();
							operandList.addAll(parent.elements("operand"));//alt的所有小操作域
							q_BigId.poll();
							
							for(Iterator<Element> operandIterator=operandList.iterator();operandIterator.hasNext();)//遍历所有operand
							{
								Element operandI = operandIterator.next();
								
								q.add(operandI);
								q_AorP.add(parent.attribute("interactionOperator").getValue());
								q_BigId.add(parent.attribute("id").getValue());
							}
							
													
							//System.out.println("是一个"+parent.attribute("interactionOperator").getValue());
						}
						
					}
					else if(parent.attribute("type").getValue().equals("uml:InteractionOperand"))//是组合片段中的操作域
					{
						WJFragment fragInfo = new WJFragment();
						//visit(parent);
						
						fragInfo.setFragId(parent.attribute("id").getValue());					
						fragInfo.setFragType(q_AorP.poll());//出列 alt或者par
						fragInfo.setFragCondition(parent.element("guard").element("specification").attribute("body").getValue());
						
						alfrags.addAll(parent.elements("fragment"));
						///
						Iterator<Element> alfragsIterator = alfrags.iterator();
						
						ArrayList<String> sID= new ArrayList<String>();
						ArrayList<String> tID= new ArrayList<String>();
						while(alfragsIterator.hasNext())//遍历这一个操作域的所有fragment
						{
							Element child_fragsI = alfragsIterator.next();
							
							if(child_fragsI.attribute("type").getValue().equals("uml:OccurrenceSpecification"))
							{	
								sID.add(child_fragsI.attribute("id").getValue());
								child_fragsI = alfragsIterator.next();
								tID.add(child_fragsI.attribute("id").getValue());
								
 							}
							else if(child_fragsI.attribute("type").getValue().equals("uml:CombinedFragment"))
							{
								q.add(child_fragsI);
								q_BigId.add(parent.attribute("id").getValue());
								//System.out.println("push:"+child_fragsI.attribute("interactionOperator").getValue());
								if(child_fragsI.attribute("interactionOperator").getValue().equals("alt")
										||child_fragsI.attribute("interactionOperator").getValue().equals("par"))
									findAltsFather.put(child_fragsI.attribute("id").getValue(), parent.attribute("id").getValue());
							}
						}		
						String[] s = new String[sID.size()];
						sID.toArray(s);
						String[] t = new String[tID.size()];
						tID.toArray(t);
						fragInfo.setSourceId(s);
						fragInfo.setTargetId(t);
						fragInfo.setBigId(q_BigId.poll());
						umlFragment.add(fragInfo);		
					}
						
					
				}
								
			}
		}
		Iterator iterator=umlFragment.iterator();  //对fragment片段的结构进行调整
		while(iterator.hasNext())
		{
			WJFragment I = (WJFragment)iterator.next();
			String bigid=I.getBigId();
			if(findAltsFather.containsKey(bigid))//alt操作域的父亲是alt 为了算法的实现，这里改成其祖父
			{
				I.setBigId(findAltsFather.get(bigid));
				I.setComId(bigid);
			}
			//由于最外层的alt的操作域找不到祖父，则交换bigID（alt's ID）和comID（null）
			if(!I.getBigId().equals("null")&&I.getComId().equals("null")&&(I.getFragType().equals("alt")||I.getFragType().equals("par")))
    		{
    			String temp = I.getBigId();
    			I.setBigId(I.comId);
    			I.setComId(temp);	    			
    		}
		}
//***************************************	 组合片段的嵌套读取		
		//设置messageList
		ArrayList <MessageComplete> messageList = new ArrayList <MessageComplete>();
		Iterator <MessageComplete> msgComplete = umlMessageComplete.iterator();
		Iterator <Element> messageIterator = EAmessagesList.iterator();
		while(messageIterator.hasNext()&&msgComplete.hasNext())
		{
			Element messageI = messageIterator.next();
			MessageComplete MC = msgComplete.next();
			
			ArrayList<Element> allargument = new ArrayList<Element>();
			allargument.addAll(messageI.elements("argument"));
			Iterator <Element> allargIterator = allargument.iterator();
			String T1 = null,T2 = null,Energe = null,R1 = null,R2 = null;
			while(allargIterator.hasNext())
			{
				Element allargI = allargIterator.next();
				if(allargI.attributeValue("name").equals("T1"))
					T1=allargI.element("defaultValue").attributeValue("value");
				if(allargI.attributeValue("name").equals("T2"))
					T2=allargI.element("defaultValue").attributeValue("value");
				if(allargI.attributeValue("name").equals("Energe"))
					Energe=allargI.element("defaultValue").attributeValue("value");
				if(allargI.attributeValue("name").equals("R1"))
					R1=allargI.element("defaultValue").attributeValue("value");
				if(allargI.attributeValue("name").equals("R2"))
					R2=allargI.element("defaultValue").attributeValue("value");
			}
			
			MessageComplete messageX = new MessageComplete();
			messageX.setName(messageI.attributeValue("name"));
			messageX.setConnectorId(messageI.attributeValue("id"));
			messageX.setSourceId(messageI.attributeValue("sendEvent"));
			messageX.setTragetId(messageI.attributeValue("receiveEvent"));
			messageX.setFromId(MC.getSourceId());
			messageX.setToId(MC.getTragetId());
			messageX.setStyleValue(MC.styleValue);
			messageX.setT1(T1);
			messageX.setT2(T2);
			messageX.setEnerge(Energe);
			messageX.setR1(R1);
			messageX.setR2(R2);
			
			messageList.add(messageX);
		}
		//设定message最后的值 0.设定各种值 1.设置5种时间约束 2.在哪个fragment中
		for(Iterator<MessageComplete> messageListIterator=messageList.iterator();messageListIterator.hasNext();)
		{
			/////////////////////////EAmessage的遍历
			MessageComplete EAmessage=messageListIterator.next();
			WJMessage message=new WJMessage();
			//1.
			message.setName(EAmessage.getName());					//name
			message.setConnectorId(EAmessage.getConnectorId());//messageID						
			message.setSendEvent(EAmessage.getSendEvent());		//event	
			message.setSourceId(EAmessage.getSourceId());		//sourceid
			message.setTragetId(EAmessage.getTragetId());		//targetid
			message.setFromId(EAmessage.getFromId());
			message.setToId(EAmessage.getToId());
			message.setT1(EAmessage.getT1());
			message.setT2(EAmessage.getT2());
			message.setEnerge(EAmessage.getEnerge());
			message.setR1(EAmessage.getR1());
			message.setR2(EAmessage.getR2());
			//2.
			setMessageTimeDurations(message, EAmessage.getStyleValue());
			for(Iterator<WJFragment> fragIterator=umlFragment.iterator();fragIterator.hasNext();)
			{
				WJFragment fragment=fragIterator.next();
				boolean finish = false;
				String[] s = fragment.getSourceId();//一个fragment有多个source
				String[] t = fragment.getTargetId();
				for(int i=0; i< fragment.getSourceId().length;i++)//看message在哪个fragment中
					if(message.getSourceId().equals(s[i]) && message.getTragetId().equals(t[i]))
					{	
						message.setInFragId(fragment.getFragId());
						message.setInFragName(fragment.getFragType());
						finish = true;
						break;
					}
				
				if(finish)
					break;
			}
			umlMsgFragment.add(message);//最终得到的message
			
		}
		
		//成员变量 umlAllDiagramData 包含所有图的list
		//获得所有图的包含id情况
		ArrayList<Element> EADiagramsList = new ArrayList();//存放读取得到的element
				
		//1.取得所有的diagram 
		EADiagramsList.addAll(root.element("Extension").element("diagrams").elements("diagram"));
		
		//2.遍历EADiagramIDsList
		for(Iterator<Element>  EADiagramsListIterator=EADiagramsList.iterator();EADiagramsListIterator.hasNext();)
		{
			//取得第i张图
			Element diagramI=EADiagramsListIterator.next();
			
			//获得这张图所有elements 
			ArrayList <Element> elements = new ArrayList <Element>();
			elements.addAll(diagramI.element("elements").elements("element"));
			
			//遍历elements 设置ids
			ArrayList <String> ids = new ArrayList<String>();	
			for(Iterator<Element>  elementsIterator=elements.iterator();elementsIterator.hasNext();)
			{
				Element elementI = elementsIterator.next();
				ids.add(elementI.attributeValue("subject").substring(13));//取得13位之后的id属性
			}
			
			//获得这张图的name
			String name = diagramI.element("properties").attributeValue("name");
			
			//创建DiagramsData对象
			WJDiagramsData diagramData = new WJDiagramsData();
			diagramData.ids = ids;
			diagramData.name = name;
			
			//将DiagramsData对象 添加到成员变量umlAllDiagramData中
			umlAllDiagramData.add(diagramData);
		}
		
		
	}
	
//DCBMX=0;DCBMGUID={65DF8856-63B5-423a-9BD1-952DEA23D616};SEQDC=10000;SEQDO=10002;SEQTC=10003;SEQTO=10004;DCBM=10001;
	//message设定5种时间约束
	private void setMessageTimeDurations(WJMessage message, String styleValue) {
		if (styleValue == null) return;
		String[] strArray = styleValue.split(";");
		for (int i = 0; i < strArray.length; i++) {
			String[] nameAndValue = strArray[i].split("=");
			if (nameAndValue[0].equals("SEQDC")) {
				message.setSEQDC(nameAndValue[1]);
			} else if (nameAndValue[0].equals("SEQDO")){
				message.setSEQDO(nameAndValue[1]);
			} else if (nameAndValue[0].equals("SEQTC")){
				message.setSEQTC(nameAndValue[1]);
			} else if (nameAndValue[0].equals("SEQTO")){
				message.setSEQTO(nameAndValue[1]);
			} else if (nameAndValue[0].equals("DCBM")){
				message.setDCBM(nameAndValue[1]);
			}
		}
		
	}

	public ArrayList<WJDiagramsData> getUmlAllDiagramData() {
		return umlAllDiagramData;
	}

	public HashMap<String , String>	getfindAltsFather()
	{
		return findAltsFather;
	}
	public ArrayList<WJLifeline> getLifeLines()
	{
		return umlLifeLines;
	}
	
	public ArrayList<WJFragment> getUmlFragmentMsg()
	{
		return umlFragment;
	}
	public ArrayList<WJMessage> getUmlMsgFragment()
	{
		return umlMsgFragment;
	}
}
	

