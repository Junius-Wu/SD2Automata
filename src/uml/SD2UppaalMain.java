package uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.xml.transform.Templates;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import uml.Write;

public class SD2UppaalMain {
	private static HashMap<String , WJFragment> id_fragment = new HashMap<String , WJFragment>();
	
	private static ArrayList<WJMessage> messageList=new ArrayList<WJMessage>();//�������������message
	private static ArrayList<WJFragment> fragmentList=new ArrayList<WJFragment>();//�������������fragment
	private static ArrayList<ArrayList<WJFragment>> table = new ArrayList<ArrayList<WJFragment>>();//��
	
	private static ArrayList<UppaalTransition> transitionList = new ArrayList<UppaalTransition>();//�������������transition
	private static ArrayList<UppaalLocation> locationList = new ArrayList<UppaalLocation>();	//�������������location
	private static int [][] map ;
	private static int m_id;
	private static int [] altEndTo;
	private static int [] parEndTo;
	private static ArrayList<HashSet <Integer>>  F = new ArrayList<HashSet <Integer>>();
	private static ArrayList<HashSet <Integer>>  G = new ArrayList<HashSet <Integer>>();
	private static HashSet <Integer> endState = new HashSet <Integer>();
	private static ArrayList <WJLoopFragment> loopFragment = new ArrayList <WJLoopFragment> ();
	private static Stack <WJParFragment> parFragment = new Stack<WJParFragment>();
	private static ArrayList <Integer> exAdd = new ArrayList <Integer>();
	public static void main(String[] args) throws Exception 
	{
		//����ͼ
		ArrayList<WJDiagramsData> DiagramsDataList = new ArrayList<WJDiagramsData>();
	
		//���ͼ��lifelines ������
		ArrayList<WJLifeline> lifeLines=new ArrayList<WJLifeline>();//
		ArrayList<WJMessage> messages=new ArrayList<WJMessage>();
		ArrayList<WJFragment> fragments=new ArrayList<WJFragment>();
		ArrayList<UppaalTemPlate> templates=new ArrayList<UppaalTemPlate>();
		
		
		HashSet<String > template_names=new HashSet<String>();
		
		
		SAXReader reader=new SAXReader();//��ȡ������
	    Document dom= reader.read("test.xml");//����XML��ȡ���������ĵ���dom����
	    Element root=dom.getRootElement();//��ȡ���ڵ�
	    
	    Read uml=new Read();
	    uml.load(root);
	    

	    //�õ�����ͼ��Ӧ����������
	    DiagramsDataList = uml.getUmlAllDiagramData();
	    
	
	    
	    // ����ͼDiagramsDataList
	    Iterator<WJDiagramsData> DiagramsDataListIterator = DiagramsDataList.iterator();   
	    while(DiagramsDataListIterator.hasNext())
	    {  
	    	
	    	//��õ�i��ͼ
	    	WJDiagramsData diagramDaraI = DiagramsDataListIterator.next();
	    	
	    	System.out.println("***************************************");
		    System.out.println("���ڴ���ͼ��Ϊ:"+diagramDaraI.name);
		    
		    //��ʼ��
		    lifeLines.clear();
		    messages.clear();
		    fragments.clear();
		    templates.clear();
		    id_fragment.clear();
		    lifeLines = diagramDaraI.getLifelineArray();
		    fragments = diagramDaraI.getFragmentArray();
		    messages = diagramDaraI.getMessageArray();
		    
		    //id����fragment hashmap
	    	id_fragment.clear();
	   	    Iterator<WJFragment> fragmentsIterator = fragments.iterator();
		    while(fragmentsIterator.hasNext())
		    {
		    	WJFragment I = (WJFragment) fragmentsIterator.next();	    		    	    	
		    	id_fragment.put(I.getFragId(),I);
		    }
		  
		    WJFragment sd = new WJFragment();//����SD��idΪnull
		    sd.setFragType("SD");
		    sd.setBigId("nothing");
		    id_fragment.put("null", sd);
		    WJFragment y = new WJFragment();
		    id_fragment.put("nothing", y);

		    
		    	UppaalTemPlate template=new UppaalTemPlate();
		    	messageList.clear();//�������
		    	fragmentList.clear();
		    	table.clear();
		    	transitionList.clear();
		    	locationList.clear();
		    	endState.clear();
		    	
		    	//��table�������һ�����ñ߽�
	    		WJFragment none0 = new WJFragment();
		    	none0.setBigId("nothingID");
		    	none0.setFragId("none");
		    	none0.setFragType("none");
		    	ArrayList <WJFragment> temp = new ArrayList <WJFragment>();
		    	temp.add(none0);
		    	table.add(0, temp);
		    	
		    	int I = 0;//��ʾ�ڼ���messageI ���ڽ�����
		    	
		    	
			    
			    if(messages.size() == 0)//û����Ϣ
			    {	    	
			    	System.out.println("û���ҵ�message���˳�");
			    	System.exit(0);
			    }
			    else
			    {//����Ϣ
			    	
			    	//����Q0
				    String q_id = "_init" ;
				    m_id=0;
				   
				    
				    UppaalLocation location0 = setLocation(q_id,q_id);	 //id,   ״̬name  
				    
				    location0.setInit(true);
				    location0.setObjId(messages.get(0).getFromId());
			    
				    Iterator<WJLifeline> lifelineIteratorForName = lifeLines.iterator();
				    while(lifelineIteratorForName.hasNext())
				    {//��������lifelineȷ��id��Ӧ������
				    	WJLifeline lifeline = lifelineIteratorForName.next();
				    	if (location0.getObjId().substring(13).equals(lifeline.getlifeLineId().substring(13))) {
							location0.setObjName(lifeline.getlifeLineName());
						}
				    }
 
				    template.locations.add(location0);	
				    locationList.add(location0);
				    
				    
			    	Iterator<WJMessage> messageIterator = messages.iterator();
			    	
			    	
			    	
			    	map = new int[messages.size()*2+3][messages.size()*2+3];
			    	
			    	messageIterator = messages.iterator();
			    	while(messageIterator.hasNext())//����message  ��location  ����
			    	{

			    		
		    				WJMessage messageI = messageIterator.next();//1toN	
			    		
			    		
			    			I++;
				    		messageList.add(messageI);									//���� ��ʾ��������ߵ�����message
				    		fragmentList.add(id_fragment.get(messageI.getInFragId()));	//��ʾ��������ߵ�����fragment
				    		WJFragment sa=id_fragment.get(messageI.getInFragId());
				    		ArrayList <WJFragment> tableI = setTableI(sa);//����table
				    		
				    		table.add(I, tableI);
				    		
					    	
					    	
		//���location
			    			UppaalLocation location = setLocation(messageI.getConnectorId().substring(4),messageI.getName());
			    			location.setR1(messageI.getR1());
		    				location.setR2(messageI.getR2()); 
		    				location.setEnerge(messageI.getEnerge());
		    				location.setObjId(messageI.getToId());
						    Iterator<WJLifeline> lifelineIteratorForName2 = lifeLines.iterator();
						    while(lifelineIteratorForName2.hasNext())
						    {//��������lifelineȷ��id��Ӧ������
						    	WJLifeline lifeline = lifelineIteratorForName2.next();
						    	
						    	if (location.getObjId().substring(13).equals(lifeline.getlifeLineId().substring(13))) {
									location.setObjName(lifeline.getlifeLineName());
								}	
						    }
//warning���������messageI����Ϣ��location��
			        		template.locations.add(location);
			        		locationList.add(location);   
			        		
			        		// ��alt���Ӵ��Ĵ���	        		 ���map
			        		if(I != 0 && hasAlt(table.get(I-1))&&hasAlt(table.get(I)))//��������alt   �������alt�еĽ��Ӵ�
			        		{
			        			int thelength = table.get(I).size()<table.get(I-1).size()? table.get(I).size():table.get(I-1).size();
			        			//��ͬ��break���㷨 ��altֻ��Ҫ���̵�һ���Ϳ�����
			        			for(int c =0;c<thelength;c++)
			        			{
			        				if(table.get(I-1).get(c).getFragType() .equals ("alt")&&
			        						table.get(I).get(c).getFragType() .equals ("alt")&&
			        					!table.get(I-1).get(c).getFragId() .equals (table.get(I).get(c).getFragId())&&
			        						table.get(I-1).get(c).getComId().equals(table.get(I).get(c).getComId())
			        						)//ͬһ��alt�еĲ�ͬ������
			        				{	        					
			        					map[I-1][I] = -1; //������� ����������
			        					break;
			        				}
			        			}
			        			
			        		}	
		    		
			    		
			    		        				    	    		    	    		
		    	    		
		        		
	
			    		
			    	
			    	}//����message ˳����ӳ���break��alt֮�����transition
			    	
			    	
	//���table
		    	int maxLength = 0;
		    	for(int i = 0 ; i<table.size();i++)
		    	{
		    		if(table.get(i).size()>maxLength)
		    			maxLength = table.get(i).size();
		    	}//����󳤶�
		    	WJFragment none = new WJFragment();
		    	none.setBigId("nothingID");
		    	none.setFragId("none");
		    	none.setFragType("none");
		    	
		    	for(int i = 0 ; i<=table.size();i++)
		    	{//���none
		    		
			    	
		    		if(i==table.size())//���ײ�
		    		{
		    			ArrayList <WJFragment> t = new ArrayList <WJFragment>();
		    			for(int j = 0; j <=maxLength;j++)
		    			t.add(none);
		    			
		    			table.add(i, t);
		    			break;
		    		}
		    		else//����ұ߲���
		    		{
		    			for(int j = table.get(i).size(); j <=maxLength;j++)
		    			table.get(i).add(j, none);
		    		}
		    	}
		    	
		    	altEndTo = new int[table.size() * 2];
		    	parEndTo = new int[table.size() * 2];
	//��ӡtable  �����ҳ����н��Ӵ�i�ĳ�altλ�÷���altEndTo ����λ������alt�Ľ��Ӵ� while��altEndTo[altEndTo[i]]!=0��{i=altEndTo[i]}return altEndTo[i]
		    	
		    	for(int i = 0;i<table.size();i++)
		    	{
		    		System.out.print(String.format("%-2d:", i));
		    		for(int c = 0;c<table.get(i).size();c++)
		    		{
		    			if(i>=1&&table.get(i-1).get(c).getFragType().equals("alt")&&
	    						table.get(i).get(c).getFragType().equals("alt")&&
	    					!table.get(i-1).get(c).getFragId().equals(table.get(i).get(c).getFragId())&&
	    						table.get(i-1).get(c).getComId().equals(table.get(i).get(c).getComId())
	    						)//ͬһ��alt�еĲ�ͬ������ �ǽ��Ӵ�����һ��
		    			altEndTo[i] = findOutOfAlt(i,c);//�ҵ���alt��λ��
		    			
		    			if(i>=1&&table.get(i-1).get(c).getFragType().equals("par")&&
	    						table.get(i).get(c).getFragType().equals("par")&&
	    					!table.get(i-1).get(c).getFragId().equals(table.get(i).get(c).getFragId())&&
	    						table.get(i-1).get(c).getComId().equals(table.get(i).get(c).getComId())
	    						)//ͬһ��par�еĲ�ͬ������ �ǽ��Ӵ�����һ��
		    			parEndTo[i] = findOutOfAlt(i,c);//�ҵ���par��λ��  findOutOfAlt������par
		    			
		    			System.out.print(String.format("%-6s", table.get(i).get(c).getFragType()));
		    		}
		    		System.out.println();
		    	}
		    	System.out.println();
		    	System.out.println();
		    	loopFragment = new ArrayList <WJLoopFragment> ();
		    	parFragment.clear();
	//��Ҫ�㷨
		    	F = new ArrayList<HashSet <Integer>>();	    		    	
		    	G = new ArrayList<HashSet <Integer>>();
	
		    	HashSet <Integer> Fi = new HashSet <Integer>();
		    	F.add(Fi);//�����ܵ���0 ������ӿ�
		    	HashSet <Integer> Gi = new HashSet <Integer>();
		    	Gi.add(1);//0�ܵ���1
		    	G.add(Gi);
		    	
			    for(int i = 1; i < table.size()-1; i++)
			    {
			    	Fi = new HashSet <Integer>();
			    	Gi = new HashSet <Integer>();
			    	
			    	Fi.add(i);
			    	if(!hasLastBreak(i) && map[i][i+1]!=-1)//����Ϣ���� break�����һ����Ϣ && ����alt�Ľ��紦
			    				Gi.add(i+1);	
			    	
			    	for(int c = 2; c < table.get(i).size();c++ )
			    	{
			    		if(table.get(i).get(c).getFragType() .equals( "none"))
			    			continue;
			    				    		
			    		if(table.get(i).get(c).getFragId() != table.get(i-1).get(c).getFragId())//�����Ƭ�εĵ�һ����Ϣ
			    		{
			    			Fi.addAll(findAlsoTo(i,c,table.get(i).get(c).getFragType()));
			    		}
			    		
			    		if(table.get(i).get(c).getFragId() != table.get(i+1).get(c).getFragId())///�����Ƭ�ε����һ����Ϣ
			    		{
			    			Gi.addAll(findOutTo(i,c,table.get(i).get(c).getFragType()));	 
			    		}
			    	}
			    	
			    	F.add(Fi);
	    			G.add(Gi);
			    }
			    Fi = new HashSet<Integer>();
			    Fi.add(table.size()-1);
			    F.add(Fi);//���һ��F(i)�������ڣ�������Ӻ���Ϊ��̬���ж���loop��end�ж�
			    fixF();//��F��һ����Ծ���ŵ������Ծ
			    
			    
	
			    //������par��loop�����������  ����map
			    for(int i=0;i<F.size()-1;i++)
			    {
			    	//i���Ե���
		    		for(int Gii :G.get(i)) //i���Ե�Gii
		    		{
		    			for(int Fii: F.get(Gii))//�ܵ���Gii�Ļ��ֿ��ܵ���F(Gii)
				    	{		    	
			    			map[i][Fii] = Fii;
				    	}
		    		}
			    }
	//par����� 	
			    while(!parFragment.isEmpty())
			    {
			    	WJParFragment pfrag = parFragment.pop();//�ȴ�������par�ٴ�������par
			    	
			    	int c = pfrag.getC();
			    	
			    	for(int i = pfrag.getStart();i<pfrag.getEnd();i++)
			    	{
			    		HashSet <Integer> fromList = new HashSet <Integer>();
				    	HashSet <Integer> toList = new HashSet <Integer>();
				    	
			    		String fragId = table.get(i).get(c).getFragId();//i���ڵĲ�����ID
			    		if(table.get(i).get(c+1).getFragType().equals("none"))
			    			fromList.add(i);
			    		else if(
			    				!table.get(i).get(c+1).getFragType().equals("alt") &&
			    				!table.get(i).get(c+1).getFragType().equals("par")&&
			    				!table.get(i).get(c+1).getFragId().equals(table.get(i+1).get(c+1).getFragId())  	    				
			    				)//���Ƕ�׵Ĳ���alt��par ������Ƕ�׵����һ��
			    		{
			    			int k = findRightI(i+1);
			    			for(int j = findStartOfFrag(i,c+1);j<findOutOfFrag(i, c+1);j++)
			    				if(map[j][k] >= 1)//�˱�Ƕ�׵����Ƭ������Щ��Ϣ�ܹ�����k
			    					fromList.add(j);
			    		}
			    		else if(		    				
			    				table.get(i).get(c+1).getFragType().equals("alt") || table.get(i).get(c+1).getFragType().equals("par")&&
			    				!table.get(i).get(c+1).getComId().equals(table.get(i+1).get(c+1).getComId())  	    						
			    				)//�����alt����par ������Ƕ�׵����һ��
			    		{
			    			int k = findRightI(i+1);
			    			for(int j = findStartOfAlt(i,c+1);j<findOutOfAlt(i, c+1);j++)
			    				if(map[j][k] >= 1)
			    					fromList.add(j);
			    		}
			    		else
			    		{
			    			continue;//�����Ƭ�ε��м䲿�� ����
			    		}
			    		
			    		for(int t = pfrag.getStart();t<pfrag.getEnd();t++)//to what?
			    		{
			    			if(table.get(t).get(c).getFragId().equals(fragId))
			    				continue;
			    			if(table.get(t).get(c+1).getFragType().equals("none"))
			    				toList.add(t);
			    			else if(!table.get(t).get(c+1).getFragId().equals(table.get(t-1).get(c+1).getFragId())  	    						
				    				)// �����Ƭ�εĵ�һ��
			    			{
			    				
			    				for(Object o: F.get(t))
			    		    	{
			    					int fii = (int) o;
			    					if(fii < findOutOfFrag(t, c))//��תʱ��������������֮��
			    						toList.add(fii);
			    		    	}
			    			}		  				
			    		}
			    		
			    		setMap(fromList,toList);//����map[fromList][toList]
			    			
			    	}
			    }
			    
	//����loop��ȥ�����		    
			    for(int k=0;k<loopFragment.size();k++)
			    {
			    	int start = loopFragment.get(k).getStart();
			    	int idEnd = loopFragment.get(k).getEnd();
			    	int realEnd = findRightLoopEnd(idEnd);//�ҳ���ȷ��end ���ɴ���alt���Ӵ�����һ��
			    	int c = loopFragment.get(k).getC();
			    	//˭���Ե���end
			    	ArrayList<Integer> loopEndList = new ArrayList<Integer>();
			    	for(int i=start;i<idEnd;i++)//|id|
			    	{
			    		if(map[i][realEnd]>0 && !islastbreak(i,c+1))//i���Ե���reaLend ���Ҳ��Ǹ�loop����һ�����һ��break
			    			loopEndList.add(i);
			    	}
			    	
			    	//˭��loop�Ŀ�ͷ
			    	ArrayList<Integer> loopStartList = new ArrayList<Integer>();
			    	
			    	for(Object obj2: F.get(start))
			    	{
			    		int startLocation = (int) obj2;
			    		if(startLocation<idEnd && startLocation>=start)//|id|
			    			loopStartList.add(startLocation);
			    	}
			    	//������пɴ�end�ĵ�loop�����п�ͷ
			    	for(int i=0;i<loopEndList.size();i++)
			    		for(int j=0;j<loopStartList.size();j++)
			    			map[loopEndList.get(i)][loopStartList.get(j)] = loopStartList.get(j);
			    	
			    }
	//�ڴ˴�������̬ 
			    for(int i=0;i<locationList.size();i++)
			    {
			    	if(map[i][locationList.size()-1] >= 1)
			    		locationList.get(i).setFnal(true);
			    }
	
			    
	//��ӡmap
			    for(int i=0;i<locationList.size()+1;i++)		
			    {	
			    	System.out.print(String.format("%-2d:", i));
			    	for(int j=0;j<locationList.size()+1;j++)
			    		System.out.print(String.format("%-3d", map[i][j]));
			    	System.out.println();
			    }
			   System.out.println("------------------�ָ���--------------------");
	//����map
			    //Queue receiveAndSend = new LinkedList();
			    boolean isSelfMessage = false;
			    for(int i=0;i<locationList.size();i++)
		    		for(int j=0;j<locationList.size();j++)
		    		{
		    			if(map[i][j] >= 1)
			    		{
		    				
			    			//message
							WJMessage messageI = messageList.get(j-1);
							
							String receiveOrSend = "";
							
							if(messageI.getReceiveAndSend().equals("null"))//�����Լ����Լ�����Ϣ��ɵ�2���ظ���transition ��Ϊһ����һ����
							{	
						    		
								if(messageI.getFromId().substring(13).equals(messageI.getToId().substring(13)))
					    		{	
					    			receiveOrSend+="!?";
					    		}		
							
					    		if(receiveOrSend.equals("!?") && j < messageList.size())
					    		{
					    			messageList.get(j).setReceiveAndSend("?");//��һ���ǣ�
					    			receiveOrSend = "!";//����ĳɣ�
					    			isSelfMessage = true;//���Լ����Լ�����Ϣ
					    		}
					    			
							}
							else
							{
								receiveOrSend = messageI.getReceiveAndSend();
							}
							
				    			
				    		
				    		
							//location ���
							UppaalLocation lastLocation0=locationList.get(i);
							//location �յ�
							UppaalLocation location=locationList.get(j);
							UppaalTransition transition = new UppaalTransition();
							if(isSelfMessage && receiveOrSend.equals("!"))
							{  
								transition = setTransition(messageI,m_id++,messageI.getName()+"!"+getTypeAndnCondition(messageI),
			    	    				lastLocation0.getId(),lastLocation0.getName(),
			        					location.getId(),    location.getName(),
				    					"0","0");//������Լ����Լ�����Ϣ ���û�����t1 t2��Ϊ0 �����ظ�������Դ�������
								template.transitions.add(transition);
								isSelfMessage = false;
								
							}else{	
								transition = setTransition(messageI,m_id++,messageI.getName()+receiveOrSend+getTypeAndnCondition(messageI),
			    	    				lastLocation0.getId(),lastLocation0.getName(),
			        					location.getId(),    location.getName(),
			        					messageI.getT1(),messageI.getT2());
								template.transitions.add(transition);
		    	    		
							}
		    	    		
		    			}
		    		}
	//��������map���transition
			    
			    }//message
			    
			    template.setName("template_");
			    //+lifelineI.getlifeLineName());
			    template_names.add("template_");
			    //+lifelineI.getlifeLineName());
			    templates.add(template);  
  
		    System.out.println("***************************************");
		    System.out.println("����д��ͼ��Ϊ"+diagramDaraI.name+"��xml");
		    Write.creatXML(diagramDaraI.name+".xml",templates,template_names);
	    }//����diagram����
	}//end
	
