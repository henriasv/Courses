import java.util.Scanner;
import java.io.File;
import java.util.*;

class ProjectPlanner {
	public static void main(String[] args) {
		Project project = new Project();
		File inFile = new File(args[0]);
		readFromFile(inFile, project);
		project.sort();
		project.printAllTasks();
		project.printAllEdges();
		project.run();
		System.out.println("Ran program");
	}

	public static void readFromFile(File inFile, Project project) {
		Scanner sc = null;
		try {
			sc = new Scanner(inFile);
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("File not found, quitting");
			System.exit(1);
		}
		if (sc.hasNext()) {
			num_tasks = Integer.parseInt(sc.next());
			System.out.println("Number of tasks: " + Integer.toString(num_tasks));
		}

		int counter = 1;

		while (sc.hasNextLine()) {
			ArrayList<String> tmp_string_arr = new ArrayList<>();
			for (String s: sc.nextLine().split("\\s"))
				tmp_string_arr.add(s);

			while(tmp_string_arr.remove(""))
				;
			while(tmp_string_arr.remove(" "))
				;

			for (String s: tmp_string_arr) 
				System.out.print(s+",");
			System.out.println("");

			String[] tmp_string = {};
			tmp_string =  tmp_string_arr.toArray(tmp_string);

			int id = -1;

			try {
				id = Integer.parseInt(tmp_string[0]);
			}
			catch (java.lang.NumberFormatException e) {
				id = -1; // Will not make new task
			}
			catch (java.lang.ArrayIndexOutOfBoundsException e) {
				id = -1;
			}

			if (id == counter) {
				String name = tmp_string[1];
				int time 	= Integer.parseInt(tmp_string[2]);
				int staff 	= Integer.parseInt(tmp_string[3]);
				int[] dependencies = new int[tmp_string.length-4];
				for (int i = 4; i<tmp_string.length; i++)
					dependencies[i-4] = Integer.parseInt(tmp_string[i]);
				project.addTask(id, name, time, staff, dependencies);
				System.out.println("Created new task");
				counter ++;
			}
		 	
		 }
	}

	static int num_tasks = 0;
}

class Task implements Comparable<Task>{

	Task(int id, Project project) {
		this.id = id;
		this.project = project;
	}

	int id, time, staff;
	String name;
	ArrayList<Edge> outEdges = new ArrayList<>();
	int cntPredecessors;
	Project project;

	// To be calculated later
	int earliestStart = -1;
	int latestStart = -1;
	int slack = -1;

	// To be used in graph algorithm
	int started_at = -1;
	int finished_at = -1;
	int finished_predecessors = 0;

	public void addPredecessor() {
		cntPredecessors ++;
	}

	public void removePredecessor() {
		cntPredecessors --;
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

	public void setParameters(String name, int time, int staff, int[] dependencies) {
		this.name = name;
		this.time = time;
		this.staff = staff;
		for (int id : dependencies) {
			if (project.tasks.contains(new Task(id, project)) && (id != -1)) {
				Task tmp_t = project.getTask(new Task(id, project));
				Edge tmp_e = new Edge(tmp_t, this);
				project.addEdge(tmp_e);
				tmp_t.addEdge(tmp_e);
			}
			else {
				Task tmp_t = new Task(id, project);
				project.addTask(tmp_t);
				Edge tmp_e = new Edge(tmp_t, this);
				tmp_t.addEdge(tmp_e);
				project.addEdge(tmp_e);
			}
		}
	}

	public void start(int t) {
		started_at = t;
		System.out.println("Started: " + Integer.toString(id));
	}

	public boolean tell_finished_prerequisite(int t) {
		finished_predecessors ++;
		if (finished_predecessors == cntPredecessors) {
			start(t);
			return true;
		}
		return false;
	}

	public boolean isFinished(int t) {
		if (t-(started_at+time)==0) {
			finished_at = t;
			System.out.println("Finished: " + Integer.toString(id));
			return true;
		}
		else 
			return false;
	}

	public boolean equals(Object other) {
		if (getClass() != other.getClass())
			return false;
		else
			return (this.id == ((Task) other).id);
	}

	public int compareTo(Task other) {
		return this.id-other.id;
	}



	public String toString() {
		String ret = 	"Id: "+ Integer.toString(id) +
						"\nName: " + name +
						"\ntime: " + Integer.toString(time) + 
						"\nslack: " + Integer.toString(slack) +
						"\nstaff: " + Integer.toString(staff) +
						"\noutEdges to: ";
						
		for (Edge edge : outEdges) {
			ret = ret + Integer.toString(edge.w.id) + " ";
		}
		ret = ret + "\nnum_predecessors: " + Integer.toString(cntPredecessors);
		return ret;
	}

}

class Edge {
	Edge(Task v, Task w) {
		this.v = v;
		this.w = w;
		w.addPredecessor();
	}

