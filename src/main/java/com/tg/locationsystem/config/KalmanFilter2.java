package com.tg.locationsystem.config;

import Jama.Matrix;
import com.tg.locationsystem.entity.TagHistory;

public class KalmanFilter2 {
	private static Matrix predictPosition = new Matrix(4, 1);
	private static double controlVlaue;
	private static Matrix matrixB = new Matrix(4, 1, 0);
	private static Matrix matrixA;
	private static Matrix matrixH;
	private static Matrix matrixQ;
	private static Matrix matrixR;
	private static Matrix matrixP = Matrix.random(4, 4);
	private static Matrix matrixK = new Matrix(4, 2, 0);
	private final static double CQ = 0.001;
    private final static double CR = 0.01;
	
	public static void init() {
		controlVlaue = 0;
		double[][] A = {{1,0,1,0},{0,1,0,1},{0,0,1,0},{0,0,0,1}};
		matrixA = new Matrix(A, 4, 4);
		double[][] H = {{1,0,0,0},{0,1,0,0}};
		matrixH = new Matrix(H, 2, 4);
		double[][] Q = {{CQ,0,0,0},{0,CQ,0,0},{0,0,CQ,0},{0,0,0,CQ}};
		double[][] R = {{CR,0},{0,CR}};
		matrixQ = new Matrix(Q, 4, 4);
		matrixR = new Matrix(R, 2, 2);
	}
	
	//匀速模型
    public static Matrix kalmanFilter(Matrix prePosition,Matrix measuredPosition){
    	//预估位置
    	predictPosition = matrixA.times(prePosition).plus(matrixB.times(controlVlaue));
    	//误差矩阵（协方差）
    	matrixP = matrixA.times(matrixP).times(matrixA.transpose()).plus(matrixQ);
    	//卡尔曼增率
    	Matrix matrixTemp = matrixH.times(matrixP).times(matrixH.transpose()).plus(matrixR);
    	matrixK = matrixP.times(matrixH.transpose()).times(matrixTemp.inverse());
    	//最优估计值
    	Matrix matrixTemp2 = measuredPosition.minus(matrixH.times(prePosition));
    	Matrix estimatePosition = prePosition.plus(matrixK.times(matrixTemp2));
    	//迭代协方差
    	Matrix matrixTemp3 = matrixK.times(matrixH).times(matrixP);
    	matrixP = matrixP.minus(matrixTemp3);
		return estimatePosition;
    }
    
    public static TagHistory printM(TagHistory prePosition, TagHistory measuredPosition) {
    	
    	double[] ad = {measuredPosition.getX(), measuredPosition.getY()};
    	Matrix meaPositionMatrix = new Matrix(ad,2);
    	double xV = 0.1;
		double yV = 0.1;
    	double[] pre = {prePosition.getX(), prePosition.getY(),xV,yV};
    	Matrix prePositionMatrix = new Matrix(pre,4);
    	
    	Matrix tMatrix = kalmanFilter(prePositionMatrix, meaPositionMatrix);
    	double x = tMatrix.get(0, 0);
    	double y = tMatrix.get(1, 0);
		TagHistory t = new TagHistory();
		t.setX(x);
		t.setY(y);
		t.setMapKey(measuredPosition.getMapKey());
		t.setPersonIdcard(measuredPosition.getPersonIdcard());
		t.setTime(measuredPosition.getTime());
		t.setId(measuredPosition.getId());
		return t;
		
		
	}
	
}