	//������Ϣ �ǲ������breakƬ�ε����һ����Ϣ
	private static boolean islastbreak(int i, int c) {
		if(table.get(i).get(c).getFragType().equals("break")&&
				!table.get(i+1).get(c).getFragId().equals(table.get(i).get(c).getFragId()))//�����һ��break
			return true;
		else
			return false;
	}
	private static void fixF() {
		
		for(int i=0;i<F.size();i++)
		{
			for(int j=i+1; j<F.size();j++)
			if(F.get(i).contains(j))
				F.get(i).addAll(F.get(j));
		}
	}
	public static int findRightLoopEnd(int i)//ֻ��alt�Ϳ����� ����par���ӵ�ʱ�ж������ʱaltEndTo[i]=0 ȡi����
	{
		if(altEndTo[i] == 0 )
			return i;
		
		while(altEndTo[i]!=0)
		{
			//i������ͬʱ��alt��par�Ľ��Ӵ�
				i=altEndTo[i];			
		}
		
		return i;
	}
	//�ҵ���ȷ�ĳ����Ƭ��λ��
	public static int findRightI(int i)//����alt�Ľ��Ӵ� ����par�Ľ��Ӵ����Ҽ���par�Ľ��Ӵ�
	{
		if(altEndTo[i] == 0 && parEndTo[i] == 0)
			return i;
		
		while(altEndTo[i]!=0 || parEndTo[i]!=0)
		{
			if(altEndTo[i]!=0)//i������ͬʱ��alt��par�Ľ��Ӵ�
				i=altEndTo[i];
			else if(parEndTo[i]!=0)
			{	
				exAdd.add(i);//par�Ľ��Ӵ�Ҳ��Ҫ�������
				i=parEndTo[i];
			}
		}
		
		return i;
	}
	//�õ�Gi���� 
	private static HashSet<Integer> findOutTo(int i, int c, String fragType) {//����G��i��
		HashSet<Integer> rt = new HashSet<Integer>();
		exAdd = new ArrayList <Integer>(); //par�Ľ��Ӵ�Ҳ��Ҫ�������
		if(fragType.equals ("opt") || fragType.equals ("loop") )//ֻ��һ��
		{
			i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
			rt.add(findRightI(i));//����alt���ӵ� ������
			
		}else if(fragType.equals ("break"))
		{
			c--;
			if(c == 1) //��������break
			{	
				rt.add(table.size()-1);//������̬
				return rt;
			}
			if(!table.get(i).get(c).getFragType().equals("alt") && !table.get(i).get(c).getFragType().equals("par"))
				i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
			else
				i=findOutOfAlt(i, c);
			
			rt.add(findRightI(i));//����alt���ӵ� ������
			
		}else if(fragType.equals ("alt") || fragType.equals ("par"))
		{
			i=findOutOfAlt(i,c);//�ҵ������Ƭ�ε�i
			rt.add(findRightI(i));//����alt���ӵ� ������
		}
		
		rt.addAll(exAdd);//par�Ľ��Ӵ�Ҳ��Ҫ�������
		return rt;
	}
	
