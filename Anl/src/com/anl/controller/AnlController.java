package com.anl.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.anl.common.CommandMap;
import com.anl.vo.Setting;

import functionSet.OtherFun;

@Controller
public class AnlController {
	
	@RequestMapping("/anldata/dataupload.do")
	public String dataupload(Model model) {
	
		model.addAttribute("center","dataupload");
		return "main";
	}
	@RequestMapping("/anldata/examdata.do")
	public String examdata(Model model,HttpSession session) {
		List<List<String>> data = new ArrayList<List<String>>();
		BufferedReader reader =null;
		try {
	    	  reader = Files.newBufferedReader(Paths.get("C:\\Users\\wlwl0\\Documents\\GitHub\\Anl\\Anl\\web\\examdata\\iris.csv"));
	    	  Charset.forName("UTF-8");
	    	  String line;		      
		      while ((line = reader.readLine()) != null) {
		        List<String> tmp = new ArrayList<String>();
				String arr[] = line.split(",");
				tmp = Arrays.asList(arr);
				data.add(tmp);
		      }  
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(reader !=null)
					reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}	  
	    
		
		session.setAttribute("filename", "IRIS");
		session.setAttribute("data", data);
		session.setAttribute("dataon", "1"); //data off
		model.addAttribute("center","datasetting");		
		List<Integer> ncolumn = new ArrayList<>();
		for (int i=1;i<=data.get(0).size();i++)
			ncolumn.add(i);
		model.addAttribute("ncolumn",ncolumn);
		model.addAttribute("firstrow",data.get(0));
		model.addAttribute("secondrow",data.get(1));
		return "main";
	}

	
	@RequestMapping(value="/anldata/datasetting.do", method=RequestMethod.POST)
	public String upload(@RequestParam("uploadfile") MultipartFile uploadfile,Model model, HttpSession session) {
		List<List<String>> data = new ArrayList<List<String>>();
		String filename = uploadfile.getOriginalFilename();		
		BufferedReader reader =null;
		
	    try {
	    	  Charset.forName("UTF-8");
		      reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(uploadfile.getBytes())));
		      String line;		      
		      while ((line = reader.readLine()) != null) {
		        List<String> tmp = new ArrayList<String>();
				String arr[] = line.split(",");
				tmp = Arrays.asList(arr);
				data.add(tmp);
		      }  
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(reader !=null)
					reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}	  
	    session.setAttribute("filename", filename);
		session.setAttribute("data", data);
		session.setAttribute("dataon", "1"); //data off
		
		model.addAttribute("center","datasetting");		
		List<Integer> ncolumn = new ArrayList<>();
		for (int i=1;i<=data.get(0).size();i++)
			ncolumn.add(i);
		model.addAttribute("ncolumn",ncolumn);
		model.addAttribute("firstrow",data.get(0));
		model.addAttribute("secondrow",data.get(1));
		return "main";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/anldata/redirectdatasetting.do")
	public String redirectstep2(Model model,HttpSession session) {
		
		List<List<String>> data =(List<List<String>>) session.getAttribute("data");
		List<String> headername = (List<String>) session.getAttribute("headername");
		Setting set = (Setting) session.getAttribute("typelist");
		System.out.println(set.toString());
		if (data==null) {
			model.addAttribute("center","dataupload");
		}else {
			if (set.getHeader().equals("t")) {
				data.add(0,headername);
			}			
			List<Integer> ncolumn = new ArrayList<>();
			for (int i=1;i<=data.get(0).size();i++)
				ncolumn.add(i);
			model.addAttribute("ncolumn",ncolumn);			
			model.addAttribute("firstrow",data.get(0));			
			model.addAttribute("secondrow",data.get(1));
			model.addAttribute("center","datasetting");
		}		
		return "main";
	}

	@RequestMapping(value= "/anldata/dataview1.do",  method=RequestMethod.POST )
	public String dataview1(Model model,HttpSession session,Setting set) {
		List<List<String>> data =(List<List<String>>) session.getAttribute("data");
		List<String> typelist =set.getType();
		String headerinfo = set.getHeader();
		//headerinfo setting
		List<String> headername = new ArrayList<String>();
		int start=0; //문자 포함 여부 확인하기
		boolean numcheck =true;
		if (headerinfo.equals("t")) {
			start = 1;
			List<String> header =  new ArrayList<String>();
			header = data.get(0); //header name					
			
			loop1:for (int i=0;i<header.size();i++) {
				if(new OtherFun().checkrealnum(header.get(i))) {
					numcheck=false;
					break loop1;
				}
				headername.add(header.get(i).replaceAll("\\p{Z}", ""));//remove trim
			}						
		}else {
			for (int i =1;i<data.get(0).size()+1;i++) {
				headername.add("V"+i); //header name
			}				
		}
		System.out.println(numcheck+"numcheck");
		String textcheck = "";
		for (int i =0;i<data.get(0).size();i++) {
			if ((typelist.get(i)).equals("numeric")) {
				roop2 :for (int j=start;j<data.size();j++) {
					if(!new OtherFun().checkrealnum(data.get(j).get(i))) {
						textcheck += ""+(i+1)+", ";
						break roop2; 
					}
				}
			}
		}		
		if (textcheck.length()>1) {
			model.addAttribute("warning",textcheck+"열에 문자가 포함되어있습니다.(data handling 후 이용)");
			List<Integer> ncolumn = new ArrayList<>();
			for (int i=1;i<=data.get(0).size();i++)
				ncolumn.add(i);
			//redirect data setting
			model.addAttribute("ncolumn",ncolumn);
			model.addAttribute("firstrow",data.get(0));
			model.addAttribute("secondrow",data.get(1));
			model.addAttribute("center","datasetting");
		}else if(numcheck==false){
			model.addAttribute("warning","테이블 헤더는 숫자가 될 수 없습니다.");
			List<Integer> ncolumn = new ArrayList<>();
			for (int i=1;i<=data.get(0).size();i++)
				ncolumn.add(i);
			//redirect data setting
			model.addAttribute("ncolumn",ncolumn);
			model.addAttribute("firstrow",data.get(0));
			model.addAttribute("secondrow",data.get(1));
			model.addAttribute("center","datasetting");
		} else {
			//date session reset
			if (headerinfo.equals("t")) {
				data.remove(0);
				session.setAttribute("data", data);
			}

			model.addAttribute("center","dataview1");
			session.setAttribute("dataon", "2"); //data on
			//importance things
			session.setAttribute("headername", headername);
			session.setAttribute("typelist", set);
		}				
		return "main";
	}
	@RequestMapping("/anldata/redirectdataview1.do")
	public String redirectdataview1(Model model,HttpSession session) {
		model.addAttribute("center","dataview1");
		return "main";
	}
	
	  
	
	////////////////////////ttest nav button
	@RequestMapping("/anldata/ttest.do")
	public String ttest(Model model) {
		model.addAttribute("center","t-test");
		return "main";
	}
