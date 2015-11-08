
/**
 * 
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;
import module.graph.MakeGraph;
import module.graph.ParserHelper;
import module.graph.SentenceToGraph;
import module.graph.helper.GraphPassingNode;
import module.graph.helper.Node;
import module.graph.helper.NodePassedToViewer;

import org.json.JSONArray;



import org.json.JSONException;
//import net.sf.json.JSONObject; 
//import net.sf.json.JSONSerializer;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.models.*;



public class Test {
	
	public static ArrayList<String> eventList = new ArrayList<String>();

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException{
		String everything;
		FileInputStream inputStream = new FileInputStream("C://Trideep//Classes//NLP//IMDBMovieSearchData//synopses//departed_coref");
		try {
		     everything = IOUtils.toString(inputStream);
		} finally {
		    inputStream.close();
		}
           
		String str = everything;
		str = Jsoup.parse(str).text();
		//String[] linesInPlot = str.split("(?<=[a-z])\\.\\s+");
		String[] linesInPlot = str.split(" \\.");
		
		ArrayList<String> eventsFromLinesInPlot = eventExtractionEngine(linesInPlot);
		
		
		System.exit(0);
	}

	private static ArrayList<String> eventExtractionEngine(String[] linesInPlot) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<linesInPlot.length;i++){	
			
			SentenceToGraph stg = new SentenceToGraph();
			GraphPassingNode gpn2 = stg.extractGraph(linesInPlot[i],false,true,false);
			ArrayList<String> list = gpn2.getAspGraph();
			HashMap<String, String> hm=gpn2.getposMap();
			
			MakeGraph mg = new MakeGraph();
			try{
					ArrayList<NodePassedToViewer> graphs = mg.createGraphUsingSentence(linesInPlot[i], false, true, false);
					Iterator<NodePassedToViewer> it = graphs.iterator();
					
					while(it.hasNext())
					{
						Node nd = it.next().getGraphNode();
						addEvents(nd);
					}
					for(String e: eventList)
						System.out.println(e);
					System.out.println("Done!!!");
				
				
				Iterator iterator = hm.keySet().iterator();
				while (iterator.hasNext()) {
				   String key = iterator.next().toString();
				   String value = hm.get(key).toString();
				  
				   System.out.println(key + " " + value);
				}
			}
			catch(StackOverflowError e)
			{
				System.err.println("ERROR: "+ linesInPlot[i] );
			}
			System.out.println("shubham");
		}	return null;
	}

	private static void addEvents(Node nd) {
		// TODO Auto-generated method stub
		if(nd.isAnEvent())
		{
			
			ArrayList<String> edgeList= nd.getEdgeList();
			ArrayList<Node> children = nd.getChildren();
			ArrayList<String> agent = new ArrayList<String>();
			ArrayList<String> entities = new ArrayList<String>() ;
			System.out.println("<< " + nd.getValue() + " >>");
			for(int j = 0 ; j < children.size() ; j++){
				if(edgeList.get(j).equals("next_event"));
				{
					try {
						addEvents(children.get(j));
					} catch (StackOverflowError e) {
						// TODO: handle exception
						System.err.println("ouch!!");
					}
				}
				if(edgeList.get(j).equals("agent"))
				{	
					agent.add(children.get(j).getValue());
					System.out.println(children.get(j).getValue() + " " + children.get(j).isAnEntity() + " " + edgeList.get(j));
				}
				else if(!edgeList.get(j).equals("agent") && children.get(j).isAnEntity())
				{
					entities.add(children.get(j).getValue() + ":" + edgeList.get(j));
				}
				if(j== children.size()-1)
				{
					if(agent.size() ==0 && entities.size() == 0)
					{
						agent.add("<NULL>");
						entities.add("<NULL>");
					}
					else if(agent.size()==0 && entities.size()>0)
					{
							agent.add("<NULL>");
					}
					else if(agent.size()>0 && entities.size()==0)
					{
						entities.add("<NULL>");
					}

					for(String a: agent)
					{
						for(String r: entities)
						{
							eventList.add(a+ "," + nd.getValue() + "," + r);
						}
					}
						
				}
			}
			
			System.out.println(edgeList.size() + " " + children.size());
			}
	}

}
