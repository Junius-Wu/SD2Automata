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
	ArrayList<WJMessage> umlMessageFinal=new ArrayList<WJMessage>();
	ArrayList<WJDiagramsData> umlAllDiagramData = new ArrayList<WJDiagramsData>();
	ArrayList<REF> umlREF = new ArrayList<REF>();
	
	HashMap<String , String> findAltsFather = new HashMap<String , String>();
	public boolean hasNoLifeline()
	{	
		if(umlLifeLines.isEmpty())                             	
			return true;
		else 
			return false;	
	}
	
	public void load(Element root) throws Exception
	{
		ArrayList<Element> EAlifeLineList=new ArrayList();
		ArrayList<Element> EAconnectorList=new ArrayList();
		ArrayList<Element> EAfragmentList=new ArrayList();
		ArrayList<Element> EAmessagesList = new ArrayList();
		
		//�½�һ��hashMap�����洢�������Ƭ��ʱ�õ���lastMessage����Ӧ��diagram��
		HashMap<String, String> lastMessageIDByKeyWithDiagramID = new HashMap<String, String>();
		//�½�һ��hashMap���洢sourceID&targetID -��messageID ���Ż�����ʱ�䣩
		HashMap<String, String> MessageIDByKeyWithSourceIDAndTargetID = new HashMap<String, String>();
		
		EAlifeLineList.addAll(root.element("Model").element("packagedElement").element("packagedElement").element("ownedBehavior").elements("lifeline"));
		EAconnectorList.addAll(root.element("Extension").element("connectors").elements("connector"));
		EAfragmentList.addAll(root.element("Model").element("packagedElement").element("packagedElement").element("ownedBehavior").elements("fragment"));
		EAmessagesList.addAll(root.element("Model").element("packagedElement").element("packagedElement").element("ownedBehavior").elements("message"));
		//��Ա���� umlAllDiagramData ��������ͼ��list
			//�������ͼ�İ���id���
			ArrayList<Element> EADiagramsList = new ArrayList();//��Ŷ�ȡ�õ���element
					
			//1.ȡ�����е�diagram 
			EADiagramsList.addAll(root.element("Extension").element("diagrams").elements("diagram"));
			
			//2.����EADiagramIDsList
			for(Iterator<Element>  EADiagramsListIterator=EADiagramsList.iterator();EADiagramsListIterator.hasNext();)
			{
				//ȡ�õ�i��ͼ
				Element diagramI=EADiagramsListIterator.next();
				
				//�������ͼ����elements 
				ArrayList <Element> elements = new ArrayList <Element>();
				elements.addAll(diagramI.element("elements").elements("element"));
				
				//����elements ����ids
				ArrayList <String> ids = new ArrayList<String>();	
				for(Iterator<Element>  elementsIterator=elements.iterator();elementsIterator.hasNext();)
				{
					Element elementI = elementsIterator.next();
					ids.add(elementI.attributeValue("subject").substring(13));//ȡ��13λ֮���id���� ��Ϊactor��idֻ�к���13λ�������
				}
				
				//�������ͼ��name
				String name = diagramI.element("properties").attributeValue("name");
				
				//����DiagramsData����
				WJDiagramsData diagramData = new WJDiagramsData();
				diagramData.ids = ids;
				diagramData.name = name;
				diagramData.diagramID = diagramI.attributeValue("id");
				lastMessageIDByKeyWithDiagramID.put(diagramData.diagramID, "init");
				//��DiagramsData���� ��ӵ���Ա����umlAllDiagramData��
				umlAllDiagramData.add(diagramData);
			}
		
		//��ȡlifeline��Ϣ
		for(Iterator<Element> lifeLineIterator=EAlifeLineList.iterator();lifeLineIterator.hasNext();)
		{
			Element elLifeLine=lifeLineIterator.next();
			WJLifeline lifeLine=new WJLifeline();
			lifeLine.setlifeLineId(elLifeLine.attribute("id").getValue());
			lifeLine.setlifeLineName(elLifeLine.attribute("name").getValue());
			umlLifeLines.add(lifeLine);
		}
		//��ȡmessage��Ϣ
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
		//��ȡconnectors��Ϣ
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
		//����message��connectors����Ϣ ����connectors�е�source��targetID ���뵽������
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
		//����messageList
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
					MessageIDByKeyWithSourceIDAndTargetID.put(messageX.getSourceId()+messageX.getTragetId(), messageX.getConnectorId());
					messageList.add(messageX);
				}
