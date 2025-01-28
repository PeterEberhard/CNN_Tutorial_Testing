//package layers;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import static data.MatrixUtility.add;
//import static data.MatrixUtility.multiply;
//
//public class ConvolutionLayer extends Layer{
//
//    private long SEED;
//
//    private List<double[][]> _filters;
//    private int _filterSize;
//    private int _stepsize;
//
//    private int _inLength;
//    private int _inRows;
//    private int _inCols;
//    private double _learningRate;
//
//    private List<double[][]> _lastInput;
//
//    public ConvolutionLayer(int _filterSize, int _stepsize, int _inLength, int _inRows, int _inCols, long SEED, int numFilters, double learningRate) {
//        this._filterSize = _filterSize;
//        this._stepsize = _stepsize;
//        this._inLength = _inLength;
//        this._inRows = _inRows;
//        this._inCols = _inCols;
//        this.SEED = SEED;
//        _learningRate = learningRate;
//
//        generateRandomFilters(numFilters);
//
//    }
//
//    private void generateRandomFilters(int numFilters){
//        List<double[][]> filters = new ArrayList<>();
//        Random random = new Random(SEED);
//
//        for(int n = 0; n < numFilters; n++) {
//            double[][] newFilter = new double[_filterSize][_filterSize];
//
//            for(int i = 0; i < _filterSize; i++){
//                for(int j = 0; j < _filterSize; j++){
//
//                    double value = random.nextGaussian();
//                    newFilter[i][j] = value;
//                }
//            }
//
//            filters.add(newFilter);
//
//        }
//
//        _filters = filters;
//
//    }
//
//    public List<double[][]> convolutionForwardPass(List<double[][]> list){
//        _lastInput = list;
//
//        List<double[][]> output = new ArrayList<>();
//
//        for (int m = 0; m < list.size(); m++){
//            for(double[][] filter : _filters){
//                output.add(convolve(list.get(m), filter, _stepsize));
//            }
//
//        }
//
//        return output;
//
//    }
//
//    private double[][] convolve(double[][] input, double[][] filter, int stepSize) {
//
//        int outRows = (input.length - filter.length)/stepSize + 1;
//        int outCols = (input[0].length - filter[0].length)/stepSize + 1;
//
//        int inRows = input.length;
//        int inCols = input[0].length;
//
//        int fRows = filter.length;
//        int fCols = filter[0].length;
//
//        double[][] output = new double[outRows][outCols];
//
//        int outRow = 0;
//        int outCol;
//
//        for(int i = 0; i <= inRows - fRows; i += stepSize){
//
//            outCol = 0;
//
//            for(int j = 0; j <= inCols - fCols; j+= stepSize){
//
//                double sum = 0.0;
//
//                //Apply Filter around this position
//                for(int x = 0; x < fRows; x++){
//                    for(int y = 0; y < fCols; y++){
//                        int inputRowIndex = i+x;
//                        int inpurColIndex = j+y;
//
//                        double value = filter[x][y] * input[inputRowIndex][inpurColIndex];
//                        sum+= value;
//                    }
//                }
//
//                output[outRow][outCol] = sum;
//                outCol++;
//            }
//
//            outRow++;
//
//        }
//
//        return output;
//
//    }
//
//    public double[][] spaceArray(double[][] input){
//
//        if(_stepsize == 1){
//            return input;
//        }
//
//        int outRows = (input.length - 1)*_stepsize + 1;
//        int outCols = (input[0].length -1)*_stepsize+1;
//
//        double[][] output = new double[outRows][outCols];
//
//        for(int i = 0; i < input.length; i++){
//            for(int j = 0; j < input[0].length; j++){
//                output[i*_stepsize][j*_stepsize] = input[i][j];
//            }
//        }
//
//        return output;
//    }
//
//
//    @Override
//    public double[] getOutput(List<double[][]> input) {
//
//        List<double[][]> output = convolutionForwardPass(input);
//
//        return _nextLayer.getOutput(output);
//
//    }
//
//    @Override
//    public double[] getOutput(double[] input) {
//
//        List<double[][]> matrixInput = vectorToMatrix(input, _inLength, _inRows, _inCols);
//
//        return getOutput(matrixInput);
//    }
//
//    @Override
//    public void backPropagation(double[] dLdO) {
//        List<double[][]> matrixInput = vectorToMatrix(dLdO, _inLength, _inRows, _inCols);
//        backPropagation(matrixInput);
//    }
//
//    @Override
//    public void backPropagation(List<double[][]> dLdO) {
//
//        List<double[][]> filtersDelta = new ArrayList<>();
//        List<double[][]> dLdOPreviousLayer= new ArrayList<>();
//
//        for(int f = 0; f < _filters.size(); f++){
//            filtersDelta.add(new double[_filterSize][_filterSize]);
//        }
//
//        for(int i = 0; i < _lastInput.size(); i++){
//
//            double[][] errorForInput = new double[_inRows][_inCols];
//
//            for(int f = 0; f < _filters.size(); f++){
//
//                double[][] currFilter = _filters.get(f);
//                double[][] error = dLdO.get(i*_filters.size() + f);
//
//                double[][] spacedError = spaceArray(error);
//                double[][] dLdF = convolve(_lastInput.get(i), spacedError, 1);
//
//                double[][] delta = multiply(dLdF, _learningRate*-1);
//                double[][] newTotalDelta = add(filtersDelta.get(f), delta);
//                filtersDelta.set(f, newTotalDelta);
//
//                double[][] flippedError = flipArrayHorizontal(flipArrayVertical(spacedError));
//                errorForInput = add(errorForInput, fullConvolve(currFilter, flippedError));
//
//            }
//
//            dLdOPreviousLayer.add(errorForInput);
//
//        }
//
//        for(int f =0; f < _filters.size(); f++){
//            double[][] modified = add(filtersDelta.get(f), _filters.get(f));
//            _filters.set(f,modified);
//        }
//
//        if(_previousLayer!= null){
//            _previousLayer.backPropagation(dLdOPreviousLayer);
//        }
//    }
//
//    public double[][] flipArrayHorizontal(double[][] array){
//        int rows = array.length;
//        int cols = array[0].length;
//
//        double[][] output = new double[rows][cols];
//
//        for(int i = 0; i < rows; i++){
//            for(int j = 0; j < cols; j++){
//                output[rows-i-1][j] = array[i][j];
//            }
//        }
//        return output;
//    }
//
//    public double[][] flipArrayVertical(double[][] array){
//        int rows = array.length;
//        int cols = array[0].length;
//
//        double[][] output = new double[rows][cols];
//
//        for(int i = 0; i < rows; i++){
//            for(int j = 0; j < cols; j++){
//                output[i][cols-j-1] = array[i][j];
//            }
//        }
//        return output;
//    }
//
//    private double[][] fullConvolve(double[][] input, double[][] filter) {
//
//        int outRows = (input.length + filter.length) + 1;
//        int outCols = (input[0].length + filter[0].length) + 1;
//
//        int inRows = input.length;
//        int inCols = input[0].length;
//
//        int fRows = filter.length;
//        int fCols = filter[0].length;
//
//        double[][] output = new double[outRows][outCols];
//
//        int outRow = 0;
//        int outCol;
//
//        for(int i = -fRows + 1; i < inRows; i ++){
//
//            outCol = 0;
//
//            for(int j = -fCols + 1; j < inCols; j++){
//
//                double sum = 0.0;
//
//                //Apply Filter around this position
//                for(int x = 0; x < fRows; x++){
//                    for(int y = 0; y < fCols; y++){
//                        int inputRowIndex = i+x;
//                        int inputColIndex = j+y;
//
//                        if(inputRowIndex >= 0 && inputColIndex >= 0 && inputRowIndex < inRows && inputColIndex < inCols){
//                            double value = filter[x][y] * input[inputRowIndex][inputColIndex];
//                            sum+= value;
//                        }
//                    }
//                }
//
//                output[outRow][outCol] = sum;
//                outCol++;
//            }
//
//            outRow++;
//
//        }
//
//        return output;
//
//    }
//
//    @Override
//    public int getOutputLength() {
//        return _filters.size()*_inLength;
//    }
//
//    @Override
//    public int getOutputRows() {
//        return (_inRows-_filterSize)/_stepsize + 1;
//    }
//
//    @Override
//    public int getOutputCols() {
//        return (_inCols-_filterSize)/_stepsize + 1;
//    }
//
//    @Override
//    public int getOutputElements() {
//        return getOutputCols()*getOutputRows()*getOutputLength();
//    }
//}
package layers;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

        import static data.MatrixUtility.add;
        import static data.MatrixUtility.multiply;

