
package network;

import data.Image;
import layers.Layer;

import java.util.ArrayList;
import java.util.List;

import static data.MatrixUtility.add;
import static data.MatrixUtility.multiply;

public class NeuralNetwork {
    List<Layer> layers;
    double scaleFactor;

    public NeuralNetwork(List<Layer> layers, double scaleFactor) {
        this.layers = layers;
        this.scaleFactor = scaleFactor;
        linkLayers();

    }

    private void linkLayers() {
        if (layers.size() <= 1) {
            return;
        }
        for (int i = 0; i < layers.size(); i++) {
            if (i == 0) {
                layers.get(i).setNextLayer(layers.get(i + 1));
            } else if (i == layers.size() - 1) {
                layers.get(i).setPreviousLayer(layers.get(i - 1));
            } else {
                layers.get(i).setPreviousLayer(layers.get(i - 1));
                layers.get(i).setNextLayer(layers.get(i + 1));
            }

        }
    }

    public double[] getError(double[] output, int answer) {
        int numClasses = output.length;
        double[] expected = new double[numClasses];
        expected[answer] = 1;

        return add(output, multiply(expected, -1));
    }

    private int getMax(double[] input) {
        double max = 0;
        int index = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] >= max) {
                max = input[i];
                index = i;

            }
        }

        return index;
    }

    public int guess(Image image) {
        List<double[][]> inputList = new ArrayList<>();
        inputList.add(multiply(image.getData(), 1.0 / scaleFactor));
        double[] output = layers.get(0).getOutput(inputList);

        int guess = getMax(output);
        return guess;
    }

    public float test(List<Image> images) {
        int correct = 0;
        for (Image img : images) {
            int guess = guess(img);
            if (guess == img.getLabel()) {
                correct++;
            }
        }

        return ((float) correct / images.size());
    }

    public void train(List<Image> images) {
        for (Image img : images) {
            List<double[][]> inputList = new ArrayList<>();
            inputList.add(multiply(img.getData(), (1.0 / scaleFactor)));
            double[] output = layers.get(0).getOutput(inputList);
            double[] dLdO = getError(output, img.getLabel());
            layers.get((layers.size() - 1)).backPropagation(dLdO);

        }
    }
}