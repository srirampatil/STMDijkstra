package sp.graph;

/* Conversation started today
Hemal Thakkar
5:53pm
Hemal Thakkar */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
class Vertex implements Comparable<Vertex> {
	public final String name;
	public Edge[] adjacencies;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Vertex previous;
	public Vertex(String argName) {
		name = argName;
	}
	public String toString() {
		return name;
	}
	public int compareTo(Vertex other) {
		return Double.compare(minDistance, other.minDistance);
	}
}
class Edge {
	public final Vertex target;
	public final double weight;
	public Edge(Vertex argTarget, double argWeight) {
		target = argTarget;
		weight = argWeight;
	}
}
public class Dijkstra {
	public static void computePaths(Vertex source) {
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);
		System.out.println("Initial vetexQueue :" + vertexQueue);
		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();
			
			// Visit each edge exiting u
			for (Edge e : u.adjacencies) {
				Vertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);
					v.minDistance = distanceThroughU;
					v.previous = u;
					vertexQueue.add(v);
				}
			}
		}
	}
	public static List<Vertex> getShortestPathTo(Vertex target) {
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);
		Collections.reverse(path);
		return path;
	}
	public static void main(String[] args) {
		//Initializing the Graph, vertex first
		Vertex vA = new Vertex("A");
		Vertex vB = new Vertex("B");
		Vertex vC = new Vertex("C");
		Vertex vD = new Vertex("D");
		Vertex vE = new Vertex("E");
		Vertex vF = new Vertex("F");
		Vertex vG = new Vertex("G");
		Vertex vH = new Vertex("H");
		Vertex vI = new Vertex("I");
		
		//Initializing all the edges
		vA.adjacencies = new Edge[] { new Edge(vB, 3), new Edge(vC, 5), new Edge(vD, 7) };
		vB.adjacencies = new Edge[] { new Edge(vA, 3), new Edge(vD, 1), new Edge(vE, 7) };
		vC.adjacencies = new Edge[] { new Edge(vA, 5), new Edge(vD, 3), new Edge(vF, 2) };
		vD.adjacencies = new Edge[] { new Edge(vA, 7), new Edge(vB, 1), new Edge(vC, 3), new Edge(vE, 2), new Edge(vF, 3), new Edge(vG, 1)};
		vE.adjacencies = new Edge[] { new Edge(vB, 7), new Edge(vD, 2), new Edge(vG, 2), new Edge(vH, 1)};
		vF.adjacencies = new Edge[] { new Edge(vC, 2), new Edge(vD, 3), new Edge(vG, 3), new Edge(vI, 4) };
		vG.adjacencies = new Edge[] { new Edge(vD, 1), new Edge(vE, 2), new Edge(vF, 3), new Edge(vE, 2), new Edge(vH, 3), new Edge(vI, 2) };
		vH.adjacencies = new Edge[] { new Edge(vE, 1), new Edge(vG, 3), new Edge(vI, 5) };
		vI.adjacencies = new Edge[] { new Edge(vF, 4), new Edge(vG, 2), new Edge(vH, 5) };
		
		Vertex[] vertices = { vA, vB, vC, vD, vE, vF, vG, vH, vI };
		
		//Compute the graph
		computePaths(vA);
		
		//Print the distance from Source to each vertex
		for (Vertex v : vertices) {
			System.out.println("Distance to " + v + ": " + v.minDistance);
			List<Vertex> path = getShortestPathTo(v);
			System.out.println("Path: " + path);
		}
	}
}