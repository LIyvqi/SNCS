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
        den = sqrta * sqrtb+1e-10; //加个小数防止是0的情况出现

        simVal = num / den;

        return simVal;
    }

    public static double pearson(double[] xData, double[] yData) {
        /*
        计算两组数的pearson相似度
         */
        if (xData.length != yData.length)
            throw new RuntimeException("数据不正确！");
        double xMeans;
        double yMeans;
        double numerator = 0;// 求解皮尔逊的分子
        double denominator = 0;// 求解皮尔逊系数的分母

        double result = 0;
        // 拿到两个数据的平均值
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        // 计算皮尔逊系数的分子
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数的分母
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数
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