	private static HashSet<Integer> findAlsoTo(int i, int c, String fragType) {//����1����ԾF��i��
		HashSet<Integer> rt = new HashSet<Integer>();
		//exAdd = new ArrayList <Integer>(); //par�Ľ��Ӵ�����Ҫ��� �����ｫ��par��������������
		
		if(fragType.equals ("opt") || fragType.equals ("loop") || fragType.equals ("break"))//ֻ��һ��
		{
			int I = i;
			i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
			if(fragType.equals ("loop"))//��¼loop��λ��
			{
				WJLoopFragment temp = new WJLoopFragment();
				temp.setStart(I);
				temp.setEnd(i);
				temp.setC(c);
				loopFragment.add(temp);
			}
			rt.add(findRightI(i));//����alt���ӵ� ������
		}
		else if(fragType.equals( "alt" )|| fragType.equals("par"))//�ж��
		{
			String comid = table.get(i).get(c).getComId();
			int I = i;
			while(table.get(i).get(c).getComId().equals( comid))
			{
				i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
				
				if(table.get(i).get(c).getComId().equals(comid))//����alt�ڲ� i��ʱ�ǽ��ӵ����һ��
					rt.add(i);
			}
			
			if(fragType.equals ("par") && !table.get(I-1).get(c).getComId().equals(comid))//��¼par��λ��
			{//��par�����ǵ�һ����Ϣ
				WJParFragment temp = new WJParFragment();
				temp.setStart(I);
				temp.setEnd(i);
				temp.setC(c);
				parFragment.push(temp);
			}
			/*rt.add(findRightI(i));//����alt��ô�����ȷ��i*///���� ��Ϊalt��par��һ�������� ������������alt�Ĳ����򵽴�alt����
			
		}
		
		return rt;
	}

