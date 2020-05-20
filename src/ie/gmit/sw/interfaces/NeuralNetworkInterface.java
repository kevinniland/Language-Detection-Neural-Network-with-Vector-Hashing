package ie.gmit.sw.interfaces;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

public interface NeuralNetworkInterface {
	public BasicNetwork configureTopology();
	public MLDataSet generateDataSet();
	public void crossValidation(BasicNetwork basicNetwork, MLDataSet mlDataSet);
	public void resilientPropagation(BasicNetwork basicNetwork, MLDataSet mlDataSet);
	public void getAccuracy(BasicNetwork basicNetwork, MLDataSet mlDataSet);
	public void getPrediction(String nnFile, double[] vector);
}