public class ConvolutionLayer extends Layer{
    private long SEED;
    private List<double[][]> filters;
    private int filterSize;
    private int stepSize;
    private int inputLength;
    private int inputRows;
    private int inputColumns;
    private List<double[][]> lastInput;
    private double learningRate;
    public ConvolutionLayer(int filterSize, int stepSize, int inputLength, int inputRows, int inputColumns, long SEED, int filterCount, double learningRate) {
        this.filterSize = filterSize;
        this.stepSize = stepSize;
        this.inputLength = inputLength;
        this.inputRows = inputRows;
        this.inputColumns = inputColumns;
        this.SEED = SEED;
        generateFilters(filterCount);
        this.learningRate = learningRate;
    }
    public void generateFilters(int filterCount){
        List<double[][]> filterList = new ArrayList<>();
        Random random = new Random(SEED);
        for(int n = 0; n < filterCount; n++){
            double[][] newFilter = new double[filterSize][filterSize];


            for(int i = 0; i < filterSize; i++) {
                for (int j = 0; j < filterSize; j++) {
                    double val = random.nextGaussian();
                    newFilter[i][j] = val;
                }
            }

            filterList.add(newFilter);
        }

        filters = filterList;
    }



    public List<double[][]> convolutionForwardPass(List<double[][]> list){
        lastInput = list;
        List<double[][]> output = new ArrayList<>();
        for(int m = 0; m < list.size(); m++){
            // put every input through every filter
            for(double[][] filter: filters){
                output.add(convolve(list.get(m),filter,stepSize));

            }
        }

        return output;
    }

