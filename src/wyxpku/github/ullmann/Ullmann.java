package wyxpku.github.ullmann;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Ullmann {
	private ArrayList<Graph> mygraphdb;
	private ArrayList<Graph> querys;

	public Ullmann() {
		setMygraphdb(new ArrayList<Graph>());
		setQuerys(new ArrayList<Graph>());
	}

	public int[][] getM0(Graph query, Graph graphdb) {
		int row = query.getVertexes().size();
		int col = graphdb.getVertexes().size();
		int[][] M0 = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				String vi = query.getVertexes().get(i).getVertex();
				String vj = graphdb.getVertexes().get(j).getVertex();
				if (graphdb.getVDegree(vj) >= query.getVDegree(vi))
					M0[i][j] = 1;
				else
					M0[i][j] = 0;
			}
		}
		return M0;
	}

	public boolean match(Graph subgraph, Graph graphdb) {
		int M0[][] = getM0(subgraph, graphdb);
		int MA[][] = subgraph.getMatrix();
		int MB[][] = graphdb.getMatrix();
		for (int i = 0; i < subgraph.vertexes.size(); i++) {
			for (int j = 0; j < graphdb.vertexes.size(); j++) {
				if (M0[i][j] == 0)
					continue;
				// else M0[i][j] = 1
				String ilabel = subgraph.vertexes.get(i).getLabel();
				String jlabel = graphdb.vertexes.get(j).getLabel();
				if (ilabel.equals(jlabel)) {
					for (int x = 0; x < subgraph.vertexes.size(); x++) {
						boolean flag = true;
						if (MA[i][x] == 1) {
							String label_xi = subgraph.getLabelByV(String.valueOf(i), String.valueOf(x));
							for (int y = 0; y < graphdb.vertexes.size(); y++) {
								String label_yj = graphdb.getLabelByV(String.valueOf(y), String.valueOf(y));
								if (label_xi.equals(label_yj)) {
									flag = true;
									break;
								}
							}
							if (!flag) {
								M0[i][j] = 0;
								break;
							}
						}
					}
				} else {
					M0[i][j] = 0;
				}
			}
		}
		for (int i = 0; i < subgraph.vertexes.size(); i++) {
			int sum = 0;
			for (int j = 0; j < graphdb.vertexes.size(); j++) {
				sum += M0[i][j];
			}
			if (sum == 0) {
				return false;
			}
		}
		int row = subgraph.vertexes.size();
		int col = graphdb.vertexes.size();

		int F[] = new int[col]; // F[i] = 1，表示第i列已选过
		int H[] = new int[row]; // H[i] = j，表示第i行选第j列

		int d = 0; // 第d行
		int k = 0; // 第k列

		int[][][] matrixList = new int[row][][]; // 记录每个d对应的M0矩阵

		for (int i = 0; i < F.length; i++) { // 初始化为-1
			F[i] = -1;
		}
		for (int i = 0; i < H.length; i++) { // 初始化为-1
			H[i] = -1;
		}

		while (true) {
			if (H[d] == -1) { // 第d行未选择，进行搜索
				k = 0;
				matrixList[d] = M0;
			} else { // 否则进行回溯
				k = H[d] + 1;
				F[H[d]] = -1;
				M0 = matrixList[d];
			}
			// 查找符合条件的列
			while (k < col) {
				if (M0[d][k] == 1 && F[k] == -1) {
					break;
				}
				k++;
			}
			if (k == col) { // 第d行中找不到满足条件的列，回溯到上一层
				H[d] = -1;
				d--;
			} else {
				// 找到对应的列，设置M0，d+=1，进行下一列搜索
				for (int j = 0; j < col; j++) {
					M0[d][j] = 0;
				}
				M0[d][k] = 1;
				H[d] = k;
				F[k] = 1;
				d++;
			}
			// 搜索结束，未找到同构的映射
			if (d == -1) {
				return false;
			}

			// 找到一个M0，验证是否符合条件
			if (d == row) {
				if (this.isTrueFor(MA, MB, M0)) {// 条件成立
					return true;
				} else {// 回溯
					d = row - 1;
				}

			}
		}
	}

	public boolean isTrueFor(int[][] MA, int[][] MB, int[][] M) {
		int row = M.length;
		int col = MB[0].length;
		int tmp[][] = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				for (int k = 0; k < M[0].length; k++) {
					tmp[i][j] += M[i][k] * MB[k][j];
				}
			}
		}
		int tmp_t[][] = new int[col][row];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				tmp_t[j][i] = tmp[i][j];
			}
		}

		int MC[][] = new int[col][row];
		for (int i = 0; i < MA.length; i++) {
			for (int j = 0; j < MA[0].length; j++) {
				for (int k = 0; k < M[0].length; k++) {
					MC[i][j] += M[i][k] * tmp_t[k][j];
				}
			}
		}
		for (int i = 0; i < MA.length; i++) {
			for (int j = 0; j < MA[0].length; ++j) {
				if (MA[i][j] == 1) {
					if (MC[i][j] == 1)
						continue;
					else
						return false;
				}
			}
		}
		return true;
	}

	public ArrayList<Graph> getMygraphdb() {
		return mygraphdb;
	}

	public void setMygraphdb(ArrayList<Graph> mygraphdb) {
		this.mygraphdb = mygraphdb;
	}

	public ArrayList<Graph> getQuerys() {
		return querys;
	}

	public void setQuerys(ArrayList<Graph> querys) {
		this.querys = querys;
	}

	public void loadfile(String dbfile, String qfile) throws IOException {
		BufferedReader dbreader = new BufferedReader(new InputStreamReader(new FileInputStream(dbfile)));
		String line = dbreader.readLine();
		Graph curg;
		if (line.startsWith("t #")) {
			curg = new Graph();
			while ((line = dbreader.readLine()) != null) {
				if (line.startsWith("t #")) {
					this.mygraphdb.add(curg);
					curg = new Graph();
					continue;
				} else if (line.startsWith("v")) {
					String vs[] = line.split(" ");
					Vertex tmpv = new Vertex();
					tmpv.setVertex(vs[1]);
					tmpv.setLabel(vs[2]);
					curg.vertexes.add(tmpv);
				} else if (line.startsWith("e")) {
					String es[] = line.split(" ");
					Edge tmpe = new Edge();
					tmpe.setFrom(es[1]);
					tmpe.setTo(es[2]);
					tmpe.setLabel(es[3]);
					curg.edges.add(tmpe);
				}
			}
		}

		BufferedReader qreader = new BufferedReader(new InputStreamReader(new FileInputStream(qfile)));
		line = qreader.readLine();
		if (line.startsWith("t #")) {
			curg = new Graph();
			while ((line = qreader.readLine()) != null) {
				if (line.startsWith("t #")) {
					this.querys.add(curg);
					curg = new Graph();
					continue;
				} else if (line.startsWith("v")) {
					String vs[] = line.split(" ");
					Vertex tmpv = new Vertex();
					tmpv.setVertex(vs[1]);
					tmpv.setLabel(vs[2]);
					curg.vertexes.add(tmpv);
				} else if (line.startsWith("e")) {
					String es[] = line.split(" ");
					Edge tmpe = new Edge();
					tmpe.setFrom(es[1]);
					tmpe.setTo(es[2]);
					tmpe.setLabel(es[3]);
					curg.edges.add(tmpe);
				}
			}
		}

	}

	public static void main(String[] args) {
		String dbfile = "C:\\Users\\wyx\\workspace\\Ullmann\\graphDB\\mygraphdb.data";
		String qfile = "C:\\Users\\wyx\\workspace\\Ullmann\\graphDB\\Q24.my";
		Ullmann ullmann = new Ullmann();
		try {
			ullmann.loadfile(dbfile, qfile);
			System.out.println("size of my graphdb: " + ullmann.getMygraphdb().size());
			System.out.println("size of my querys: " + ullmann.getQuerys().size());

			long start = System.currentTimeMillis();
			for (int i = 0; i < ullmann.getMygraphdb().size(); i++) {
				Graph target = ullmann.getMygraphdb().get(i);
				for (int j = 0; j < 1; j++) {
					Graph query = ullmann.getQuerys().get(j);
					if (ullmann.match(query, target)) {
						// System.out.println("Target #" + i + " matches query
						// #" + j);
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("程序运行时间： " + (end - start) + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
