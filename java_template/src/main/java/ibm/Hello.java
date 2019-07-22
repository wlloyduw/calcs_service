package ibm;
import com.google.gson.*;
import faasinspector.Inspector;
import java.util.HashMap;
import java.util.ArrayList;
import shared.calcThread;


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
    public JsonObject main(JsonObject args) {
        //Collect data
        Inspector inspector = new Inspector();
        inspector.inspectAll();
        inspector.addTimeStamp("frameworkRuntime");

        int threads = Integer.parseInt(args.getAsJsonPrimitive("threads").getAsString());
        int calcs = Integer.parseInt(args.getAsJsonPrimitive("calcs").getAsString());
        int sleep = Integer.parseInt(args.getAsJsonPrimitive("sleep").getAsString());
        int loops = Integer.parseInt(args.getAsJsonPrimitive("loops").getAsString());
        int arraySize = Integer.parseInt(args.getAsJsonPrimitive("arraySize").getAsString());

        ArrayList<Thread> threadList = new ArrayList<>();
        
        //Create threads that will do math.
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(new calcThread(calcs, sleep, loops, arraySize, inspector, i));
            threadList.add(t);
            t.start();
        }
        
        //Using this thread, wait for threads to finish.
        for (Thread t : threadList) {
            try {
                t.join();
            } catch (Exception e) {
                inspector.addAttribute("ERROR", e.getStackTrace());
            }
        }

        inspector.addAttribute("threads", threads);
        inspector.addAttribute("calcs", calcs);
        inspector.addAttribute("loops", loops);
        inspector.addAttribute("sleep", sleep);
        inspector.addAttribute("arraySize", arraySize);
        
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
}
