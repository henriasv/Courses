--------------------------------------------------------------
Usage:

>> java ProjectPlanner <projectname>.txt manpower

only 999=infinity is accepted as manpower.

--------------------------------------------------------------

Program seems to sun fine for all the given project files. buildhouse2.txt is stopped because it contains a cycle.

The complexity is O(|E| + |V|) because all nodes are visited a constant number of times. Starting points ar found by cheching each node for having indegree 0 (which is saved on each node): O(|V|). The algorithm for finding cycles is effectively a depth first search, which takes O(|E|). Then, when executing the project, the tasks are done once each, but following edges, thus possibly O(|E|+|V|). The search for latest possible starting times is done by going backwards, only once per edge, thus O(|E|). In total, all of the exercises have a complexity of O(|E|+|V|).

I am not too happy with my implementation, it feels like a way too specific solution. If i did it again, I would have made the program much more general, using general graph properties, such as topological paths, and interpreting them as project- and task properties.