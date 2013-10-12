import java.util.Scanner;
import java.io.File;
import java.util.*;

class ProjectPlanner {
	public static void main(String[] args) {
		Project project = new Project("./input_files/buildhouse1.txt");
		System.out.println("Ran program");
	}
}

class Task {
	int id, time, staff;
	String name;
	ArrayList<Edge> outEdges;
	int cntPredecessors;

	public void addPredecessor() {
		cntPredecessors ++;
	}

	public void addEdge(Edge edge) {
		if (!outEdges.contains(edge)) {
			outEdges.add(edge);
		}
		else {
			System.out.println("Edge between " + Integer.toString(edge.v.id) + " and "  +
				Integer.toString(edge.w.id) + " was already added to Task, tried to add again");
		}
	}

	public defineFromString(String info) {

	}
}

class Edge {
	Edge(Task v, Task w) {
		this.v = v;
		this.w = w;
	}
	Task v, w;
}

class Project {
	Project(String filename) {
		readFromFile(filename);
	}

	File inFile = null;

	private void readFromFile(String filename) {
		;
	}

	public void addEdge(Task v, Task w) {
		v.addEdge(new Edge(v, w));
	}
}