////////////////////////reg nav button
	@RequestMapping("/anldata/simplereg.do")
	public String simplereg(Model model) {
		
		model.addAttribute("center", "simplereg");
		return "main";
	}
	@RequestMapping("/anldata/multireg.do")
	public String multireg(Model model) {
		
		model.addAttribute("center", "multireg");
		return "main";
	}
	@RequestMapping("/anldata/normaltest.do")
	public String normaltest(Model model) {
		
		model.addAttribute("center", "normaltest");
		return "main";
	}
	
	//view sheet
	@RequestMapping("/anldata/sheet.do")
	public String sheet(Model model,HttpSession session) {
		model.addAttribute("center", "sheet");
		return "main";
	}
	
	//call present session data 
	@RequestMapping("/anldata/sheet2.do")
	@ResponseBody
	public String sheet2(Model model,HttpSession session) {
		List<List<String>> data =(List<List<String>>) session.getAttribute("data");
		List<String> headername = (List<String>) session.getAttribute("headername");
		JSONArray jr = new JSONArray();
		for (int i=0;i<data.size();i++) {
			jr.add(data.get(i));
		}
		
		JSONArray jr2 = new JSONArray();
		for (int i=0;i<headername.size();i++) {
			jr2.add(headername.get(i));
		}
		JSONObject jo = new JSONObject();
		jo.put("data",jr);
		jo.put("headername",jr2);
		return jo.toJSONString();
	}
	
	//get new sheet data (from sheet2, type: json)
	@RequestMapping(value="/anldata/sheet3.do",method=RequestMethod.POST)
	public String sheet3(Model model,HttpSession session, CommandMap map) throws ParseException {
		session.removeAttribute("headername");
		session.removeAttribute("data");
		String jsonstr =(String) map.get("json");
		System.out.println(jsonstr);
		JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(jsonstr);
		List<List<String>> data = new ArrayList<>();
		for (int i =0;i<json.size();i++) {
			List<String> list = (List<String>) json.get(i);
			data.add(list);
		}
		session.setAttribute("filename", "MyData");
		session.setAttribute("data", data);
		session.setAttribute("dataon", "1"); //data off
		model.addAttribute("center","datasetting");		
		List<Integer> ncolumn = new ArrayList<>();
		for (int i=1;i<=data.get(0).size();i++)
			ncolumn.add(i);
		model.addAttribute("ncolumn",ncolumn);
		model.addAttribute("firstrow",data.get(0));
		model.addAttribute("secondrow",data.get(1));

		return "main";
	}
}
