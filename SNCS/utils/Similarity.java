package utils;



public class Similarity {
    public static double cos(double[] va, double[] vb) {
        int size = va.length;
        double simVal = 0;


        double num = 0;
        double den = 1;
        double powa_sum = 0;
        double powb_sum = 0;
        for (int i = 0; i < size; i++) {
            double a = va[i];
            double b = vb[i];

            num = num + a * b;
            powa_sum = powa_sum + (double) Math.pow(a, 2);
            powb_sum = powb_sum + (double) Math.pow(b, 2);
        }
        double sqrta = (double) Math.sqrt(powa_sum);
        double sqrtb = (double) Math.sqrt(powb_sum);
        den = sqrta * sqrtb+1e-10; 

        simVal = num / den;

        return simVal;
    }

    public static double pearson(double[] xData, double[] yData) {

        if (xData.length != yData.length)
            throw new RuntimeException("error");
        double xMeans;
        double yMeans;
        double numerator = 0;
        double denominator = 0;

        double result = 0;
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        result = numerator / denominator;
        return result;
    }


    private static double generateNumerator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double numerator = 0.0;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    private static double generateDenomiator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double xSum = 0.0;
        for (double xDatum : xData) {
            xSum += (xDatum - xMeans) * (xDatum - xMeans);
        }
        double ySum = 0.0;
        for (double yDatum : yData) {
            ySum += (yDatum - yMeans) * (yDatum - yMeans);
        }
        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    private static double getMeans(double[] datas) {
        double sum = 0.0;
        for (double data : datas) {
            sum += data;
        }
        return sum / datas.length;
    }

}
