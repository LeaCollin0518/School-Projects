import networkx as nx

# using shortest-path to find valid s-t path in residual graph
def BFS(graph, start, end):
    # will make nodes dictionaries to easily keep track of paths
    initial = {'Node': start, 'Path': [start]}
    goal = {'Node': end}

    # queue
    q = []
    curr = initial

    # keep track of already explored nodes
    explored = []
    explored.append(initial['Node'])
    q.append(curr)

    while len(q) != 0:
        curr = q.pop(0)
        explored.append(curr['Node'])
        neighbors = graph.neighbors(curr['Node'])

        for n in neighbors:
            # make sure you can use this edge
            if graph.edge[curr['Node']][n]['capacity'] > 0:
                n = {'Node': n, 'Path' : curr['Path'] + [n]}
                if n['Node'] not in explored:
                    q.append(n)
            else:
                pass

        # we found the end node
        if curr['Node'] == goal['Node']:
            return curr['Path']
    # return 0 if we could not find a valid path
    return 0

def augment(graph_r, flow, P, value):
    # max int
    c_p = 9223372036854775807
    
    # find the capacity of the path
    for i in range(len(P) - 1):
        u = P[i]
        v = P[i+1]
        if graph_r.edge[u][v]['capacity'] < c_p:
            c_p = graph_r.edge[u][v]['capacity']
            
    # add capacity to max flow (augmenting max flow)
    value += c_p
        
    # essentially follow pseudocode
    for i in range(len(P) - 1):
        u = P[i]
        v = P[i+1]
        if graph_r.edge[u][v]['direction'] == 'forward':
            # have already pushed some flow along this edge
            # capacity now decreases along forward edge
            graph_r.edge[u][v]['capacity'] -= c_p
            if flow[u][v] != 0:
                flow[u][v] += c_p
                # capacity increases along backward edge
                graph_r.edge[v][u]['capacity'] += c_p
            # have not already pushed flow; need to create a backwards edge
            else:
                flow[u][v] = c_p
                graph_r.add_edge(v, u)
                graph_r.edge[v][u]['direction'] = 'backward'
                graph_r.edge[v][u]['capacity'] = c_p
                
        # edge is a backward edge; essentially do opposite as above
        else:
            flow[v][u] -= c_p
            graph_r.edge[u][v]['capacity'] += c_p
            graph_r.edge[v][u]['capacity'] -= c_p
            
    return graph_r, flow, value
            
                
def FF(g, start, end):
    value = 0
    flow = {}
    for node in g.nodes():
        flow[node] = {}
        for neighbor in g.neighbors(node):
            flow[node][neighbor] = 0

    """
        creating initial residual graph, which is a copy of g
        adding what direction each edge is going in
        initially, all edges are forward
    """

    residual_graph = g.copy()
    for (n1, n2) in residual_graph.edges():
        residual_graph[n1][n2]['direction'] = 'forward'

    while BFS(residual_graph, start, end) != 0:
        P = BFS(residual_graph, start, end)
        residual_graph, flow, value = augment(residual_graph, flow, P, value)
    return value, flow