//***************************************	 ���Ƭ�ε�Ƕ�׶�ȡ			
		for(Iterator<Element> fragListIterator=EAfragmentList.iterator();fragListIterator.hasNext();)//��������fragment
		{
			Element fragment=fragListIterator.next();
			if(fragment.attribute("type").getValue().equals("uml:OccurrenceSpecification"))
			{	
				String sourceID = fragment.attribute("id").getValue();
				fragment = fragListIterator.next();
				String targetID = fragment.attribute("id").getValue();		
				//ͨ��sourceID��targetID�ҵ���Ӧ��messageID
				String lastMessageID = MessageIDByKeyWithSourceIDAndTargetID.get(sourceID+targetID);
				//����lastMessageID��Ӧ��DiagramID
				lastMessageIDByKeyWithDiagramID.put(findDiagramIDByChildID(lastMessageID), lastMessageID);
					
				
			}
			if (fragment.attribute("type").getValue().equals("uml:InteractionUse")) {//����������һ��ref
				REF ref = new REF();
				ref.setRefID(fragment.attributeValue("id"));
				ref.setDiagramName(fragment.attributeValue("name"));
				String diagramID = findDiagramIDByChildID(ref.getRefID());//�ҵ����ref������diagram
				ref.setLastMessageID(lastMessageIDByKeyWithDiagramID.get(diagramID));//����ref��lastmessageIDΪ���ͼ��lastmessageID
				
				ref.setInFragID("null");
				ref.setInFragName("SD");
				lastMessageIDByKeyWithDiagramID.put(diagramID, "REF:"+ref.getInFragID());//��һ��REF���
				umlREF.add(ref);
			}
			if(fragment.attribute("type").getValue().equals("uml:CombinedFragment"))
			{//�����Ƭ��
				Queue<Element> q = new LinkedList<Element>();
				Queue<String> q_AorP = new LinkedList<String>();
				Queue<String> q_BigId = new LinkedList<String>();
				q_BigId.add("null");
				q.add(fragment);
				while(!q.isEmpty())
				{
					Element parent = q.poll();
					/*if(parent.attribute("type").getValue().equals("uml:CombinedFragment"))//��ӡpop���Ƭ������loop
						System.out.println("pop:"+parent.attribute("interactionOperator").getValue());
					else																//��ӡalt����par
						System.out.println("pop:"+q_AorP.peek()+parent.attribute("id").getValue());*/
					
					ArrayList<Element> alfrags=new ArrayList<Element>();

					if(parent.attribute("type").getValue().equals("uml:CombinedFragment"))//���������Ƭ��
					{
						
						WJFragment fragInfo = new WJFragment();
						//visit(parent);
						fragInfo.setFragId(parent.attribute("id").getValue());					
						fragInfo.setFragType(parent.attribute("interactionOperator").getValue());//loop
					
						if(fragInfo.getFragType().equals("opt") || fragInfo.getFragType().equals("loop") || fragInfo.getFragType().equals("break"))//��alt par ֻ��1��operand
						{
							//System.out.println(parent.element("operand").element("guard").element("specification").attribute("body").getValue());
							fragInfo.setFragCondition(parent.element("operand").element("guard").element("specification").attribute("body").getValue());
							alfrags.addAll(parent.element("operand").elements("fragment"));
								//�ܶ�fragment ����Ǩ��(uml:OccurrenceSpecification������Ƕ�����Ƭ��(uml:CombinedFragment)																														
							Iterator<Element> alfragsIterator = alfrags.iterator();
							
							ArrayList<String> sID= new ArrayList<String>();//������еĿ�ʼid
							ArrayList<String> tID= new ArrayList<String>();
							while(alfragsIterator.hasNext())//������һ��operand������fragment
							{ 
								Element child_fragsI = alfragsIterator.next();
								
								if (child_fragsI.attribute("type").getValue().equals("uml:InteractionUse")) {//opt loop break���������һ��ref
									REF ref = new REF();
									ref.setRefID(child_fragsI.attributeValue("id"));
									ref.setDiagramName(child_fragsI.attributeValue("name"));
									String diagramID = findDiagramIDByChildID(ref.getRefID());//�ҵ����ref������diagram
									ref.setLastMessageID(lastMessageIDByKeyWithDiagramID.get(diagramID));//����ref��lastmessageIDΪ���ͼ��lastmessageID
									
									ref.setInFragID(fragInfo.getFragId());
									ref.setInFragName(fragInfo.getFragType());
									lastMessageIDByKeyWithDiagramID.put(diagramID, "REF:"+ref.getInFragID());//��һ��REF���
									umlREF.add(ref);
								}
								if(child_fragsI.attribute("type").getValue().equals("uml:OccurrenceSpecification"))//2��fragment��Ӧһ��message
								{	
									String sourceID = child_fragsI.attribute("id").getValue();
									sID.add(sourceID);
									
									child_fragsI = alfragsIterator.next();
									
									String targetID = child_fragsI.attribute("id").getValue();
									tID.add(targetID);
									
									//ͨ��sourceID��targetID�ҵ���Ӧ��messageID
									String lastMessageID = MessageIDByKeyWithSourceIDAndTargetID.get(sourceID+targetID);
									//����lastMessageID��Ӧ��DiagramID
									lastMessageIDByKeyWithDiagramID.put(findDiagramIDByChildID(lastMessageID), lastMessageID);
										
									
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
							
						}//��alt par ֻ��1��operand
						else if(fragInfo.getFragType().equals("alt") || fragInfo.getFragType().equals("par"))//alt ||par ���operand
						{
							ArrayList<Element> operandList=new ArrayList();
							operandList.addAll(parent.elements("operand"));//alt������С������
							q_BigId.poll();
							
							for(Iterator<Element> operandIterator=operandList.iterator();operandIterator.hasNext();)//��������operand
							{
								Element operandI = operandIterator.next();
								
								q.add(operandI);
								q_AorP.add(parent.attribute("interactionOperator").getValue());
								q_BigId.add(parent.attribute("id").getValue());
							}
							
													
							//System.out.println("��һ��"+parent.attribute("interactionOperator").getValue());
						}
						
					}
					else if(parent.attribute("type").getValue().equals("uml:InteractionOperand"))//�����Ƭ���еĲ�����
					{
						WJFragment fragInfo = new WJFragment();
						//visit(parent);
						
						fragInfo.setFragId(parent.attribute("id").getValue());					
						fragInfo.setFragType(q_AorP.poll());//���� alt����par
						fragInfo.setFragCondition(parent.element("guard").element("specification").attribute("body").getValue());
						
						alfrags.addAll(parent.elements("fragment"));
						///
						Iterator<Element> alfragsIterator = alfrags.iterator();
						
						ArrayList<String> sID= new ArrayList<String>();
						ArrayList<String> tID= new ArrayList<String>();
						while(alfragsIterator.hasNext())//������һ�������������fragment
						{
							Element child_fragsI = alfragsIterator.next();
							if (child_fragsI.attribute("type").getValue().equals("uml:InteractionUse")) {//������������������һ��ref
								REF ref = new REF();
								ref.setRefID(child_fragsI.attributeValue("id"));
								ref.setDiagramName(child_fragsI.attributeValue("name"));
								String diagramID = findDiagramIDByChildID(ref.getRefID());//�ҵ����ref������diagram
								ref.setLastMessageID(lastMessageIDByKeyWithDiagramID.get(diagramID));//����ref��lastmessageIDΪ���ͼ��lastmessageID
								
								ref.setInFragID(fragInfo.getFragId());
								ref.setInFragName(fragInfo.getFragType());
								lastMessageIDByKeyWithDiagramID.put(diagramID, "REF:"+ref.getInFragID());//��һ��REF���
								umlREF.add(ref);
							}
							if(child_fragsI.attribute("type").getValue().equals("uml:OccurrenceSpecification"))
							{	
								String sourceID = child_fragsI.attribute("id").getValue();
								sID.add(sourceID);
								child_fragsI = alfragsIterator.next();
								String targetID = child_fragsI.attribute("id").getValue();
								tID.add(targetID);
								
								//ͨ��sourceID��targetID�ҵ���Ӧ��messageID
								String lastMessageID = MessageIDByKeyWithSourceIDAndTargetID.get(sourceID+targetID);
								//����lastMessageID��Ӧ��DiagramID
								lastMessageIDByKeyWithDiagramID.put(findDiagramIDByChildID(lastMessageID), lastMessageID);
									
								
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
		Iterator iterator=umlFragment.iterator();  //��fragmentƬ�εĽṹ���е���
		while(iterator.hasNext())
		{
			WJFragment I = (WJFragment)iterator.next();
			String bigid=I.getBigId();
			if(findAltsFather.containsKey(bigid))//alt������ĸ�����alt Ϊ���㷨��ʵ�֣�����ĳ����游
			{
				I.setBigId(findAltsFather.get(bigid));
				I.setComId(bigid);
			}
			//����������alt�Ĳ������Ҳ����游���򽻻�bigID��alt's ID����comID��null��
			if(!I.getBigId().equals("null")&&I.getComId().equals("null")&&(I.getFragType().equals("alt")||I.getFragType().equals("par")))
    		{
    			String temp = I.getBigId();
    			I.setBigId(I.comId);
    			I.setComId(temp);	    			
    		}
		}
//***************************************	 ���Ƭ�ε�Ƕ�׶�ȡ	end	
		
		//�趨message����ֵ 0.�趨����ֵ 1.����5��ʱ��Լ�� 2.���ĸ�fragment��
		for(Iterator<MessageComplete> messageListIterator=messageList.iterator();messageListIterator.hasNext();)
		{
			/////////////////////////EAmessage�ı���
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
				String[] s = fragment.getSourceId();//һ��fragment�ж��source
				String[] t = fragment.getTargetId();
				for(int i=0; i< fragment.getSourceId().length;i++)//��message���ĸ�fragment��
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
			umlMessageFinal.add(message);//���յõ���message
			
		}
		///�ҳ�lifeline fragment message ref �Ĺ���
		for(WJDiagramsData diagramData : umlAllDiagramData) {
			for(WJLifeline lifeline : umlLifeLines) {
				if (diagramData.getIds().contains(lifeline.getlifeLineId().substring(13))) {
					diagramData.getLifelineArray().add(lifeline);
				}
			}
			for(WJFragment fragment : umlFragment) {
				if (diagramData.getIds().contains(fragment.getFragId().substring(13))) {
					diagramData.getFragmentArray().add(fragment);
				}
			}
			for(WJMessage message : umlMessageFinal) {
				if (diagramData.getIds().contains(message.getConnectorId().substring(13))) {
					diagramData.getMessageArray().add(message);
				}
			}
			for(REF ref : umlREF) {
				if (diagramData.getIds().contains(ref.getRefID().substring(13))) {
					diagramData.getRefArray().add(ref);
				}
			}
		}
		
		//ͼ������
		
		//��ref���д��� �ϲ���ͼ����ͼ
		for(WJDiagramsData diagramData : umlAllDiagramData) {
			DFSDiagramByREF(diagramData);
		}
		
		
		
	}
/*method-----------------------------------------------------------*/	
	private void DFSDiagramByREF(WJDiagramsData diagramData) {
		if (diagramData.refArray.size() == 0) {//����û������ ���������Ѿ���ɾ���� �Ѿ�����ȫͼ 
			return ;
		}
		
		for(REF ref : diagramData.refArray) {
			WJDiagramsData childDiagram = findDiagramByName(ref.getDiagramName());
			DFSDiagramByREF(childDiagram);//������ͼ ʹ���������õ���ȫͼ
			fixDiagramData(diagramData,childDiagram,ref);
			//diagramData.refArray.remove(ref);
		}
		diagramData.refArray.clear();
	}
	//���ϲ���ɵ�û�����õ���ȫ��ͼ �ϲ��� ��ͼ
	private void fixDiagramData(WJDiagramsData diagramData, WJDiagramsData childDiagram, REF ref) {
		//���lifeline
		for(WJLifeline lifeline : childDiagram.getLifelineArray()) {
			if (!diagramData.getLifelineArray().contains(lifeline)) {//��Ӹ�ͼû�е�lifeline
				diagramData.getLifelineArray().add(lifeline);
			}
		}
		
		//�޸�������fragment ����һ�ݵ���ͼ��
		ArrayList<WJFragment> copyFragmentArray = new ArrayList<>();
		for(WJFragment fragment : childDiagram.getFragmentArray()) {//������е����Ƭ��
			WJFragment copyFragment = (WJFragment) fragment.clone();
			if (fragment.BigId.equals("null")) {//��������sd
				copyFragment.setBigId(ref.getInFragID());	
			}
			copyFragmentArray.add(copyFragment);	
		}
		diagramData.getFragmentArray().addAll(copyFragmentArray);
		
		//�ȸ���һ����ͼ��messageArray
		ArrayList<WJMessage> copyMessageArray = new ArrayList<>(); 
		for(WJMessage message : childDiagram.getMessageArray()) {
			WJMessage copyMessage = (WJMessage) message.clone();
			copyMessageArray.add(copyMessage);
		}
		//Ȼ��infragIDΪ"null"(˵����SD��) ��ΪrefƬ�����ڵ����Ƭ��id
		for(WJMessage message : copyMessageArray) {
			if (message.getInFragId().equals("null")) {
				message.setInFragId(ref.getInFragID());
				message.setInFragName(ref.getInFragName());
			}
		}
		//��copyMessage�ӵ��ض�λ�� : init REF EA
		//1. init ��ʾ��ͼ�������ڳ�ʼ 
		if (ref.getLastMessageID().equals("init")) {
			diagramData.getMessageArray().addAll(0, copyMessageArray);
			diagramData.setRefEndTo(copyMessageArray.size());
		} else if (ref.getLastMessageID().substring(0, 3).equals("REF")) {
		//2. REF ˵����һ����ref ���뵽��һ��ref����֮��
			diagramData.getMessageArray().addAll(diagramData.getRefEndTo(), copyMessageArray);
			diagramData.setRefEndTo(diagramData.getRefEndTo() + copyMessageArray.size());//�����µ�endTo
		} else {//���һ������ͨ����� ����ֱ�Ӳ��뵽ĳ��message֮��
			for(int i = 0; i < diagramData.getMessageArray().size(); i++) {
				if (diagramData.getMessageArray().get(i).getConnectorId().equals(ref.getLastMessageID())) {
					//�ҵ���n�� ���뵽n+1λ��֮ǰ
					diagramData.getMessageArray().addAll(i + 1, copyMessageArray);
					diagramData.setRefEndTo(i + copyMessageArray.size() + 1);
					break;
				}
			}
		}
		
		

	}

	private WJDiagramsData findDiagramByName(String diagramName) {
		
		for(WJDiagramsData diagramsData :umlAllDiagramData) 
		{
			//����diagram�洢����13λ���id �����ú�13λ�����ж�
			if (diagramsData.getName().equals(diagramName))//���������id
				return diagramsData;
			
		}
		return null;
	}

	private String findDiagramIDByChildID(String childID) {

		for(WJDiagramsData diagramsData :umlAllDiagramData) 
		{
			//����diagram�洢����13λ���id �����ú�13λ�����ж�
			if (diagramsData.ids.contains(childID.substring(13)))//���������id
				return diagramsData.diagramID;
			
		}
		return null;
	}

//DCBMX=0;DCBMGUID={65DF8856-63B5-423a-9BD1-952DEA23D616};SEQDC=10000;SEQDO=10002;SEQTC=10003;SEQTO=10004;DCBM=10001;
	//message�趨5��ʱ��Լ��
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

	public ArrayList<REF> getUmlREF() {
		return umlREF;
	}


	public ArrayList<WJDiagramsData> getUmlAllDiagramData() {
		return umlAllDiagramData;
	}

	public HashMap<String , String>	getfindAltsFather()
	{
		return findAltsFather;
	}

	public void setUmlAllDiagramData(ArrayList<WJDiagramsData> umlAllDiagramData) {
		this.umlAllDiagramData = umlAllDiagramData;
	}

	public ArrayList<WJLifeline> getUmlLifeLines() {
		return umlLifeLines;
	}

	public void setUmlLifeLines(ArrayList<WJLifeline> umlLifeLines) {
		this.umlLifeLines = umlLifeLines;
	}

	public ArrayList<MessageClass> getUmlMessages() {
		return umlMessages;
	}

	public void setUmlMessages(ArrayList<MessageClass> umlMessages) {
		this.umlMessages = umlMessages;
	}

	public ArrayList<WJFragment> getUmlFragment() {
		return umlFragment;
	}

	public void setUmlFragment(ArrayList<WJFragment> umlFragment) {
		this.umlFragment = umlFragment;
	}

	public void setUmlREF(ArrayList<REF> umlREF) {
		this.umlREF = umlREF;
	}

	public ArrayList<WJMessage> getUmlMessageFinal() {
		return umlMessageFinal;
	}

	public void setUmlMessageFinal(ArrayList<WJMessage> umlMessageFinal) {
		this.umlMessageFinal = umlMessageFinal;
	}
	
}
	

