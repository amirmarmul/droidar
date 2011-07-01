package de.rwth;

import gl.GLCamera;
import gl.MeshComponent;
import util.Vec;
import android.opengl.Matrix;
import android.util.Log;
import gl.MarkerObject;

public class VirtualObjectMarker implements MarkerObject {

	final static float rad2deg = (float) (180.0f / Math.PI);

	private MeshComponent myTargetMesh;
	private GLCamera myCamera;

	// private float[] viewMatrix = null;

	public VirtualObjectMarker(MeshComponent m, GLCamera camera) {
		myTargetMesh = m;
		myCamera = camera;
	}

	@Override
	public int getMyId() {
		return 0;
	}

	@Override
	public void OnMarkerPositionRecognized(float[] markerRotMatrix,
			int startOffset, int end, int sideAngle) {

		float[] invertedCameraMatrix = new float[16];
		Matrix.invertM(invertedCameraMatrix, 0, myCamera.getRotationMatrix(), 0);

		float[] resultPosVec = { 0, 0, 0, 1 };
		float[] markerCenterPosVec = { markerRotMatrix[startOffset + 12],
				markerRotMatrix[startOffset + 13],
				markerRotMatrix[startOffset + 14], 1 };
		Matrix.multiplyMV(resultPosVec, 0, invertedCameraMatrix, 0,
				markerCenterPosVec, 0);

		Vec camPos = myCamera.getMyPosition();
		myTargetMesh.myPosition = new Vec(resultPosVec[0] + camPos.x,
				resultPosVec[1] + camPos.y, resultPosVec[2] + camPos.z);

		float[] antiCameraRotMatrix = new float[16];
		Matrix.multiplyMM(antiCameraRotMatrix, 0, invertedCameraMatrix, 0,
				markerRotMatrix, startOffset);

		antiCameraRotMatrix[12] = 0;
		antiCameraRotMatrix[13] = 0;
		antiCameraRotMatrix[14] = 0;

		myTargetMesh.setRotationMatrix(antiCameraRotMatrix, sideAngle);

		// float[] resultingAngles = { 0, 0, 0, 1 };
		// float[] rotationMatrix = new float[16];
		// Matrix.transposeM(antiCameraRotMatrix, 0, antiCameraRotMatrix, 0);
		// getAngles(resultingAngles, antiCameraRotMatrix);

		// Matrix.multiplyMV(resultingAngles, 0, invertedCameraMatrix, 0,
		// resultingAngles, 0);

		// if (myTargetMesh.myRotation == null)
		// myTargetMesh.myRotation = new Vec(resultingAngles[0],
		// resultingAngles[1], resultingAngles[2]);
		// else {
		// myTargetMesh.myRotation.x = resultingAngles[0];
		// myTargetMesh.myRotation.y = resultingAngles[1];
		// myTargetMesh.myRotation.z = resultingAngles[2];
		// }
	}

	private void getAngles(float[] resultingAngles, float[] rotationMatrix) {

		resultingAngles[2] = (float) (Math.asin(-rotationMatrix[8]));
		final float cosB = (float) Math.cos(resultingAngles[2]);
		resultingAngles[2] = resultingAngles[2] * rad2deg;
		resultingAngles[1] = (float) (Math.acos(rotationMatrix[9] / cosB))
				* rad2deg;
		resultingAngles[0] = (float) (Math.asin(rotationMatrix[4] / cosB))
				* rad2deg;

	}

}