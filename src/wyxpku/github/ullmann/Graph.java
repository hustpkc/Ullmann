package wyxpku.github.ullmann;

import java.util.ArrayList;

public class Graph {
	public ArrayList<Vertex> vertexes;
	public ArrayList<Edge> edges;
	
	public Graph(){
		setVertexes(new ArrayList<Vertex>());
		setEdges(new ArrayList<Edge>());
	}

	public ArrayList<Vertex> getVertexes() {
		return vertexes;
	}

	public void setVertexes(ArrayList<Vertex> vertexes) {
		this.vertexes = vertexes;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	
	public int[][] getMatrix(){  
        int[][] M = new int[vertexes.size()][vertexes.size()];  
        for (int index = 0; index < edges.size(); index++) {  
            Edge eb = edges.get(index);  
            int i = Integer.parseInt(eb.getFrom());  
            int j = Integer.parseInt(eb.getTo());  
            M[i][j] = 1;  
            M[j][i] = 1;  
        }
        return M;
	}
	public int getVDegree(String v){
		int degree = 0;
		for (int i = 0; i < edges.size(); ++i){
			if (edges.get(i).getFrom().equals(v) || edges.get(i).getTo().equals(v))
				degree++;
		}
		return degree;
	}
	public String getLabelByV(String s1, String s2){
		for (int i = 0; i < edges.size(); ++i) {
			Edge e = edges.get(i);
			if (s1.equals(e.getFrom()) && s2.equals(e.getTo()))
				return e.getLabel();
			if (s2.equals(e.getFrom()) && s1.equals(e.getTo()))
				return e.getLabel();
		}
		return null;
	}
}