	private static boolean hasLastBreak(int i) {
		
		for(int a=2;a<table.get(i).size();a++)//i���Ƿ�������һ��break
			if(table.get(i).get(a).getFragType().equals ("break") && !table.get(i+1).get(a).getFragId().equals (table.get(i).get(a).getFragId()) )
				return true;
		
		return false;
	}
	private static int findStartOfFrag(int i, int c) {
		String id = table.get(i).get(c).getFragId();
		
		while(table.get(i).get(c).getFragId().equals(id) )
			i--;
		
		return i+1;
	}
	private static int findStartOfAlt(int i, int c) {
		String id = table.get(i).get(c).getComId();
		
		while(table.get(i).get(c).getComId().equals(id) )
			i--;
		
		return i+1;
	}
	
	private static int findOutOfFrag(int i, int c) {//�ҵ�����alt��par��ȥ��λ��
		
		String id = table.get(i).get(c).getFragId();
		
		while(table.get(i).get(c).getFragId().equals(id) )
			i++;
		
		return i;
	}
	private static int findOutOfAlt(int i, int c) {//�ҵ�(i,c)alt�ĳ�ȥ��λ�� ������par
		
		String comid = table.get(i).get(c).getComId();
		
		while(table.get(i).get(c).getComId().equals(comid))
			i++;
		
		return i;
	}

	
		
