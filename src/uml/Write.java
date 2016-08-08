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
		System.out.println("开始生成XML文件");
		Document document = DocumentHelper.createDocument();  //创建文档   
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
		    	if (location.getTimeDuration() == null) {
		    		loc.addAttribute("timeDuration", "null");
				} else {
					loc.addAttribute("timeDuration", location.getTimeDuration());
				}
		    	
		    	loc.addAttribute("Energe",location.getEnerge());
		    	loc.addAttribute("final", location.getFnal().toString());
		    	
		    		
		    	//loc.addElement("R1").setText(location.getR1());
		    	//loc.addElement("R2").setText(location.getR2());
		    	
		    	Element name2=loc.addElement("name");
		    	name2.addAttribute("x", xx);
		    	name2.addAttribute("y", yy);
		    	name2.setText(location.getObjName()+":"+location.getName()
		    	+"|"+location.getTimeDuration());
		    	
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
		    tem.addElement("init").addAttribute("ref","loc_id"+inittemp);
		    while(transitonIterator.hasNext())
		    { 
		    	UppaalTransition transition=transitonIterator.next();
		    	Element tran=tem.addElement("transition");
		    	tran.addElement("source").addAttribute("ref","loc_id"+transition.getSourceId());
		    	tran.addElement("target").addAttribute("ref","loc_id"+transition.getTargetId());
		    	tran.addAttribute("id", "tran_id"+transition.getSourceId()+transition.getTargetId());
		    	tran.addAttribute("T1", transition.getT1());
		    	tran.addAttribute("T2", transition.getT2());
		    	if (transition.getSEQDO() == null) {
		    		tran.addAttribute("timeDuration", "null");
				} else {
					tran.addAttribute("timeDuration", transition.getSEQDO());
				}
//				
//				tran.addElement("label").addAttribute("kind","guard")
//										.addText("DCBM = " + transition.getDCBM()+","+
//												"SEQDC = " + transition.getSEQDC()+","+
//												"SEQDO = " + transition.getSEQDO()+","+
//												"SEQTC = " + transition.getSEQTC()+","+
//												"SEQTO = " + transition.getSEQTO()
//												);
		    	tran.addElement("label").addAttribute("kind",transition.getKind()).addAttribute("x",xx).addAttribute("y", yy).setText(
		    			"#name:"+transition.getNameText()+
		    			"#receiveOrSend:"+transition.getReceiveOrSend()+
		    			"#typeAndCondition:"+transition.getTypeAndCondition()+
		    			"#time:"+transition.getSEQDO()+
		    			"#in:"+transition.inString+
		    			"#out:"+transition.outString);
		    	tran.addAttribute("name", transition.getNameText());
		    	tran.addAttribute("receiveOrSend", transition.getReceiveOrSend());
		    	if (transition.getTypeAndCondition().equals("null")) {
		    		tran.addAttribute("type", "null");
				} else {
					tran.addAttribute("type", transition.getTypeAndCondition().split("/")[0]);
				}
		    	if (transition.getTypeAndCondition().equals("null")) {
		    		tran.addAttribute("condition", "null");
				} else {
					if (transition.getTypeAndCondition().contains("/")) {
						tran.addAttribute("condition", transition.getTypeAndCondition().split("/")[1]);
					} else {
						tran.addAttribute("condition", transition.getTypeAndCondition());

					}
					
				}
		    	
		    	if (transition.inString == null) {
		    		transition.inString = "null";
				}
		    	if (transition.outString == null) {
		    		transition.outString = "null";
				}
		    	tran.addAttribute("in", transition.inString);
		    	tran.addAttribute("out", transition.outString);
		    	tran.addAttribute("use", transition.use);
		    	tran.addAttribute("def", transition.def);
		    	tran.addAttribute("typeId", transition.getTypeId());
		    	//tran.addElement("label").addAttribute("kind",transition.getKind()).addAttribute("x",xx).
		    	//addAttribute("y", yy).setText(
		    	//		transition.getNameText()+"|"+transition.getSEQDO());
		    	
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
		System.out.println("转换完成!");   
		 System.out.println("***************************************");
	}  
}  

