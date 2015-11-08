package EventExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import module.graph.MakeGraph;
import module.graph.SentenceToGraph;
import module.graph.helper.GraphPassingNode;
import module.graph.helper.Node;
import module.graph.helper.NodePassedToViewer;

public class EventExtraction {

	public static ArrayList<String> eventList = new ArrayList<String>();
	
	

	public static ArrayList<String> eventExtractionEngine(String[] linesInPlot) {
		
		for(int i=0;i<linesInPlot.length;i++){	
			
			SentenceToGraph stg = new SentenceToGraph();
			GraphPassingNode gpn2 = stg.extractGraph(linesInPlot[i],false,true,false);
			ArrayList<String> list = gpn2.getAspGraph();
			HashMap<String, String> hm=gpn2.getposMap();
			
			MakeGraph mg = new MakeGraph();
			try{
					ArrayList<NodePassedToViewer> graphs = mg.createGraphUsingSentence(linesInPlot[i], false, true, false);
					Iterator<NodePassedToViewer> it = graphs.iterator();
					
					while(it.hasNext()){
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
				   //System.out.println(key + " " + value);
				}
			}
			catch(StackOverflowError e)
			{
				System.err.println("ERROR: "+ linesInPlot[i] );
			}
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
				if(edgeList.get(j).equals("next_event"))
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
				else if(!edgeList.get(j).equals("agent") && children.get(j).isAnEntity()){
					entities.add(children.get(j).getValue() + ":" + edgeList.get(j));
				}
				if(j== children.size()-1){
					if(agent.size() ==0 && entities.size() == 0){
						agent.add("<NULL>");
						entities.add("<NULL>");
					}
					else if(agent.size()==0 && entities.size()>0){
							agent.add("<NULL>");
					}
					else if(agent.size()>0 && entities.size()==0){
						entities.add("<NULL>");
					}

					for(String a: agent){
						for(String r: entities){
							eventList.add(a+ "," + nd.getValue() + "," + r);
						}
					}
						
				}
			}
			
			System.out.println(edgeList.size() + " " + children.size());
		}
	}

}