	public static boolean hasBreak(ArrayList <WJFragment> tableI)//messageI����break
	{
		for(int i =0;i<tableI.size();i++)
		{
			if(tableI.get(i).getFragType().equals("break"))
				return true;
		}
		
			return false;
	}
	public static boolean hasAlt(ArrayList <WJFragment> tableI)//messageI����alt
	{
		for(int i =0;i<tableI.size();i++)
		{
			if(tableI.get(i).getFragType().equals("alt"))
				return true;
		}
		
			return false;
	}
	public static ArrayList <WJFragment> setTableI(WJFragment fragment)
	{
		ArrayList <WJFragment> temp = new ArrayList <WJFragment>();
		Stack <WJFragment> s = new Stack <WJFragment>();
		while(!fragment.getFragType().equals("SD"))
		{
			s.push(fragment);
			fragment = id_fragment.get(fragment.getBigId());
		}
		s.push(fragment);
		WJFragment none = new WJFragment();
    	none.setBigId("meiyou");
    	none.setFragId("none");
    	none.setFragType("none");
    	s.push(none);
		//��ջ ����arraylist
		while(!s.isEmpty())
		{
			temp.add(s.pop());
		}
		return temp;
	}
	public static UppaalLocation setLocation(String id, String name)
	{
		UppaalLocation location = new UppaalLocation();
			location.setId(id);
		    
		    location.setName(name);
		    
		return location;
	}
	
