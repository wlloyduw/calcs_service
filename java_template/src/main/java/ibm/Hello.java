package ibm;
import com.google.gson.*;
import saaf.Inspector;
import java.util.HashMap;

/**
 * @author Robert Cordingly
 */
public class Hello {
   
    /**
     * IBM Cloud Functions default handler.
     * 
     * @param args JsonObject of input Json.
     * @return JsonObject of output.
     */
    public static JsonObject main(JsonObject args) {
        //Collect data
        Inspector inspector = new Inspector();
        inspector.inspectAll();
        inspector.addTimeStamp("frameworkRuntime");

        int threads = Integer.parseInt(args.getAsJsonPrimitive("threads").getAsString());
        int calcs = request.getCalcs();
        int sleep = request.getSleep();
        int loops = request.getLoops();
        int arraySize = request.getArraySize();

        ArrayList<Thread> threadList = new ArrayList<>();
        
        //Create threads that will do math.
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(new calcThread(calcs, sleep, loops, arraySize));
            t.start();
        }

        //Using this thread, wait for threads to finish.
        for (Thread t : threadList) {
            t.join();
        }
        
        //Calculate CPU deltas.
        inspector.inspectCPUDelta();
        
        //Convert Inspector Hashmap to JsonObject
        JsonObject output = new JsonObject();
        HashMap<String, Object> results = inspector.finish();
        results.keySet().forEach((s) -> {
            output.addProperty(s, String.valueOf(results.get(s)));
        });
        return output;
    }
    
    /**
     * Threads that does all of the math.
     */
    private class calcThread implements Runnable {

        private final int calcs;
        private final int sleep;
        private final int loops;
        private final int arraySize;
        
        long[] operand_a;
        long[] operand_b;
        long[] operand_c;
        
        private long lastCalc = 0;
        
        //Set seed so random always returns the same set of values.
        Random rand = new Random(42);

        private calcThread(int calcs, int sleep, int loops, int arraySize) {
            this.calcs = calcs;
            this.sleep = sleep;
            this.loops = loops;
            this.arraySize = arraySize;
            
            this.operand_a = new long[arraySize];
            this.operand_b = new long[arraySize];
            this.operand_c = new long[arraySize];
        }

        @Override
        public void run() {
            
            if (loops > 0) {
                for (int i = 0; i < loops; i++) {
                    lastCalc = (long) randomMath(calcs);
                    if (sleep > 0) {
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException ie) {
                            System.out.println("Sleep was interrupted - calc mode...");
                        }
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

        private double randomMath(int calcs) {
            // By not reusing the same variables in the calc, this should prevent
            // compiler optimization... Also each math operation should operate
            // on between operands in different memory locations.
            long mult;
            double div1 = 0;

            for (int i = 0; i < calcs; i++) {
                // By not using sequential locations in the array, we should 
                // reduce memory lookup efficiency
                int j = rand.nextInt(arraySize);
                operand_a[j] = rand.nextInt(99999);
                operand_b[j] = rand.nextInt(99999);
                operand_c[j] = rand.nextInt(99999);
                mult = operand_a[j] * operand_b[j];
                div1 = (double) mult / (double) operand_c[j];
            }
            return div1;
        }
    }
}
