package it.polimi.ingsw.ps23.model;

import java.util.HashMap;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import it.polimi.ingsw.ps23.model.map.City;

public class CitiesGraph {	
	
	DirectedGraph<City, DefaultEdge> citiesGraph;
	
	public CitiesGraph(List<String[]> citiesConnections, HashMap<String, City> cities) {
		citiesGraph = new DefaultDirectedGraph<City, DefaultEdge>(DefaultEdge.class);
		for(String[] connections : citiesConnections) {
			citiesGraph.addVertex(cities.get(connections[0]));
		}
		for(String[] connections : citiesConnections) {			
			for(int i = 1 ; i < connections.length; i++){
				citiesGraph.addEdge(cities.get(connections[0]), cities.get(connections[i]));				
			}
		}
	}
	
	@Override
	public String toString() {
 		return "Cities: " + citiesGraph.toString().replace("[(", "\nConnections: [(");
	}
	

}