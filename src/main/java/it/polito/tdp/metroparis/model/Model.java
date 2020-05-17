package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

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
	System.out.println(String.format("Grafo caricato con %d vertici %d archi", graph.vertexSet().size(), graph.edgeSet().size()));
	
}
	
	public static void main(String args[]) {
		Model m= new Model();
	}
}