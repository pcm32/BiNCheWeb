/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BiNGO.methods.saddlesum;

import cern.jet.stat.Gamma;

/**
 * There is no progress here. This is just were this class is suppose to be to 
 * add a new test and distribution (SaddleSum) to the list of posssibilities of
 * BiNGO. This was an attempt to incorporate a test that would take into account
 * the scores of each element and not only how many elements are thare in the group.
 * 
 * Another way to go would be to implement a weighted (through the scores) version
 * of the hypergeometric test.
 *
 * @author pmoreno
 */
class SaddleSumDistribution {
    // x out of X genes in cluster A belong to GO category B which
    // is shared by n out of N genes in the reference set.
    /**
     * number of successes in sample.
     */
    private static int x;
    /**
     * sample size.
     */
    private static int bigX;
    /**
     * number of successes in population.
     */
    private static int n;
    /**
     * population size.
     */

    private static int bigN;

    public SaddleSumDistribution(int x, int bigX, int n, int bigN) {
        SaddleSumDistribution.x = x;
        SaddleSumDistribution.bigX = bigX;
        SaddleSumDistribution.n = n;
        SaddleSumDistribution.bigN = bigN;
    }

    public String calculateSaddleSumDistr() {
        	if(bigN >= 2){
        double sum = 0;
		//mode of distribution, integer division (returns integer <= double result)!
		int mode = (bigX+1)*(n+1)/(bigN+2) ;
		if(x >= mode){
                    int i = x ;
                    while ((bigN - n >= bigX - i) && (i <= Math.min(bigX, n))) {	
                        double pdfi = Math.exp(Gamma.logGamma(n+1)-Gamma.logGamma(i+1)-Gamma.logGamma(n-i+1) + Gamma.logGamma(bigN-n+1)-Gamma.logGamma(bigX-i+1)-Gamma.logGamma(bigN-n-bigX+i+1)- Gamma.logGamma(bigN+1)+Gamma.logGamma(bigX+1)+Gamma.logGamma(bigN-bigX+1)) ;
                        sum = sum+pdfi;
                        i++;
                    }	
		}	
		else{
                    int i = x - 1;
                    while ((bigN - n >= bigX - i) && (i >= 0)) {
			double pdfi = Math.exp(Gamma.logGamma(n+1)-Gamma.logGamma(i+1)-Gamma.logGamma(n-i+1) + Gamma.logGamma(bigN-n+1)-Gamma.logGamma(bigX-i+1)-Gamma.logGamma(bigN-n-bigX+i+1)- Gamma.logGamma(bigN+1)+Gamma.logGamma(bigX+1)+Gamma.logGamma(bigN-bigX+1)) ;
                        sum = sum+pdfi;
                        i--;
                    }	
                    sum = 1-sum;
                }
                return (new Double(sum)).toString();
            }
            else{return (new Double(1)).toString();}
        //SaddleSumData BiNGO = new SaddleSumData(backgroundWeights);
       // return null;

        
    }
}