	public static UppaalTransition setTransition(WJMessage messageI, int message_id, String message_name,String string, String source_name,String string2, String target_name, String T1,String T2)
	{
		UppaalTransition transition = new UppaalTransition();
		transition.setId(message_id);
		transition.setNameS(source_name);
		transition.setNameT(target_name);
		transition.setSourceId(string);
		transition.setTargetId(string2);
		transition.setNameText(message_name);
		transition.setT1(T1);
		transition.setT2(T2);
		transition.setDCBM(messageI.DCBM);
		transition.setSEQDC(messageI.SEQDC);
		transition.setSEQDO(messageI.SEQDO);
		transition.setSEQTC(messageI.SEQTC);
		transition.setSEQTO(messageI.SEQTO);
		return transition;
	}
	
	public static String getTypeAndnCondition(WJMessage messageI)
	{
		String nCondition = "";
		String type = "";
		String id = messageI.getInFragId();
		while(!id.equals("null"))//�������������н���  
		{
			
			nCondition =   id_fragment.get(id).getFragCondition()+"&&"+nCondition; 
			
			type = id_fragment.get(id).getFragType()+"-"+type;
			id=id_fragment.get(id).getBigId();
		}
		if(type.equals(""))
		return "";
		else
		return "["+type.substring(0,type.length()-1)+"]"+"/"+nCondition.substring(0,nCondition.length()-2)+"/";
	}
	public static void setMap( HashSet <Integer> a,HashSet <Integer> b)
	{//����a��b
		
		Iterator<Integer> ii =a.iterator();
		Iterator<Integer> jj =b.iterator();
		while(ii.hasNext())
		{
			int i = (int)ii.next();
			
			jj =b.iterator();
			while(jj.hasNext())
			{
				int j = (int)jj.next();
				if(map[i][j]!=-1)
				{										
    	    		map[i][j] = j;
				}
			}
		}
		return ;
	}
}
