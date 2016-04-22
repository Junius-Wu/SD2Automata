package uml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.transform.Templates;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class Write
{
	public static void creatXML(String Path,ArrayList<UppaalTemPlate> temPlates,HashSet<String> template_instantiations ) throws IOException 
	{
		int x=30,y=30;
		System.out.println("��ʼ����XML�ļ�");
		Document document = DocumentHelper.createDocument();  //�����ĵ�   
		Element nta=document.addElement("nta");  
		Element declaration=nta.addElement("declaration");
		declaration.addText("// Place global declarations here.");
		Iterator<UppaalTemPlate> templateIterator=temPlates.iterator();
		while(templateIterator.hasNext())
		{
			UppaalTemPlate temlPlate=templateIterator.next();
		    Element tem=nta.addElement("template");
		    Element nameElement=tem.addElement("name");
		    String xx = String.valueOf(x++);
		    String yy=String.valueOf(y++);
		    nameElement.addAttribute("x",xx );
		    nameElement.addAttribute("y",yy );
		    nameElement.setText(temlPlate.getName());
		    tem.addElement("declaration");
		    String  inittemp = "";
		    Iterator<UppaalLocation> locationIterator=temlPlate.locations.iterator();
		    Iterator<UppaalTransition> transitonIterator=temlPlate.transitions.iterator();
		    while(locationIterator.hasNext())
		    {
		    	UppaalLocation location=locationIterator.next();
		    	Element loc =tem.addElement("location");
		    	loc.addAttribute("id","loc_id"+location.getId());
		    	loc.addAttribute("x", xx);
		    	loc.addAttribute("y",yy);
		    	loc.addAttribute("R1", location.getR1());
		    	loc.addAttribute("R2", location.getR2());
		    	
		    	loc.addAttribute("Energe",location.getEnerge());
		    	//��ʹ��˳��ͼת�����Զ����� �����Ա�ʾ���״̬�����ĸ������id�����Ƕ���ͼ���Զ��� ������������������
		    	
		    		
		    	//loc.addElement("R1").setText(location.getR1());
		    	//loc.addElement("R2").setText(location.getR2());
		    	
		    	Element name2=loc.addElement("name");
		    	name2.addAttribute("x", xx);
		    	name2.addAttribute("y", yy);
		    	name2.setText(location.getObjName()+":"+location.getName());
		    	
		    	/*Element name3=loc.addElement("R1R2");
		    	name3.addAttribute("x", xx);
		    	name3.addAttribute("y", String.valueOf((Integer.valueOf(yy)+10)));
		    	name3.setText("<"+location.getR1()+","+location.getR2()+">");*/
		    	
		    	/*	if(location.getType()==1)
		   		{   //System.out.println("XXXXXXXX");
		    		loc.addElement("urgent");
		    	}*/
		    	//System.out.println(location.getInit());
		    	if(location.getInit())
		    		inittemp=location.getId();
		    	
		    }
		    tem.addElement("init").addAttribute("ref","id"+inittemp);
		    while(transitonIterator.hasNext())
		    { 
		    	UppaalTransition transition=transitonIterator.next();
		    	Element tran=tem.addElement("transition");
		    	tran.addElement("source").addAttribute("ref","loc_id"+transition.getSourceId());
		    	tran.addElement("target").addAttribute("ref","loc_id"+transition.getTargetId());
		    	tran.addAttribute("id", "tran_id"+transition.getSourceId()+transition.getTargetId());
		    	tran.addAttribute("T1", transition.getT1());
		    	tran.addAttribute("T2", transition.getT2());
				tran.addAttribute("DCBM", transition.getDCBM());
				tran.addAttribute("SEQDC", transition.getSEQDC());
				tran.addAttribute("SEQDO", transition.getSEQDO());
				tran.addAttribute("SEQTC", transition.getSEQTC());
				tran.addAttribute("SEQTO", transition.getSEQTO());
				tran.addElement("label").addAttribute("kind","guard")
										.addText("DCBM = " + transition.getDCBM()+","+
												"SEQDC = " + transition.getSEQDC()+","+
												"SEQDO = " + transition.getSEQDO()+","+
												"SEQTC = " + transition.getSEQTC()+","+
												"SEQTO = " + transition.getSEQTO()
												);
		    	tran.addElement("label").addAttribute("kind",transition.getKind()).addAttribute("x",xx).addAttribute("y", yy).setText(transition.getNameText());
		    	//System.out.println(transition.getNameText()+"/["+transition.getT1()+","+transition.getT2()+"]");
		    } 
		}
		Element sysElement =nta.addElement("system");
		String instantiations= template_instantiations.toString().substring(1, template_instantiations.toString().length()-1);
		sysElement.setText("system"+" "+instantiations+";");	
		String doString=document.asXML();
		//String[] out=doString.split("[?]>");    
		//String dTDString="?><!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd'>";
		FileOutputStream outputStream = new FileOutputStream(Path);
		outputStream.write(doString.getBytes());
		
		//outputStream.write(out[0].getBytes());
		//outputStream.write(dTDString.getBytes());
		//outputStream.write(out[1].getBytes());
		outputStream.close();	
		System.out.println("ת�����!");   
		 System.out.println("***************************************");
	}  
}  