    private double[][] convolve(double[][] input, double[][] filter, int stepSize) {
        // using these calcs here instead of the method is because we are going to convolve again in back prop and
        // dimensions change.
        int outRows = (input.length - filter.length) / stepSize + 1;
        int outColumns = (input[0].length - filter[0].length) / stepSize + 1;
        int inputRows = input.length;
        int inputColumns = input[0].length;
        int filterRows = filter.length;
        int filterColumns = filter[0].length;

        double[][] output = new double[outRows][outColumns];
        int outRow = 0;
        int outColumn;


        for(int i = 0; i <= inputRows - filterRows; i+= stepSize){
            outColumn = 0;
            for(int j = 0; j <= inputColumns - filterColumns; j+= stepSize){

                double sum = 0.0;
                // apply filter around pos
                for(int x = 0; x < filterRows; x++){
                    for(int y = 0; y <filterColumns; y++){
                        int inputRowIndex = i + x;
                        int inputColumnIndex = j + y;
                        double value = filter[x][y] * input[inputRowIndex][inputColumnIndex];
                        sum += value;
                    }
                }

                output[outRow][outColumn] = sum;
                outColumn++;
            }
            outRow++;
        }
        return output;
    }

    public double[][] spaceArray(double[][] input){
        if(stepSize == 1){
            return input;
        }

        int outRows = (input.length - 1)*stepSize+1;
        int outColumns = (input[0].length - 1)*stepSize+1;

        double[][] output = new double[outRows][outColumns];

        for(int i = 0; i < input.length; i++){
            for(int j = 0; j < input[0].length; j++){
                output[i*stepSize][j*stepSize] = input[i][j];
            }
        }

        return output;
    }

    @Override
    public double[] getOutput(List<double[][]> input) {
        List<double[][]> output = convolutionForwardPass(input);
        //TODO getNextLayer or nextlayer?
        return nextLayer.getOutput(output);
    }

