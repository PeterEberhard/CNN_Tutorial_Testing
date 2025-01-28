//package layers;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class Layer {
//    public Layer get_nextLayer() {
//        return _nextLayer;
//    }
//
//    public void set_nextLayer(Layer _nextLayer) {
//        this._nextLayer = _nextLayer;
//    }
//
//    public Layer get_previousLayer() {
//        return _previousLayer;
//    }
//
//    public void set_previousLayer(Layer _previousLayer) {
//        this._previousLayer = _previousLayer;
//    }
//
//    protected Layer _nextLayer;
//    protected Layer _previousLayer;
//
//    public abstract double[] getOutput(List<double[][]> input);
//    public abstract double[] getOutput(double[] input);
//
//    public abstract void backPropagation(double[] dLdO);
//    public abstract void backPropagation(List<double[][]> dLdO);
//
//    public abstract int getOutputLength();
//    public abstract int getOutputRows();
//    public abstract int getOutputCols();
//    public abstract int getOutputElements();
//
//
//    public double[] matrixToVector(List<double[][]> input){
//
//        int length = input.size();
//        int rows = input.get(0).length;
//        int cols = input.get(0)[0].length;
//
//        double[] vector = new double[length*rows*cols];
//
//        int i = 0;
//        for(int l = 0; l < length; l++ ){
//            for(int r = 0; r < rows; r++){
//                for(int c = 0; c < cols; c++){
//                    vector[i] = input.get(l)[r][c];
//                    i++;
//                }
//            }
//        }
//
//        return vector;
//    }
//
//    List<double[][]> vectorToMatrix(double[] input, int length, int rows, int cols){
//
//        List<double[][]> out = new ArrayList<>();
//
//        int i = 0;
//        for(int l = 0; l < length; l++ ){
//
//            double[][] matrix = new double[rows][cols];
//
//            for(int r = 0; r < rows; r++){
//                for(int c = 0; c < cols; c++){
//                    matrix[r][c] = input[i];
//                    i++;
//                }
//            }
//
//            out.add(matrix);
//        }
//
//        return out;
//    }
//
//}
package layers;
import java.util.ArrayList;
import java.util.List;

public abstract class Layer {

    public Layer getNextLayer() {
        return nextLayer;
    }

    public void setNextLayer(Layer nextLayer) {
        this.nextLayer = nextLayer;
    }

    public Layer getPreviousLayer() {
        return previousLayer;
    }

    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    protected Layer nextLayer;
    protected Layer previousLayer;

    public abstract double[] getOutput(List<double[][]> input);
    public abstract double[] getOutput(double[] input);


    // polymorpism for back propagation
    // loss is a function of how bad the result is
    public abstract void backPropagation(double[] dLdO);
    public abstract void backPropagation(List<double[][]> dLdO);

    public abstract int getOutputLength();
    public abstract int getOutputRows();
    public abstract int getOutputCols();
    public abstract int getOutputElements();
    public double[] matrixConvertToVector(List<double[][]> matrix){
        int length = matrix.size();
        int rows = matrix.get(0).length;
        int columns = matrix.get(0)[0].length;

        double[] vector = new double[length*rows*columns];

        // split matrix into 1d
        int i = 0;
        // for every item in entire matrix
        for(int l = 0; l < length; l++){
            for(int row = 0; row < rows; row++){
                for(int column = 0; column < columns; column++){
                    vector[i] = matrix.get(l)[row][column];
                    i++;
                }
            }
        }

        return vector;
    }

    public List<double[][]> vectorConvertToMatrix(double[] vector, int length, int rows, int columns){
        List<double[][]> output = new ArrayList<>();

        // for every slot in matrix
        int i = 0;
        for(int l = 0; l < length; l++){
            double[][] matrix = new double[rows][columns];

            for(int row = 0; row < rows; row++){
                for(int column = 0; column < columns; column++){
                    matrix[row][column] = vector[i];
                }
            }
            output.add(matrix);
        }

        return output;

    }
}