	Task v, w;

	public String toString() {
		return Integer.toString(v.id) + " --> " + Integer.toString(w.id);
	}
}

class Project {
	Project() {
	}

	ArrayList<Task> tasks = new ArrayList<>();
	ArrayList<Edge> edges = new ArrayList<>();

	private void readFromFile(String filename) {
		;
	}

	public void addEdge(Task v, Task w) {
		v.addEdge(new Edge(v, w));
	}

	public void addTask(Task t) {
		if (!tasks.contains(t))
			tasks.add(t);
		else 
			System.out.println("Tried to add already existing task to project");
	}

	public void addTask(int id, String name, int time, int staff, int[] dependencies) {
		Task tmp = new Task(id, this);
		if (!tasks.contains(tmp)) {
			addTask(tmp);
		}
		else { 
			tmp = getTask(tmp);
		}
		tmp.setParameters(name, time, staff, dependencies);
	}


	public void addEdge(Edge e) {
		edges.add(e);
	}

	public Task getTask(Task t) {
		return tasks.get(tasks.indexOf(t));
	}

	public int getSize() {
		return tasks.size();
	}

	public void sort() {
		Collections.sort(tasks);
	}

	public void printAllTasks() {
		for (Task task : tasks) {
			System.out.println(task.toString());
		}
	}

	public void printAllEdges() {
		for (Edge edge : edges) {
			System.out.println(edge.toString());
		}
	}

	public void run() {
		int t = 0;
		ArrayList<Task> active_tasks = findTaskWithIndegreeZero();
		System.out.println("---------------- Starting project from");
		for (Task task : active_tasks) {
			System.out.println(task.toString());
			task.start(t);
		}
		boolean finished = false;
		while (true) {

			ArrayList<Task> to_be_removed = new ArrayList<>();
			ArrayList<Task> to_be_added = new ArrayList<>();
			for (Task task : active_tasks) {

				if (task.isFinished(t)) {
					for (Edge edge : task.outEdges) {
						if(edge.w.tell_finished_prerequisite(t)) {
							to_be_added.add(edge.w);
						}
					}
					to_be_removed.add(task);
				}
			}
			active_tasks.removeAll(to_be_removed);
			active_tasks.addAll(to_be_added);
			int current_staff = 0;

			for (Task task:active_tasks) {
				current_staff += task.staff;
			}
			System.out.println("Current staff: " + Integer.toString(current_staff));

			if (active_tasks.size() == 0) {
				for (Task task : tasks) {
					if (task.finished_at == -1) {
						System.out.println("Task " + Integer.toString(task.id) + " was not able to finish!");
					}
				}
				break;
			}
			t ++;
			System.out.println(" ");
			System.out.println("t: " + Integer.toString(t));

		}
		System.out.println("*** Shortest possible execution time is " + Integer.toString(t) + " ***");
	}

	private ArrayList<Task> findTaskWithIndegreeZero() {
		ArrayList<Task> ret = new ArrayList<>();
		for (Task task : tasks) {
			if (task.cntPredecessors == 0) {
				ret.add(task);
			}
		}
		return ret;
	}
}