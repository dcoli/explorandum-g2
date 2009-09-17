package explorandum.f09.g2;

public interface Constants {

	public static int[][] vertexNeighborOffsets = new int[][] { { -1, -1 },
			{ -1, 1 }, { 1, 1 }, { 1, -1 } };
	public static int[][] edgeNeighborOffsets = new int[][] { { -1, 0 },
			{ 1, 0 }, { 0, 1 }, { 0, -1 } };
}