    @Override
    public double[] getOutput(double[] input) {
        List<double[][]> matrix = vectorConvertToMatrix(input,inputLength,inputRows,inputColumns);
        return getOutput(matrix);
    }

    @Override
    public void backPropagation(double[] dLdO) {
        List<double[][]> matrix = vectorConvertToMatrix(dLdO,inputLength,inputRows,inputColumns);
        backPropagation(matrix);
    }

    @Override
    public void backPropagation(List<double[][]> dLdO) {
        List<double[][]> filtersDelta = new ArrayList<>();
        List<double[][]> dLdOPrevious = new ArrayList<>();


        for(int f = 0; f<filters.size(); f++){
            filtersDelta.add(new double[filterSize][filterSize]);
        }

        for(int i = 0; i < lastInput.size(); i++){
            double[][] errorForInput = new double[inputRows][inputColumns];
            for(int f = 0; f < filters.size(); f++){

                double[][] currentFilter = filters.get(f);
                double[][] error = dLdO.get(i*filters.size() + f);
                double[][] spacedError = spaceArray(error);
                double[][] dLdF = convolve(lastInput.get(i), spacedError, 1);

                double[][] delta = multiply(dLdF, learningRate*-1);
                double[][] newTotalDelta = add(filtersDelta.get(f), delta);
                filtersDelta.set(f,newTotalDelta);

                double[][] flippedError = flipArrayHorizontal(flipArrayVertical(spacedError));
                errorForInput = add(errorForInput, fullConvolve(currentFilter, flippedError));
            }

            dLdOPrevious.add(errorForInput);
        }

        for(int f = 0; f < filters.size(); f++){
            double[][] modified = add(filtersDelta.get(f), filters.get(f));
            filters.set(f,modified);
        }
        //TODO getpreviouslayer() or previousLayer
        if(previousLayer != null){
            previousLayer.backPropagation(dLdOPrevious);
        }
    }

    public double[][] flipArrayHorizontal(double[][] array) {
        int rows = array.length;
        int cols = array[0].length;

        double[][] output = new double[rows][cols];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                output[rows-i-1][j] = array[i][j];
            }
        }
        return output;
    }

    public double[][] flipArrayVertical(double[][] array) {
        int rows = array.length;
        int cols = array[0].length;

        double[][] output = new double[rows][cols];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                output[i][cols-j-1] = array[i][j];
            }
        }
        return output;
    }

    private double[][] fullConvolve(double[][] input, double[][] filter) {
        // using these calcs here instead of the method is because we are going to convolve again in back prop and
        // dimensions change.
        int outRows = (input.length + filter.length)+1;
        int outColumns = (input[0].length + filter[0].length)+ 1;

        int inputRows = input.length;
        int inputColumns = input[0].length;

        int filterRows = filter.length;
        int filterColumns = filter[0].length;

        double[][] output = new double[outRows][outColumns];
        int outRow = 0;
        int outColumn;


        for(int i = -filterRows + 1; i < inputRows; i++){
            outColumn = 0;
            for(int j = -filterColumns + 1; j < inputColumns; j++){

                double sum = 0.0;
                // apply filter around pos
                for(int x = 0; x < filterRows; x++){
                    for(int y = 0; y <filterColumns; y++){
                        int inputRowIndex = i + x;
                        int inputColumnIndex = j + y;

                        if(inputRowIndex >= 0 && inputColumnIndex >= 0 && inputRowIndex < inputRows && inputColumnIndex < inputColumns){
                            double value = filter[x][y] * input[inputRowIndex][inputColumnIndex];
                            sum += value;
                        }

                    }
                }

                output[outRow][outColumn] = sum;
                outColumn++;
            }
            outRow++;
        }
        return output;
    }



    @Override
    public int getOutputLength() {
        return filters.size() *inputLength;
    }

    @Override
    public int getOutputRows() {
        return (inputRows-filterSize)/stepSize +1;
    }

    @Override
    public int getOutputCols() {
        return (inputColumns-filterSize)/stepSize +1;

    }

    @Override
    public int getOutputElements() {
        return getOutputCols()*getOutputRows()*getOutputLength();
    }
}