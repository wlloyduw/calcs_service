package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import faasinspector.Inspector;
import java.util.HashMap;
import java.util.Random;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class Hello implements RequestHandler<Request, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     *
     * @param request Request POJO with defined variables from Request.java
     * @param context
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(Request request, Context context) {

        //Collect data
        Inspector inspector = new Inspector();
        inspector.inspectAll();
        inspector.addTimeStamp("frameworkRuntime");
        
        int threads = request.getThreads() - 1;
        int calcs = request.getCalcs();
        int sleep = request.getSleep();
        int loops = request.getLoops();
        
        //Kick off secondary threads to process math.
        for (int i = 0; i < threads; i++) {
            (new Thread(new calcThread(calcs, sleep, loops))).start();
        }
        
        //Use this thread to do some math too.
        new calcThread(calcs, sleep, loops).run();
        
        inspector.inspectCPUDelta();
        return inspector.finish();
    }

    /**
     * Threads that does all of the math.
     */
    private class calcThread implements Runnable {

        private final int calcs;
        private final int sleep;
        private final int loops;
        
        long[] operand_a;
        long[] operand_b;
        long[] operand_c;

        private calcThread(int calcs, int sleep, int loops) {
            this.calcs = calcs;
            this.sleep = sleep;
            this.loops = loops;
            
            this.operand_a = new long[calcs];
            this.operand_b = new long[calcs];
            this.operand_c = new long[calcs];
        }

        @Override
        public void run() {
            
            if (loops > 0) {
                for (int i = 0; i < loops; i++) {
                    randomMath(calcs);
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException ie) {
                        System.out.println("Sleep was interrupted - calc mode...");
                    }
                }
            } else {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ie) {
                    System.out.println("Sleep was interrupted - no calc mode...");
                }
            }
        }

        private void randomMath(int calcs) {
            Random rand = new Random();
            // By not reusing the same variables in the calc, this should prevent
            // compiler optimization... Also each math operation should operate
            // on between operands in different memory locations.
            long mult;
            double div1;

            for (int i = 0; i < calcs; i++) {
                // By not using sequential locations in the array, we should 
                // reduce memory lookup efficiency
                int j = rand.nextInt(calcs);
                operand_a[j] = rand.nextInt(99999);
                operand_b[j] = rand.nextInt(99999);
                operand_c[j] = rand.nextInt(99999);
                mult = operand_a[j] * operand_b[j];
                div1 = (double) mult / (double) operand_c[j];
            }
        }
    }
}
