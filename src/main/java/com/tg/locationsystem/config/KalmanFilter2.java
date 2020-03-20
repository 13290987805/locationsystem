package com.tg.locationsystem.config;

import Jama.Matrix;
import com.tg.locationsystem.entity.TagHistory;
import com.tg.locationsystem.entity.TagHistoryVO;

public class KalmanFilter2 {
	private Matrix predictPosition = new Matrix(4, 1);
	private double controlVlaue;
	private Matrix matrixB = new Matrix(4, 1, 0);
	private Matrix matrixA;
	private Matrix matrixH;
	private Matrix matrixQ;
	private Matrix matrixR;
	private Matrix matrixP = Matrix.random(4, 4);
	private Matrix matrixK = new Matrix(4, 2, 0);
	private final double CQ = 0.001;
    private final double CR = 0.01;

	private KalmanFilter2() {
		this.controlVlaue = 0;
		double[][] A = {{1,0,1,0},{0,1,0,1},{0,0,1,0},{0,0,0,1}};
		matrixA = new Matrix(A, 4, 4);
		double[][] H = {{1,0,0,0},{0,1,0,0}};
		matrixH = new Matrix(H, 2, 4);
		double[][] Q = {{CQ,0,0,0},{0,CQ,0,0},{0,0,CQ,0},{0,0,0,CQ}};
		double[][] R = {{CR,0},{0,CR}};
		matrixQ = new Matrix(Q, 4, 4);
		matrixR = new Matrix(R, 2, 2);
	}
	private static class SingletonHolder{
		private static KalmanFilter2 singleton = new KalmanFilter2();
	}
	public static KalmanFilter2 getInstance() {
		return SingletonHolder.singleton;
	}
	//匀速模型
    private Matrix kalmanFilter(Matrix prePosition,Matrix measuredPosition){
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
    
    public TagHistory printM(TagHistory prePosition, TagHistory measuredPosition) {
    	
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
	public TagHistoryVO printM2(TagHistoryVO prePosition, TagHistoryVO measuredPosition) {

		double[] ad = {measuredPosition.getX(), measuredPosition.getY()};
		Matrix meaPositionMatrix = new Matrix(ad,2);
		double xV = 0.1;
		double yV = 0.1;
		double[] pre = {prePosition.getX(), prePosition.getY(),xV,yV};
		Matrix prePositionMatrix = new Matrix(pre,4);

		Matrix tMatrix = kalmanFilter(prePositionMatrix, meaPositionMatrix);
		double x = tMatrix.get(0, 0);
		double y = tMatrix.get(1, 0);
		TagHistoryVO t = new TagHistoryVO();
		t.setX(x);
		t.setY(y);
		t.setMapKey(measuredPosition.getMapKey());
		t.setPersonIdcard(measuredPosition.getPersonIdcard());
		t.setTime(measuredPosition.getTime());
		return t;
	}
}
