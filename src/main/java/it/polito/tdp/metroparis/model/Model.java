package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;


public class Model {
	private Graph<Fermata,DefaultEdge> graph;
	private List <Fermata> fermate;
	private Map <Integer,Fermata> fermateIdMap;

	public Model(){
	this.graph= new SimpleDirectedGraph<>(DefaultEdge.class);
	MetroDAO dao= new MetroDAO();
	
	//CREAZIONE DEI VERTICI
	this.fermate= dao.getAllFermate();
	
	//Mi creo una mappa
	this.fermateIdMap= new HashMap<>();
	
	for(Fermata f: fermate) {
		fermateIdMap.put(f.getIdFermata(), f);
	}
	
	Graphs.addAllVertices(graph, fermate);
	
	System.out.println(graph);
	//CREAZIONE DEGLI ARCHI
	//Metodo 1
	
	/*for(Fermata fp: this.fermate) {
		for(Fermata fa: this.fermate) {
			//esiste una connessione tra fa e fb
			if(dao.fermateConnesse(fp, fa)) {
				this.graph.addEdge(fp, fa);
			}
		}
	}*/
	//System.out.println(graph);
	
	
	//Metodo 2
	/*for(Fermata fp: this.fermate) {
		//fermate adiacenti ad fp
		List <Fermata> connesse= dao.fermateSuccessive(fp, fermateIdMap);
				for(Fermata fa: connesse) {
					this.graph.addEdge(fp, fa);
				}
	}
	System.out.println(graph);
	
	
	*/
	//Metodo 3
	//Se c'è un grado alto
	List <CoppiaFermate> coppie= dao.coppieFermate(fermateIdMap);
	for(CoppiaFermate c: coppie) {
		this.graph.addEdge(c.getFp(), c.getFa());
	}
	System.out.println(graph);
	System.out.println(String.format("Grafo caricato con %d vertici %d archi\n", graph.vertexSet().size(), graph.edgeSet().size()));
	
}
	/**
	 * Visita l'intero grafo con la strategia Breadth First 
	 * e ritorna l'insieme dei vertici incontrati.
	 * @param source vertice di partenza della visita
	 * @return insieme dei vertici (fermate) incontrati
	 */
	
	
	public List <Fermata>visitaAmpiezza(Fermata source){
		List <Fermata> visita = new ArrayList <>();
		
		
		//parto da un vertice e analizzo tutti gli altri
		BreadthFirstIterator <Fermata, DefaultEdge> bfv= new BreadthFirstIterator<>(graph,source);
		while(bfv.hasNext()) {
			visita.add(bfv.next());
		}
		return visita;

		
	}
	
	public List <Fermata>visitaProfondita(Fermata source){
		List <Fermata> visita = new ArrayList <>();
		
		
		//parto da un vertice e analizzo tutti gli altri
		DepthFirstIterator <Fermata, DefaultEdge> dfv= new DepthFirstIterator<>(graph,source);
		while(dfv.hasNext()) {
			visita.add(dfv.next());
		}
		return visita;

		
	}
	
	public Map <Fermata,Fermata> alberoVisita(Fermata source){
		Map <Fermata,Fermata> albero = new HashMap<>();
		//parto da source ed esploro
		albero.put(source, null);
		
		
		GraphIterator <Fermata,DefaultEdge> bfv= new BreadthFirstIterator<>(graph, source);
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {	}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				//LA VISITA STA CONSIDERANDO UN NUOVO ARCO
				//questo arco ha scoperto un nuovo vertice?
				//se si, proveniente da dove?
				
				DefaultEdge edge= e.getEdge();
				//(a,b): 2 situazioni
				//ho scoperto a partendo da b
				//oppure ho scoperto b da a
				Fermata a = graph.getEdgeSource(edge);
				Fermata b= graph.getEdgeTarget(edge);
				
				if( albero.containsKey(a)) {
					//ho scoperto b arrivando da a
					albero.put(b, a);
				}
				else {
					albero.put(a, b);
				}
		
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {}
			
		});
		
		while(bfv.hasNext()) {
			bfv.next(); //estrai l'elemento e ignoralo
		}
		return albero;
	
		
	}
	
	
	public List <Fermata> camminiMinimi(Fermata partenza, Fermata arrivo) {
		DijkstraShortestPath <Fermata, DefaultEdge> dij = new DijkstraShortestPath<Fermata, DefaultEdge>(graph);
	
		GraphPath <Fermata, DefaultEdge> cammino= dij.getPath(partenza, arrivo);
		
		return cammino.getVertexList();
	}
	
	
	public static void main(String args[]) {
		Model m= new Model();
		
		List<Fermata> visita= m.visitaAmpiezza(m.fermate.get(0));
		System.out.println(visita);
		
		Map <Fermata, Fermata> albero= m.alberoVisita(m.fermate.get(0));
		for(Fermata f: albero.keySet()) {
			System.out.format("%s <- %s\n", f, albero.get(f));
		}
		
		
		
		List <Fermata> cammino= m.camminiMinimi(m.fermate.get(0), m.fermate.get(1));
	}
}